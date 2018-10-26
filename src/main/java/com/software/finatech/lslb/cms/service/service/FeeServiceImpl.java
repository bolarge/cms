package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
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

    @Override
    public Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto, HttpServletRequest request) {
        try {
            LocalDate startDate = new LocalDate(feeCreateDto.getStartDate());
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            String gameTypeId = feeCreateDto.getGameTypeId();
            String feePaymentTypeId = feeCreateDto.getFeePaymentTypeId();
            Fee existingFeeWithParams = findMostRecentFeeByLicenseTypeGameTypeAndFeePaymentType(feeCreateDto.getRevenueNameId(),
                    feeCreateDto.getGameTypeId(), feeCreateDto.getFeePaymentTypeId());
            if (existingFeeWithParams != null) {
                LocalDate existingFeeEndDate = existingFeeWithParams.getEndDate();
                if (existingFeeEndDate == null) {
                    return Mono.just(new ResponseEntity<>("Please set the end date of the previous fee configuration", HttpStatus.BAD_REQUEST));
                }

                if (startDate.isBefore(existingFeeEndDate)) {
                    return Mono.just(new ResponseEntity<>("New Fee start date should not be less than old fee end date", HttpStatus.BAD_REQUEST));
                }
            }
            PendingFee pendingFee = new PendingFee();
            pendingFee.setId(UUID.randomUUID().toString());
            pendingFee.setAmount(Double.valueOf(feeCreateDto.getAmount()));
            pendingFee.setFeePaymentTypeId(feePaymentTypeId);
            pendingFee.setGameTypeId(gameTypeId);
            pendingFee.setLicenseTypeId(feeCreateDto.getRevenueNameId());
            pendingFee.setEffectiveDate(new LocalDate(feeCreateDto.getStartDate()));
            mongoRepositoryReactive.saveOrUpdate(pendingFee);

            FeeApprovalRequest feeApprovalRequest = new FeeApprovalRequest();
            feeApprovalRequest.setInitiatorId(loggedInUser.getId());
            feeApprovalRequest.setPendingFeeId(pendingFee.getId());
            feeApprovalRequest.setId(UUID.randomUUID().toString());
            feeApprovalRequest.setFeeApprovalRequestTypeId(FeeApprovalRequestTypeReferenceData.CREATE_FEE_ID);
            mongoRepositoryReactive.saveOrUpdate(feeApprovalRequest);

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Created Fee Approval Request -> Request Type -> %s, License Type -> %s, FeePaymentType -> %s, Category -> %s, Amount -> %s",
                    feeApprovalRequest.getFeeApprovalRequestType(), pendingFee.getLicenseType(), pendingFee.getFeePaymentType(), pendingFee.getGameType(), pendingFee.getAmount());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                    currentAuditorName, currentAuditorName,
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(feeApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> setFeeEndDate(FeeEndDateUpdateDto feeEndDateUpdateDto, HttpServletRequest request) {
        try {
            String feeId = feeEndDateUpdateDto.getFeeId();
            String endDateString = feeEndDateUpdateDto.getEndDate();
            Fee fee = findFeeById(feeId);
            if (fee == null) {
                return Mono.just(new ResponseEntity<>(String.format("Fee with id %s does not exist", feeId), HttpStatus.BAD_REQUEST));
            }
            LocalDate endDate = new LocalDate(endDateString);
            if (endDate.isBefore(fee.getEffectiveDate())) {
                return Mono.just(new ResponseEntity<>("New End Date should be greater than fee effective date", HttpStatus.BAD_REQUEST));
            }

            FeeApprovalRequest feeApprovalRequest = new FeeApprovalRequest();
            feeApprovalRequest.setId(UUID.randomUUID().toString());
            feeApprovalRequest.setFeeId(feeId);
            feeApprovalRequest.setEndDate(endDate);
            feeApprovalRequest.setFeeApprovalRequestTypeId(FeeApprovalRequestTypeReferenceData.SET_FEE_END_DATE_ID);
            mongoRepositoryReactive.saveOrUpdate(feeApprovalRequest);
            return Mono.just(new ResponseEntity<>(feeApprovalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating fee end date", e);
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
                    currentAuditorName, currentAuditorName,
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


    @Override
    public Mono<ResponseEntity> getLicenseTypes() {
        return getAllEnumeratedEntity("LicenseType");
    }

    @Override
    public Mono<ResponseEntity> getAllFeePaymentType() {
        return getAllEnumeratedEntity("FeePaymentType");
    }

    @Override
    public Mono<ResponseEntity> findActiveFeeByGameTypeAndPaymentTypeAndRevenueName(String gameTypeId, String feePaymentTypeId, String licenseTypeId) {
        if (StringUtils.isEmpty(licenseTypeId) || StringUtils.isEmpty(gameTypeId) || StringUtils.isEmpty(feePaymentTypeId)) {
            return Mono.just(new ResponseEntity<>("None of the request params should be empty", HttpStatus.BAD_REQUEST));
        }
        Fee fee = findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
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
                    || StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, licenseTypeId)
                    || StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, licenseTypeId)) {
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
        LicenseTypeSearch search = new LicenseTypeSearch(institutionId, agentId);
        try {
            Collection<FactObject> factObjects = ReferenceDataUtil.getAllEnumeratedFacts("LicenseType");
            List<EnumeratedFactDto> dtos = new ArrayList<>();
                for (FactObject factObject : factObjects) {
                    LicenseType licenseType = (LicenseType) factObject;
                    if (search.isAgentSearch() && licenseType.appliesToAgent()) {
                        dtos.add(licenseType.convertToDto());
                    }
                    if (search.isInstitutionSearch() && licenseType.appliesToInstitution()) {
                        dtos.add(licenseType.convertToDto());
                    }
                }
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding revenue names by param", e);
        }
    }

    @Override
    public Fee findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(String licenseTypeId, String gameTypeId, String feePaymentTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
        query.addCriteria(Criteria.where("active").is(true));
        return (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
    }

    @Override
    public Fee findMostRecentFeeByLicenseTypeGameTypeAndFeePaymentType(String licenseTypeId, String gameTypeId, String feePaymentTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
        query.with(new Sort(Sort.Direction.DESC, "endDate"));
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

    private class LicenseTypeSearch {
        private String institutionId;
        private String agentId;

        public String getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(String institutionId) {
            this.institutionId = institutionId;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public boolean isAgentSearch() {
            return !StringUtils.isEmpty(this.agentId)
                    && StringUtils.isEmpty(this.institutionId);
        }

        public boolean isInstitutionSearch() {
            return StringUtils.isEmpty(this.agentId)
                    && !StringUtils.isEmpty(this.institutionId);
        }

        public LicenseTypeSearch(String institutionId, String agentId) {
            this.institutionId = institutionId;
            this.agentId = agentId;
        }
    }
}
