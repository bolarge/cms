package com.software.finatech.lslb.cms.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.exception.VigiPayServiceException;
import com.software.finatech.lslb.cms.service.model.vigipay.VigiPayMessage;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.*;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.PaymentEmailNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.BadRequestResponse;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;
import static com.software.finatech.lslb.cms.service.util.RequestAddressUtil.getClientIpAddr;

@Service
public class PaymentRecordDetailServiceImpl implements PaymentRecordDetailService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordDetailServiceImpl.class);
    public static final String paymentAuditActionId = AuditActionReferenceData.PAYMENT_ID;

    private FeeService feeService;
    private PaymentRecordService paymentRecordService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private VigipayService vigipayService;
    private InstitutionService institutionService;
    private AgentService agentService;
    private MachineService gamingMachineService;
    private AuthInfoService authInfoService;
    private LicenseService licenseService;
    private ApplicationFormService applicationFormService;
    private PaymentEmailNotifierAsync paymentEmailNotifierAsync;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private LicenseTransferService licenseTransferService;
    private InstitutionOnboardingWorkflowService institutionOnboardingWorkflowService;
    private EnvironmentUtils environmentUtils;

    @Autowired
    public PaymentRecordDetailServiceImpl(FeeService feeService,
                                          PaymentRecordService paymentRecordService,
                                          MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                          VigipayService vigipayService,
                                          InstitutionService institutionService,
                                          AgentService agentService,
                                          MachineService gamingMachineService,
                                          AuthInfoService authInfoService,
                                          LicenseService licenseService,
                                          ApplicationFormService applicationFormService,
                                          PaymentEmailNotifierAsync paymentEmailNotifierAsync,
                                          SpringSecurityAuditorAware springSecurityAuditorAware,
                                          AuditLogHelper auditLogHelper,
                                          EnvironmentUtils environmentUtils,
                                          LicenseTransferService licenseTransferService,
                                          InstitutionOnboardingWorkflowService institutionOnboardingWorkflowService) {
        this.feeService = feeService;
        this.environmentUtils = environmentUtils;
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
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.licenseTransferService = licenseTransferService;
        this.institutionOnboardingWorkflowService = institutionOnboardingWorkflowService;
    }

    @Override
    public Mono<ResponseEntity> updatePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto, HttpServletRequest httpServletRequest) {
        try {
            String paymentRecordDetailId = paymentRecordDetailUpdateDto.getId();
            PaymentRecordDetail existingPaymentRecordDetail = findById(paymentRecordDetailId);
            if (existingPaymentRecordDetail == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment with is %s does not exist", paymentRecordDetailId), HttpStatus.BAD_REQUEST));
            }

            PaymentRecord paymentRecord = paymentRecordService.findById(existingPaymentRecordDetail.getPaymentRecordId());
            if (paymentRecordDetailUpdateDto.isSuccessFulPayment()
                    && !existingPaymentRecordDetail.isSuccessfulPayment()) {
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

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Payment record detail callback -> Payment Record Detail id: %s, Invoice Number -> %s, Status Id -> %s", paymentRecordDetailId, existingPaymentRecordDetail.getInvoiceNumber(), paymentRecordDetailUpdateDto.getPaymentStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(paymentAuditActionId,
                    currentAuditorName, currentAuditorName,
                    true, getClientIpAddr(httpServletRequest), verbiage));
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
    public Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request) {
        try {
            String invoiceNumber = null;
            Agent agent;
            Institution institution;
            String feeId = paymentRecordDetailCreateDto.getFeeId();
            Fee fee = null;
            String feeDescription = "";
            MachineMultiplePayment machineMultiplePayment = new MachineMultiplePayment();

            if (paymentRecordDetailCreateDto.isFirstPayment() && (paymentRecordDetailCreateDto.isAgentPayment() || paymentRecordDetailCreateDto.isInstitutionPayment())) {
                fee = feeService.findFeeById(feeId);
                if (fee == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
                }
                Mono<ResponseEntity> validateFirstPaymentResponse = validateFirstPayment(paymentRecordDetailCreateDto, fee);
                if (validateFirstPaymentResponse != null) {
                    return validateFirstPaymentResponse;
                }
            }

            if (fee != null) {
                FeeDto feeDto = fee.convertToDto();
                String feeName = feeDto.getFeePaymentTypeName();
                String gameTypeName = feeDto.getGameTypeName();
                String revenueName = feeDto.getRevenueName();
                feeDescription = buildFeeDescription(feeName, gameTypeName, revenueName);
                if (paymentRecordDetailCreateDto.getAmount() < fee.getAmount()) {
                    feeDescription = String.format("%s (Part Payment)", feeDescription);
                }
            }

            PaymentRecord paymentRecord = new PaymentRecord();
            String paymentRecordId = paymentRecordDetailCreateDto.getPaymentRecordId();
            if (!paymentRecordDetailCreateDto.isFirstPayment()) {
                paymentRecord = paymentRecordService.findById(paymentRecordId);
                if (paymentRecord == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s does not exist", paymentRecordDetailCreateDto.getPaymentRecordId()), HttpStatus.BAD_REQUEST));
                }
                String feeName = paymentRecord.getFeePaymentTypeName();
                String gameTypeName = paymentRecord.getGameTypeName();
                String revenueName = paymentRecord.getLicenseTypeName();
                feeDescription = buildFeeDescription(feeName, gameTypeName, revenueName);
                if (paymentRecordDetailCreateDto.getAmount() < paymentRecord.getAmount()) {
                    feeDescription = String.format("%s (Part Payment)", feeDescription);
                }
            }

            ArrayList<AuthInfo> institutionAdmins;
            if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
                String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
                institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(institutionId);
                if (institutionAdmins.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("There are no gaming operator users for institution", HttpStatus.BAD_REQUEST));
                }
                institution = institutionService.findByInstitutionId(institutionId);
                if (institution == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
                }
                if (StringUtils.isEmpty(institution.getVgPayCustomerCode())) {
                    return Mono.just(new ResponseEntity<>("Customer not created", HttpStatus.BAD_REQUEST));
                }
                invoiceNumber = createInBranchRecordDetailForInstitution(institution, feeDescription, paymentRecordDetailCreateDto, institutionAdmins);
            }

            if (paymentRecordDetailCreateDto.isGamingMachinePayment()) {
                institution = institutionService.findByInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
                institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(paymentRecordDetailCreateDto.getInstitutionId());
                if (institutionAdmins.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("There are no gaming operator admins for institution owning gaming machine", HttpStatus.BAD_REQUEST));
                }
                if (StringUtils.isEmpty(institution.getVgPayCustomerCode())) {
                    return Mono.just(new ResponseEntity<>("Customer not created", HttpStatus.BAD_REQUEST));
                }
                Pair<Mono<ResponseEntity>, MachineMultiplePayment> multiplePaymentPair = validateGamingMachinePayment(paymentRecordDetailCreateDto);
                if (multiplePaymentPair.getLeft() != null) {
                    return multiplePaymentPair.getLeft();
                }
                machineMultiplePayment = multiplePaymentPair.getRight();
                invoiceNumber = createInBranchRecordDetailForGamingMachine(institution, machineMultiplePayment.getMachinePaymentDetailList(), institutionAdmins);
            }

            if (paymentRecordDetailCreateDto.isGamingTerminalPayment()) {
                agent = agentService.findAgentById(paymentRecordDetailCreateDto.getAgentId());
                if (agent == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Agent with Id %s does not exist", paymentRecordDetailCreateDto.getAgentId()), HttpStatus.BAD_REQUEST));
                }
                Pair<Mono<ResponseEntity>, MachineMultiplePayment> multiplePaymentPair = validateGamingTerminalPayment(paymentRecordDetailCreateDto);
                if (multiplePaymentPair.getLeft() != null) {
                    return multiplePaymentPair.getLeft();
                }
                machineMultiplePayment = multiplePaymentPair.getRight();
                invoiceNumber = createInBranchRecordDetailForGamingTerminal(agent, machineMultiplePayment.getMachinePaymentDetailList());
            }

            if (paymentRecordDetailCreateDto.isAgentPayment()) {
                agent = agentService.findAgentById(paymentRecordDetailCreateDto.getAgentId());
                if (agent != null) {
                    invoiceNumber = createInBranchRecordDetailForAgent(agent, feeDescription, paymentRecordDetailCreateDto);
                }
            }

            if (StringUtils.isEmpty(invoiceNumber)) {
                return Mono.just(new ResponseEntity<>("Invoice was not created successfully", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            if (paymentRecordDetailCreateDto.isFirstPayment()) {
                if (paymentRecordDetailCreateDto.isInstitutionPayment() || paymentRecordDetailCreateDto.isAgentPayment()) {
                    paymentRecord = newPaymentFromFee(fee, paymentRecordDetailCreateDto);
                }
                if (paymentRecordDetailCreateDto.isGamingMachinePayment() || paymentRecordDetailCreateDto.isGamingTerminalPayment()) {
                    paymentRecord = newPaymentForMachine(paymentRecordDetailCreateDto, machineMultiplePayment);
                }
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

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = "";
            if (paymentRecordDetailCreateDto.isInstitutionPayment() || paymentRecordDetailCreateDto.isAgentPayment()) {
                verbiage = String.format("Created In Branch payment record detail -> License Type -> %s, Amount -> %s, Fee Payment Type -> %s,Category -> %s, Id -> %s",
                        paymentRecord.getLicenseType(), paymentRecord.getAmount(), paymentRecord.getFeePaymentType(), paymentRecord.getGameType(), paymentRecordDetail.getId());
            }
            if (paymentRecordDetailCreateDto.isGamingTerminalPayment() || paymentRecordDetailCreateDto.isGamingMachinePayment()) {
                verbiage = String.format("Created Multiple Machine payment -> Machine Serial Numbers -> %s, Amount -> %s", multipleMachinePaymentToAuditString(machineMultiplePayment), machineMultiplePayment.getTotalAmount());
            }
            paymentEmailNotifierAsync.handlePostPaymentInitiationEvents(paymentRecord, paymentRecordDetailCreateDto);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(paymentAuditActionId,
                    currentAuditorName, currentAuditorName,
                    true, getClientIpAddr(request), verbiage));
            paymentEmailNotifierAsync.sendInitialVigiPayPaymentNotificationToInitiator(paymentRecordDetail, paymentRecord);
            return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
        } catch (VigiPayServiceException e) {
            String errorMessage = "An error occurred while creating invoice";
            return Mono.just(new ResponseEntity<>(new PaymentErrorResponse(errorMessage, e.getMessage()), HttpStatus.EXPECTATION_FAILED));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating payment record detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request) {
        try {
            String feeId = paymentRecordDetailCreateDto.getFeeId();
            Fee fee = null;
            MachineMultiplePayment machineMultiplePayment = new MachineMultiplePayment();

            if (paymentRecordDetailCreateDto.isFirstPayment() && (paymentRecordDetailCreateDto.isAgentPayment() || paymentRecordDetailCreateDto.isInstitutionPayment())) {
                fee = feeService.findFeeById(feeId);
                if (fee == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
                }
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

            Agent agent;
            if (paymentRecordDetailCreateDto.isAgentPayment()) {
                String agentId = paymentRecordDetailCreateDto.getAgentId();
                agent = agentService.findAgentById(agentId);
                if (agent == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Agent with id %s does not exist", agentId), HttpStatus.BAD_REQUEST));
                }
            }
            Institution institution;
            if (paymentRecordDetailCreateDto.isGamingMachinePayment() && paymentRecordDetailCreateDto.isFirstPayment()) {
                Pair<Mono<ResponseEntity>, MachineMultiplePayment> machineMultiplePaymentPair = validateGamingMachinePayment(paymentRecordDetailCreateDto);
                if (machineMultiplePaymentPair.getLeft() != null) {
                    return machineMultiplePaymentPair.getLeft();
                }
                machineMultiplePayment = machineMultiplePaymentPair.getRight();
            }

            if (paymentRecordDetailCreateDto.isGamingTerminalPayment() && paymentRecordDetailCreateDto.isFirstPayment()) {
                Pair<Mono<ResponseEntity>, MachineMultiplePayment> machineMultiplePaymentPair = validateGamingTerminalPayment(paymentRecordDetailCreateDto);
                if (machineMultiplePaymentPair.getLeft() != null) {
                    return machineMultiplePaymentPair.getLeft();
                }
                machineMultiplePayment = machineMultiplePaymentPair.getRight();
            }


            if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
                String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
                institution = institutionService.findByInstitutionId(institutionId);
                if (institution == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
                }
            }
            if (paymentRecordDetailCreateDto.isFirstPayment()) {
                if (paymentRecordDetailCreateDto.isGamingMachinePayment() || paymentRecordDetailCreateDto.isGamingTerminalPayment()) {
                    paymentRecord = newPaymentForMachine(paymentRecordDetailCreateDto, machineMultiplePayment);
                }
                if (paymentRecordDetailCreateDto.isAgentPayment() || paymentRecordDetailCreateDto.isInstitutionPayment()) {
                    paymentRecord = newPaymentFromFee(fee, paymentRecordDetailCreateDto);
                }
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

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = "";
            if (paymentRecordDetailCreateDto.isInstitutionPayment() || paymentRecordDetailCreateDto.isAgentPayment()) {
                verbiage = String.format("Created web payment record detail -> License Type -> %s, Amount -> %s, Fee Payment Type -> %s,Category -> %s, Id -> %s",
                        paymentRecord.getLicenseType(), paymentRecord.getAmount(), paymentRecord.getFeePaymentType(), paymentRecord.getGameType(), paymentRecordDetail.getId());
            }
            if (paymentRecordDetailCreateDto.isGamingTerminalPayment() || paymentRecordDetailCreateDto.isGamingMachinePayment()) {
                verbiage = String.format("Created Multiple Machine payment  -> Machine Serial Numbers -> %s, Amount -> %s", multipleMachinePaymentToAuditString(machineMultiplePayment), machineMultiplePayment.getTotalAmount());
            }
            paymentEmailNotifierAsync.handlePostPaymentInitiationEvents(paymentRecord, paymentRecordDetailCreateDto);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(paymentAuditActionId,
                    currentAuditorName, currentAuditorName,
                    true, getClientIpAddr(request), verbiage));

            return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating payment record detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findAllPaymentRecordDetailForPaymentRecord(String paymentRecordId) {
        try {
            PaymentRecord paymentRecord = paymentRecordService.findById(paymentRecordId);
            if (paymentRecord == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s not found", paymentRecordId), HttpStatus.BAD_REQUEST));
            }
            PaymentRecordDto paymentRecordDto = paymentRecord.convertToFullDto();
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
            return Mono.just(new ResponseEntity<>(new PaymentDetailResponse(paymentRecordDetailDtos, paymentRecordDto),
                    HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting payment record details", e);
        }
    }

    @Override
    public Mono<ResponseEntity> handleVigipayInBranchNotification(VigiPayMessage vigiPayMessage) {
        String invoiceNumber = vigiPayMessage.getInvoiceNumber();
        ObjectMapper objectMapper = new ObjectMapper();
        VigipayPaymentNotification paymentNotification = new VigipayPaymentNotification();
        paymentNotification.setId(UUID.randomUUID().toString());
        try {
            paymentNotification.setRequest(objectMapper.writeValueAsString(vigiPayMessage));
            if (StringUtils.isEmpty(invoiceNumber)) {
                String response = "Invalid Invoice Number";
                paymentNotification.setResponse(response);
                mongoRepositoryReactive.saveOrUpdate(paymentNotification);

                return BadRequestResponse(response);
            }
            if (vigiPayMessage != null) {
                Query query = new Query();
                query.addCriteria(Criteria.where("invoiceNumber").is(vigiPayMessage.getInvoiceNumber()));
                PaymentRecordDetail existingPaymentRecordDetail = (PaymentRecordDetail) mongoRepositoryReactive.find(query, PaymentRecordDetail.class).block();
                if (existingPaymentRecordDetail == null) {
                    paymentNotification.setResponse("Invoice does not exist");
                    mongoRepositoryReactive.saveOrUpdate(paymentNotification);
                    return Mono.just(new ResponseEntity<>("Invoice does not exist", HttpStatus.BAD_REQUEST));
                }
                if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingPaymentRecordDetail.getPaymentStatusId())) {

                    //Confirms payment for only production environment
                    boolean isConfirmedPayment = true;
                    if (environmentUtils.isProductionEnvironment()) {
                        try {
                            String vigipayPaymentStatus = vigipayService.getVigipayPaymentStatusForInvoice(existingPaymentRecordDetail.getInvoiceNumber());
                            isConfirmedPayment = StringUtils.equals("1", vigipayPaymentStatus);//vigipay payment status is "1" for paid
                            if (!isConfirmedPayment) {
                                existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
                                savePaymentRecordDetail(existingPaymentRecordDetail);
                                String message = "Payment Status not confirmed as paid , Got  " + vigipayPaymentStatus + " as payment status";
                                paymentNotification.setResponse(message);
                                mongoRepositoryReactive.saveOrUpdate(paymentNotification);
                                return Mono.just(new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR));
                            }
                        } catch (VigiPayServiceException e) {
                            logger.error("An error occurred while confirming payment status from vigipay", e);
                            existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
                            savePaymentRecordDetail(existingPaymentRecordDetail);
                            String message = "An error occurred while confirming payment from vigipay => " + e.getMessage();
                            paymentNotification.setResponse(message);
                            mongoRepositoryReactive.saveOrUpdate(paymentNotification);
                            return Mono.just(new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR));
                        }
                    }
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
                        paymentNotification.setResponse("updated successfully");
                        mongoRepositoryReactive.saveOrUpdate(paymentNotification);
                        return Mono.just(new ResponseEntity<>("updated successfully", HttpStatus.OK));
                    }
                    existingPaymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
                    savePaymentRecordDetail(existingPaymentRecordDetail);
                    paymentNotification.setResponse("Payment status was not confirmed ");
                    mongoRepositoryReactive.saveOrUpdate(paymentNotification);
                    return Mono.just(new ResponseEntity<>("Payment status was not confirmed ", HttpStatus.FAILED_DEPENDENCY));
                }
                paymentNotification.setResponse("Payment completed already");
                mongoRepositoryReactive.saveOrUpdate(paymentNotification);
                return Mono.just(new ResponseEntity<>("Payment completed already", HttpStatus.BAD_REQUEST));
            }
            paymentNotification.setResponse("Empty Message object");
            mongoRepositoryReactive.saveOrUpdate(paymentNotification);
            return Mono.just(new ResponseEntity<>("Empty Message object", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while processing the vigi pay notification", e);
        }
    }

    private String createInBranchRecordDetailForInstitution(Institution institution, String feeDescription, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, List<AuthInfo> admins) throws VigiPayServiceException {
        VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
        vigipayInvoiceItem.setAmount(paymentRecordDetailCreateDto.getAmount());
        vigipayInvoiceItem.setDetail(feeDescription);
        vigipayInvoiceItem.setQuantity(1);
        vigipayInvoiceItem.setProductCode("");
        List<VigipayInvoiceItem> vigipayInvoiceItems = new ArrayList<>();
        vigipayInvoiceItems.add(vigipayInvoiceItem);
        return vigipayService.createInBranchInvoiceForInstitution(institution, admins, vigipayInvoiceItems);
    }

    private String createInBranchRecordDetailForAgent(Agent agent, String feeDescription, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) throws VigiPayServiceException {
        VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
        vigipayInvoiceItem.setAmount(paymentRecordDetailCreateDto.getAmount());
        vigipayInvoiceItem.setDetail(feeDescription);
        vigipayInvoiceItem.setQuantity(1);
        vigipayInvoiceItem.setProductCode("");
        List<VigipayInvoiceItem> vigipayInvoiceItems = new ArrayList<>();
        vigipayInvoiceItems.add(vigipayInvoiceItem);
        return vigipayService.createInBranchInvoiceForAgent(agent, vigipayInvoiceItems);
    }

    private String createInBranchRecordDetailForGamingMachine(Institution institution, List<MachinePaymentDetail> machinePaymentDetails, List<AuthInfo> admins) throws VigiPayServiceException {
        List<VigipayInvoiceItem> vigipayInvoiceItems = new ArrayList<>();
        for (MachinePaymentDetail machinePaymentDetail : machinePaymentDetails) {
            VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
            vigipayInvoiceItem.setProductCode("");
            vigipayInvoiceItem.setQuantity(1);
            vigipayInvoiceItem.setAmount(machinePaymentDetail.getAmount());
            String feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(String.format("%s for Gaming Machine %s , in %s",
                    machinePaymentDetail.getFeePaymentTypeName(),
                    machinePaymentDetail.getMachineSerialNumber(), machinePaymentDetail.getGameTypeName()));
            vigipayInvoiceItem.setDetail(feeDescription);
            vigipayInvoiceItems.add(vigipayInvoiceItem);

        }
        return vigipayService.createInBranchInvoiceForInstitution(institution, admins, vigipayInvoiceItems);
    }

    private String createInBranchRecordDetailForGamingTerminal(Agent agent, List<MachinePaymentDetail> machinePaymentDetails) throws VigiPayServiceException {
        List<VigipayInvoiceItem> vigipayInvoiceItems = new ArrayList<>();
        for (MachinePaymentDetail machinePaymentDetail : machinePaymentDetails) {
            VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
            vigipayInvoiceItem.setProductCode("");
            vigipayInvoiceItem.setQuantity(1);
            vigipayInvoiceItem.setAmount(machinePaymentDetail.getAmount());
            String feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(String.format("%s for Gaming Terminal %s , in category %s",
                    machinePaymentDetail.getFeePaymentTypeName(),
                    machinePaymentDetail.getMachineSerialNumber(), machinePaymentDetail.getGameTypeName()));
            vigipayInvoiceItem.setDetail(feeDescription);
            vigipayInvoiceItems.add(vigipayInvoiceItem);

        }
        return vigipayService.createInBranchInvoiceForAgent(agent, vigipayInvoiceItems);
    }


    private void updatePaymentRecord(PaymentRecord paymentRecord) {
        if (paymentRecord.getAmountOutstanding() <= 0) {
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
            paymentRecord.setCompletionDate(LocalDate.now());

            //for existing operators that paid offline
            if (paymentRecord.isForIncompleteOfflineLicenceRenewal()) {
                licenseService.createRenewedLicenseForMigratedOperatorPayment(paymentRecord);
                paymentRecordService.savePaymentRecord(paymentRecord);
                return;
            }

            if (paymentRecord.isInstitutionPayment() && (paymentRecord.isLicensePayment() || paymentRecord.isLicenseTransferPayment())) {
                licenseService.createAIPLicenseForCompletedPayment(paymentRecord);
            }

            if (paymentRecord.isGamingMachinePayment() && paymentRecord.isTaxPayment()) {
                licenseService.createLicenseForGamingMachinePayment(paymentRecord);
            }

            if (paymentRecord.isGamingTerminalPayment() && paymentRecord.isTaxPayment()) {
                licenseService.createLicenseForGamingTerminalPayment(paymentRecord);
            }

            if (paymentRecord.isAgentPayment() && paymentRecord.isLicensePayment()) {
                licenseService.createFirstLicenseForAgentPayment(paymentRecord);
            }

            if (paymentRecord.isLicenseRenewalPayment()) {
                licenseService.createRenewedLicenseForPayment(paymentRecord);
            }

            if (paymentRecord.isInstitutionPayment() && paymentRecord.isApplicationPayment()) {
                institutionOnboardingWorkflowService.updateWorkflowForApplicationFeePayment(paymentRecord);
            }

        } else {
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID);
        }
        paymentRecordService.savePaymentRecord(paymentRecord);
    }

    private Mono<ResponseEntity> validateFirstPayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) throws Exception {
        if (fee.isLicenseRenewalFee()) {
            Mono<ResponseEntity> validateRenewalLicenseResponse = validateLicenseRenewalPayment(paymentRecordDetailCreateDto, fee);
            if (validateRenewalLicenseResponse != null) {
                return validateRenewalLicenseResponse;
            }
        }

        if (fee.isApplicationFee()) {
            Mono<ResponseEntity> validateApplicationPaymentResponse = validateApplicationPayment(paymentRecordDetailCreateDto.getInstitutionId(), fee.getGameTypeId());
            if (validateApplicationPaymentResponse != null) {
                return validateApplicationPaymentResponse;
            }
        }

        if (fee.isLicenseFee()) {
            Mono<ResponseEntity> validateLicensePaymentResponse = validateLicensePayment(paymentRecordDetailCreateDto, fee);
            if (validateLicensePaymentResponse != null) {
                return validateLicensePaymentResponse;
            }
        }

        if (fee.isLicenseTransferFee()) {
            Mono<ResponseEntity> validateLicenseTransferResponse = validateLicenceTransferPayment(paymentRecordDetailCreateDto, fee);
            if (validateLicenseTransferResponse != null) {
                return validateLicenseTransferResponse;
            }
        }
        return null;
    }

    private Mono<ResponseEntity> validateLicenceTransferPayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) {
        String licenseTransferId = paymentRecordDetailCreateDto.getLicenseTransferId();
        if (StringUtils.isEmpty(licenseTransferId)) {
            return Mono.just(new ResponseEntity<>("Licence Transfer id should not be empty", HttpStatus.BAD_REQUEST));
        }
        LicenseTransfer licenseTransfer = licenseTransferService.findLicenseTransferById(licenseTransferId);
        if (licenseTransfer == null) {
            return Mono.just(new ResponseEntity<>(String.format("Licence Transfer with id %s not found", licenseTransferId), HttpStatus.BAD_REQUEST));
        }
        if (!StringUtils.equals(licenseTransfer.getGameTypeId(), fee.getGameTypeId())) {
            return Mono.just(new ResponseEntity<>("Kindly Select the correct category for Licence Transfer", HttpStatus.BAD_REQUEST));
        }
        if (!licenseTransfer.isFinallyApproved()) {
            return Mono.just(new ResponseEntity<>("The Licence Transfer is not yet approved", HttpStatus.BAD_REQUEST));
        }
        return null;
    }


    private Mono<ResponseEntity> validateLicensePayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) throws Exception {
        if (!StringUtils.isEmpty(paymentRecordDetailCreateDto.getInstitutionId())) {
            Institution institution = institutionService.findByInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
            if (institution != null && institution.isFromLiveData()) {
                Query query = new Query();
                query.addCriteria(Criteria.where("gameTypeId").is(fee.getGameTypeId()));
                query.addCriteria(Criteria.where("institutionId").is(paymentRecordDetailCreateDto.getInstitutionId()));
                query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
                ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                if (licenses.size() == 1 && !licenses.get(0).isAIPRelatedLicense()) {
                    return BadRequestResponse("Kindly make payment for licence renewal");
                }
            }
        }
        String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
        String agentId = paymentRecordDetailCreateDto.getAgentId();
        String gameTypeId = fee.getGameTypeId();
        String revenueNameId = fee.getLicenseTypeId();
        if (paymentRecordDetailCreateDto.isAgentPayment()) {
            gameTypeId = fee.getGameTypeId();
        }
        PaymentRecordDto existingLicensePaymentRecordDto = getLicensePaymentRecord(revenueNameId, gameTypeId, agentId, institutionId);
        if (existingLicensePaymentRecordDto != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("Kindly complete payment of your existing licence payment", HttpStatus.BAD_REQUEST));
        }

        if (existingLicensePaymentRecordDto != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("You  have an existing license payment for this category and can only proceed to make payment for renewal", HttpStatus.BAD_REQUEST));
        }

        if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
            boolean institutionHasApplicationPayment = institutionHasApplicationPaymentInCategory(institutionId, gameTypeId);
            if (!institutionHasApplicationPayment) {
                return Mono.just(new ResponseEntity<>("Kindly make payment for application fees for category before proceeding", HttpStatus.BAD_REQUEST));
            }

            boolean institutionHasApprovedForm = applicationFormService.institutionHasCompletedApplicationForGameType(institutionId, gameTypeId);
            if (!institutionHasApprovedForm) {
                String errorMsg = "Kindly make sure you have an approved application in the selected category before proceeding to pay for licence";
                return Mono.just(new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST));
            }
        }
        return null;
    }

    private Mono<ResponseEntity> validateLicenseRenewalPayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, Fee fee) {
        String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
        String agentId = paymentRecordDetailCreateDto.getAgentId();
        String gameTypeId = fee.getGameTypeId();
        int page = 0;
        int pageSize = 10000;
        String sortDirection = "DESC";
        String sortProperty = "createdAt";
        Mono<ResponseEntity> findLicenseResponse = licenseService.findAllLicense(page, pageSize, sortDirection, sortProperty, institutionId, agentId, null, null, gameTypeId, null, null, null, null, null, null, null, null);
        if (findLicenseResponse.block() != null && findLicenseResponse.block().getStatusCode() != HttpStatus.OK) {
            String categoryName = fee.getGameTypeName();
            return Mono.just(new ResponseEntity<>(String.format("You do not have an existing license for %s, please get licensed for category %s before attempting to pay for licence renewal for the category", categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }

        License latestExistingLicense = licenseService.getPreviousConfirmedLicense(paymentRecordDetailCreateDto.getInstitutionId(), paymentRecordDetailCreateDto.getAgentId(), fee.getGameTypeId(), fee.getLicenseTypeId());
        if (latestExistingLicense == null) {
            String categoryName = fee.getGameTypeName();
            return Mono.just(new ResponseEntity<>(String.format("You do not have a valid %s licence, kindly contact LSLB before attempting to pay for licence renewal for %s", categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }
        if (latestExistingLicense.isRevokedLicence()) {
            String categoryName = fee.getGameTypeName();
            return BadRequestResponse(String.format("Your %s licence is revoked", categoryName));
        }
        if (latestExistingLicense.isTerminatedLicence()) {
            String categoryName = fee.getGameTypeName();
            return BadRequestResponse(String.format("Your %s licence is terminated", categoryName));
        }
        if (latestExistingLicense.isSuspendedLicence()) {
            String categoryName = fee.getGameTypeName();
            return BadRequestResponse(String.format("Your %s licence is suspended", categoryName));
        }
        return null;
    }


    private Mono<ResponseEntity> validateApplicationPayment(String institutionId, String gameTypeId) {
        if (!StringUtils.isEmpty(institutionId)) {
            Institution institution = institutionService.findByInstitutionId(institutionId);
            if (institution != null && institution.isFromLiveData()) {
                Query query = new Query();
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
                query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
                ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                if ((licenses.size() >= 1)) {
                    if ((licenses.size() == 1) && (licenses.get(0).isAIPRelatedLicense())) {
                        return BadRequestResponse("Kindly make payment for licence");
                    }
                    return BadRequestResponse("Kindly make payment for licence renewal");
                }
            }
        }

        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        String institutionLicenseTypeId = LicenseTypeReferenceData.INSTITUTION_ID;

        Query query = new Query();
        query.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(institutionLicenseTypeId));

        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        if (paymentRecord != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("You have an existing application fee payment  for the category specified", HttpStatus.BAD_REQUEST));
        }
        if (paymentRecord != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("Please complete your existing application fee payment for category", HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    private boolean institutionHasApplicationPaymentInCategory(String institutionId, String gameTypeId) {
        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        String institutionLicenseTypeId = LicenseTypeReferenceData.INSTITUTION_ID;
        Query query = new Query();
        query.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(institutionLicenseTypeId));
        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        return paymentRecord != null;
    }

    private PaymentRecordDto getLicensePaymentRecord(String revenueNameId,
                                                     String gameTypeId,
                                                     String agentId,
                                                     String institutionId) throws Exception {
        try {

            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
            String sortDirection = "DESC";
            String sortProperty = "createdAt";
            int page = 0;
            int pageSize = 1000;

            Mono<ResponseEntity> paymentRecordsResponse = paymentRecordService.findAllPaymentRecords(page,
                    pageSize, sortDirection, sortProperty, institutionId, agentId, null, gameTypeId, feePaymentTypeId, revenueNameId, null, null, null, null, null, null);

            if (paymentRecordsResponse.block().getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            ArrayList<PaymentRecordDto> paymentRecordDtos = (ArrayList<PaymentRecordDto>) paymentRecordsResponse.block().getBody();
            if (paymentRecordDtos == null) {
                logger.error("An error occurred while getting past license payment records");
                throw new Exception("An error occurred while getting license payments");
            }
            if (paymentRecordDtos.isEmpty()) {
                return null;
            }
            return paymentRecordDtos.get(0);
        } catch (Exception e) {
            logger.error("An error occurred while getting past license payment records");
            throw new Exception("An error occurred while getting license payments");
        }
    }

    private PaymentRecord newPaymentFromFee(Fee fee, PaymentRecordDetailCreateDto createDto) {
        PaymentRecord paymentRecord = new PaymentRecord();
        if (fee != null) {
            paymentRecord.setId(UUID.randomUUID().toString());
            paymentRecord.setAmountOutstanding(fee.getAmount());
            paymentRecord.setAmount(fee.getAmount());
            paymentRecord.setAmountPaid(0);
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
            paymentRecord.setFeeId(fee.getId());
            paymentRecord.setGamingTerminalIds(createDto.getGamingTerminalIds());
            paymentRecord.setGamingMachineIds(createDto.getGamingMachineIds());
            paymentRecord.setGameTypeId(fee.getGameTypeId());
            paymentRecord.setLicenseTypeId(fee.getLicenseTypeId());
            paymentRecord.setFeePaymentTypeId(fee.getFeePaymentTypeId());
            paymentRecord.setLicenseTransferId(createDto.getLicenseTransferId());
            paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
            paymentRecord.setAgentId(createDto.getAgentId());
            paymentRecord.setCreationDate(LocalDate.now());
            paymentRecord.setInstitutionId(createDto.getInstitutionId());
        }
        return paymentRecord;
    }

    private PaymentRecord newPaymentForMachine(PaymentRecordDetailCreateDto createDto, MachineMultiplePayment machineMultiplePayment) {
        double totalAmount = machineMultiplePayment.getTotalAmount();
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(UUID.randomUUID().toString());
        paymentRecord.setAmountOutstanding(totalAmount);
        paymentRecord.setAmount(totalAmount);
        paymentRecord.setAmountPaid(0);
        paymentRecord.setGameTypeId(machineMultiplePayment.getGameTypeId());
        paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
        paymentRecord.setGamingTerminalIds(createDto.getGamingTerminalIds());
        paymentRecord.setGamingMachineIds(createDto.getGamingMachineIds());
        paymentRecord.setFeePaymentTypeId(FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID);
        paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
        paymentRecord.setAgentId(createDto.getAgentId());
        paymentRecord.setMachineMultiplePayment(machineMultiplePayment);
        paymentRecord.setInstitutionId(createDto.getInstitutionId());
        paymentRecord.setCreationDate(LocalDate.now());
        if (createDto.isGamingMachinePayment()) {
            paymentRecord.setLicenseTypeId(LicenseTypeReferenceData.GAMING_MACHINE_ID);
        }
        if (createDto.isGamingTerminalPayment()) {
            paymentRecord.setLicenseTypeId(LicenseTypeReferenceData.GAMING_TERMINAL_ID);
        }
        return paymentRecord;
    }


    private Pair<Mono<ResponseEntity>, MachineMultiplePayment> validateGamingMachinePayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        Set<String> machineIds = paymentRecordDetailCreateDto.getGamingMachineIds();
        List<MachinePaymentDetail> machinePaymentDetails = new ArrayList<>();
        Mono<ResponseEntity> responseEntityMono;
        String gameTypeId = null;
        double totalAmount = 0;
        for (String gamingMachineId : machineIds) {
            Machine gamingMachine = gamingMachineService.findMachineById(gamingMachineId);
            if (gamingMachine == null) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("Gaming machine with Id %s not found", gamingMachineId), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }
            if (!gamingMachine.isGamingMachine()) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("Machine with id %s is not a gaming machine", gamingMachineId), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }
            String newGameTypeId = gamingMachine.getGameTypeId();
            if (gameTypeId == null) {
                gameTypeId = newGameTypeId;
            } else {
                if (!StringUtils.equals(gameTypeId, newGameTypeId)) {
                    responseEntityMono = Mono.just(new ResponseEntity<>("All Machines should belong to same category", HttpStatus.BAD_REQUEST));
                    return new ImmutablePair<>(responseEntityMono, null);
                }
            }
            if (!gamingMachine.isActive()) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("Machine with serial number %s is no active", gamingMachine.getSerialNumber()), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }

            Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(LicenseTypeReferenceData.GAMING_MACHINE_ID,
                    gamingMachine.getGameTypeId(), FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID);
            if (fee == null) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("There is no tax fee configured for Gaming Machines for category %s",
                        gamingMachine.getGameType()), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }
            MachinePaymentDetail paymentDetail = new MachinePaymentDetail();
            double amount = fee.getAmount();
            paymentDetail.setAmount(amount);
            paymentDetail.setFeePaymentTypeName(String.valueOf(fee.getFeePaymentType()));
            paymentDetail.setGameTypeName(String.valueOf(fee.getGameType()));
            paymentDetail.setMachineSerialNumber(gamingMachine.getSerialNumber());
            paymentDetail.setMachineType(String.valueOf(gamingMachine.getMachineType()));
            totalAmount = totalAmount + amount;
            machinePaymentDetails.add(paymentDetail);
        }
        MachineMultiplePayment machineMultiplePayment = new MachineMultiplePayment();
        machineMultiplePayment.setTotalAmount(totalAmount);
        machineMultiplePayment.setGameTypeId(gameTypeId);
        machineMultiplePayment.setMachinePaymentDetailList(machinePaymentDetails);
        return new ImmutablePair<>(null, machineMultiplePayment);
    }

    private Pair<Mono<ResponseEntity>, MachineMultiplePayment> validateGamingTerminalPayment(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        Set<String> machineIds = paymentRecordDetailCreateDto.getGamingTerminalIds();
        List<MachinePaymentDetail> machinePaymentDetails = new ArrayList<>();
        double totalAmount = 0;
        Mono<ResponseEntity> responseEntityMono;
        String gameTypeId = null;
        for (String gamingMachineId : machineIds) {
            Machine gamingMachine = gamingMachineService.findMachineById(gamingMachineId);
            if (gamingMachine == null) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("Gaming terminal with Id %s not found", gamingMachineId), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }
            String newGameTypeId = gamingMachine.getGameTypeId();
            if (gameTypeId == null) {
                gameTypeId = newGameTypeId;
            } else {
                if (!StringUtils.equals(gameTypeId, newGameTypeId)) {
                    responseEntityMono = Mono.just(new ResponseEntity<>("All Terminals should belong to same category", HttpStatus.BAD_REQUEST));
                    return new ImmutablePair<>(responseEntityMono, null);
                }
            }
            if (!gamingMachine.isGamingTerminal()) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("Machine with id %s is not a gaming terminal", gamingMachineId), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }
            if (!gamingMachine.isActive()) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("Machine with serial number %s is no active", gamingMachine.getSerialNumber()), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }

            Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(LicenseTypeReferenceData.GAMING_TERMINAL_ID,
                    gamingMachine.getGameTypeId(), FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID);
            if (fee == null) {
                responseEntityMono = Mono.just(new ResponseEntity<>(String.format("There is no tax fee configured for Gaming Terminals for category %s",
                        gamingMachine.getGameType()), HttpStatus.BAD_REQUEST));
                return new ImmutablePair<>(responseEntityMono, null);
            }
            MachinePaymentDetail paymentDetail = new MachinePaymentDetail();
            double amount = fee.getAmount();
            paymentDetail.setAmount(amount);
            paymentDetail.setFeePaymentTypeName(String.valueOf(fee.getFeePaymentType()));
            paymentDetail.setGameTypeName(String.valueOf(fee.getGameType()));
            paymentDetail.setMachineSerialNumber(gamingMachine.getSerialNumber());
            paymentDetail.setMachineType(String.valueOf(gamingMachine.getMachineType()));
            totalAmount = totalAmount + amount;
            machinePaymentDetails.add(paymentDetail);
        }
        MachineMultiplePayment machineMultiplePayment = new MachineMultiplePayment();
        machineMultiplePayment.setTotalAmount(totalAmount);
        machineMultiplePayment.setGameTypeId(gameTypeId);
        machineMultiplePayment.setMachinePaymentDetailList(machinePaymentDetails);
        return new ImmutablePair<>(null, machineMultiplePayment);
    }

    private String multipleMachinePaymentToAuditString(MachineMultiplePayment machineMultiplePayment) {
        StringBuilder builder = new StringBuilder();
        for (MachinePaymentDetail machinePaymentDetail : machineMultiplePayment.getMachinePaymentDetailList()) {
            builder.append(String.format("Serial Number : %s", machinePaymentDetail.getMachineSerialNumber()));
            builder.append("\n");
        }
        return builder.toString();
    }

    private String buildFeeDescription(String feeName, String gameTypeName, String revenueName) {
        String feeDescription = String.format("%s category %s for %ss", gameTypeName, feeName, revenueName);
        feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(feeDescription);
        return feeDescription;
    }

    @Override
    public Mono<ResponseEntity> getPaymentInvoiceDetails(String id) {
        try {
            PaymentRecordDetail detail = findById(id);
            if (detail == null) {
                return BadRequestResponse("Invalid Id");
            }
            return OKResponseUtil.OKResponse(detail.convertToPaymentInvoice());
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getiinginvoice details", e);
        }
    }
}