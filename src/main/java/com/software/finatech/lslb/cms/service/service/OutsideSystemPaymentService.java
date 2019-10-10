package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.FullPaymentConfirmationRequest;
import com.software.finatech.lslb.cms.service.dto.PartialPaymentConfirmationRequest;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.PaymentEmailNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData.PAYMENT_ID;
import static com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData.INSTITUTION_ID;
import static com.software.finatech.lslb.cms.service.referencedata.ModeOfPaymentReferenceData.OFFLINE_CONFIRMATION_ID;
import static com.software.finatech.lslb.cms.service.referencedata.PaymentConfirmationApprovalRequestTypeReferenceData.*;
import static com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData.UNPAID_STATUS_ID;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.BadRequestResponse;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.ErrorResponse;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;
import static com.software.finatech.lslb.cms.service.util.NumberUtil.generateInvoiceNumber;
import static com.software.finatech.lslb.cms.service.util.OKResponseUtil.OKResponse;
import static com.software.finatech.lslb.cms.service.util.RequestAddressUtil.getClientIpAddr;

@Service
public class OutsideSystemPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(OutsideSystemPaymentService.class);
    private static final String paymentAuditActionId = AuditActionReferenceData.PAYMENT_ID;

    private PaymentRecordDetailService paymentRecordDetailService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private LicenseService licenseService;
    private GameTypeService gameTypeService;
    private InstitutionService institutionService;
    private AgentService agentService;
    private PaymentRecordService paymentRecordService;
    private ApplicationFormService applicationFormService;
    private LicenseTransferService licenseTransferService;
    private PaymentEmailNotifierAsync paymentEmailNotifierAsync;


    @Autowired
    public OutsideSystemPaymentService(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                       SpringSecurityAuditorAware springSecurityAuditorAware,
                                       AuditLogHelper auditLogHelper,
                                       LicenseService licenseService,
                                       GameTypeService gameTypeService,
                                       InstitutionService institutionService,
                                       AgentService agentService,
                                       PaymentRecordService paymentRecordService,
                                       ApplicationFormService applicationFormService,
                                       LicenseTransferService licenseTransferService,
                                       PaymentRecordDetailService paymentRecordDetailService,
                                       PaymentEmailNotifierAsync paymentEmailNotifierAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.licenseService = licenseService;
        this.gameTypeService = gameTypeService;
        this.institutionService = institutionService;
        this.agentService = agentService;
        this.paymentRecordService = paymentRecordService;
        this.applicationFormService = applicationFormService;
        this.licenseTransferService = licenseTransferService;
        this.paymentRecordDetailService = paymentRecordDetailService;
        this.paymentEmailNotifierAsync = paymentEmailNotifierAsync;
    }

    public Mono<ResponseEntity> updateOfflinePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto, HttpServletRequest httpServletRequest) {
        try {
            String paymentInvoice = paymentRecordDetailUpdateDto.getInvoiceNumber();
            PaymentRecordDetail existingInvoicedPayment =  (PaymentRecordDetail) mongoRepositoryReactive.find(Query.query(Criteria.where("invoiceNumber").is(paymentRecordDetailUpdateDto.getInvoiceNumber())), PaymentRecordDetail.class).block();
            if (existingInvoicedPayment == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment with invoice %s does not exist", paymentInvoice), HttpStatus.BAD_REQUEST));
            }

            PaymentRecord paymentRecord = paymentRecordService.findById(existingInvoicedPayment.getPaymentRecordId());
            if (paymentRecordDetailUpdateDto.isSuccessFulPayment()
                    && !existingInvoicedPayment.isSuccessfulPayment()) {
                double amountPaid = paymentRecord.getAmountPaid();
                amountPaid = amountPaid + existingInvoicedPayment.getAmount();
                paymentRecord.setAmountPaid(amountPaid);

                double amountOutstanding = paymentRecord.getAmountOutstanding();
                amountOutstanding = amountOutstanding - existingInvoicedPayment.getAmount();
                paymentRecord.setAmountOutstanding(amountOutstanding);

                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            }
            existingInvoicedPayment.setInvoiceNumber(paymentRecordDetailUpdateDto.getInvoiceNumber());
            existingInvoicedPayment.setPaymentDate(LocalDateTime.now());
            existingInvoicedPayment.setPaymentStatusId(paymentRecordDetailUpdateDto.getPaymentStatusId());
            existingInvoicedPayment.setVigiPayTransactionReference(paymentRecordDetailUpdateDto.getVigipayReference());
            mongoRepositoryReactive.saveOrUpdate(existingInvoicedPayment);

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Payment record detail callback -> Payment Record Detail id: %s, Invoice Number -> %s, Status Id -> %s", paymentInvoice, existingInvoicedPayment.getInvoiceNumber(), paymentRecordDetailUpdateDto.getPaymentStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(paymentAuditActionId,
                    currentAuditorName, currentAuditorName,
                    true, getClientIpAddr(httpServletRequest), verbiage));
            //paymentEmailNotifierAsync.sendPaymentNotificationForPaymentRecordDetail(existingInvoicedPayment, paymentRecord);
            paymentEmailNotifierAsync.sendPaymentNotificationToLSLBUsers(existingInvoicedPayment, paymentRecord);
            return Mono.just(new ResponseEntity<>(existingInvoicedPayment.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating payment record detail", e);
        }
    }

    public Mono<ResponseEntity> createFullPaymentConfirmationRequest(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            logger.info("Step 1 Checks who LoggedInUser is: " + loggedInUser);
            if (loggedInUser == null) {
                return ErrorResponse("Could not find logged in user");
            }
            Mono<ResponseEntity> validateOutsidePaymentResponse = validateOutsidePayment(fullPaymentConfirmationRequest);
            if (validateOutsidePaymentResponse != null) {
                return validateOutsidePaymentResponse;
            }
            logger.info("Step to be an Outside Payment ");
            String invoiceNumber = NumberUtil.generateInvoiceNumber();

            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setAmountOutstanding(fullPaymentConfirmationRequest.getAmountPaid());
            paymentRecord.setAmount(fullPaymentConfirmationRequest.getAmountPaid());
            paymentRecord.setPaymentStatusId(UNPAID_STATUS_ID);
            paymentRecord.setGameTypeId(fullPaymentConfirmationRequest.getGameTypeId());
            paymentRecord.setLicenseTypeId(fullPaymentConfirmationRequest.getLicenseTypeId());
            paymentRecord.setAgentId(fullPaymentConfirmationRequest.getAgentId());
            paymentRecord.setInstitutionId(fullPaymentConfirmationRequest.getInstitutionId());
            paymentRecord.setFeePaymentTypeId(fullPaymentConfirmationRequest.getFeePaymentTypeId());
            paymentRecord.setAmountPaid(0);
            paymentRecord.setForOutsideSystemPayment(true);
            paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
            paymentRecord.setGamingMachineIds(fullPaymentConfirmationRequest.getGamingMachineIds());
            paymentRecord.setGamingTerminalIds(fullPaymentConfirmationRequest.getGamingTerminalIds());
            paymentRecord.setLicenseTransferId(fullPaymentConfirmationRequest.getLicenseTransferId());
            paymentRecord.setCreationDate(LocalDate.now());
            paymentRecord.setInvoiceNumber(invoiceNumber);
            paymentRecord.setCreationDate(LocalDate.now());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            PaymentRecordDetail detail = new PaymentRecordDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setPaymentStatusId(UNPAID_STATUS_ID);
            detail.setModeOfPaymentId(OFFLINE_CONFIRMATION_ID);
            detail.setPaymentRecordId(paymentRecord.getId());
            detail.setAmount(fullPaymentConfirmationRequest.getAmountPaid());
            detail.setInvoiceNumber(invoiceNumber);
            mongoRepositoryReactive.saveOrUpdate(detail);

            String gameTypeName = gameTypeService.findNameById(fullPaymentConfirmationRequest.getGameTypeId());
            String feePaymentTypeName = FeePaymentTypeReferenceData.getFeePaymentTypeNameById(mongoRepositoryReactive, fullPaymentConfirmationRequest.getFeePaymentTypeId());
            String licenseTypeName = LicenseTypeReferenceData.getLicenseTypeNameById(mongoRepositoryReactive, fullPaymentConfirmationRequest.getLicenseTypeId());
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);

            PaymentConfirmationApprovalRequest approvalRequest = new PaymentConfirmationApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setPaymentRecordId(paymentRecord.getId());
            approvalRequest.setPaymentRecordDetailId(detail.getId());
            approvalRequest.setApprovalRequestTypeId(CONFIRM_FULL_PAYMENT_ID);
            approvalRequest.setInitiatorId(loggedInUser.getId());
            approvalRequest.setInvoiceNumber(invoiceNumber);
            approvalRequest.setPaymentOwnerName(ownerName);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);

            String verbiage = String.format("Created Payment Confirmation Approval Request:" +
                            " Payment Owner -> %s , " +
                            "Category -> %s, " +
                            "Payment Type -> %s, for -> %s, Reference -> %s, " +
                            "Request Type -> %s", ownerName, gameTypeName,
                    feePaymentTypeName, licenseTypeName,
                    paymentRecord.getPaymentReference(),
                    getTypeNameById(mongoRepositoryReactive, approvalRequest.getApprovalRequestTypeId()));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), ownerName,
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));
            //return OKResponse(approvalRequest.convertToDto());

                return OKResponse(paymentRecord.convertToDto());
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while making outside payment", e);
        }
    }


    /**
     * For confirming outside payments that has a
     * PAYMENT RECORD existing on the system
     *
     * @param confirmationRequest
     * @param request
     * @return
     */
    public Mono<ResponseEntity> createPartialPaymentConfirmationRequest(PartialPaymentConfirmationRequest confirmationRequest, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return ErrorResponse("Could not find logged in user");
            }
            PaymentRecord paymentRecord = paymentRecordService.findById(confirmationRequest.getPaymentRecordId());
            if (paymentRecord == null) {
                return BadRequestResponse("Invalid Payment Record");
            }
            if (paymentRecord.isCompletedPayment()) {
                return BadRequestResponse("Payment already completed");
            }
            if (paymentRecord.getAmountOutstanding() < confirmationRequest.getAmount()) {
                return BadRequestResponse("The amount entered is more than the outstanding amount on the payment");
            }
            String ownerName = paymentRecord.getOwnerName();
            PaymentRecordDetail recordDetail = new PaymentRecordDetail();
            recordDetail.setId(UUID.randomUUID().toString());
            recordDetail.setAmount(confirmationRequest.getAmount()); //Amount is on Invoice
            recordDetail.setPaymentRecordId(paymentRecord.getId());
            recordDetail.setModeOfPaymentId(OFFLINE_CONFIRMATION_ID);
            recordDetail.setInvoiceNumber(generateInvoiceNumber());
            recordDetail.setPaymentStatusId(UNPAID_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(recordDetail);
            PaymentConfirmationApprovalRequest approvalRequest = new PaymentConfirmationApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setPaymentRecordDetailId(recordDetail.getId());
            approvalRequest.setPaymentRecordId(paymentRecord.getId());
            approvalRequest.setApprovalRequestTypeId(CONFIRM_PARTIAL_PAYMENT_ID);
            approvalRequest.setInitiatorId(loggedInUser.getId());
            approvalRequest.setPaymentOwnerName(ownerName);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);

            String verbiage = String.format("Created Payment Confirmation Approval Request:" +
                            " Payment Owner -> %s , Reference -> %s, Invoice Number -> %s, Id -> %s" +
                            "", ownerName, paymentRecord.getPaymentReference(),
                    recordDetail.getInvoiceNumber(), approvalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), ownerName,
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));
            return OKResponse(approvalRequest.convertToDto());
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while creating existing payment confirmation request", e);
        }
    }

    private Mono<ResponseEntity> validateOutsidePayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) throws Exception {
        if (fullPaymentConfirmationRequest.isLicenseRenewalPayment()) {
            Mono<ResponseEntity> validateRenewalLicenseResponse = validateLicenseRenewalPayment(fullPaymentConfirmationRequest);
            if (validateRenewalLicenseResponse != null) {
                return validateRenewalLicenseResponse;
            }
        }

        if (fullPaymentConfirmationRequest.isApplicationFeePayment()) {
            Mono<ResponseEntity> validateApplicationPaymentResponse = validateApplicationPayment(fullPaymentConfirmationRequest);
            if (validateApplicationPaymentResponse != null) {
                return validateApplicationPaymentResponse;
            }
        }

        if (fullPaymentConfirmationRequest.isLicenseFeePayment()) {
            Mono<ResponseEntity> validateLicensePaymentResponse = validateLicensePayment(fullPaymentConfirmationRequest);
            if (validateLicensePaymentResponse != null) {
                return validateLicensePaymentResponse;
            }
        }

        if (fullPaymentConfirmationRequest.isLicenseTransferPayment()) {
            Mono<ResponseEntity> validateLicenseTransferResponse = validateLicenceTransferPayment(fullPaymentConfirmationRequest);
            if (validateLicenseTransferResponse != null) {
                return validateLicenseTransferResponse;
            }
        }
        return null;
    }

    private Mono<ResponseEntity> validateLicenseRenewalPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) {
        String institutionId = fullPaymentConfirmationRequest.getInstitutionId();
        String agentId = fullPaymentConfirmationRequest.getAgentId();
        String gameTypeId = fullPaymentConfirmationRequest.getGameTypeId();
        int page = 0;
        int pageSize = 10000;
        String sortDirection = "DESC";
        String sortProperty = "createdAt";
        Mono<ResponseEntity> findLicenseResponse = licenseService.findAllLicense(page, pageSize, sortDirection, sortProperty, institutionId, agentId, null, null, gameTypeId, null, null, null, null, null, null, null, null);
        if (findLicenseResponse.block() != null && findLicenseResponse.block().getStatusCode() != HttpStatus.OK) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(String.format("%s does not have an existing license for %s, please get licensed for category %s before attempting to pay for licence renewal for the category", ownerName, categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }

        License latestExistingLicense = licenseService.getPreviousConfirmedLicense(fullPaymentConfirmationRequest.getInstitutionId(), fullPaymentConfirmationRequest.getAgentId(), fullPaymentConfirmationRequest.getGameTypeId(), fullPaymentConfirmationRequest.getLicenseTypeId());
        if (latestExistingLicense == null) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(String.format("%s does not have a valid %s licence, kindly contact LSLB before attempting to pay for licence renewal for %s", ownerName, categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }
        if (latestExistingLicense.isRevokedLicence()) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return BadRequestResponse(String.format("%s's %s licence is revoked", ownerName, categoryName));
        }
        if (latestExistingLicense.isTerminatedLicence()) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return BadRequestResponse(String.format("%s's %s licence is terminated", ownerName, categoryName));
        }
        if (latestExistingLicense.isSuspendedLicence()) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return BadRequestResponse(String.format("%s's %s licence is suspended", ownerName, categoryName));
        }
        return null;
    }

    private Mono<ResponseEntity> validateApplicationPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) {
        String gameTypeId = fullPaymentConfirmationRequest.getGameTypeId();
        String institutionId = fullPaymentConfirmationRequest.getInstitutionId();
        if (!StringUtils.isEmpty(institutionId)) {
            Institution institution = institutionService.findByInstitutionId(institutionId);
            if (institution != null && institution.isFromLiveData()) {
                Query query = new Query();
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
                query.addCriteria(Criteria.where("licenseTypeId").is(INSTITUTION_ID));
                ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                if ((licenses.size() >= 1)) {
                    if ((licenses.size() == 1) && (licenses.get(0).isAIPRelatedLicense())) {
                        String ownerName = getOwnerName(fullPaymentConfirmationRequest);
                        return BadRequestResponse("Kindly make payment for licence for " + ownerName);
                    }
                    String ownerName = getOwnerName(fullPaymentConfirmationRequest);
                    return BadRequestResponse("Kindly make payment for licence renewal for " + ownerName);
                }
            }
        }

        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;

        Query query = new Query();
        query.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(INSTITUTION_ID));

        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        if (paymentRecord != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(ownerName + " has an existing application fee payment  for the category specified", HttpStatus.BAD_REQUEST));
        }
        if (paymentRecord != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>("Please complete " + ownerName + "'s  existing application fee payment for category", HttpStatus.BAD_REQUEST));
        }
        return null;
    }


    private Mono<ResponseEntity> validateLicensePayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) throws Exception {
        String institutionId = fullPaymentConfirmationRequest.getInstitutionId();
        String gameTypeId = fullPaymentConfirmationRequest.getGameTypeId();
        String licenseTypeId = fullPaymentConfirmationRequest.getLicenseTypeId();
        String feePaymentTypeId = fullPaymentConfirmationRequest.getFeePaymentTypeId();
        if (!StringUtils.isEmpty(institutionId)) {
            Institution institution = institutionService.findByInstitutionId(institutionId);
            if (institution != null && institution.isFromLiveData()) {
                Query query = new Query();
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
                query.addCriteria(Criteria.where("licenseTypeId").is(INSTITUTION_ID));
                ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                if (licenses.size() == 1 && !licenses.get(0).isAIPRelatedLicense()) {
                    return BadRequestResponse("Kindly make payment for licence renewal");
                }
            }
        }
        String agentId = fullPaymentConfirmationRequest.getAgentId();
        PaymentRecordDto existingLicensePaymentRecordDto = getLicensePaymentRecord(licenseTypeId, gameTypeId, agentId, institutionId);
        if (existingLicensePaymentRecordDto != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("Kindly complete payment of your existing licence payment", HttpStatus.BAD_REQUEST));
        }

        if (existingLicensePaymentRecordDto != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("You  have an existing license payment for this category and can only proceed to make payment for renewal", HttpStatus.BAD_REQUEST));
        }

        if (fullPaymentConfirmationRequest.isBeingPaidByOperator()) {
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


    private boolean institutionHasApplicationPaymentInCategory(String institutionId, String gameTypeId) {
        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        Query query = new Query();
        query.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(INSTITUTION_ID));
        PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        return paymentRecord != null;
    }

    private Mono<ResponseEntity> validateLicenceTransferPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) {
        String licenseTransferId = fullPaymentConfirmationRequest.getLicenseTransferId();
        String gameTypeId = fullPaymentConfirmationRequest.getGameTypeId();
        if (StringUtils.isEmpty(licenseTransferId)) {
            return Mono.just(new ResponseEntity<>("Licence Transfer id should not be empty", HttpStatus.BAD_REQUEST));
        }
        LicenseTransfer licenseTransfer = licenseTransferService.findLicenseTransferById(licenseTransferId);
        if (licenseTransfer == null) {
            return Mono.just(new ResponseEntity<>(String.format("Licence Transfer with id %s not found", licenseTransferId), HttpStatus.BAD_REQUEST));
        }
        if (!StringUtils.equals(licenseTransfer.getGameTypeId(), gameTypeId)) {
            return Mono.just(new ResponseEntity<>("Kindly Select the correct category for Licence Transfer", HttpStatus.BAD_REQUEST));
        }
        if (!licenseTransfer.isFinallyApproved()) {
            return Mono.just(new ResponseEntity<>("The Licence Transfer is not yet approved", HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    public String getOwnerName(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) {
        if (StringUtils.isNotEmpty(fullPaymentConfirmationRequest.getInstitutionId())) {
            Institution institution = institutionService.findByInstitutionId(fullPaymentConfirmationRequest.getInstitutionId());
            if (institution != null) {
                return institution.getInstitutionName();
            }
        }
        if (StringUtils.isNotEmpty(fullPaymentConfirmationRequest.getAgentId())) {
            Agent agent = agentService.findAgentById(fullPaymentConfirmationRequest.getAgentId());
            if (agent != null) {
                return agent.getFullName();
            }
        }
        return null;
    }
}
