package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.dto.CustomerComplainCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface CustomerComplainService {
    Mono<ResponseEntity> findAllCustomerComplains(int page,
                                                  int pageSize,
                                                  String sortDirection,
                                                  String sortProperty,
                                                  String customerEmail,
                                                  String customerPhone,
                                                  String customerComplainStatusId,
                                                  HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getCustomerComplainFullDetail(String customerComplainId);

    Mono<ResponseEntity> resolveCustomerComplain(String userId, String customerComplainId);

    Mono<ResponseEntity> closeCustomerComplain(String userId, String customerComplainId);

    Mono<ResponseEntity> createCustomerComplain(CustomerComplainCreateDto customerComplainCreateDto);
}
