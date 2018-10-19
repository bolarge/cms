package com.software.finatech.lslb.cms.service.service.contracts;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FeeApprovalRequestService {

    Mono<ResponseEntity> findAllFeeApprovalRequests(int page,
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
                                                    HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> approveFeeApprovalRequest(String feeApprovalRequestId, HttpServletRequest request);

    Mono<ResponseEntity> rejectFeeApprovalRequest(String feeApprovalRequestId, HttpServletRequest request);

    Mono<ResponseEntity> getFeeApprovalRequestFullDetail(String feeApprovalRequestId);

    Mono<ResponseEntity> getFeeApprovalRequestTypes();
}
