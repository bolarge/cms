package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.GamingMachine;
import com.software.finatech.lslb.cms.service.dto.GamingMachineCreateDto;
import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.GamingMachineService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
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
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GamingMachineServiceImpl implements GamingMachineService {

    private static final Logger logger = LoggerFactory.getLogger(GamingMachineServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> findAllGamingMachines(int page, int pageSize, String sortDirection, String sortProperty, String institutionId, String agentId, HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }


            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, GamingMachine.class).block();
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

            ArrayList<GamingMachine> gamingMachines = (ArrayList<GamingMachine>) mongoRepositoryReactive.findAll(query, GamingMachine.class).toStream().collect(Collectors.toList());
            if (gamingMachines == null || gamingMachines.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<GamingMachineDto> gamingMachineDtos = new ArrayList<>();

            gamingMachines.forEach(paymentRecord -> {
                gamingMachineDtos.add(paymentRecord.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(gamingMachineDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all gaming machines";
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createGamingMachine(GamingMachineCreateDto gamingMachineCreateDto) {

        try {
            if (!gamingMachineCreateDto.isManagedByInstitution() && StringUtils.isEmpty(gamingMachineCreateDto.getAgentId())) {
                return Mono.just(new ResponseEntity<>("Please provide agent Id ", HttpStatus.BAD_REQUEST));
            }

            //TODO: validate if Institution has license and agent has paid for agent license
            GamingMachine gamingMachine = fromGamingMachineCreateDto(gamingMachineCreateDto);
            saveGamingMachine(gamingMachine);
            return Mono.just(new ResponseEntity<>(gamingMachine.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while saving gaming machine", e);
        }
    }

    private GamingMachine fromGamingMachineCreateDto(GamingMachineCreateDto gamingMachineCreateDto) {
        GamingMachine gamingMachine = new GamingMachine();
        gamingMachine.setAgentId(gamingMachineCreateDto.getAgentId());
        gamingMachine.setInstitutionId(gamingMachineCreateDto.getInstitutionId());
        gamingMachine.setManagedByInstitution(gamingMachineCreateDto.isManagedByInstitution());
        gamingMachine.setManufacturer(gamingMachineCreateDto.getManufacturer());
        gamingMachine.setSerialNumber(gamingMachineCreateDto.getSerialNumber());
        gamingMachine.setMachineNumber(gamingMachineCreateDto.getGameMachineNumber());
        gamingMachine.setGameDetailsList(gamingMachineCreateDto.getGameDetailsList());
        return gamingMachine;
    }

    private void saveGamingMachine(GamingMachine gamingMachine) {
        mongoRepositoryReactive.saveOrUpdate(gamingMachine);
    }
}
