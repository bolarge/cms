package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.domain.RevenueName;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.util.MapValues;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class FeeServiceImpl implements FeeService {
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private static final Logger logger = LoggerFactory.getLogger(FeeServiceImpl.class);

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Autowired
    MapValues mapValues;

    public Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto) {
        try {
            String gameTypeId = feeCreateDto.getGameTypeId();
            String feePaymentTypeId = feeCreateDto.getFeePaymentTypeId();
            Query query = new Query();
            query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            query.addCriteria(Criteria.where("revenueNameId").is(feeCreateDto.getRevenueNameId()));
            if (StringUtils.isEmpty(feePaymentTypeId)) {
                return Mono.just(new ResponseEntity<>("FeePayment Type ID is required", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(gameTypeId)) {
                return Mono.just(new ResponseEntity<>("GameType ID is required", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(feeCreateDto.getRevenueNameId())) {
                return Mono.just(new ResponseEntity<>("Revenue Name ID is required", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(feeCreateDto.getAmount())) {
                return Mono.just(new ResponseEntity<>("Amount is required", HttpStatus.BAD_REQUEST));
            }
            Fee existingFeeWithGameTypeAndFeePaymentType = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType != null) {
                return Mono.just(new ResponseEntity<>("A fee setting already exist with the Fee Type and Game Type please update it", HttpStatus.BAD_REQUEST));
            }
            Fee fee = new Fee();
            fee.setId(UUID.randomUUID().toString());
            fee.setAmount(Double.valueOf(feeCreateDto.getAmount()));
            fee.setFeePaymentTypeId(feePaymentTypeId);
            fee.setGameTypeId(gameTypeId);
            fee.setRevenueNameId(feeCreateDto.getRevenueNameId());
            fee.setActive(true);
            mongoRepositoryReactive.saveOrUpdate(fee);
            return Mono.just(new ResponseEntity<>(fee.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateFee(FeeUpdateDto feeUpdateDto) {
        try {
            if (StringUtils.isEmpty(feeUpdateDto.getFeePaymentTypeId())) {
                return Mono.just(new ResponseEntity<>("FeePayment Type ID is required", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(feeUpdateDto.getGameTypeId())) {
                return Mono.just(new ResponseEntity<>("GameType ID is required", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(feeUpdateDto.getRevenueNameId())) {
                return Mono.just(new ResponseEntity<>("Revenue Name ID is required", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.isEmpty(feeUpdateDto.getId())) {
                return Mono.just(new ResponseEntity<>("Fee ID is required", HttpStatus.BAD_REQUEST));
            }

            String feeId = feeUpdateDto.getId();
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(feeId));
            query.addCriteria(Criteria.where("gameTypeId").is(feeUpdateDto.getGameTypeId()));
            query.addCriteria(Criteria.where("revenueNameId").is(feeUpdateDto.getRevenueNameId()));
            query.addCriteria(Criteria.where("feePaymentTypeId").is(feeUpdateDto.getFeePaymentTypeId()));
            Fee fee = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
            if (fee == null) {
                return Mono.just(new ResponseEntity<>("This Fee setting does not exist", HttpStatus.BAD_REQUEST));
            }
            fee.setAmount(Double.valueOf(feeUpdateDto.getAmount()));
            fee.setActive(feeUpdateDto.isActive());
            mongoRepositoryReactive.saveOrUpdate(fee);
            return Mono.just(new ResponseEntity<>(fee.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateFeePaymentType(FeePaymentTypeDto feeTypeUpdateDto) {
        try {
            String feePaymentTypeId = feeTypeUpdateDto.getId();
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(feePaymentTypeId));
            FeePaymentType feePaymentType = (FeePaymentType) mongoRepositoryReactive.find(query, FeePaymentType.class).block();
            if (feePaymentType == null) {
                return Mono.just(new ResponseEntity<>("This FeePayment setting does not exist", HttpStatus.BAD_REQUEST));
            }
            feePaymentType.setDescription(feeTypeUpdateDto.getDescription());
            feePaymentType.setName(feeTypeUpdateDto.getName());
            mongoRepositoryReactive.saveOrUpdate(feePaymentType);
            return Mono.just(new ResponseEntity<>(feePaymentType.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createFeePaymentType(FeePaymentTypeDto feeTypeCreateDto) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(feeTypeCreateDto.getName()));
            FeePaymentType existingFeeWithGameTypeAndFeePaymentType = (FeePaymentType) mongoRepositoryReactive.find(query, FeePaymentType.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType != null) {
                return Mono.just(new ResponseEntity<>("This FeePayment setting exist", HttpStatus.BAD_REQUEST));
            }
            FeePaymentType feePaymentType = new FeePaymentType();
            feePaymentType.setId(UUID.randomUUID().toString());
            feePaymentType.setDescription(feeTypeCreateDto.getDescription());
            feePaymentType.setName(feeTypeCreateDto.getName());
            mongoRepositoryReactive.saveOrUpdate(feePaymentType);
            return Mono.just(new ResponseEntity<>(feePaymentType.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createRevenueName(RevenueNameDto revenueNameDto) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(revenueNameDto.getName()));
            RevenueName existingRevenueNameWithGameTypeAndFeePaymentType = (RevenueName) mongoRepositoryReactive.find(query, RevenueName.class).block();
            if (existingRevenueNameWithGameTypeAndFeePaymentType != null) {
                return Mono.just(new ResponseEntity<>("This RevenueName setting exist", HttpStatus.BAD_REQUEST));
            }
            RevenueName revenueName = new RevenueName();
            revenueName.setId(UUID.randomUUID().toString());
            revenueName.setDescription(revenueNameDto.getDescription());
            revenueName.setName(revenueNameDto.getName());
            mongoRepositoryReactive.saveOrUpdate(revenueName);
            return Mono.just(new ResponseEntity<>(revenueName.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId, String revenueNameId) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(feePaymentTypeId)) {
                query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(revenueNameId)) {
                query.addCriteria(Criteria.where("revenueNameId").is(revenueNameId));
            }


            ArrayList<Fee> fees = (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
            if (fees == null || fees.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<FeeDto> feeDtos = new ArrayList<>();
            fees.forEach(fee -> {
                feeDtos.add(fee.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(feeDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all fees";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public List<FeesTypeDto> getAllFeesType() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("active").is(true));
            List<Fee> fees = (ArrayList<Fee>) mongoRepositoryReactive.findAll(query, Fee.class).toStream().collect(Collectors.toList());
            ArrayList<FeesTypeDto> feeDtos = new ArrayList<>();
            fees.forEach(fee -> {
                FeesTypeDto feesTypeDto = new FeesTypeDto();
                feesTypeDto.setFeeId(fee.getId());
                feesTypeDto.setFee(fee.convertToDto().getRevenueName() + " " + fee.convertToDto().getGameTypeName() + " " + fee.convertToDto().getFeePaymentTypeName());
                feeDtos.add(feesTypeDto);
            });
            return feeDtos;
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all fees";
            return null;
        }
    }

    public List<EnumeratedFactDto> getFeePaymentType() {
        Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
        ArrayList<FeePaymentType> feePaymentTypes = new ArrayList<FeePaymentType>(feePaymentTypeMap.values());
        List<EnumeratedFactDto> feePaymentTypeDtoList = new ArrayList<>();
        feePaymentTypes.forEach(factObject -> {
            FeePaymentType feePaymentType = factObject;
            feePaymentTypeDtoList.add(feePaymentType.convertToDto());
        });
        return feePaymentTypeDtoList;
    }

    @Override
    public List<EnumeratedFactDto> getRevenueNames() {
        Map revenueNameMap = Mapstore.STORE.get("RevenueName");
        ArrayList<RevenueName> revenueNames = new ArrayList<RevenueName>(revenueNameMap.values());
        List<EnumeratedFactDto> revenueNameDtoList = new ArrayList<>();
        revenueNames.forEach(factObject -> {
            RevenueName revenueName = factObject;
            revenueNameDtoList.add(revenueName.convertToDto());
        });
        return revenueNameDtoList;
    }

    @Override
    public Mono<ResponseEntity> getAllFeePaymentType() {
        try {
            return Mono.just(new ResponseEntity<>(getFeePaymentType(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all payment types";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> findActiveFeeByGameTypeAndPaymentTypeAndRevenueName(String gameTypeId, String feePaymentTypeId, String revenueNameId) {
        if (StringUtils.isEmpty(revenueNameId) || StringUtils.isEmpty(gameTypeId) || StringUtils.isEmpty(feePaymentTypeId)) {
            return Mono.just(new ResponseEntity<>("None of the request params should be empty", HttpStatus.BAD_REQUEST));
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("revenueNameId").is(revenueNameId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
        query.addCriteria(Criteria.where("active").is(true));
        Fee fee = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
        if (fee == null) {
            return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
        }
        FeeDto feeDto = new FeeDto();
        feeDto.setId(fee.getId());
        feeDto.setAmount(fee.getAmount());
        return Mono.just(new ResponseEntity<>(feeDto, HttpStatus.OK));
    }

    @Override
    public Fee findFeeById(String feeId) {
        return (Fee) mongoRepositoryReactive.findById(feeId, Fee.class).block();
    }

    @Override
    public Mono<ResponseEntity> findAllFeePaymentTypeForRevenueName(String revenueNameId) {
        try {
            if (StringUtils.equals(RevenueNameReferenceData.AGENT_REVENUE_ID, revenueNameId)
                    || StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, revenueNameId)) {
                Query query = new Query();
                query.addCriteria(Criteria.where("id").ne(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID));

                ArrayList<FeePaymentType> feePaymentTypes = (ArrayList<FeePaymentType>) mongoRepositoryReactive.findAll(query, FeePaymentType.class).toStream().collect(Collectors.toList());
                ArrayList<EnumeratedFactDto> feePaymentTypeDtos = new ArrayList<>();
                for (FeePaymentType feePaymentType : feePaymentTypes) {
                    feePaymentTypeDtos.add(feePaymentType.convertToDto());
                }
                return Mono.just(new ResponseEntity<>(feePaymentTypeDtos, HttpStatus.OK));
            }
            if (StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, revenueNameId)) {
                return getAllFeePaymentType();
            }
            return Mono.just(new ResponseEntity<>("Invalid Revenue Name Supplied", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding fee payment types for revenue", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findRevenueNameByParams(String institutionId, String agentId) {
        try {
            if (!StringUtils.isEmpty(institutionId) && StringUtils.isEmpty(agentId)) {
                Query query = Query.query(Criteria.where("id").ne(RevenueNameReferenceData.AGENT_REVENUE_ID));

                ArrayList<RevenueName> revenueNames = (ArrayList<RevenueName>) mongoRepositoryReactive.findAll(query, RevenueName.class).toStream().collect(Collectors.toList());
                if (revenueNames == null || revenueNames.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
                }
                ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
                for (RevenueName revenueName : revenueNames) {
                    enumeratedFactDtos.add(revenueName.convertToDto());
                }
                return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
            }

            if (!StringUtils.isEmpty(agentId) && StringUtils.isEmpty(institutionId)) {
                Query query = Query.query(Criteria.where("id").is(RevenueNameReferenceData.AGENT_REVENUE_ID));
                ArrayList<RevenueName> revenueNames = (ArrayList<RevenueName>) mongoRepositoryReactive.findAll(query, RevenueName.class).toStream().collect(Collectors.toList());
                if (revenueNames == null || revenueNames.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
                }
                ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
                for (RevenueName revenueName : revenueNames) {
                    enumeratedFactDtos.add(revenueName.convertToDto());
                }
                return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
            }
            return Mono.just(new ResponseEntity<>("Please make either institution or agent id null", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding revenue names by param", e);
        }
    }

    @Override
    public Fee findFeeByRevenueNameGameTypeAndFeePaymentType(String revenueNameId, String gameTypeId, String feePaymentTypeId){
       Query query = new Query();
       query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
       query.addCriteria(Criteria.where("revenueNameId").is(revenueNameId));
       query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
      return (Fee)mongoRepositoryReactive.find(query, Fee.class).block();
    }


    @Override
    public FeePaymentType getFeePaymentTypeById(String feePaymentTypeId) {
        if (feePaymentTypeId == null) {
            return null;
        }
        Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
        FeePaymentType feePaymentType = null;
        if (feePaymentTypeMap != null) {
            feePaymentType = (FeePaymentType) feePaymentTypeMap.get(feePaymentTypeId);
        }
        if (feePaymentType == null) {
            feePaymentType = (FeePaymentType) mongoRepositoryReactive.findById(feePaymentTypeId, FeePaymentType.class).block();
            if (feePaymentType != null && feePaymentTypeMap != null) {
                feePaymentTypeMap.put(feePaymentTypeId, feePaymentType);
            }
        }
        return feePaymentType;
    }
}
