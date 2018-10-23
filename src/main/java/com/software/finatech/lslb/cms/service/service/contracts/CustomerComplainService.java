package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.CustomerComplain;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainCreateDto;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CustomerComplainService {
    Mono<ResponseEntity> findAllCustomerComplains(int page,
                                                  int pageSize,
                                                  String sortDirection,
                                                  String sortProperty,
                                                  String customerEmail,
                                                  String customerPhone,
                                                  String customerComplainStatusId,
                                                  String startDate,
                                                  String endDate,
                                                  HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getCustomerComplainFullDetail(String customerComplainId);

    Mono<ResponseEntity> closeCustomerComplain(String customerComplainId, HttpServletRequest request);

    Mono<ResponseEntity> createCustomerComplain(CustomerComplainCreateDto customerComplainCreateDto, HttpServletRequest request);

    CustomerComplain findCustomerComplainById(String customerComplainId);

    Mono<ResponseEntity> updateCustomerComplainStatus(CustomerComplainUpdateDto customerComplainUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> getAllCustomerComplainStatus();
}
