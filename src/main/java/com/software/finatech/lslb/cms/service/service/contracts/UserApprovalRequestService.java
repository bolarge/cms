package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.UserApprovalRequest;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserApprovalRequestService {

    Mono<ResponseEntity> findAllUserApprovalRequests(int page,
                                                     int pageSize,
                                                     String sortDirection,
                                                     String sortProperty,
                                                     String approvalRequestStatusId,
                                                     String userApprovalRequestTypeId,
                                                     String initiatorId,
                                                     String approverId,
                                                     String rejectorId,
                                                     String userId,
                                                     String startDate,
                                                     String endDate,
                                                     HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllUserApprovalRequestType();

    Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    UserApprovalRequest findApprovalRequestById(String approvalRequestId);

    Mono<ResponseEntity> getUserApprovalRequestFullDetail(String userApprovalRequestId);
}
