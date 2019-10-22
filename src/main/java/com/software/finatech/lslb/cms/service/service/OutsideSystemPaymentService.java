package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.controller.BaseController;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
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

import static com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData.PAYMENT_ID;
import static com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData.INSTITUTION_ID;
import static com.software.finatech.lslb.cms.service.referencedata.ModeOfPaymentReferenceData.LSLB_OFFLINE_ID;
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
    private FeeService feeService;
    private AuthInfoService authInfoService;
    private MachineService gamingMachineService;


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
                                       PaymentEmailNotifierAsync paymentEmailNotifierAsync,
                                       FeeService feeService,
                                       AuthInfoService authInfoService,
                                       MachineService gamingMachineService) {
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
        this.feeService = feeService;
        this.authInfoService = authInfoService;
        this.gamingMachineService = gamingMachineService;
    }

    public Mono<ResponseEntity> updateOfflinePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto, HttpServletRequest httpServletRequest) {
        try {
            String paymentInvoice = paymentRecordDetailUpdateDto.getInvoiceNumber();
            PaymentRecordDetail existingInvoicedPayment =  (PaymentRecordDetail) mongoRepositoryReactive.find(Query.query(Criteria.where("invoiceNumber").is(paymentRecordDetailUpdateDto.getInvoiceNumber())), PaymentRecordDetail.class).block();
            if (existingInvoicedPayment == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment with invoice %s does not exist", paymentInvoice), HttpStatus.BAD_REQUEST));
            }

            //Prevent Multi Payment on an Invoice
            if (existingInvoicedPayment != null && !StringUtils.equals(PaymentStatusReferenceData.UNPAID_STATUS_ID, existingInvoicedPayment.getPaymentStatusId())) {
                //String ownerName = getOwnerName(paymentRecordDetailUpdateDto);
                return Mono.just(new ResponseEntity<>(String.format("Invoice number %s is pending payment approval", paymentInvoice), HttpStatus.BAD_REQUEST));
            }/*else if((existingInvoicedPayment != null && !StringUtils.equals(PaymentStatusReferenceData.PENDING_PAYMENT_STATUS_ID, existingInvoicedPayment.getPaymentStatusId())))
            {
                String ownerName = getOwnerName(paymentRecordDetailUpdateDto);
                return Mono.just(new ResponseEntity<>(ownerName + " has an existing license renewal payment for the game type specified", HttpStatus.BAD_REQUEST));
            }*/

            PaymentRecord paymentRecord = paymentRecordService.findById(existingInvoicedPayment.getPaymentRecordId());
            //Check if FullPayment or Partial Payment
            if(existingInvoicedPayment.getPaymentConfirmationApprovalRequestType() == "01"){
                if (paymentRecordDetailUpdateDto.isSuccessFulPayment() && !existingInvoicedPayment.isSuccessfulPayment()) {
                    double amountPaid = paymentRecord.getAmountPaid();
                    amountPaid = amountPaid + existingInvoicedPayment.getAmount();
                    paymentRecord.setAmountPaid(amountPaid);

                    double amountOutstanding = paymentRecord.getAmountOutstanding();
                    amountOutstanding = amountOutstanding - existingInvoicedPayment.getAmount();
                    paymentRecord.setAmountOutstanding(amountOutstanding);
                    mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                }
            }else {
                if (paymentRecordDetailUpdateDto.isSuccessFulPayment() && !existingInvoicedPayment.isSuccessfulPayment()) {
                    double amountPaid = paymentRecord.getAmountPaid();
                    amountPaid = amountPaid + existingInvoicedPayment.getAmount();
                    paymentRecord.setAmountPaid(amountPaid);

                    double amountOutstanding = paymentRecord.getAmountOutstanding();
                    amountOutstanding = amountOutstanding - existingInvoicedPayment.getAmount();
                    paymentRecord.setAmountOutstanding(amountOutstanding);
                    mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                }
            }
            PaymentRecordDetail detail = new PaymentRecordDetail();
            existingInvoicedPayment.setPaymentDate(LocalDateTime.now());
            existingInvoicedPayment.setPaymentStatusId(PENDING_PAYMENT_STATUS_ID);
            existingInvoicedPayment.setPaymentStatusId(paymentRecordDetailUpdateDto.getPaymentStatusId());
            mongoRepositoryReactive.saveOrUpdate(existingInvoicedPayment);

            //Request Payment Approval Request
            PaymentConfirmationApprovalRequest approvalRequest = new PaymentConfirmationApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setPaymentRecordId(paymentRecord.getId());
            approvalRequest.setPaymentRecordDetailId(existingInvoicedPayment.getId());

            //Check PaymentApprovalRequest Type 1 or 2
            Institution institution = institutionService.findByInstitutionId(paymentRecord.getInstitutionId());
            Agent agent = agentService.findAgentById(paymentRecord.getAgentId());
            if(paymentRecord.getPaymentConfirmationApprovalRequestType() == "01") {
                approvalRequest.setApprovalRequestTypeId(CONFIRM_FULL_PAYMENT_ID);
            }else{
                approvalRequest.setApprovalRequestTypeId(CONFIRM_PARTIAL_PAYMENT_ID);
            }
            approvalRequest.setInitiatorId(springSecurityAuditorAware.getLoggedInUser().getId());
            approvalRequest.setInvoiceNumber(existingInvoicedPayment.getInvoiceNumber());
            //Check IF Payment is by Institution or Agent
            if(paymentRecord.isInstitutionPayment()) {
                approvalRequest.setPaymentOwnerName(institution.getInstitutionName());
            }else {
                approvalRequest.setPaymentOwnerName(agent.getFullName());
            }
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            //Retrieve PaymentConfirmationApprovalRequest Object ID and set on PaymentRecordDetails
            PaymentConfirmationApprovalRequest paymentConfirmationApprovalRequest = (PaymentConfirmationApprovalRequest) mongoRepositoryReactive.find(Query.query(Criteria.where("invoiceNumber").is(paymentInvoice)), PaymentConfirmationApprovalRequest.class).block();
            //logger.info("Payment Approval Confirmation Request is: " + paymentConfirmationApprovalRequest.getId());
            existingInvoicedPayment.setPaymentConfirmationApprovalRequest(paymentConfirmationApprovalRequest.getId());
            //Document Activity and write to into Audit
            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Payment record detail callback -> Payment Record Detail id: %s, Invoice Number -> %s, Status Id -> %s", paymentInvoice, existingInvoicedPayment.getInvoiceNumber(), paymentRecordDetailUpdateDto.getPaymentStatusId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(paymentAuditActionId,
                    currentAuditorName, currentAuditorName, true, getClientIpAddr(httpServletRequest), verbiage));
            //Trigger notification upon update of Payment Information on an existing Invoice to Business Users
            paymentEmailNotifierAsync.sendPaymentNotificationToLSLBUsers(existingInvoicedPayment, paymentRecord);
            return Mono.just(new ResponseEntity<>(existingInvoicedPayment.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating payment record detail", e);
        }
    }

    public Mono<ResponseEntity> createFullPaymentConfirmationRequest(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, HttpServletRequest request) {
        try {
            //String invoiceNumber = null;
            Agent agent;
            Institution institution;
            String feeId = fullPaymentConfirmationRequest.getFeeId();
            logger.info("Fee ID IS : + " + feeId);
            Fee fee = null;
            String feeDescription = "";
            MachineMultiplePayment machineMultiplePayment = new MachineMultiplePayment();
            //Create New Payment Record
            PaymentRecord paymentRecord = new PaymentRecord();

            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            logger.info("Step 1 Checks who LoggedInUser is: " + loggedInUser);
            if (loggedInUser == null) {
                return ErrorResponse("Could not find logged in user");
            }

            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX VALIDATIONS XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            if (fullPaymentConfirmationRequest.isFirstPayment() && (fullPaymentConfirmationRequest.isAgentPayment() || fullPaymentConfirmationRequest.isInstitutionPayment())) {
                fee = feeService.findFeeById(feeId);
                logger.info("FEEEES IS FROZEN " + fee.getGameType());
                if (fee == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
                }
                Mono<ResponseEntity> validateFirstPaymentResponse = validateFirstPayment(fullPaymentConfirmationRequest, fee);
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
                if (fullPaymentConfirmationRequest.getAmountPaid() < fee.getAmount()) {
                    feeDescription = String.format("%s (Part Payment)", feeDescription);
                }
            }

            String paymentRecordId = fullPaymentConfirmationRequest.getPaymentRecordId();
            if (!fullPaymentConfirmationRequest.isFirstPayment()) {
                paymentRecord = paymentRecordService.findById(paymentRecordId);
                if (paymentRecord == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s does not exist", fullPaymentConfirmationRequest.getPaymentRecordId()), HttpStatus.BAD_REQUEST));
                }
                String feeName = paymentRecord.getFeePaymentTypeName();
                String gameTypeName = paymentRecord.getGameTypeName();
                String revenueName = paymentRecord.getLicenseTypeName();
                feeDescription = buildFeeDescription(feeName, gameTypeName, revenueName);
                if (fullPaymentConfirmationRequest.getAmountPaid() < paymentRecord.getAmount()) {
                    feeDescription = String.format("%s (Part Payment)", feeDescription);
                }
            }
            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            ArrayList<AuthInfo> institutionAdmins;
            if (fullPaymentConfirmationRequest.isInstitutionPayment()) {
                String institutionId = fullPaymentConfirmationRequest.getInstitutionId();
                institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(institutionId);
                if (institutionAdmins.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("There are no gaming operator users for institution", HttpStatus.BAD_REQUEST));
                }
                institution = institutionService.findByInstitutionId(institutionId);
                if (institution == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
                }
                /*if (StringUtils.isEmpty(institution.getVgPayCustomerCode())) {
                    return Mono.just(new ResponseEntity<>("Customer not created", HttpStatus.BAD_REQUEST));
                }*/
                //invoiceNumber = createInBranchRecordDetailForInstitution(institution, feeDescription, fullPaymentConfirmationRequest, institutionAdmins);
            }

            if (fullPaymentConfirmationRequest.isGamingMachinePayment()) {
                institution = institutionService.findByInstitutionId(fullPaymentConfirmationRequest.getInstitutionId());
                institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(fullPaymentConfirmationRequest.getInstitutionId());
                if (institutionAdmins.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("There are no gaming operator admins for institution owning gaming machine", HttpStatus.BAD_REQUEST));
                }
                if (StringUtils.isEmpty(institution.getVgPayCustomerCode())) {
                    return Mono.just(new ResponseEntity<>("Customer not created", HttpStatus.BAD_REQUEST));
                }
                Pair<Mono<ResponseEntity>, MachineMultiplePayment> multiplePaymentPair = validateGamingMachinePayment(fullPaymentConfirmationRequest);
                if (multiplePaymentPair.getLeft() != null) {
                    return multiplePaymentPair.getLeft();
                }
                /*machineMultiplePayment = multiplePaymentPair.getRight();
                invoiceNumber = createInBranchRecordDetailForGamingMachine(institution, machineMultiplePayment.getMachinePaymentDetailList(), institutionAdmins);
                */
            }

            if (fullPaymentConfirmationRequest.isGamingTerminalPayment()) {
                agent = agentService.findAgentById(fullPaymentConfirmationRequest.getAgentId());
                if (agent == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Agent with Id %s does not exist", fullPaymentConfirmationRequest.getAgentId()), HttpStatus.BAD_REQUEST));
                }
                Pair<Mono<ResponseEntity>, MachineMultiplePayment> multiplePaymentPair = validateGamingTerminalPayment(fullPaymentConfirmationRequest);
                if (multiplePaymentPair.getLeft() != null) {
                    return multiplePaymentPair.getLeft();
                }
                /*machineMultiplePayment = multiplePaymentPair.getRight();
                invoiceNumber = createInBranchRecordDetailForGamingTerminal(agent, machineMultiplePayment.getMachinePaymentDetailList());
                */
            }
            //xxxxxx

            /*if (fullPaymentConfirmationRequest.isAgentPayment()) {
                agent = agentService.findAgentById(fullPaymentConfirmationRequest.getAgentId());
                if (agent != null) {
                    invoiceNumber = createInBranchRecordDetailForAgent(agent, feeDescription, fullPaymentConfirmationRequest);
                }
            }*/

            /*if (StringUtils.isEmpty(invoiceNumber)) {
                return Mono.just(new ResponseEntity<>("Invoice was not created successfully", HttpStatus.INTERNAL_SERVER_ERROR));
            }*/

            if (fullPaymentConfirmationRequest.isFirstPayment()) {
                if (fullPaymentConfirmationRequest.isInstitutionPayment() || fullPaymentConfirmationRequest.isAgentPayment()) {
                    paymentRecord = newPaymentFromFee(fee, fullPaymentConfirmationRequest);
                }
                if (fullPaymentConfirmationRequest.isGamingMachinePayment() || fullPaymentConfirmationRequest.isGamingTerminalPayment()) {
                    paymentRecord = newPaymentForMachine(fullPaymentConfirmationRequest, machineMultiplePayment);
                }
            }

            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


            //XXXXXXXXXXXXXX DISABLE FROM THIS POINT TO OBSERVE
            /*Query query = new Query();
            if(fullPaymentConfirmationRequest.getInstitutionId() != null) {
                query.addCriteria(Criteria.where("institutionId").is(fullPaymentConfirmationRequest.getInstitutionId()));
            }else if(fullPaymentConfirmationRequest.getAgentId() != null){
                query.addCriteria(Criteria.where("agentId").is(fullPaymentConfirmationRequest.getAgentId()));
            }
            query.addCriteria(Criteria.where("gameTypeId").is(fullPaymentConfirmationRequest.getGameTypeId()));
            query.addCriteria(Criteria.where("paymentStatusId").is(PaymentStatusReferenceData.UNPAID_STATUS_ID));
            query.addCriteria(Criteria.where("forOutsideSystemPayment").is(fullPaymentConfirmationRequest.isForOutsideSystemPayment()));

            PaymentRecord existingUnpaidGamePaymentRecord = (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
            if (existingUnpaidGamePaymentRecord != null && StringUtils.equals(PaymentStatusReferenceData.UNPAID_STATUS_ID, existingUnpaidGamePaymentRecord.getPaymentStatusId())) {
                String ownerName = getOwnerName(fullPaymentConfirmationRequest);
                return Mono.just(new ResponseEntity<>(ownerName + " has a pending payment for the game type selected", HttpStatus.BAD_REQUEST));
            }*/

            //Check If Payer has pending payment within the same GameType, revenue or fees types
            Mono<ResponseEntity> validateOutsidePaymentResponse = validateOutsidePayment(fullPaymentConfirmationRequest, fee);
            if (validateOutsidePaymentResponse != null) {
                return validateOutsidePaymentResponse;
            }


            //Check if FullPayment or Partial Payment
            if(fullPaymentConfirmationRequest.getPaymentConfirmationApprovalRequestType() == "01"){
                //Calculate balance for Partial or FullPayment
                paymentRecord.setAmountOutstanding(0);
                paymentRecord.setAmount(fullPaymentConfirmationRequest.getAmountPaid());
                paymentRecord.setAmountPaid(fullPaymentConfirmationRequest.getAmountPaid());
            }else if(fullPaymentConfirmationRequest.getPaymentConfirmationApprovalRequestType() == "02"){
                //Get total amount
                if (fullPaymentConfirmationRequest.isFirstPayment() && (fullPaymentConfirmationRequest.isAgentPayment() || fullPaymentConfirmationRequest.isInstitutionPayment())) {
                     fee = feeService.findFeeById(fullPaymentConfirmationRequest.getFeeId());
                    if (fee == null) {
                        return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", fullPaymentConfirmationRequest.getFeeId()), HttpStatus.BAD_REQUEST));
                    }
                    Mono<ResponseEntity> validateOutPaymentPaymentResponse = validateOutsidePayment(fullPaymentConfirmationRequest, fee);
                    if (validateOutPaymentPaymentResponse != null) {
                        return validateOutPaymentPaymentResponse;
                    }
                }
                    double amountToPaid = fullPaymentConfirmationRequest.getAmountPaid();
                    double amountBalance = fee.getAmount() - amountToPaid;
                    paymentRecord.setAmountPaid(amountToPaid);
                    paymentRecord.setAmount(fee.getAmount());
                    paymentRecord.setAmountOutstanding(amountBalance);
                    mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            }
            //
            //Generate Invoice Tag
            String invoiceNumber = NumberUtil.generateInvoiceNumber();
            paymentRecord.setPaymentStatusId(UNPAID_STATUS_ID);
            paymentRecord.setPaymentConfirmationApprovalRequestType(fullPaymentConfirmationRequest.getPaymentConfirmationApprovalRequestType());
            paymentRecord.setGameTypeId(fullPaymentConfirmationRequest.getGameTypeId());
            paymentRecord.setLicenseTypeId(fullPaymentConfirmationRequest.getLicenseTypeId());
            paymentRecord.setAgentId(fullPaymentConfirmationRequest.getAgentId());
            paymentRecord.setInstitutionId(fullPaymentConfirmationRequest.getInstitutionId());
            paymentRecord.setFeePaymentTypeId(fullPaymentConfirmationRequest.getFeeId());
            paymentRecord.setForOutsideSystemPayment(true);
            paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
            paymentRecord.setGamingMachineIds(fullPaymentConfirmationRequest.getGamingMachineIds());
            paymentRecord.setGamingTerminalIds(fullPaymentConfirmationRequest.getGamingTerminalIds());
            paymentRecord.setLicenseTransferId(fullPaymentConfirmationRequest.getLicenseTransferId());
            paymentRecord.setCreationDate(LocalDate.now());
            paymentRecord.setInvoiceNumber(invoiceNumber);
            paymentRecord.setCreationDate(LocalDate.now());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);

            //Create Payment Record (Invoice) Detail
            PaymentRecordDetail detail = new PaymentRecordDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setPaymentStatusId(UNPAID_STATUS_ID); //PENDING_PAYMENT_STATUS_ID
            detail.setModeOfPaymentId(LSLB_OFFLINE_ID);
            detail.setPaymentRecordId(paymentRecord.getId());
            detail.setAmount(fullPaymentConfirmationRequest.getAmountPaid());
            detail.setInvoiceNumber(invoiceNumber);
            //Condition is false at this point
            detail.isUnpaidPayment();
            mongoRepositoryReactive.saveOrUpdate(detail);

            //Get Object Name Description using their IDs
            String gameTypeName = gameTypeService.findNameById(fullPaymentConfirmationRequest.getGameTypeId());
            String feePaymentTypeName = FeePaymentTypeReferenceData.getFeePaymentTypeNameById(mongoRepositoryReactive, fullPaymentConfirmationRequest.getFeeId());
            String licenseTypeName = LicenseTypeReferenceData.getLicenseTypeNameById(mongoRepositoryReactive, fullPaymentConfirmationRequest.getLicenseTypeId());
            String ownerName = getOwnerName(fullPaymentConfirmationRequest);

            //Log for Audit Trail
            String verbiage = "";
            if (paymentRecord.isInstitutionPayment() || paymentRecord.isAgentPayment()) {
                verbiage = String.format("Created offline payment record detail -> License Type -> %s, Amount -> %s, Fee Payment Type -> %s,Category -> %s, Id -> %s",
                        paymentRecord.getLicenseType(), paymentRecord.getAmount(), paymentRecord.getFeePaymentType(), paymentRecord.getGameType(), paymentRecord.getId());
            }
            /*//
             *//*if (paymentRecord.isGamingTerminalPayment() || paymentRecord.isGamingMachinePayment()) {
                    verbiage = String.format("Created Multiple Machine payment -> Machine Serial Numbers -> %s, Amount -> %s", multipleMachinePaymentToAuditString(machineMultiplePayment), machineMultiplePayment.getTotalAmount());
                }*/
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID, springSecurityAuditorAware.getCurrentAuditorNotNull(), ownerName, true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            //Trigger Notification
            paymentEmailNotifierAsync.sendOfflinePaymentNotificationForPaymentRecordDetail(detail, paymentRecord);
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

    private Mono<ResponseEntity> validateOutsidePayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, Fee fee) throws Exception {

        if (fullPaymentConfirmationRequest.isLicenseRenewalPayment()) {
            Mono<ResponseEntity> validateRenewalLicenseResponse = validateLicenseRenewalPayment(fullPaymentConfirmationRequest, fee);
            if (validateRenewalLicenseResponse != null) {
                return validateRenewalLicenseResponse;
            }
        }

        if (fullPaymentConfirmationRequest.isApplicationFeePayment()) {
            Mono<ResponseEntity> validateApplicationPaymentResponse = validateApplicationPayment(fullPaymentConfirmationRequest.getInstitutionId(), fee.getGameTypeId());
            if (validateApplicationPaymentResponse != null) {
                return validateApplicationPaymentResponse;
            }
        }

        if (fullPaymentConfirmationRequest.isLicenseFeePayment()) {
            Mono<ResponseEntity> validateLicensePaymentResponse = validateLicensePayment(fullPaymentConfirmationRequest, fee);
            if (validateLicensePaymentResponse != null) {
                return validateLicensePaymentResponse;
            }
        }

        if (fullPaymentConfirmationRequest.isLicenseTransferPayment()) {
            Mono<ResponseEntity> validateLicenseTransferResponse = validateLicenceTransferPayment(fullPaymentConfirmationRequest, fee);
            if (validateLicenseTransferResponse != null) {
                return validateLicenseTransferResponse;
            }
        }
        return null;
    }

    private Mono<ResponseEntity> validateLicenseRenewalPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, Fee fee) {
        String institutionId = fullPaymentConfirmationRequest.getInstitutionId();
        logger.info(" THISIISISISIS " + fullPaymentConfirmationRequest.getAgentId());
        String agentId = fullPaymentConfirmationRequest.getAgentId();
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

        License latestExistingLicense = licenseService.getPreviousConfirmedLicense(fullPaymentConfirmationRequest.getInstitutionId(), fullPaymentConfirmationRequest.getAgentId(), fee.getGameTypeId(), fee.getLicenseTypeId());
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


    private Mono<ResponseEntity> validateLicensePayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, Fee fee) throws Exception {
        if (!StringUtils.isEmpty(fullPaymentConfirmationRequest.getInstitutionId())) {
            Institution institution = institutionService.findByInstitutionId(fullPaymentConfirmationRequest.getInstitutionId());
            if (institution != null && institution.isFromLiveData()) {
                Query query = new Query();
                query.addCriteria(Criteria.where("gameTypeId").is(fee.getGameTypeId()));
                query.addCriteria(Criteria.where("institutionId").is(fullPaymentConfirmationRequest.getInstitutionId()));
                query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
                ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                if (licenses.size() == 1 && !licenses.get(0).isAIPRelatedLicense()) {
                    return BadRequestResponse("Kindly make payment for licence renewal");
                }
            }
        }
        String institutionId = fullPaymentConfirmationRequest.getInstitutionId();
        String agentId = fullPaymentConfirmationRequest.getAgentId();
        String gameTypeId = fee.getGameTypeId();
        String revenueNameId = fee.getLicenseTypeId();
        if (fullPaymentConfirmationRequest.isAgentPayment()) {
            gameTypeId = fee.getGameTypeId();
        }
        PaymentRecordDto existingLicensePaymentRecordDto = getLicensePaymentRecord(revenueNameId, gameTypeId, agentId, institutionId);
        if (existingLicensePaymentRecordDto != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("Kindly complete payment of your existing licence payment", HttpStatus.BAD_REQUEST));
        }

        if (existingLicensePaymentRecordDto != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePaymentRecordDto.getPaymentStatusId())) {
            return Mono.just(new ResponseEntity<>("You  have an existing license payment for this category and can only proceed to make payment for renewal", HttpStatus.BAD_REQUEST));
        }

        if (fullPaymentConfirmationRequest.isInstitutionPayment()) {
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

    private PaymentRecordDto getLicensePaymentRecord(String revenueNameId, String gameTypeId, String agentId, String institutionId) throws Exception {
        try {
            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
            String sortDirection = "DESC";
            String sortProperty = "createdAt";
            int page = 0;
            int pageSize = 1000;

            Mono<ResponseEntity> paymentRecordsResponse = paymentRecordService.findAllPaymentRecords(page, pageSize, sortDirection, sortProperty, institutionId, agentId,
                    null, gameTypeId, feePaymentTypeId, revenueNameId, null, null, null, null,
                    null, null);

            if (paymentRecordsResponse.block().getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.info("Institution ID is  Null");
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
            logger.info("Institution ID is  Null");
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

    private Mono<ResponseEntity> validateLicenceTransferPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, Fee fee) {
        String licenseTransferId = fullPaymentConfirmationRequest.getLicenseTransferId();
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

    //XXXXXXXXXXXXXXXXXXXXXXXXXX
    private Mono<ResponseEntity> validateFirstPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest, Fee fee) throws Exception {
        if (fee.isLicenseRenewalFee()) {
            Mono<ResponseEntity> validateRenewalLicenseResponse = validateLicenseRenewalPayment(fullPaymentConfirmationRequest, fee);
            if (validateRenewalLicenseResponse != null) {
                return validateRenewalLicenseResponse;
            }
        }

        if (fee.isApplicationFee()) {
            Mono<ResponseEntity> validateApplicationPaymentResponse = validateApplicationPayment(fullPaymentConfirmationRequest.getInstitutionId(), fee.getGameTypeId());
            if (validateApplicationPaymentResponse != null) {
                return validateApplicationPaymentResponse;
            }
        }

        if (fee.isLicenseFee()) {
            Mono<ResponseEntity> validateLicensePaymentResponse = validateLicensePayment(fullPaymentConfirmationRequest, fee);
            if (validateLicensePaymentResponse != null) {
                return validateLicensePaymentResponse;
            }
        }

        if (fee.isLicenseTransferFee()) {
            Mono<ResponseEntity> validateLicenseTransferResponse = validateLicenceTransferPayment(fullPaymentConfirmationRequest, fee);
            if (validateLicenseTransferResponse != null) {
                return validateLicenseTransferResponse;
            }
        }
        return null;
    }

    private String buildFeeDescription(String feeName, String gameTypeName, String revenueName) {
        String feeDescription = String.format("%s category %s for %ss", gameTypeName, feeName, revenueName);
        feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(feeDescription);
        return feeDescription;
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    private PaymentRecord newPaymentFromFee(Fee fee, FullPaymentConfirmationRequest createDto) {
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

    private PaymentRecord newPaymentForMachine(FullPaymentConfirmationRequest createDto, MachineMultiplePayment machineMultiplePayment) {
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


    private Pair<Mono<ResponseEntity>, MachineMultiplePayment> validateGamingMachinePayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) {
        Set<String> machineIds = fullPaymentConfirmationRequest.getGamingMachineIds();
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

    private Pair<Mono<ResponseEntity>, MachineMultiplePayment> validateGamingTerminalPayment(FullPaymentConfirmationRequest fullPaymentConfirmationRequest) {
        Set<String> machineIds = fullPaymentConfirmationRequest.getGamingTerminalIds();
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
}
