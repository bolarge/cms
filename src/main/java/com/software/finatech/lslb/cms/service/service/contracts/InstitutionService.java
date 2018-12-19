package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.InstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InstitutionService {
    Mono<ResponseEntity> createInstitution(InstitutionCreateDto institutionCreateDto);

    Mono<ResponseEntity> updateInstitution(InstitutionUpdateDto institutionUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> disableInstitution(InstitutionDto institutionDto, HttpServletRequest request);

    Mono<ResponseEntity> enableInstitution(InstitutionDto institutionDto, HttpServletRequest request);

    Mono<ResponseEntity> findAllInstitutions(int page,
                                             int pageSize,
                                             String sortType,
                                             String sortProperty,
                                             String gameTypeIds,
                                             String institutionId,
                                             HttpServletResponse httpServletResponse);

    Institution findByInstitutionId(String institutionId);

    void saveInstitution(Institution institution);

    Mono<ResponseEntity> createApplicantInstitution(InstitutionCreateDto institutionCreateDto, AuthInfo applicantUser);

    Mono<ResponseEntity> uploadMultipleExistingLicensedInstitutions(MultipartFile multipartFile, HttpServletRequest request);

    Mono<ResponseEntity> findInstitutionsBySearchKey(String searchKey);

    Mono<ResponseEntity> getInstitutionFullDetailById(String id);

    void saveOperatorMembersDetailsToOperator(ApplicationForm applicationForm);

    InstitutionCategoryDetails findInstitutionCategoryDetailsByInstitutionIdAndGameTypeId(String institutionId, String gameTypeId);
    void expireAllOperatorTerminalsAndMachines(LicenseTransfer licenseTransfer);
}
