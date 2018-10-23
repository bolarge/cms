package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.GamingTerminal;
import com.software.finatech.lslb.cms.service.dto.AgentGamingTeriminalsUpdateDto;
import com.software.finatech.lslb.cms.service.dto.GamingTerminalCreateDto;
import com.software.finatech.lslb.cms.service.dto.GamingTerminalMultiplePaymentRequest;
import com.software.finatech.lslb.cms.service.dto.GamingTerminalUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface GamingTerminalService {

    Mono<ResponseEntity> findAllGamingTerminals(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createGamingTerminal(GamingTerminalCreateDto gamingTerminalCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> updateGamingTerminal(GamingTerminalUpdateDto gamingTerminalUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> uploadMultipleGamingTerminalsForInstitution(String institutionId, String gameTypeId, MultipartFile multipartFile, HttpServletRequest request);

    GamingTerminal findById(String gamingTerminalId);

    Mono<ResponseEntity> validateMultipleGamingTerminalTaxPayment(GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest);

    Mono<ResponseEntity> validateMultipleGamingTerminalTaxRenewalPayment(GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest);

    Mono<ResponseEntity> findGamingTerminalBySearchKey(String searchKey);
    Mono<ResponseEntity> getUnAssignedInstitutionTerminals(String institutionId);

    Mono<ResponseEntity> assignGamingTerminals(AgentGamingTeriminalsUpdateDto agentGamingTeriminalsUpdateDto, HttpServletRequest request);
}
