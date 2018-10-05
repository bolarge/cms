package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.LoggedCase;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseActionCreateDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseCommentCreateDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface LoggedCaseService {


    Mono<ResponseEntity> findAllLoggedCases(int page,
                                            int pageSize,
                                            String sortDirection,
                                            String sortProperty,
                                            String reporterId,
                                            String institutionId,
                                            String loggedCaseStatusId,
                                            String agentId,
                                            String startDate,
                                            String endDate,
                                            HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createCase(LoggedCaseCreateDto loggedCaseCreateDto);

    Mono<ResponseEntity> addLoggedCaseAction(LoggedCaseActionCreateDto caseActionCreateDto);

    Mono<ResponseEntity> addLoggedCaseComment(LoggedCaseCommentCreateDto caseCommentCreateDto);

    Mono<ResponseEntity> getAllLoggedCaseStatus();

    LoggedCase findCaseById(String caseId);
}
