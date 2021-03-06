package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.FeeService;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.MapValues;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class FeeServiceImpl implements FeeService {

    private static final Logger logger = LoggerFactory.getLogger(FeeServiceImpl.class);
    private static final String feeAuditActionId = AuditActionReferenceData.CONFIGURATIONS_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;
    private GameTypeService gameTypeService;

    @Autowired
    public FeeServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                          SpringSecurityAuditorAware springSecurityAuditorAware,
                          AuditLogHelper auditLogHelper,
                          ApprovalRequestNotifierAsync approvalRequestNotifierAsync,
                          GameTypeService gameTypeService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.approvalRequestNotifierAsync = approvalRequestNotifierAsync;
        this.gameTypeService = gameTypeService;
    }

    @Autowired
    MapValues mapValues;

    @Override
    public Mono<ResponseEntity> createFee(FeeCreateDto feeCreateDto, HttpServletRequest request) {
        try {
            LocalDate startDate = new LocalDate(feeCreateDto.getStartDate());
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not get logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String gameTypeId = feeCreateDto.getGameTypeId();
            String feePaymentTypeId = feeCreateDto.getFeePaymentTypeId();
            String licenseTypeId = feeCreateDto.getRevenueNameId();
            Fee existingFeeWithParams = findMostRecentFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId,
                    gameTypeId, feePaymentTypeId);
            if (existingFeeWithParams != null) {
                LocalDate existingFeeEndDate = existingFeeWithParams.getEndDate();
                if (existingFeeEndDate == null) {
                    return Mono.just(new ResponseEntity<>("Please set the end date of the previous fee configuration", HttpStatus.BAD_REQUEST));
                }

                if (startDate.isBefore(existingFeeEndDate)) {
                    return Mono.just(new ResponseEntity<>("New Fee start date should not be less than old fee end date", HttpStatus.BAD_REQUEST));
                }
            }
            PendingFee existingPendingFeeWithParams = findPendingApprovalPendingFeeLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
            if (existingPendingFeeWithParams != null) {
                return Mono.just(new ResponseEntity<>("There is a pending fee with the same param, kindly wait for it to be approved", HttpStatus.BAD_REQUEST));
            }
            PendingFee pendingFee = new PendingFee();
            pendingFee.setId(UUID.randomUUID().toString());
            pendingFee.setAmount(Double.valueOf(feeCreateDto.getAmount()));
            pendingFee.setFeePaymentTypeId(feePaymentTypeId);
            pendingFee.setGameTypeId(gameTypeId);
            pendingFee.setActive(true);
            pendingFee.setLicenseTypeId(feeCreateDto.getRevenueNameId());
            pendingFee.setEffectiveDate(new LocalDate(feeCreateDto.getStartDate()));
            if (!StringUtils.isEmpty(feeCreateDto.getEndDate())) {
                pendingFee.setEndDate(new LocalDate(feeCreateDto.getEndDate()));
            }
            mongoRepositoryReactive.saveOrUpdate(pendingFee);

            FeeApprovalRequest feeApprovalRequest = new FeeApprovalRequest();
            feeApprovalRequest.setInitiatorId(loggedInUser.getId());
            feeApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            feeApprovalRequest.setPendingFeeId(pendingFee.getId());
            feeApprovalRequest.setId(UUID.randomUUID().toString());
            feeApprovalRequest.setFeeApprovalRequestTypeId(FeeApprovalRequestTypeReferenceData.CREATE_FEE_ID);
            mongoRepositoryReactive.saveOrUpdate(feeApprovalRequest);
            approvalRequestNotifierAsync.sendNewFeeApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, feeApprovalRequest);

            String currentAuditorName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            String verbiage = String.format("Created Fee Approval Request -> Request Type -> %s, License Type -> %s, FeePaymentType -> %s, Category -> %s, Amount -> %s",
                    feeApprovalRequest.getFeeApprovalRequestType(), pendingFee.getLicenseType(), pendingFee.getFeePaymentType(), pendingFee.getGameType(), pendingFee.getAmount());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(feeAuditActionId,
                    currentAuditorName, currentAuditorName,
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));
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
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not get logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
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
            feeApprovalRequest.setInitiatorId(loggedInUser.getId());
            feeApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            feeApprovalRequest.setFeeApprovalRequestTypeId(FeeApprovalRequestTypeReferenceData.SET_FEE_END_DATE_ID);
            mongoRepositoryReactive.saveOrUpdate(feeApprovalRequest);
            approvalRequestNotifierAsync.sendNewFeeApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, feeApprovalRequest);
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
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));

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
                    true,RequestAddressUtil.getClientIpAddr(request), verbiage));

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
                    true,RequestAddressUtil.getClientIpAddr(request), verbiage));
            return Mono.just(new ResponseEntity<>(licenseType.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating the fee setting";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllFees(String feePaymentTypeId,
                                           String gameTypeId,
                                           String revenueNameId,
                                           int page,
                                           int pageSize,
                                           HttpServletResponse httpServletResponse) {
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

            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, Fee.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort = new Sort(Sort.Direction.DESC, "createdAt");

            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

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
    public Mono<ResponseEntity> getLicenseTypes() {
        return getAllEnumeratedEntity("LicenseType", LicenseType.class);
    }

    @Override
    public Mono<ResponseEntity> getAllFeePaymentType() {
        return getAllEnumeratedEntity("FeePaymentType", FeePaymentType.class);
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
            List<String> feePaymentTypes = new ArrayList<>();
            if (StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, licenseTypeId)
                    || StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, licenseTypeId)) {
                feePaymentTypes.add(FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID);
            }
            if (StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, licenseTypeId)) {
                feePaymentTypes.add(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
                feePaymentTypes.add(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID);
            }

            if (StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, licenseTypeId)) {
                feePaymentTypes.add(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
                feePaymentTypes.add(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID);
                feePaymentTypes.add(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID);
                feePaymentTypes.add(FeePaymentTypeReferenceData.LICENSE_TRANSFER_FEE_TYPE_ID);
            }
            if (feePaymentTypes.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(getFeePaymentTypeDtoFromIds(feePaymentTypes), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding fee payment types for revenue", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findLicenseTypeByParams(String institutionId, String agentId, String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        if (gameType == null) {
            return Mono.just(new ResponseEntity<>(String.format("Category with id %s not found", gameTypeId), HttpStatus.BAD_REQUEST));
        }
        LicenseTypeSearch search = new LicenseTypeSearch(institutionId, agentId);
        try {
            //Source of Failure. Collection type enumeratedFacts is uninitialized from the call to ReferenceDataUtil.getAllEnumeratedFacts("LicenseTypes")
            //Issues is that the Cache did not return the Cached LicenseType as earlier stored in the ConcurrentHashMap instance. BIG ISSUE

            //Collection<EnumeratedFact> enumeratedFacts = ReferenceDataUtil.getAllEnumeratedFacts("LicenseType");
            ArrayList<LicenseType> allLicenseTypes = (ArrayList<LicenseType>)mongoRepositoryReactive.findAll(new Query(),LicenseType.class).toStream().collect(Collectors.toList());
            List<LicenseType> licenseTypes = new ArrayList<LicenseType>();

            List<EnumeratedFactDto> dtos = new ArrayList<>();
            for (EnumeratedFact enumeratedFact : allLicenseTypes) {
                LicenseType licenseType = (LicenseType) enumeratedFact;
                if (search.isAgentSearch() && licenseType.appliesToAgent()) {
                    if (gameType.getAgentLicenseDurationMonths() == 0 && licenseType.isAgent()) {
                        continue;
                    }
                    if (!gameType.getAllowsGamingTerminal() && licenseType.isGamingTerminal()){
                        continue;
                    }
                    dtos.add(licenseType.convertToDto());
                }
                if (search.isInstitutionSearch() && licenseType.appliesToInstitution()) {
                    if (!gameType.getAllowsGamingMachine() && licenseType.isGamingMachine()) {
                        continue;
                    }
                    dtos.add(licenseType.convertToDto());
                }
            }
            if (dtos.isEmpty()) {
                return Mono.just(new ResponseEntity<>(dtos, HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));
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
      //  query.addCriteria(Criteria.where("active").is(true));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
        query.with(new Sort(Sort.Direction.DESC, "effectiveDate"));
        return (Fee) mongoRepositoryReactive.find(query, Fee.class).block();
    }

    private PendingFee findPendingApprovalPendingFeeLicenseTypeGameTypeAndFeePaymentType(String licenseTypeId, String gameTypeId, String feePaymentTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
        query.addCriteria(Criteria.where("approvalRequestStatusId").is(ApprovalRequestStatusReferenceData.PENDING_ID));
        query.with(new Sort(Sort.Direction.DESC, "effectiveDate"));
        return (PendingFee) mongoRepositoryReactive.find(query, PendingFee.class).block();
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


    private List<EnumeratedFactDto> getFeePaymentTypeDtoFromIds(Collection<String> ids) {
        List<EnumeratedFactDto> dtos = new ArrayList<>();
        for (String id : ids) {
            FeePaymentType feePaymentType = getFeePaymentTypeById(id);
            if (feePaymentType != null) {
                dtos.add(feePaymentType.convertToDto());
            }
        }
        return dtos;
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
