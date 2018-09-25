package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.FeeDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.model.vigipay.VigiPayMessage;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInBranchNotification;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ModeOfPaymentReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import com.software.finatech.lslb.cms.service.util.async_helpers.PaymentEmailNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class PaymentRecordDetailServiceImpl implements PaymentRecordDetailService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordDetailServiceImpl.class);

    private FeeService feeService;
    private PaymentRecordService paymentRecordService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private VigipayService vigipayService;
    private InstitutionService institutionService;
    private AgentService agentService;
    private GamingMachineService gamingMachineService;
    private AuthInfoService authInfoService;
    private LicenseService licenseService;
    private ApplicationFormService applicationFormService;
    private PaymentEmailNotifierAsync paymentEmailNotifierAsync;

    @Autowired
    public PaymentRecordDetailServiceImpl(FeeService feeService,
                                          PaymentRecordService paymentRecordService,
                                          MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                          VigipayService vigipayService,
                                          InstitutionService institutionService,
                                          AgentService agentService,
                                          GamingMachineService gamingMachineService,
                                          AuthInfoService authInfoService,
                                          LicenseService licenseService,
                                          ApplicationFormService applicationFormService,
                                          PaymentEmailNotifierAsync paymentEmailNotifierAsync) {
        this.feeService = feeService;
        this.paymentRecordService = paymentRecordService;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.vigipayService = vigipayService;
        this.institutionService = institutionService;
        this.agentService = agentService;
        this.gamingMachineService = gamingMachineService;
        this.authInfoService = authInfoService;
        this.licenseService = licenseService;
        this.applicationFormService = applicationFormService;
        this.paymentEmailNotifierAsync = paymentEmailNotifierAsync;
    }

    @Override
    public Mono<ResponseEntity> updateWebPaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto) {
        try {
            String paymentRecordDetailId = paymentRecordDetailUpdateDto.getId();
            PaymentRecordDetail existingPaymentRecordDetail = findById(paymentRecordDetailId);
            if (existingPaymentRecordDetail == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment with is %s does not exist", paymentRecordDetailId), HttpStatus.BAD_REQUEST));
            }

            PaymentRecord paymentRecord = paymentRecordService.findById(existingPaymentRecordDetail.getPaymentRecordId());
            if (StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecordDetailUpdateDto.getPaymentStatusId())
                    && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingPaymentRecordDetail.getPaymentStatusId())) {
                double amountPaid = paymentRecord.getAmountPaid();
                amountPaid = amountPaid + existingPaymentRecordDetail.getAmount();
                paymentRecord.setAmountPaid(amountPaid);

                double amountOutstanding = paymentRecord.getAmountOutstanding();
                amountOutstanding = amountOutstanding - existingPaymentRecordDetail.getAmount();
                paymentRecord.setAmountOutstanding(amountOutstanding);

                updatePaymentRecord(paymentRecord);
            }
            existingPaymentRecordDetail.setInvoiceNumber(paymentRecordDetailUpdateDto.getInvoiceNumber());
            existingPaymentRecordDetail.setPaymentDate(LocalDateTime.now());
            existingPaymentRecordDetail.setPaymentStatusId(paymentRecordDetailUpdateDto.getPaymentStatusId());
            existingPaymentRecordDetail.setVigiPayTransactionReference(paymentRecordDetailUpdateDto.getVigipayReference());
            savePaymentRecordDetail(existingPaymentRecordDetail);
            paymentEmailNotifierAsync.sendPaymentNotificationForPaymentRecordDetail(existingPaymentRecordDetail, paymentRecord);
            return Mono.just(new ResponseEntity<>(existingPaymentRecordDetail.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating payment record detail", e);
        }
    }

    @Override
    public void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail) {
        mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
    }

    @Override
    public PaymentRecordDetail findById(String paymentRecordDetailId) {
        return (PaymentRecordDetail) mongoRepositoryReactive.findById(paymentRecordDetailId, PaymentRecordDetail.class).block();
    }

    @Override
    public Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        String invoiceNumber = null;
        Agent agent = null;
        Institution institution = null;
        String feeId = paymentRecordDetailCreateDto.getFeeId();
        Fee fee = feeService.findFeeById(feeId);
        if (fee == null) {
            return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
        }

        if (paymentRecordDetailCreateDto.isFirstPayment()) {
            Mono<ResponseEntity> validateFirstPaymentResponse = validateFirstPayment(paymentRecordDetailCreateDto, fee);
            if (validateFirstPaymentResponse != null) {
                return validateFirstPaymentResponse;
            }
        }

        FeeDto feeDto = fee.convertToDto();
        String feeName = feeDto.getFeePaymentTypeName();
        String gameTypeName = feeDto.getGameTypeName();
        String revenueName = feeDto.getRevenueName();
        String feeDescription = String.format("%s for %ss for category : %s ", feeName, revenueName, gameTypeName);
        feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(feeDescription);
        if (paymentRecordDetailCreateDto.getAmount() < fee.getAmount()) {
            feeDescription = String.format("%s (Part Payment)", feeDescription);
        }


        PaymentRecord paymentRecord = new PaymentRecord();
        String paymentRecordId = paymentRecordDetailCreateDto.getPaymentRecordId();
        if (!paymentRecordDetailCreateDto.isFirstPayment()) {
            paymentRecord = paymentRecordService.findById(paymentRecordId);
            if (paymentRecord == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s does not exist", paymentRecordDetailCreateDto.getPaymentRecordId()), HttpStatus.BAD_REQUEST));
            }
        }

        ArrayList<AuthInfo> institutionAdmins;
        if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
            String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
            institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(institutionId);
            if (institutionAdmins.isEmpty()) {
                return Mono.just(new ResponseEntity<>("There are no gaming operator admins for institution", HttpStatus.BAD_REQUEST));
            }
            institution = institutionService.findById(institutionId);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(institution.getVgPayCustomerCode())) {
                return Mono.just(new ResponseEntity<>("Customer not created", HttpStatus.BAD_REQUEST));
            }
            invoiceNumber = createInBranchRecordDetailForInstitution(institution, feeDescription, paymentRecordDetailCreateDto, institutionAdmins);
        }

        if (paymentRecordDetailCreateDto.isGamingMachinePayment()) {
            GamingMachine gamingMachine = gamingMachineService.findById(paymentRecordDetailCreateDto.getGamingMachineId());
            if (gamingMachine != null) {
                institution = institutionService.findById(gamingMachine.getInstitutionId());
                institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(institution.getId());
                if (institutionAdmins.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("There are no gaming operator admins for institution owning gaming machine", HttpStatus.BAD_REQUEST));
                }
                if (StringUtils.isEmpty(institution.getVgPayCustomerCode())) {
                    return Mono.just(new ResponseEntity<>("Customer not created", HttpStatus.BAD_REQUEST));
                }
                invoiceNumber = createInBranchRecordDetailForInstitution(institution, feeDescription, paymentRecordDetailCreateDto, institutionAdmins);
            }
        }
        if (paymentRecordDetailCreateDto.isAgentPayment()) {
            agent = agentService.findById(paymentRecordDetailCreateDto.getAgentId());
            if (agent != null) {
                invoiceNumber = createInBranchRecordDetailForAgent(agent, feeDescription, paymentRecordDetailCreateDto);
            }
        }

        if (StringUtils.isEmpty(invoiceNumber)) {
            return Mono.just(new ResponseEntity<>("Invoice was not created successfully", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        if (paymentRecordDetailCreateDto.isFirstPayment()) {
            paymentRecord = newPaymentFromFee(fee, agent, institution, paymentRecordDetailCreateDto.getGamingMachineId());
        }


        PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
        paymentRecordDetail.setId(UUID.randomUUID().toString());
        paymentRecordDetail.setAmount(paymentRecordDetailCreateDto.getAmount());
        paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.IN_BRANCH_ID);
        paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
        paymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
        paymentRecordDetail.setInvoiceNumber(invoiceNumber);
        savePaymentRecordDetail(paymentRecordDetail);

        List<String> paymentRecordDetailIdList = paymentRecord.getPaymentRecordDetailIds();
        paymentRecordDetailIdList.add(paymentRecordDetail.getId());
        paymentRecord.setPaymentRecordDetailIds(paymentRecordDetailIdList);
        paymentRecordService.savePaymentRecord(paymentRecord);
        return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        try {
            String feeId = paymentRecordDetailCreateDto.getFeeId();
            Fee fee = feeService.findFeeById(feeId);
            if (fee == null) {
                return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
            }

            if (paymentRecordDetailCreateDto.isFirstPayment()) {
                Mono<ResponseEntity> validateFirstPaymentResponse = validateFirstPayment(paymentRecordDetailCreateDto, fee);
                if (validateFirstPaymentResponse != null) {
                    return validateFirstPaymentResponse;
                }
            }

            PaymentRecord paymentRecord = new PaymentRecord();
            String paymentRecordId = paymentRecordDetailCreateDto.getPaymentRecordId();
            if (!paymentRecordDetailCreateDto.isFirstPayment()) {
                paymentRecord = paymentRecordService.findById(paymentRecordId);
                if (paymentRecord == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s does not exist", paymentRecordDetailCreateDto.getPaymentRecordId()), HttpStatus.BAD_REQUEST));
                }
            }

            Agent agent = null;
            if (paymentRecordDetailCreateDto.isAgentPayment()) {
                String agentId = paymentRecordDetailCreateDto.getAgentId();
                agent = agentService.findById(agentId);
                if (agent == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Agent with id %s does not exist", agentId), HttpStatus.BAD_REQUEST));
                }
            }
            Institution institution = null;
            if (paymentRecordDetailCreateDto.isGamingMachinePayment()) {
                String gamingMachineId = paymentRecordDetailCreateDto.getGamingMachineId();
                GamingMachine gamingMachine = gamingMachineService.findById(gamingMachineId);
                if (gamingMachine == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
                }
                institution = institutionService.findById(gamingMachine.getInstitutionId());
            }

            if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
                String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
                institution = institutionService.findById(institutionId);
                if (institution == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
                }
            }
            if (paymentRecordDetailCreateDto.isFirstPayment()) {
                paymentRecord = newPaymentFromFee(fee, agent, institution, paymentRecordDetailCreateDto.getGamingMachineId());
            }

            PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
            paymentRecordDetail.setId(UUID.randomUUID().toString());
            paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.WEB_PAYMENT_ID);
            paymentRecordDetail.setAmount(paymentRecordDetailCreateDto.getAmount());
            paymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
            paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
            savePaymentRecordDetail(paymentRecordDetail);

            List<String> paymentRecordDetailIds = paymentRecord.getPaymentRecordDetailIds();
            paymentRecordDetailIds.add(paymentRecordDetail.getId());
            paymentRecord.setPaymentRecordDetailIds(paymentRecordDetailIds);
            paymentRecordService.savePaymentRecord(paymentRecord);

            return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating payment record detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findAllPaymentRecordDetailForPaymentRecord(String paymentRecordId) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            query.with(new Sort(Sort.Direction.DESC, "paymentDate"));
            ArrayList<PaymentRecordDetail> paymentRecordDetails = (ArrayList<PaymentRecordDetail>) mongoRepositoryReactive.findAll(query, PaymentRecordDetail.class).toStream().collect(Collectors.toList());
            if (paymentRecordDetails.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            List<PaymentRecordDetailDto> paymentRecordDetailDtos = new ArrayList<>();
            for (PaymentRecordDetail paymentRecordDetail : paymentRecordDetails) {
                paymentRecordDetailDtos.add(paymentRecordDetail.convertToDto());
            }
            return Mono.just(new ResponseEntity<>(paymentRecordDetailDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting payment record details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> handleVigipayInBranchNotification(VigipayInBranchNotification vigipayInBranchNotification) {
        try {
            VigiPayMessage vigiPayMessage = vigipayInBranchNotification.getMessage();
            if (vigiPayMessage != null) {

                Query query = new Query();
                query.addCriteria(Criteria.where("invoiceNumber").is(vigiPayMessage.getInvoiceNumber()));
                PaymentRecordDetail existingPaymentRecordDetail = (PaymentRecordDetail) mongoRepositoryReactive.find(query, PaymentRecordDetail.class).block();
                if (existingPaymentRecordDetail == null) {
                    return Mono.just(new ResponseEntity<>("Invoice does not exist", HttpStatus.BAD_REQUEST));
                }
                if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingPaymentRecordDetail.getPaymentStatusId())) {
                    boolean isConfirmedPayment = true;


                    //TODO: remember to validate payment from vigipay
//                    try {
//                     //   isConfirmedPayment = vigipayService.isConfirmedInvoicePayment(invoiceNumber);
//                    } catch (VigiPayServiceException e) {
//                        logger.error("An error occurred while confirming payment status from vigipay", e);
//                        existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
//                        savePaymentRecordDetail(existingPaymentRecordDetail);
//                        return Mono.just(new ResponseEntity<>("An error occurred while confirming payment from vigipay", HttpStatus.INTERNAL_SERVER_ERROR));
//                    }
                    if (isConfirmedPayment) {
                        PaymentRecord paymentRecord = paymentRecordService.findById(existingPaymentRecordDetail.getPaymentRecordId());
                        double amountPaid = paymentRecord.getAmountPaid();
                        amountPaid = amountPaid + vigiPayMessage.getAmountPaid();
                        paymentRecord.setAmountPaid(amountPaid);

                        double amountOutstanding = paymentRecord.getAmountOutstanding();
                        amountOutstanding = amountOutstanding - vigiPayMessage.getAmountPaid();
                        paymentRecord.setAmountOutstanding(amountOutstanding);

                        updatePaymentRecord(paymentRecord);

                        if (existingPaymentRecordDetail.getAmount() > vigiPayMessage.getAmountPaid()) {
                            existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID);
                        } else {
                            existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
                        }
                        existingPaymentRecordDetail.setPaymentDate(LocalDateTime.now());
                        existingPaymentRecordDetail.setVigiPayTransactionReference(vigiPayMessage.getPaymentReference());
                        existingPaymentRecordDetail.setInvoiceNumber(vigiPayMessage.getInvoiceNumber());
                        savePaymentRecordDetail(existingPaymentRecordDetail);
                        paymentEmailNotifierAsync.sendPaymentNotificationForPaymentRecordDetail(existingPaymentRecordDetail, paymentRecord);
                        return Mono.just(new ResponseEntity<>("updated successfully", HttpStatus.OK));
                    }
                    existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
                    savePaymentRecordDetail(existingPaymentRecordDetail);
                    return Mono.just(new ResponseEntity<>("Payment status was not confirmed ", HttpStatus.FAILED_DEPENDENCY));
                }
                return Mono.just(new ResponseEntity<>("Payment completed already", HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>("Empty Message object", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while processing the vigi pay notification", e);
        }
    }

    private String createInBranchRecordDetailForInstitution(Institution institution, String feeDescription, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, List<AuthInfo> admins) {
        VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
        vigipayInvoiceItem.setAmount(paymentRecordDetailCreateDto.getAmount());
        vigipayInvoiceItem.setDetail(feeDescription);
        vigipayInvoiceItem.setQuantity(1);
        vigipayInvoiceItem.setProductCode("");
        return vigipayService.createInBranchInvoiceForInstitution(institution, admins, vigipayInvoiceItem);
    }

    private String createInBranchRecordDetailForAgent(Agent agent, String feeDescription, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
        vigipayInvoiceItem.setAmount(paymentRecordDetailCreateDto.getAmount());
        vigipayInvoiceItem.setDetail(feeDescription);
        vigipayInvoiceItem.setQuantity(1);
        vigipayInvoiceItem.setProductCode("");
        return vigipayService.createInBranchInvoiceForAgent(agent, vigipayInvoiceItem);
    }

    private void updatePaymentRecord(PaymentRecord paymentRecord) {
        if (paymentRecord.getAmountOutstanding() <= 0) {
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
            String revenueNameId = paymentRecord.getRevenueNameId();
            String feePaymentTypeId = paymentRecord.getFeePaymentTypeId();

            if (StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, revenueNameId)
                    && StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, feePaymentTypeId)) {
                licenseService.createAIPLicenseForCompletedPayment(paymentRecord);
            }

            if (StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, revenueNameId) &&
                    StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, feePaymentTypeId)) {
                licenseService.createFirstLicenseForGamingMachinePayment(paymentRecord);
            }

            if (StringUtils.equals(RevenueNameReferenceData.AGENT_REVENUE_ID, revenueNameId) &&
                    StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, feePaymentTypeId)) {
                licenseService.createFirstLicenseForAgentPayment(paymentRecord);
            }

            if (StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID, feePaymentTypeId)) {
                licenseService.createRenewedLicenseForPayment(paymentRecord);
            }

        } else {
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID);
        }
        paymentRecordService.savePaymentRecord(paymentRecord);
    }

    private Mono<ResponseEntity> validateFirstPayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) {
        if (StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID, fee.getFeePaymentTypeId())) {
            Mono<ResponseEntity> validateRenewalLicenseResponse = validateLicenseRenewalPayment(paymentRecordDetailCreateDto, fee);
            if (validateRenewalLicenseResponse != null) {
                return validateRenewalLicenseResponse;
            }
        }

        if (StringUtils.equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID, fee.getFeePaymentTypeId())) {
            Mono<ResponseEntity> validateApplicationPaymentResponse = validateApplicationPayment(paymentRecordDetailCreateDto.getInstitutionId(), fee.getGameTypeId());
            if (validateApplicationPaymentResponse != null) {
                return validateApplicationPaymentResponse;
            }
        }

        if (StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, fee.getFeePaymentTypeId())) {
            Mono<ResponseEntity> validateLicensePaymentResponse = validateLicensePayment(paymentRecordDetailCreateDto, fee);
            if (validateLicensePaymentResponse != null) {
                return validateLicensePaymentResponse;
            }
        }
        return null;
    }


    private Mono<ResponseEntity> validateLicensePayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) {
        String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
        String agentId = paymentRecordDetailCreateDto.getAgentId();
        String gamingMachineId = paymentRecordDetailCreateDto.getGamingMachineId();
        String gameTypeId = fee.getGameTypeId();
        String revenueNameId = fee.getRevenueNameId();
        if (paymentRecordDetailCreateDto.isAgentPayment()) {
            gameTypeId = fee.getGameTypeId();
        }
        boolean licensePaymentExist = licensePaymentRecordExists(revenueNameId, gameTypeId, gamingMachineId, agentId, institutionId);
        if (licensePaymentExist) {
            return Mono.just(new ResponseEntity<>("Payment for licence for category already exists, Licence Renewal payment is what is applicable", HttpStatus.BAD_REQUEST));
        }

        if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
            boolean institutionHasApplicationPayment = institutionHasApplicationPaymentInCategory(institutionId, gameTypeId);
            if (!institutionHasApplicationPayment) {
                return Mono.just(new ResponseEntity<>("Kindly make payment for application fees for category before proceeding", HttpStatus.BAD_REQUEST));
            }

            boolean institutionHasApprovedForm = applicationFormService.institutionHasCompletedApplicationForGameType(institutionId, gameTypeId);
            if (!institutionHasApprovedForm) {
                String errorMsg = "Kindly make sure you have an approved application in category before paying for licence before proceeding";
                return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));
            }
        }
        return null;
    }

    private Mono<ResponseEntity> validateLicenseRenewalPayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) {
        String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
        String agentId = paymentRecordDetailCreateDto.getAgentId();
        String gamingMachineId = paymentRecordDetailCreateDto.getGamingMachineId();
        String gameTypeId = fee.getGameTypeId();
        int page = 0;
        int pageSize = 10000;
        String sortDirection = "DESC";
        String sortProperty = "createdAt";
        Mono<ResponseEntity> findLicenseResponse = licenseService.findAllLicense(page, pageSize, sortDirection, sortProperty, institutionId, agentId, gamingMachineId, null, gameTypeId, null, null, null, null);
        if (findLicenseResponse.block().getStatusCode() != HttpStatus.OK) {
            String categoryName = fee.getGameTypeName();
            return Mono.just(new ResponseEntity<>(String.format("You do not have an existing license for category %s, please get licensed for category %s before attempting to pay for license renewal for the category", categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }
        return null;
    }


    private Mono<ResponseEntity> validateApplicationPayment(String institutionId, String gameTypeId) {
        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        String institutionRevenueNameId = RevenueNameReferenceData.INSTITUTION_REVENUE_ID;

        Query query = new Query();
        query.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("revenueNameId").is(institutionRevenueNameId));

        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        if (paymentRecord != null) {
            return Mono.just(new ResponseEntity<>("You have an existing payment for application for license  for the category specified", HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    private boolean institutionHasApplicationPaymentInCategory(String institutionId, String gameTypeId) {
        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        String institutionRevenueNameId = RevenueNameReferenceData.INSTITUTION_REVENUE_ID;

        Query query = new Query();
        query.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("revenueNameId").is(institutionRevenueNameId));

        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        return paymentRecord != null;
    }


    private boolean licensePaymentRecordExists(String revenueNameId,
                                               String gameTypeId,
                                               String gamingMachineId,
                                               String agentId,
                                               String institutionId) {
        String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
        String sortDirection = "DESC";
        String sortProperty = "createdAt";
        int page = 0;
        int pageSize = 1000;

        Mono<ResponseEntity> paymentRecordsResponse = paymentRecordService.findAllPaymentRecords(page,
                pageSize, sortDirection, sortProperty, institutionId, agentId, gamingMachineId, gameTypeId, feePaymentTypeId, revenueNameId, null, null);

        return paymentRecordsResponse.block().getStatusCode() != HttpStatus.NOT_FOUND;
    }

    private PaymentRecord newPaymentFromFee(Fee fee, Agent agent, Institution institution, String gamingMachineId) {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(UUID.randomUUID().toString());
        paymentRecord.setAmountOutstanding(fee.getAmount());
        paymentRecord.setAmount(fee.getAmount());
        paymentRecord.setAmountPaid(0);
        paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
        paymentRecord.setFeeId(fee.getId());
        paymentRecord.setGamingMachineId(gamingMachineId);
        paymentRecord.setGameTypeId(fee.getGameTypeId());
        paymentRecord.setRevenueNameId(fee.getRevenueNameId());
        paymentRecord.setFeePaymentTypeId(fee.getFeePaymentTypeId());
        if (agent != null) {
            paymentRecord.setAgentId(agent.getId());
        }
        if (institution != null) {
            paymentRecord.setInstitutionId(institution.getId());
        }
        return paymentRecord;
    }
}
