package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PaymentConfirmationApprovalRequestService {
    Mono<ResponseEntity> findAllPaymentConfirmationApprovalRequests(int page,
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
                                                                    HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllApprovalRequestType();

    Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> getPaymentConfirmationApprovalRequestFullDetail(String approvalRequestId);
}
