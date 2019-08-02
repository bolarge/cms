package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.MachineApprovalRequest;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MachineApprovalRequestService {

    Mono<ResponseEntity> findAllMachineApprovalRequests(int page,
                                                        int pageSize,
                                                        String sortDirection,
                                                        String sortProperty,
                                                        String approvalRequestStatusId,
                                                        String requestTypeId,
                                                        String initiatorId,
                                                        String approverId,
                                                        String rejectorId,
                                                        String institutionId,
                                                        String gamingMachineId,
                                                        String gamingTerminalId,
                                                        String startDate,
                                                        String endDate,
                                                        String machineTypeId,
                                                        HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllMachineApprovalRequestType();

    Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    MachineApprovalRequest findApprovalRequestById(String approvalRequestId);

    Mono<ResponseEntity> getMachineApprovalRequestFullDetail(String approvalRequestId);


    Mono<ResponseEntity> approveMultipleRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);

    Mono<ResponseEntity> rejectMultipleRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request);
}
