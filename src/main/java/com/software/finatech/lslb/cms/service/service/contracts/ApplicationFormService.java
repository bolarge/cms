package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormCreateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface ApplicationFormService {

    Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto);

    Mono<ResponseEntity> findAllApplicationForm(int page,
                                                int pageSize,
                                                String sortDirection,
                                                String sortProperty,
                                                String institutionId,
                                                String applicationFormTypeId,
                                                String applicationFormStatusId,
                                                HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getAllApplicationFormTypes();
}
