package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.domain.FeePaymentType;
import com.software.finatech.lslb.cms.service.domain.LicenseType;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.MapValues;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class FeeServiceImpl implements FeeService {

    private static final Logger logger = LoggerFactory.getLogger(FeeServiceImpl.class);
    public static final String feeAuditActionId = AuditActionReferenceData.CONFIGURATIONS_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;

    @Autowired
    public void setSpringSecurityAuditorAware(SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }

    @Autowired
    public void setAuditLogHelper(AuditLogHelper auditLogHelper) {
        this.auditLogHelper = auditLogHelper;
    }

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Autowired
    MapValues mapValues;

    public Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto, HttpServletRequest request) {
        try {
            String gameTypeId = feeCreateDto.getGameTypeId();
            String feePaymentTypeId = feeCreateDto.getFeePaymentTypeId();
            Query query = new Query();
            query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            query.addCriteria(Criteria.where("licenseTypeId").is(feeCreateDto.getRevenueNameId()));
            query.addCriteria(Criteria.where("active").is(true));
            Fee existingFeeWithGameTypeAndFeePaymentType = (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
            if (existingFeeWithGameTypeAndFeePaymentType != null) {
                existingFeeWithGameTypeAndFeePaymentType.setActive(false);
                mongoRepositoryReactive.saveOrUpdate(existingFeeWithGameTypeAndFeePaymentType);
            }
            Fee fee = new Fee();
            fee.setId(UUID.randomUUID().toString());
            fee.setAmount(Double.valueOf(feeCreateDto.getAmount()));
            fee.setFeePaymentTypeId(feePaymentTypeId);
            fee.setGameTypeId(gameTypeId);
            fee.setLicenseTypeId(feeCreateDto.getRevenueNameId());
            fee.setActive(true);
            mongoRepositoryReactive.saveOrUpdate(fee);

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Created Fee -> License Type : %s, FeePaymentType -> %s, Category -> %s, Amount -> %s", fee.getLicenseType(), fee.getFeePaymentType(), fee.getGameType(), fee.getAmount());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                    currentAuditorName, currentAuditorName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(fee.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateFeePaymentType(FeePaymentTypeDto feeTypeUpdateDto, HttpServletRequest request) {
        try {
            String feePaymentTypeId = feeTypeUpdateDto.getId();
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(feePaymentTypeId));
            FeePaymentType feePaymentType = (FeePaymentType) mongoRepositoryReactive.find(query, FeePaymentType.class).block();
            if (feePaymentType == null) {
                return Mono.just(new ResponseEntity<>("This FeePayment setting does not exist", HttpStatus.BAD_REQUEST));
            }
            String feePaymentTypeName = feePaymentType.getName();
            feePaymentType.setDescription(feeTypeUpdateDto.getDescription());
            feePaymentType.setName(feeTypeUpdateDto.getName());
            mongoRepositoryReactive.saveOrUpdate(feePaymentType);

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Updated Fee Payment Type, Name -> %s ", feePaymentTypeName);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                    currentAuditorName, currentAuditorName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(feePaymentType.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createFeePaymentType(FeePaymentTypeDto feeTypeCreateDto, HttpServletRequest request) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(feeTypeCreateDto.getName()));
            FeePaymentType existingFeePaymentTypeWithName = (FeePaymentType) mongoRepositoryReactive.find(query, FeePaymentType.class).block();
            if (existingFeePaymentTypeWithName != null) {
                return Mono.just(new ResponseEntity<>("This FeePayment setting exist", HttpStatus.BAD_REQUEST));
            }
            FeePaymentType feePaymentType = new FeePaymentType();
            feePaymentType.setId(UUID.randomUUID().toString());
            feePaymentType.setDescription(feeTypeCreateDto.getDescription());
            feePaymentType.setName(feeTypeCreateDto.getName());
            Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
            feePaymentTypeMap.put(feePaymentType.getId(), feePaymentType);
            mongoRepositoryReactive.saveOrUpdate(feePaymentType);

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Updated Fee Payment Type, Name -> %s ", feePaymentType);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                  currentAuditorName, currentAuditorName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(feePaymentType.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createLicenseType(RevenueNameDto revenueNameDto, HttpServletRequest request) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(revenueNameDto.getName()));
            LicenseType existingLicenseTypeWithName = (LicenseType) mongoRepositoryReactive.find(query, LicenseType.class).block();
            if (existingLicenseTypeWithName != null) {
                return Mono.just(new ResponseEntity<>("Licence type with name already exist", HttpStatus.BAD_REQUEST));
            }
            LicenseType licenseType = new LicenseType();
            licenseType.setId(UUID.randomUUID().toString());
            licenseType.setDescription(revenueNameDto.getDescription());
            licenseType.setName(revenueNameDto.getName());
            mongoRepositoryReactive.saveOrUpdate(licenseType);
            Map licenseTypeMap = Mapstore.STORE.get("LicenseType");
            if (licenseTypeMap != null) {
                licenseTypeMap.put(licenseType.getId(), licenseType);
            }

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Created License Type, Name -> %s ", licenseType);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                currentAuditorName,currentAuditorName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(licenseType.convertToDto(), HttpStatus.OK));
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
                query.addCriteria(Criteria.where("licenseTypeId").is(revenueNameId));
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
            logger.error(errorMsg, e);
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
    public List<EnumeratedFactDto> getLicenseTypes() {
        Map licenseTypeMap = Mapstore.STORE.get("LicenseType");
        ArrayList<LicenseType> licenseTypes = new ArrayList<LicenseType>(licenseTypeMap.values());
        List<EnumeratedFactDto> revenueNameDtoList = new ArrayList<>();
        licenseTypes.forEach(factObject -> {
            LicenseType licenseType = factObject;
            revenueNameDtoList.add(licenseType.convertToDto());
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
        query.addCriteria(Criteria.where("licenseTypeId").is(revenueNameId));
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
    public Mono<ResponseEntity> findAllFeePaymentTypeForLicenseType(String licenseTypeId) {
        try {
            if (StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, licenseTypeId)
                    || StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, licenseTypeId)) {
                Query query = new Query();
                query.addCriteria(Criteria.where("id").ne(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID));

                ArrayList<FeePaymentType> feePaymentTypes = (ArrayList<FeePaymentType>) mongoRepositoryReactive.findAll(query, FeePaymentType.class).toStream().collect(Collectors.toList());
                ArrayList<EnumeratedFactDto> feePaymentTypeDtos = new ArrayList<>();
                for (FeePaymentType feePaymentType : feePaymentTypes) {
                    feePaymentTypeDtos.add(feePaymentType.convertToDto());
                }
                return Mono.just(new ResponseEntity<>(feePaymentTypeDtos, HttpStatus.OK));
            }
            if (StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, licenseTypeId)) {
                return getAllFeePaymentType();
            }
            return Mono.just(new ResponseEntity<>("Invalid Revenue Name Supplied", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding fee payment types for revenue", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findLicenseTypeByParams(String institutionId, String agentId) {
        try {
            if (!StringUtils.isEmpty(institutionId) && StringUtils.isEmpty(agentId)) {
                Query query = Query.query(Criteria.where("id").ne(LicenseTypeReferenceData.AGENT_ID));

                ArrayList<LicenseType> licenseTypes = (ArrayList<LicenseType>) mongoRepositoryReactive.findAll(query, LicenseType.class).toStream().collect(Collectors.toList());
                if (licenseTypes == null || licenseTypes.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
                }
                ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
                for (LicenseType licenseType : licenseTypes) {
                    enumeratedFactDtos.add(licenseType.convertToDto());
                }
                return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
            }

            if (!StringUtils.isEmpty(agentId) && StringUtils.isEmpty(institutionId)) {
                Query query = Query.query(Criteria.where("id").is(LicenseTypeReferenceData.AGENT_ID));
                ArrayList<LicenseType> licenseTypes = (ArrayList<LicenseType>) mongoRepositoryReactive.findAll(query, LicenseType.class).toStream().collect(Collectors.toList());
                if (licenseTypes == null || licenseTypes.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
                }
                ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
                for (LicenseType licenseType : licenseTypes) {
                    enumeratedFactDtos.add(licenseType.convertToDto());
                }
                return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
            }
            return Mono.just(new ResponseEntity<>("Please make either institution or agent id null", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding revenue names by param", e);
        }
    }

    @Override
    public Fee findFeeByLicenseTypeGameTypeAndFeePaymentType(String licenseTypeId, String gameTypeId, String feePaymentTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
        return (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
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
