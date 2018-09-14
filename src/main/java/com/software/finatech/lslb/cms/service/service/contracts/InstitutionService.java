package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.InstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface InstitutionService {
    Mono<ResponseEntity> createInstitution(InstitutionCreateDto institutionCreateDto);

    Mono<ResponseEntity> updateInstitution(InstitutionUpdateDto institutionUpdateDto);

    Mono<ResponseEntity> disableInstitution(InstitutionDto institutionDto);

    Mono<ResponseEntity> findAllInstitutions(int page,
                                             int pageSize,
                                             String sortType,
                                             String sortProperty,
                                             String gameTypeIds,
                                             HttpServletResponse httpServletResponse);

    Institution findById(String institutionId);

    Mono<ResponseEntity> createApplicantInstitution(InstitutionCreateDto institutionCreateDto, AuthInfo applicantUser);
}
