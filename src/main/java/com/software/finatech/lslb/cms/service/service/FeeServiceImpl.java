package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
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
            Fee existingFeeWithGameTypeAndFeePaymentType = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType != null) {
                return Mono.just(new ResponseEntity<>("A fee setting already exist with the Fee Type and Game Type please update it", HttpStatus.BAD_REQUEST));
            }
            Fee fee = new Fee();
            fee.setId(UUID.randomUUID().toString());
            fee.setAmount(Double.valueOf(feeCreateDto.getAmount()));
            fee.setDuration(feeCreateDto.getDuration());
            fee.setFeePaymentTypeId(feePaymentTypeId);
            fee.setGameTypeId(gameTypeId);
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
            String feeId = feeUpdateDto.getId();
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(feeId));
            Fee existingFeeWithGameTypeAndFeePaymentType = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType == null) {
                return Mono.just(new ResponseEntity<>("This Fee setting does not exist", HttpStatus.BAD_REQUEST));
            }
            Fee fee = new Fee();
            fee.setAmount(Double.valueOf(feeUpdateDto.getAmount()));
            fee.setDuration(feeUpdateDto.getDuration());
            fee.setFeePaymentTypeId(feeUpdateDto.getFeePaymentTypeId());
            fee.setGameTypeId(feeUpdateDto.getGameTyeId());
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
            FeePaymentType existingFeeWithGameTypeAndFeePaymentType = (FeePaymentType) mongoRepositoryReactive.find(query, FeePaymentType.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType == null) {
                return Mono.just(new ResponseEntity<>("This FeePayment setting does not exist", HttpStatus.BAD_REQUEST));
            }
            FeePaymentType feePaymentType = new FeePaymentType();
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
            query.addCriteria(Criteria.where("id").is(feeTypeCreateDto.getName()));
            FeePaymentType existingFeeWithGameTypeAndFeePaymentType = (FeePaymentType) mongoRepositoryReactive.find(query, FeePaymentType.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType != null) {
                return Mono.just(new ResponseEntity<>("This FeePayment setting exist", HttpStatus.BAD_REQUEST));
            }
            FeePaymentType feePaymentType = new FeePaymentType();
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
    public Mono<ResponseEntity> getAllFees(String feePaymentTypeId, String gameTypeId) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(feePaymentTypeId)) {
                query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
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
    public List<EnumeratedFactDto> getFeePaymentType() {
        Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
        ArrayList<FeePaymentType> feePaymentTypes = new ArrayList<FeePaymentType> (feePaymentTypeMap.values());
        List<EnumeratedFactDto> feePaymentTypeDtoList = new ArrayList<>();
        feePaymentTypes.forEach(factObject -> {
            FeePaymentType feePaymentType = factObject;
            feePaymentTypeDtoList.add(feePaymentType.convertToDto());
        });
        return feePaymentTypeDtoList;
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
}
