package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.PaymentConfirmationRequest;
import com.software.finatech.lslb.cms.service.dto.PaymentUpdateConfirmationRequest;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.PaymentEmailNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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
import static com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData.PENDING_PAYMENT_STATUS_ID;
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
        this.paymentEmailNotifierAsync = paymentEmailNotifierAsync;
    }

    public Mono<ResponseEntity> createFullPaymentConfirmationRequest(PaymentConfirmationRequest paymentConfirmationRequest, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return ErrorResponse("Could not find logged in user");
            }
            Mono<ResponseEntity> validateOutsidePaymentResponse = validateOutsidePayment(paymentConfirmationRequest);
            if (validateOutsidePaymentResponse != null) {
                return validateOutsidePaymentResponse;
            }
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setAmountOutstanding(paymentConfirmationRequest.getAmountPaid());
            paymentRecord.setAmount(paymentConfirmationRequest.getAmountPaid());
            paymentRecord.setPaymentStatusId(UNPAID_STATUS_ID);
            paymentRecord.setGameTypeId(paymentConfirmationRequest.getGameTypeId());
            paymentRecord.setLicenseTypeId(paymentConfirmationRequest.getLicenseTypeId());
            paymentRecord.setAgentId(paymentConfirmationRequest.getAgentId());
            paymentRecord.setInstitutionId(paymentConfirmationRequest.getInstitutionId());
            paymentRecord.setFeePaymentTypeId(paymentConfirmationRequest.getFeePaymentTypeId());
            paymentRecord.setAmountPaid(0);
            paymentRecord.setForOutsideSystemPayment(true);
            paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
            paymentRecord.setGamingMachineIds(paymentConfirmationRequest.getGamingMachineIds());
            paymentRecord.setGamingTerminalIds(paymentConfirmationRequest.getGamingTerminalIds());
            paymentRecord.setLicenseTransferId(paymentConfirmationRequest.getLicenseTransferId());
            paymentRecord.setCreationDate(LocalDate.now());
            paymentRecord.setPaymentConfirmationApprovalRequestType(paymentConfirmationRequest.getPaymentConfirmationApprovalRequestType());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            //
            PaymentRecordDetail detail = new PaymentRecordDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setPaymentStatusId(UNPAID_STATUS_ID);
            detail.setModeOfPaymentId(OFFLINE_CONFIRMATION_ID);
            detail.setPaymentRecordId(paymentRecord.getId());
            detail.setAmount(paymentConfirmationRequest.getAmountPaid());
            detail.setInvoiceNumber(generateInvoiceNumber());
            mongoRepositoryReactive.saveOrUpdate(detail);
            //
            String gameTypeName = gameTypeService.findNameById(paymentConfirmationRequest.getGameTypeId());
            String feePaymentTypeName = FeePaymentTypeReferenceData.getFeePaymentTypeNameById(mongoRepositoryReactive, paymentConfirmationRequest.getFeePaymentTypeId());
            String licenseTypeName = LicenseTypeReferenceData.getLicenseTypeNameById(mongoRepositoryReactive, paymentConfirmationRequest.getLicenseTypeId());
            String ownerName = getOwnerName(paymentConfirmationRequest);

            /*PaymentConfirmationApprovalRequest approvalRequest = new PaymentConfirmationApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setPaymentRecordId(paymentRecord.getId());
            approvalRequest.setPaymentRecordDetailId(detail.getId());
            approvalRequest.setApprovalRequestTypeId(CONFIRM_FULL_PAYMENT_ID);
            approvalRequest.setInitiatorId(loggedInUser.getId());
            approvalRequest.setPaymentOwnerName(ownerName);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);*/
            //
            /*String verbiage = String.format("Created Payment Confirmation Approval Request:" +
                            " Payment Owner -> %s , " +
                            "Category -> %s, " +
                            "Payment Type -> %s, for -> %s, Reference -> %s, " +
                            "Request Type -> %s", ownerName, gameTypeName,
                    feePaymentTypeName, licenseTypeName,
                    paymentRecord.getPaymentReference(),
                    getTypeNameById(mongoRepositoryReactive, approvalRequest.getApprovalRequestTypeId()));*/

            String verbiage = "";
            if (paymentRecord.isInstitutionPayment() || paymentRecord.isAgentPayment()) {
                verbiage = String.format("Created offline payment record detail -> License Type -> %s, Amount -> %s, Fee Payment Type -> %s,Category -> %s, Id -> %s",
                        paymentRecord.getLicenseType(), paymentRecord.getAmount(), paymentRecord.getFeePaymentType(), paymentRecord.getGameType(), paymentRecord.getId());
            }
            //
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), ownerName, true, getClientIpAddr(request), verbiage));
            //Trigger Notification
            //paymentEmailNotifierAsync.sendOfflinePaymentNotificationForPaymentRecordDetail(detail, paymentRecord);
            return OKResponse(paymentRecord.convertToDto());
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while making outside payment", e);
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
    public Mono<ResponseEntity> updateOfflinePaymentRecordDetail(PaymentUpdateConfirmationRequest confirmationRequest, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return ErrorResponse("Could not find logged in user");
            }
            PaymentRecord paymentRecord = paymentRecordService.findById(confirmationRequest.getPaymentRecordId());
            PaymentRecordDetail paymentRecordDetail =  (PaymentRecordDetail) mongoRepositoryReactive.find(Query.query(Criteria.where("invoiceNumber").is(confirmationRequest.getInvoiceNumber())), PaymentRecordDetail.class).block();
            logger.info("AMOUNT TO BE PAID : " + paymentRecordDetail.getAmount());
            if (paymentRecord == null) {
                return BadRequestResponse("Invalid Payment Record");
            }
            if (paymentRecordDetail != null && !StringUtils.equals(UNPAID_STATUS_ID, paymentRecordDetail.getPaymentStatusId())) {
                //return Mono.just(new ResponseEntity<>(String.format("Payment %s is pending payment approval", paymentRecord.getId()), HttpStatus.BAD_REQUEST));
                return BadRequestResponse("Payment is pending approval");
            }
            if (paymentRecord.isCompletedPayment()) {
                return BadRequestResponse("Payment already completed");
            }
            if (paymentRecord.getAmountOutstanding() < confirmationRequest.getAmount()) {
                return BadRequestResponse("The amount entered is more than the outstanding amount on the payment");
            }
           //
            //Check if Full or Partial Payment  //Calculate payment balance
            if(paymentRecord.getPaymentConfirmationApprovalRequestType() == "1"){
                //paymentRecord.setAmount(confirmationRequest.getAmount());
                paymentRecord.setAmountPaid(confirmationRequest.getAmount());
                paymentRecord.setAmountOutstanding(0);
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            }
            if(paymentRecord.getPaymentConfirmationApprovalRequestType() == "2"){
                double amountOutstanding = paymentRecord.getAmount() - confirmationRequest.getAmount();
                paymentRecord.setAmountPaid(confirmationRequest.getAmount());
                paymentRecord.setAmountOutstanding(amountOutstanding);
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            }
            //
            String ownerName = paymentRecord.getOwnerName();
            //PaymentRecordDetail recordDetail = new PaymentRecordDetail();
            //paymentRecordDetail.setId(UUID.randomUUID().toString());
            paymentRecordDetail.setId(paymentRecordDetail.getId());  //Update with existing same object PK
            paymentRecordDetail.setAmount(confirmationRequest.getAmount());
            paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
            paymentRecordDetail.setPaymentConfirmationApprovalRequestType(paymentRecord.getPaymentConfirmationApprovalRequestType());
            paymentRecordDetail.setModeOfPaymentId(OFFLINE_CONFIRMATION_ID);
            paymentRecordDetail.setPaymentStatusId(PENDING_PAYMENT_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
            //
            PaymentConfirmationApprovalRequest approvalRequest = new PaymentConfirmationApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setPaymentRecordDetailId(paymentRecordDetail.getId());
            approvalRequest.setAmountPaid(confirmationRequest.getAmount());
            approvalRequest.setPaymentRecordId(paymentRecord.getId());
            if(paymentRecord.getPaymentConfirmationApprovalRequestType() == "1") {
                approvalRequest.setApprovalRequestTypeId(CONFIRM_FULL_PAYMENT_ID);
            }else{
                approvalRequest.setApprovalRequestTypeId(CONFIRM_PARTIAL_PAYMENT_ID);
            }
            approvalRequest.setInitiatorId(loggedInUser.getId());
            approvalRequest.setPaymentOwnerName(ownerName);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);

            String verbiage = String.format("Created Payment Confirmation Approval Request:" +
                            " Payment Owner -> %s , Reference -> %s, Invoice Number -> %s, Id -> %s" +
                            "", ownerName, paymentRecord.getPaymentReference(),
                    paymentRecordDetail.getInvoiceNumber(), approvalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), ownerName,
                    true, getClientIpAddr(request), verbiage));
            //Trigger notification upon update of Payment Information on an existing Invoice to Business Users
            paymentEmailNotifierAsync.sendPaymentNotificationToLSLBUsers(paymentRecordDetail, paymentRecord);
            return OKResponse(approvalRequest.convertToDto());
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating existing payment confirmation request", e);
        }
    }

    private Mono<ResponseEntity> validateOutsidePayment(PaymentConfirmationRequest paymentConfirmationRequest) throws Exception {

        if (paymentConfirmationRequest.isLicenseRenewalPayment()) {
            logger.info("Payment is License Renewal Payment XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Mono<ResponseEntity> validateRenewalLicenseResponse = validateLicenseRenewalPayment(paymentConfirmationRequest);
            if (validateRenewalLicenseResponse != null) {
                return validateRenewalLicenseResponse;
            }
        }

        if (paymentConfirmationRequest.isApplicationFeePayment()) {
            logger.info("Payment is Application Fee Payment XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Mono<ResponseEntity> validateApplicationPaymentResponse = validateApplicationPayment(paymentConfirmationRequest);
            if (validateApplicationPaymentResponse != null) {
                return validateApplicationPaymentResponse;
            }
        }

        if (paymentConfirmationRequest.isLicenseFeePayment()) {
            logger.info("Payment is License Fee Payment XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Mono<ResponseEntity> validateLicensePaymentResponse = validateLicensePayment(paymentConfirmationRequest);
            if (validateLicensePaymentResponse != null) {
                return validateLicensePaymentResponse;
            }
        }

        if (paymentConfirmationRequest.isLicenseTransferPayment()) {
            logger.info("Payment is License Transfer Payment XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Mono<ResponseEntity> validateLicenseTransferResponse = validateLicenceTransferPayment(paymentConfirmationRequest);
            if (validateLicenseTransferResponse != null) {
                return validateLicenseTransferResponse;
            }
        }
        return null;
    }

    private Mono<ResponseEntity> validateLicenseRenewalPayment(PaymentConfirmationRequest paymentConfirmationRequest) {
        logger.info("In validated License Renewal Validator XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        String institutionId = paymentConfirmationRequest.getInstitutionId();
        String agentId = paymentConfirmationRequest.getAgentId();
        String gameTypeId = paymentConfirmationRequest.getGameTypeId();
        int page = 0;
        int pageSize = 10000;
        String sortDirection = "DESC";
        String sortProperty = "createdAt";

        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(paymentConfirmationRequest.getInstitutionId()));
        query.addCriteria(Criteria.where("agentId").is(paymentConfirmationRequest.getAgentId()));
        query.addCriteria(Criteria.where("gameTypeId").is(paymentConfirmationRequest.getGameTypeId()));
        query.addCriteria(Criteria.where("paymentStatusId").is(PaymentStatusReferenceData.UNPAID_STATUS_ID));
        //
        PaymentRecord existingUnpaidGamePaymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
        if (existingUnpaidGamePaymentRecord != null && StringUtils.equals(PaymentStatusReferenceData.UNPAID_STATUS_ID, existingUnpaidGamePaymentRecord.getPaymentStatusId())) {
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(ownerName + " has an existing license renewal payment for the game type specified", HttpStatus.BAD_REQUEST));
        }

        Mono<ResponseEntity> findLicenseResponse = licenseService.findAllLicense(page, pageSize, sortDirection, sortProperty, institutionId, agentId, null, null, gameTypeId, null, null, null, null, null, null, null, null);
        if (findLicenseResponse.block() != null && findLicenseResponse.block().getStatusCode() != HttpStatus.OK) {
            logger.info("1 XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(String.format("%s does not have an existing license for %s, please get licensed for category %s before attempting to pay for licence renewal for the category", ownerName, categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }

        License latestExistingLicense = licenseService.getPreviousConfirmedLicense(paymentConfirmationRequest.getInstitutionId(), paymentConfirmationRequest.getAgentId(), paymentConfirmationRequest.getGameTypeId(), paymentConfirmationRequest.getLicenseTypeId());
        if (latestExistingLicense == null) {
            logger.info("2 XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(String.format("%s does not have a valid %s licence, kindly contact LSLB before attempting to pay for licence renewal for %s", ownerName, categoryName, categoryName), HttpStatus.BAD_REQUEST));
        }
        if (latestExistingLicense.isRevokedLicence()) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return BadRequestResponse(String.format("%s's %s licence is revoked", ownerName, categoryName));
        }
        if (latestExistingLicense.isTerminatedLicence()) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return BadRequestResponse(String.format("%s's %s licence is terminated", ownerName, categoryName));
        }
        if (latestExistingLicense.isSuspendedLicence()) {
            String categoryName = gameTypeService.findNameById(gameTypeId);
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return BadRequestResponse(String.format("%s's %s licence is suspended", ownerName, categoryName));
        }
        return null;
    }

    private Mono<ResponseEntity> validateApplicationPayment(PaymentConfirmationRequest paymentConfirmationRequest) {
        String gameTypeId = paymentConfirmationRequest.getGameTypeId();
        String institutionId = paymentConfirmationRequest.getInstitutionId();
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
                        String ownerName = getOwnerName(paymentConfirmationRequest);
                        return BadRequestResponse("Kindly make payment for licence for " + ownerName);
                    }
                    String ownerName = getOwnerName(paymentConfirmationRequest);
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
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>(ownerName + " has an existing application fee payment  for the category specified", HttpStatus.BAD_REQUEST));
        }
        if (paymentRecord != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
            String ownerName = getOwnerName(paymentConfirmationRequest);
            return Mono.just(new ResponseEntity<>("Please complete " + ownerName + "'s  existing application fee payment for category", HttpStatus.BAD_REQUEST));
        }
        return null;
    }


    private Mono<ResponseEntity> validateLicensePayment(PaymentConfirmationRequest paymentConfirmationRequest) throws Exception {
        String institutionId = paymentConfirmationRequest.getInstitutionId();
        String gameTypeId = paymentConfirmationRequest.getGameTypeId();
        String licenseTypeId = paymentConfirmationRequest.getLicenseTypeId();
        String feePaymentTypeId = paymentConfirmationRequest.getFeePaymentTypeId();
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
        String agentId = paymentConfirmationRequest.getAgentId();
        PaymentRecordDto existingLicensePaymentRecordDto = getLicensePaymentRecord(licenseTypeId, gameTypeId, agentId, institutionId);
        if (existingLicensePaymentRecordDto != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("Kindly complete payment of your existing licence payment", HttpStatus.BAD_REQUEST));
        }

        if (existingLicensePaymentRecordDto != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("You  have an existing license payment for this category and can only proceed to make payment for renewal", HttpStatus.BAD_REQUEST));
        }

        if (paymentConfirmationRequest.isBeingPaidByOperator()) {
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

    private Mono<ResponseEntity> validateLicenceTransferPayment(PaymentConfirmationRequest paymentConfirmationRequest) {
        String licenseTransferId = paymentConfirmationRequest.getLicenseTransferId();
        String gameTypeId = paymentConfirmationRequest.getGameTypeId();
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

    public String getOwnerName(PaymentConfirmationRequest paymentConfirmationRequest) {
        if (StringUtils.isNotEmpty(paymentConfirmationRequest.getInstitutionId())) {
            Institution institution = institutionService.findByInstitutionId(paymentConfirmationRequest.getInstitutionId());
            if (institution != null) {
                return institution.getInstitutionName();
            }
        }
        if (StringUtils.isNotEmpty(paymentConfirmationRequest.getAgentId())) {
            Agent agent = agentService.findAgentById(paymentConfirmationRequest.getAgentId());
            if (agent != null) {
                return agent.getFullName();
            }
        }
        return null;
    }
}
