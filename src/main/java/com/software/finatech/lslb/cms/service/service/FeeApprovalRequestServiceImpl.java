package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.FeeApprovalRequestDto;
import com.software.finatech.lslb.cms.service.exception.ApprovalRequestProcessException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.FeeApprovalRequestService;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class FeeApprovalRequestServiceImpl implements FeeApprovalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(FeeApprovalRequestServiceImpl.class);
    private static final String feeAuditActionId = AuditActionReferenceData.CONFIGURATIONS_ID;

    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private FeeService feeService;
    private AuditLogHelper auditLogHelper;
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;

    @Autowired
    public FeeApprovalRequestServiceImpl(SpringSecurityAuditorAware springSecurityAuditorAware,
                                         MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                         FeeService feeService,
                                         AuditLogHelper auditLogHelper,
                                         ApprovalRequestNotifierAsync approvalRequestNotifierAsync) {
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.feeService = feeService;
        this.auditLogHelper = auditLogHelper;
        this.approvalRequestNotifierAsync = approvalRequestNotifierAsync;
    }

    @Override
    public Mono<ResponseEntity> findAllFeeApprovalRequests(int page,
                                                           int pageSize,
                                                           String sortDirection,
                                                           String sortProperty,
                                                           String approvalRequestStatusId,
                                                           String feeApprovalRequestTypeId,
                                                           String initiatorId,
                                                           String approverId,
                                                           String rejectorId,
                                                           String startDate,
                                                           String endDate,
                                                           HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(approvalRequestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(approvalRequestStatusId));
            }
            if (!StringUtils.isEmpty(feeApprovalRequestTypeId)) {
                query.addCriteria(Criteria.where("feeApprovalRequestTypeId").is(feeApprovalRequestTypeId));
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
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                query.addCriteria(Criteria.where("dateCreated").gte(new LocalDate(startDate)).lte(new LocalDate(endDate)));
            }

            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser != null) {
                query.addCriteria(Criteria.where("initiatorId").ne(loggedInUser.getId()));
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, FeeApprovalRequest.class).block();
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

            ArrayList<FeeApprovalRequest> feeApprovalRequests = (ArrayList<FeeApprovalRequest>) mongoRepositoryReactive.findAll(query, FeeApprovalRequest.class).toStream().collect(Collectors.toList());
            if (feeApprovalRequests == null || feeApprovalRequests.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<FeeApprovalRequestDto> feeApprovalRequestDtos = new ArrayList<>();

            feeApprovalRequests.forEach(feeApprovalRequest -> {
                feeApprovalRequestDtos.add(feeApprovalRequest.convertToHalfDto());
            });
            return Mono.just(new ResponseEntity<>(feeApprovalRequestDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get fee approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> approveFeeApprovalRequest(String feeApprovalRequestId, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            FeeApprovalRequest feeApprovalRequest = findFeeApprovalRequestById(feeApprovalRequestId);
            if (feeApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Fee Approval Request With id %s does not exist", feeApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (feeApprovalRequest.isApprovedRequest()
                    || feeApprovalRequest.isRejectedRequest() ||
                    !feeApprovalRequest.canBeApprovedByUser(loggedInUser.getId())) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }

            if (feeApprovalRequest.isCreateFee()) {
                approveCreateFeeRequest(feeApprovalRequest);
            } else if (feeApprovalRequest.isSetFeeEndDate()) {
                approveSetFeeEndDateRequest(feeApprovalRequest);
            } else {
                return Mono.just(new ResponseEntity<>("Invalid Request supplied", HttpStatus.BAD_REQUEST));
            }
            feeApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            feeApprovalRequest.setApproverId(loggedInUser.getId());
            mongoRepositoryReactive.saveOrUpdate(feeApprovalRequest);

            String verbiage = String.format("Approved Fee approval request ->  Type -> %s,Id -> %s ", feeApprovalRequest.getFeeApprovalRequestType(), feeApprovalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), loggedInUser.getFullName(),
                    LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return Mono.just(new ResponseEntity<>(feeApprovalRequest.convertToDto(), HttpStatus.OK));
        } catch (ApprovalRequestProcessException e) {
            return logAndReturnError(logger, e.getMessage(), e);
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectFeeApprovalRequest(String feeApprovalRequestId, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            FeeApprovalRequest feeApprovalRequest = findFeeApprovalRequestById(feeApprovalRequestId);
            if (feeApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Fee Approval Request With id %s does not exist", feeApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (feeApprovalRequest.isApprovedRequest() ||
                    feeApprovalRequest.isRejectedRequest() ||
                    !feeApprovalRequest.canBeApprovedByUser(loggedInUser.getId())) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }

            if (feeApprovalRequest.isCreateFee()) {
                PendingFee pendingFee = feeApprovalRequest.getPendingFee();
                if (pendingFee != null) {
                    pendingFee.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
                    mongoRepositoryReactive.saveOrUpdate(pendingFee);
                }
            }

            feeApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            feeApprovalRequest.setRejectorId(loggedInUser.getId());
            mongoRepositoryReactive.saveOrUpdate(feeApprovalRequest);

            String verbiage = String.format("Rejected Fee approval request ->  Type -> %s,Id -> %s ", feeApprovalRequest.getFeeApprovalRequestType(), feeApprovalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), loggedInUser.getFullName(),
                    LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));


            approvalRequestNotifierAsync.sendRejectedFeeApprovalRequestEmailToInitiator(feeApprovalRequest);
            return Mono.just(new ResponseEntity<>(feeApprovalRequest.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getFeeApprovalRequestFullDetail(String feeApprovalRequestId) {
        try {
            FeeApprovalRequest feeApprovalRequest = findFeeApprovalRequestById(feeApprovalRequestId);
            if (feeApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("There is no fee approval request with id %s", feeApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(feeApprovalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting fee approval request full detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getFeeApprovalRequestTypes() {
        return getAllEnumeratedEntity("FeeApprovalRequestType", FeeApprovalRequestType.class);
    }

    private void approveSetFeeEndDateRequest(FeeApprovalRequest feeApprovalRequest) {
        Fee fee = feeApprovalRequest.getFee();
        if (fee != null) {
            fee.setEndDate(feeApprovalRequest.getEndDate());
            if (fee.getEndDate().isEqual(LocalDate.now()) ||  fee.getEndDate().isBefore(LocalDate.now())) {
                fee.setActive(false);
            }
            fee.setNextNotificationDate(fee.getEndDate().minusDays(7));
            mongoRepositoryReactive.saveOrUpdate(fee);
        }
    }

    private void approveCreateFeeRequest(FeeApprovalRequest feeApprovalRequest) throws ApprovalRequestProcessException {
        PendingFee pendingFee = feeApprovalRequest.getPendingFee();
        if (pendingFee != null) {
            pendingFee.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            Fee existingFee = feeService.findMostRecentFeeByLicenseTypeGameTypeAndFeePaymentType(pendingFee.getLicenseTypeId(), pendingFee.getGameTypeId(), pendingFee.getFeePaymentTypeId());
            if (existingFee != null) {
                if (existingFee.getEndDate() == null) {
                    throw new ApprovalRequestProcessException("The previous fee configuration does not have end date set");
                }
                if (pendingFee.getEffectiveDate().isBefore(existingFee.getEndDate())) {
                    String exceptionMessage = "The previous fee configuration end date is before the pending fee start date";
                    throw new ApprovalRequestProcessException(exceptionMessage);
                }
            }
            Fee fee = new Fee();
            fee.setId(UUID.randomUUID().toString());
            //  if (pendingFee.getEffectiveDate().isBefore(LocalDate.now()) || pendingFee.getEffectiveDate().isEqual(LocalDate.now())) {
            fee.setActive(true);
            //  } else {
            //      fee.setActive(false);
            //  }
            LocalDate today = LocalDate.now();
            fee.setGameTypeId(pendingFee.getGameTypeId());
            fee.setFeePaymentTypeId(pendingFee.getFeePaymentTypeId());
            fee.setLicenseTypeId(pendingFee.getLicenseTypeId());
            fee.setEffectiveDate(pendingFee.getEffectiveDate());
            fee.setAmount(pendingFee.getAmount());
            fee.setEndDate(pendingFee.getEndDate());
            if (fee.getEndDate() != null) {
                fee.setNextNotificationDate(fee.getEndDate().minusDays(7));
//                if (fee.getEndDate().isBefore(today) || fee.getEndDate().isEqual(today)) {
//                    fee.setActive(false);
//                }}
            }
            mongoRepositoryReactive.saveOrUpdate(fee);
            mongoRepositoryReactive.saveOrUpdate(pendingFee);
        }
    }

    private FeeApprovalRequest findFeeApprovalRequestById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return (FeeApprovalRequest) mongoRepositoryReactive.findById(id, FeeApprovalRequest.class).block();
    }
}
