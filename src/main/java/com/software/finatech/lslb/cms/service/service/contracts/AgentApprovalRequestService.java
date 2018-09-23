package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestOperationtDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

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
                                                      HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllAgentApprovalRequestType();

    Mono<ResponseEntity> getAllApprovalRequestStatus();

    Mono<ResponseEntity> approveRequest(AgentApprovalRequestOperationtDto agentApprovalRequestOperationtDto);

    Mono<ResponseEntity> rejectRequest(AgentApprovalRequestOperationtDto agentApprovalRequestRejectDto);

    Mono<ResponseEntity> getAgentApprovalRequestFullDetail(String agentApprovalRequestId);
}
