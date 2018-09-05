package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.InstitutionCreateDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionUpdateDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class InstitutionServiceImpl implements InstitutionService {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> createInstitution(InstitutionCreateDto institutionCreateDto) {
        Mono<ResponseEntity> validateInstitutionResponse = validateInstitutionCreateInstitution(institutionCreateDto);
        if (validateInstitutionResponse != null) {
            return validateInstitutionResponse;
        }
        Institution newInstitution = fromCreateInstitutionDto(institutionCreateDto);
        try {
            mongoRepositoryReactive.saveOrUpdate(newInstitution);
            return Mono.just(new ResponseEntity<>(newInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to save institution";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateInstitution(InstitutionUpdateDto institutionUpdateDto) {
        Query queryForId = new Query();
        String institutionId = institutionUpdateDto.getId();
        String institutionName = institutionUpdateDto.getInstitutionName();
        String institutionEmail = institutionUpdateDto.getEmailAddress();
        queryForId.addCriteria(Criteria.where("id").is(institutionId));
        Institution existingInstitution = (Institution) mongoRepositoryReactive.find(queryForId, Institution.class).block();
        if (existingInstitution == null) {
            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
        }
        if (!StringUtils.equals(existingInstitution.getInstitutionName(), institutionUpdateDto.getInstitutionName())) {
            Query queryForName = new Query();
            queryForName.addCriteria(Criteria.where("institutionName").is(institutionName));
            Institution existingInstitutionWithName = (Institution) mongoRepositoryReactive.find(queryForName, Institution.class).block();
            if (existingInstitutionWithName != null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with name %s already exist", institutionName), HttpStatus.BAD_REQUEST));
            }
        }

        if (!StringUtils.equals(existingInstitution.getEmailAddress(), institutionUpdateDto.getEmailAddress())) {
            Query queryForEmail = new Query();
            queryForEmail.addCriteria(Criteria.where("emailAddress").is(institutionEmail));
            Institution existingInstitutionWithEmail = (Institution) mongoRepositoryReactive.find(queryForEmail, Institution.class).block();
            if (existingInstitutionWithEmail != null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with email %s already exist", institutionName), HttpStatus.BAD_REQUEST));
            }
        }
        existingInstitution.setGameTypeIds(institutionUpdateDto.getGameTypeIds());
        existingInstitution.setEmailAddress(institutionUpdateDto.getEmailAddress());
        existingInstitution.setPhoneNumber(institutionUpdateDto.getPhoneNumber());
        existingInstitution.setDescription(institutionUpdateDto.getDescription());
        existingInstitution.setInstitutionName(institutionUpdateDto.getInstitutionName());
        try {
            mongoRepositoryReactive.saveOrUpdate(existingInstitution);
            return Mono.just(new ResponseEntity<>(existingInstitution.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while updating the institution";
            return logAndReturnError(logger, errorMsg, e);
        }

    }

    @Override
    public Mono<ResponseEntity> disableInstitution(InstitutionDto institutionDto) {
        return null;
    }

    @Override
    public Mono<ResponseEntity> findAllInstitutions(int page,
                                                    int pageSize,
                                                    String sortType,
                                                    String sortProperty,
                                                    String gameTypeIds,
                                                    HttpServletResponse response) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("\\s*,\\s*"));
                query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeIdList));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, Institution.class).block();
                response.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort;
            if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive
                    .findAll(query, Institution.class).toStream().collect(Collectors.toList());
            if (institutions.size() == 0) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<InstitutionDto> institutionDtos = new ArrayList<>();
            institutions.forEach(entry -> {
                institutionDtos.add(entry.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(institutionDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Institution findById(String institutionId) {
        return (Institution)mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private Mono<ResponseEntity> validateInstitutionCreateInstitution(InstitutionCreateDto institutionCreateDto) {
        String institutionName = institutionCreateDto.getInstitutionName();
        Query queryForName = Query.query(Criteria.where("institutionName").is(institutionName));
        Institution existingInstitutionWithName = (Institution) mongoRepositoryReactive.find(queryForName, Institution.class).block();
        if (existingInstitutionWithName != null) {
            return Mono.just(new ResponseEntity<>(String.format("An institution already exist with name %s", institutionName), HttpStatus.BAD_REQUEST));
        }

        String emailAddress = institutionCreateDto.getEmailAddress();
        Query queryForEmail = Query.query(Criteria.where("emailAddress").is(emailAddress));
        Institution existingInstitutionWithEmail = (Institution) mongoRepositoryReactive.find(queryForEmail, Institution.class).block();
        if (existingInstitutionWithEmail != null) {
            return Mono.just(new ResponseEntity<>(String.format("An institution already exist with email %s", emailAddress), HttpStatus.BAD_REQUEST));
        }

        return null;
    }

    private Institution fromCreateInstitutionDto(InstitutionCreateDto institutionCreateDto) {
        Institution institution = new Institution();
        institution.setId(UUID.randomUUID().toString());
        institution.setInstitutionName(institutionCreateDto.getInstitutionName());
        institution.setDescription(institutionCreateDto.getDescription());
        institution.setPhoneNumber(institutionCreateDto.getPhoneNumber());
        institution.setEmailAddress(institutionCreateDto.getEmailAddress());
        institution.setGameTypeIds(institutionCreateDto.getGameTypeIds());
        return institution;
    }
}
