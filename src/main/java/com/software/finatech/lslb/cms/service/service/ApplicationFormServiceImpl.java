package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.ApplicationForm;
import com.software.finatech.lslb.cms.service.domain.ApplicationFormType;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFormServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> createApplicationForm(ApplicationFormCreateDto applicationFormCreateDto) {
        try {
            String institutionId = applicationFormCreateDto.getInstitutionId();
            String gameTypeId = applicationFormCreateDto.getGameTypeId();
            String applicationFormTypeId = applicationFormCreateDto.getApplicationFormTypeId();
            Query query = new Query();
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
            query.addCriteria(Criteria.where("applicationFormTypeId").is(applicationFormTypeId));

            ApplicationForm existingApplicationFormForInstitutionWithGameTypeAndApplicationType = (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
            if (existingApplicationFormForInstitutionWithGameTypeAndApplicationType != null) {
                return Mono.just(new ResponseEntity<>("An application form already exists for the institution with the same game type and application type", HttpStatus.BAD_REQUEST));
            }
            ApplicationForm applicationForm = fromCreateDto(applicationFormCreateDto);
            mongoRepositoryReactive.saveOrUpdate(applicationForm);
            return Mono.just(new ResponseEntity<>(applicationForm.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the application form";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> findAllApplicationForm(int page,
                                                       int pageSize,
                                                       String sortDirection,
                                                       String sortProperty,
                                                       String institutionId,
                                                       String applicationFormTypeId,
                                                       String applicationFormStatusId,
                                                       HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(applicationFormTypeId)) {
                query.addCriteria(Criteria.where("applicationFormTypeId").is(applicationFormTypeId));
            }
            if (!StringUtils.isEmpty(applicationFormStatusId)) {
                query.addCriteria(Criteria.where("applicationFormStatusId").is(applicationFormStatusId));
            }

            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, ApplicationForm.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);
            ArrayList<ApplicationForm> applicationForms = (ArrayList<ApplicationForm>) mongoRepositoryReactive.findAll(query, ApplicationForm.class).toStream().collect(Collectors.toList());
            if (applicationForms == null || applicationForms.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<ApplicationFormDto> applicationFormDtos = new ArrayList<>();

            applicationForms.forEach(applicationForm -> {
                applicationFormDtos.add(applicationForm.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(applicationFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding application forms";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllApplicationFormTypes() {
        try {
            List<FactObject> applicationFormTypes = (List<FactObject>) mongoRepositoryReactive.findAll(ApplicationFormType.class).collect(Collectors.toList());
            if (applicationFormTypes == null || applicationFormTypes.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> applicationFormTypeDtos = new ArrayList<>();
            applicationFormTypes.forEach(applicationFormType -> {
                ApplicationFormType applicationFormTypeConc = (ApplicationFormType) applicationFormType;
                applicationFormTypeDtos.add(applicationFormTypeConc.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(applicationFormTypeDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all application form types";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    private ApplicationForm fromCreateDto(ApplicationFormCreateDto applicationFormCreateDto) {
        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setId(UUID.randomUUID().toString());
        BeanUtils.copyProperties(applicationFormCreateDto, applicationForm);
        return applicationForm;
    }
}
