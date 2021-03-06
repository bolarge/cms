package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.DocumentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.DocumentApprovalRequestService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DocumentApprovalRequestServiceImpl implements DocumentApprovalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentApprovalRequestServiceImpl.class);
    private static final String configAuditActionId = AuditActionReferenceData.CONFIGURATIONS_ID;


    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;

    public DocumentApprovalRequestServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                              SpringSecurityAuditorAware springSecurityAuditorAware,
                                              AuditLogHelper auditLogHelper,
                                              ApprovalRequestNotifierAsync approvalRequestNotifierAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.approvalRequestNotifierAsync = approvalRequestNotifierAsync;
    }

    @Override
    public Mono<ResponseEntity> findAllDocumentApprovalRequests(int page,
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
                                                                HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(approvalRequestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(approvalRequestStatusId));
            }
            if (!StringUtils.isEmpty(approvalRequestTypeId)) {
                query.addCriteria(Criteria.where("documentApprovalRequestTypeId").is(approvalRequestTypeId));
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

            /**
             *   Get the logged in user and use him to filter requests
             */
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser != null) {
                query.addCriteria(Criteria.where("initiatorId").ne(loggedInUser.getId()));
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, DocumentApprovalRequest.class).block();
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

            ArrayList<DocumentApprovalRequest> documentApprovalRequests = (ArrayList<DocumentApprovalRequest>) mongoRepositoryReactive.findAll(query, DocumentApprovalRequest.class).toStream().collect(Collectors.toList());
            if (documentApprovalRequests == null || documentApprovalRequests.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<DocumentApprovalRequestDto> dtos = new ArrayList<>();

            documentApprovalRequests.forEach(approvalRequest -> {
                dtos.add(approvalRequest.convertToHalfDto());
            });
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get user approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllDocumentApprovalRequestType() {
        return getAllEnumeratedEntity("DocumentApprovalRequestType", DocumentApprovalRequestType.class);
    }

    @Override
    public Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        try {
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            String approvalRequestId = requestOperationtDto.getApprovalRequestId();
            DocumentApprovalRequest documentApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (documentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Approval request with id %s not found", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (documentApprovalRequest.isApprovedRequest() ||
                    documentApprovalRequest.isRejectedRequest() ||
                    !documentApprovalRequest.canBeApprovedByUser(user.getId())) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }

            if (documentApprovalRequest.isCreateDocumentType()) {
                approveCreateDocumentType(documentApprovalRequest);
            } else if (documentApprovalRequest.isSetApprover()) {
                approveSetApprover(documentApprovalRequest);
            } else if (documentApprovalRequest.isRemoveApprover()) {
                approveRemoveApprover(documentApprovalRequest);
            } else {
                return Mono.just(new ResponseEntity<>("Invallid Request supplied", HttpStatus.BAD_REQUEST));
            }

            documentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            documentApprovalRequest.setApproverId(user.getId());
            mongoRepositoryReactive.saveOrUpdate(documentApprovalRequest);
            String verbiage = String.format("Approved Document approval request ->  Type -> %s,Id -> %s ", documentApprovalRequest.getDocumentApprovalRequestType(), documentApprovalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(configAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), String.valueOf(documentApprovalRequest.getSubjectDocumentType()),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return Mono.just(new ResponseEntity<>(documentApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving user approval request ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        try {
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }

            String approvalRequestId = requestOperationtDto.getApprovalRequestId();
            DocumentApprovalRequest documentApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (documentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Approval request with id %s not found", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (documentApprovalRequest.isApprovedRequest() ||
                    documentApprovalRequest.isRejectedRequest() ||
                    !documentApprovalRequest.canBeApprovedByUser(user.getId())) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }
            if (documentApprovalRequest.isCreateDocumentType()) {
                rejectCreateDocumentTypeRequest(documentApprovalRequest);
            }

            documentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            documentApprovalRequest.setRejectorId(user.getId());
            documentApprovalRequest.setRejectionReason(requestOperationtDto.getReason());
            mongoRepositoryReactive.saveOrUpdate(documentApprovalRequest);
            String verbiage = String.format("Rejected Document approval request ->  Type -> %s, Id -> %s", documentApprovalRequest.getDocumentApprovalRequestType(), documentApprovalRequest.getId());

            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(configAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), String.valueOf(documentApprovalRequest.getSubjectDocumentType()),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            approvalRequestNotifierAsync.sendRejectedDocumentApprovalRequestEmailToInitiator(documentApprovalRequest);
            return Mono.just(new ResponseEntity<>(documentApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting approval request ", e);
        }
    }

    @Override
    public DocumentApprovalRequest findApprovalRequestById(String approvalRequestId) {
        if (StringUtils.isEmpty(approvalRequestId)) {
            return null;
        }
        return (DocumentApprovalRequest) mongoRepositoryReactive.findById(approvalRequestId, DocumentApprovalRequest.class).block();
    }

    @Override
    public Mono<ResponseEntity> getDocumentApprovalRequestFullDetail(String approvalRequestId) {
        try {
            DocumentApprovalRequest documentApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (documentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Approval request with id %s does not exist", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(documentApprovalRequest.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting user approval request full detail", e);
        }
    }

    private void rejectCreateDocumentTypeRequest(DocumentApprovalRequest documentApprovalRequest) {
        PendingDocumentType pendingDocumentType = documentApprovalRequest.getPendingDocumentType();
        if (pendingDocumentType != null) {
            pendingDocumentType.setApprovalRequestStatusIds(ApprovalRequestStatusReferenceData.REJECTED_ID);
            mongoRepositoryReactive.saveOrUpdate(pendingDocumentType);
        }
    }

    private void approveCreateDocumentType(DocumentApprovalRequest documentApprovalRequest) {
        PendingDocumentType pendingDocumentType = documentApprovalRequest.getPendingDocumentType();
        if (pendingDocumentType != null) {
            DocumentType documentType = new DocumentType();
            documentType.setId(UUID.randomUUID().toString());
            documentType.setApproverId(pendingDocumentType.getApproverId());
            documentType.setGameTypeIds(pendingDocumentType.getGameTypeIds());
            documentType.setDocumentPurposeId(pendingDocumentType.getDocumentPurposeId());
            documentType.setActive(pendingDocumentType.isActive());
            documentType.setRequired(pendingDocumentType.isRequired());
            documentType.setName(pendingDocumentType.getName());
            documentType.setDescription(pendingDocumentType.getDescription());
            documentType.setGameTypeIds(pendingDocumentType.getGameTypeIds());
            mongoRepositoryReactive.saveOrUpdate(documentType);
            pendingDocumentType.setApprovalRequestStatusIds(ApprovalRequestStatusReferenceData.APPROVED_ID);
            mongoRepositoryReactive.saveOrUpdate(pendingDocumentType);
        }
    }

    private void approveSetApprover(DocumentApprovalRequest documentApprovalRequest) {
        DocumentType documentType = documentApprovalRequest.getDocumentType();
        if (documentType != null) {
            documentType.setApproverId(documentApprovalRequest.getNewApproverId());
            mongoRepositoryReactive.saveOrUpdate(documentType);
        }
    }

    private void approveRemoveApprover(DocumentApprovalRequest documentApprovalRequest) {
        DocumentType documentType = documentApprovalRequest.getDocumentType();
        if (documentType != null) {
            documentType.setApproverId(null);
            mongoRepositoryReactive.saveOrUpdate(documentType);
        }
    }
}
