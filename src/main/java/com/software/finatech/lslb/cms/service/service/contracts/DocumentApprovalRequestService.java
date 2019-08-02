package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.DocumentApprovalRequest;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DocumentApprovalRequestService {


    Mono<ResponseEntity> findAllDocumentApprovalRequests(int page,
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
                                                         HttpServletResponse httpServletResponse);


    Mono<ResponseEntity> getAllDocumentApprovalRequestType();

    Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    DocumentApprovalRequest findApprovalRequestById(String approvalRequestId);

    Mono<ResponseEntity> getDocumentApprovalRequestFullDetail(String userApprovalRequestId);
}
