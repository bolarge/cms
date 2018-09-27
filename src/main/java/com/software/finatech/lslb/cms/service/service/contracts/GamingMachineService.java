package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.GamingMachine;
import com.software.finatech.lslb.cms.service.dto.GamingMachineCreateDto;
import com.software.finatech.lslb.cms.service.dto.GamingMachineMultiplePaymentRequest;
import com.software.finatech.lslb.cms.service.dto.GamingMachineUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface GamingMachineService {

    Mono<ResponseEntity> findAllGamingMachines(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createGamingMachine(GamingMachineCreateDto gamingMachineCreateDto);

    Mono<ResponseEntity> updateGamingMachine(GamingMachineUpdateDto gamingMachineUpdateDto);

    Mono<ResponseEntity> uploadMultipleGamingMachinesForInstitution(String institutionId,String gameTypeId, MultipartFile multipartFile);

    GamingMachine findById(String gamingMachineId);

    Mono<ResponseEntity> validateMultipleGamingMachineLicensePayment(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest);

    Mono<ResponseEntity> validateMultipleGamingMachineLicenseRenewalPayment(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest);
}
