package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.LoggedCase;
import com.software.finatech.lslb.cms.service.dto.CaseOutcomeRequest;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseActionCreateDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseCommentCreateDto;
import com.software.finatech.lslb.cms.service.dto.LoggedCaseCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
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
                                            String categoryId,
                                            String typeId,
                                            String gameTypeId,
                                            String outcomeId,
                                            String licenseTypeId,
                                            HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createCase(LoggedCaseCreateDto loggedCaseCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> addLoggedCaseAction(LoggedCaseActionCreateDto caseActionCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> addLoggedCaseComment(LoggedCaseCommentCreateDto caseCommentCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> getAllLoggedCaseStatus();

    Mono<ResponseEntity> getLoggedCaseFullDetail(String loggedCaseId);

    LoggedCase findCaseById(String caseId);

    Mono<ResponseEntity> getAllCaseAndComplainType();

    Mono<ResponseEntity> getAllCaseAndComplainCategory();

    Mono<ResponseEntity> takeActionOnCase(CaseOutcomeRequest caseActionRequest, HttpServletRequest request);

    Mono<ResponseEntity> getAllCaseOutcomes();
}
