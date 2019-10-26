package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.PaymentConfirmationApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.exception.ApprovalRequestProcessException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentConfirmationApprovalRequestService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.PaymentEmailNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData.PAYMENT_ID;
import static com.software.finatech.lslb.cms.service.referencedata.PaymentConfirmationApprovalRequestTypeReferenceData.getTypeNameById;
import static com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
import static com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData.FAILED_PAYMENT_STATUS_ID;
import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.*;
import static com.software.finatech.lslb.cms.service.util.OKResponseUtil.OKResponse;

@Service
public class PaymentConfirmationApprovalRequestServiceImpl implements PaymentConfirmationApprovalRequestService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentConfirmationApprovalRequestServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private PaymentRecordDetailService paymentRecordDetailService;
    private PaymentEmailNotifierAsync paymentEmailNotifierAsync;
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;

    @Autowired
    public PaymentConfirmationApprovalRequestServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                                         SpringSecurityAuditorAware springSecurityAuditorAware,
                                                         AuditLogHelper auditLogHelper,
                                                         PaymentRecordDetailService paymentRecordDetailService,
                                                         PaymentEmailNotifierAsync paymentEmailNotifierAsync,
                                                         ApprovalRequestNotifierAsync approvalRequestNotifierAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.paymentRecordDetailService = paymentRecordDetailService;
        this.paymentEmailNotifierAsync = paymentEmailNotifierAsync;
        this.approvalRequestNotifierAsync = approvalRequestNotifierAsync;
    }

    @Override
    public Mono<ResponseEntity> findAllPaymentConfirmationApprovalRequests(int page,
                                                                           int pageSize,
                                                                           String sortDirection,
                                                                           String sortProperty,
                                                                           String approvalRequestStatusId,
                                                                           String approvalRequestTypeId,
                                                                           String initiatorId,
                                                                           String approverId,
                                                                           String rejectorId,
                                                                           String startDate,
                                                                           String endDate,
                                                                           String ownerName,
                                                                           HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(approvalRequestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(approvalRequestStatusId));
            }
            if (!StringUtils.isEmpty(approvalRequestTypeId)) {
                query.addCriteria(Criteria.where("approvalRequestTypeId").is(approvalRequestTypeId));
            }
            if (!StringUtils.isEmpty(initiatorId)) {
                query.addCriteria(Criteria.where("initiatorId").is(initiatorId));
            }
            if (!StringUtils.isEmpty(approverId)) {
                query.addCriteria(Criteria.where("approverId").is(approverId));
            }
            if (!StringUtils.isEmpty(rejectorId)) {
                query.addCriteria(Criteria.where("rejectorId").is(rejectorId));
            }
            if (StringUtils.isEmpty(ownerName)) {
                query.addCriteria(Criteria.where("paymentOwnerName").regex(ownerName, "i"));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser != null) {
                query.addCriteria(Criteria.where("initiatorId").ne(loggedInUser.getId()));
            }
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                query.addCriteria(Criteria.where("dateCreated").gte(new LocalDate(startDate)).lte(new LocalDate(endDate)));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, PaymentConfirmationApprovalRequest.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<PaymentConfirmationApprovalRequest> approvalRequests = (ArrayList<PaymentConfirmationApprovalRequest>) mongoRepositoryReactive.findAll(query, PaymentConfirmationApprovalRequest.class).toStream().collect(Collectors.toList());
            if (approvalRequests == null || approvalRequests.isEmpty()) {
                return ErrorResponseUtil.NoRecordResponse();
            }
            ArrayList<PaymentConfirmationApprovalRequestDto> dtos = new ArrayList<>();

            approvalRequests.forEach(approvalRequest -> {
                dtos.add(approvalRequest.convertToFullDto());
            });
            return OKResponse(dtos);
        } catch (IllegalArgumentException e) {
            return BadRequestResponse("Invalid Date format , please use yyyy-MM-dd");
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get payment confirmation approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllApprovalRequestType() {
        return getAllEnumeratedEntity("PaymentConfirmationApprovalRequestType", PaymentConfirmationApprovalRequestType.class);
    }

    @Override
    public Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
        if (user == null) {
            return ErrorResponse("Cannot find logged in user");
        }
        String approvalRequestId = requestOperationtDto.getApprovalRequestId();
        logger.info("Approval Request ID is: " + requestOperationtDto.getApprovalRequestId());
        PaymentConfirmationApprovalRequest approvalRequest = findApprovalRequestById(approvalRequestId);
        logger.info("Found Approval Request ID is: " + approvalRequest.getId());
        if (approvalRequest == null) {
            return BadRequestResponse(String.format("Approval request with id %s not found", approvalRequestId));
        }
        if (approvalRequest.isApprovedRequest() || approvalRequest.isRejectedRequest() || !approvalRequest.canBeApprovedByUser(user.getId())) {
            logger.info("Approval isApp: " + approvalRequest.isApprovedRequest() + "  Approval isRej:" + approvalRequest.isRejectedRequest() + "  UserIs "  + !approvalRequest.canBeApprovedByUser(user.getId()));
            return BadRequestResponse("Invalid Request");
        }
        try {
            if (approvalRequest.isConfirmFullPayment()) {
                logger.info("WAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH: ");
                approveConfirmFullPaymentRequest(approvalRequest, request);
            } else if (approvalRequest.isConfirmPartialPayment()) {
                logger.info("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB: ");
                approveConfirmPartialPaymentRequest(approvalRequest, request);
            } else {
                return BadRequestResponse("Invalid Request supplied");
            }

            approvalRequest.setAsApprovedByUser(user.getId());
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            String ownerName = approvalRequest.getPaymentOwnerName();
            String verbiage = String.format("Approved Payment Confirmation Approval Request -> " +
                            " Type -> %s,Payment Owner Name -> %s, Id -> %s ",
                    getTypeNameById(mongoRepositoryReactive, approvalRequest.getApprovalRequestTypeId()),
                    ownerName, approvalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), ownerName,
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));
            return OKResponse(approvalRequest.convertToDto());
        } catch (ApprovalRequestProcessException e) {
            return logAndReturnError(logger, e.getMessage(), e);
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving user approval request ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        try {
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String approvalRequestId = requestOperationtDto.getApprovalRequestId();
            PaymentConfirmationApprovalRequest approvalRequest = findApprovalRequestById(approvalRequestId);
            if (approvalRequest == null) {
                return BadRequestResponse(String.format("Approval request with id %s not found", approvalRequestId));
            }
            if (approvalRequest.isApprovedRequest() ||
                    approvalRequest.isRejectedRequest() ||
                    !approvalRequest.canBeApprovedByUser(user.getId())) {
                return BadRequestResponse("Invalid Request");
            }
            //Update Payment Status ID to FAILED when Rejected
            PaymentRecordDetail paymentRecordDetail =  (PaymentRecordDetail) mongoRepositoryReactive.find(Query.query(Criteria.where("paymentRecordId").is(approvalRequest.getPaymentRecordId())), PaymentRecordDetail.class).block();
            logger.info("Approval Request Invoice is: " + paymentRecordDetail.getInvoiceNumber());
            paymentRecordDetail.setPaymentStatusId(FAILED_PAYMENT_STATUS_ID);
            //Reject Approval Request
            approvalRequest.setAsRejectedByUserWithReason(user.getId(), requestOperationtDto.getReason());
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
            //Log for Audit Trail
            String verbiage = String.format("Rejected Payment Confirmation approval request -> " +
                            " Type -> %s, Owner Name -> %s, Id -> %s",
                    getTypeNameById(mongoRepositoryReactive, approvalRequest.getApprovalRequestTypeId()),
                    approvalRequest.getPaymentOwnerName(), approvalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(PAYMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), approvalRequest.getPaymentOwnerName(),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));
            //Trigger Notification
            //paymentEmailNotifierAsync.sendOfflinePaymentNotificationForPaymentRecordDetail(detail, paymentRecord);
            approvalRequestNotifierAsync.sendRejectedPaymentConfirmationApprovalRequestEmailToInitiator(approvalRequest);
            return OKResponse(approvalRequest.convertToDto());
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting approval request ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getPaymentConfirmationApprovalRequestFullDetail(String approvalRequestId) {
        try {
            PaymentConfirmationApprovalRequest approvalRequest = findApprovalRequestById(approvalRequestId);
            if (approvalRequest == null) {
                return BadRequestResponse("Invalid Approval Request");
            }
            return OKResponse(approvalRequest.convertToFullDto());
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting user approval request full detail", e);
        }
    }

    private void approveConfirmPartialPaymentRequest(PaymentConfirmationApprovalRequest approvalRequest, HttpServletRequest request) throws ApprovalRequestProcessException {
        PaymentRecord paymentRecord = approvalRequest.getPaymentRecord();
        if (paymentRecord == null) {
            throw new ApprovalRequestProcessException("Invalid Payment Record");
        }
        PaymentRecordDetail paymentRecordDetail = approvalRequest.getPaymentRecordDetail();
        if (paymentRecordDetail == null) {
            throw new ApprovalRequestProcessException("Invalid Payment Record detail");
        }
        if (paymentRecord.isCompletedPayment()) {
            throw new ApprovalRequestProcessException("Payment already completed, kindly reject request");
        }
        if (paymentRecordDetail.getAmount() > paymentRecord.getAmountOutstanding()) {
            throw new ApprovalRequestProcessException("The payment amount to be confirmed is more than the amount outstanding, kindly reject and re-initiate a new request");
        }
        doPaymentRecordDetailUpdate(paymentRecordDetail, request);
    }

    private void approveConfirmFullPaymentRequest(PaymentConfirmationApprovalRequest approvalRequest, HttpServletRequest httpServletRequest) throws ApprovalRequestProcessException {
        PaymentRecord paymentRecord = approvalRequest.getPaymentRecord();
        if (paymentRecord == null) {
            throw new ApprovalRequestProcessException("Invalid Payment Record");
        }
        if (!paymentRecord.isForOutsideSystemPayment()) {
            throw new ApprovalRequestProcessException("Kindly reject request, payment record is not for outside payment");
        }
        if (paymentRecord.isCompletedPayment()) {
            throw new ApprovalRequestProcessException("Payment already completed, kindly reject request");
        }
        PaymentRecordDetail recordDetail = approvalRequest.getPaymentRecordDetail();
        if (recordDetail == null) {
            throw new ApprovalRequestProcessException("Invalid Payment record detail");
        }
        doPaymentRecordDetailUpdate(recordDetail, httpServletRequest);
    }

    private void doPaymentRecordDetailUpdate(PaymentRecordDetail paymentRecordDetail, HttpServletRequest httpServletRequest) throws ApprovalRequestProcessException {
        PaymentRecordDetailUpdateDto detailUpdateDto = PaymentRecordDetailUpdateDto.fromIdAndPaymentStatus(paymentRecordDetail.getId(), COMPLETED_PAYMENT_STATUS_ID);
        detailUpdateDto.setInvoiceNumber(paymentRecordDetail.getInvoiceNumber());

        ResponseEntity<String> responseEntity = paymentRecordDetailService.updatePaymentRecordDetail(detailUpdateDto, httpServletRequest).block();
        if (responseEntity != null && responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ApprovalRequestProcessException(responseEntity.getBody());
        }
    }

    private PaymentConfirmationApprovalRequest findApprovalRequestById(String approvalRequestId) {
        if (StringUtils.isEmpty(approvalRequestId)) {
            return null;
        }
        return (PaymentConfirmationApprovalRequest) mongoRepositoryReactive.findById(approvalRequestId, PaymentConfirmationApprovalRequest.class).block();
    }
}