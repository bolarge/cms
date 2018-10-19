package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AgentApprovalRequestService {

    Mono<ResponseEntity> findAllAgentApprovalRequests(int page,
                                                      int pageSize,
                                                      String sortDirection,
                                                      String sortProperty,
                                                      String institutionId,
                                                      String agentId,
                                                      String approverId,
                                                      String gameTypeId,
                                                      String rejectorId,
                                                      String requestTypeId,
                                                      String requestStatusId,
                                                      String startDate,
                                                      String endDate,
                                                      HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllAgentApprovalRequestType();

    Mono<ResponseEntity> getAllApprovalRequestStatus();

    Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto agentApprovalRequestRejectDto, HttpServletRequest request);

    Mono<ResponseEntity> getAgentApprovalRequestFullDetail(String agentApprovalRequestId);
}
