package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.dto.AIPCheckDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateAIPToLicenseDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import com.software.finatech.lslb.cms.service.util.ExpirationList;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.SendEmaill;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class LicenseServiceImpl implements LicenseService {
    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    @Autowired
    ExpirationList expirationList;
    @Autowired
    SendEmaill sendEmaill;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;


    @Override
    public Mono<ResponseEntity> findAllLicense(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               String agentId,
                                               String gamingMachineId,
                                               String licenseStatusId,
                                               String gameTypeId,
                                               String paymentRecordId,
                                               String date,
                                               String licenseTypeId, HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(gamingMachineId)) {
                query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            }
            if (!StringUtils.isEmpty(licenseStatusId)) {
                query.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));
            }
            if (!StringUtils.isEmpty(paymentRecordId)) {
                query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(licenseTypeId)) {
                query.addCriteria(Criteria.where("licenseType").is(licenseTypeId));
            }

            if (!StringUtils.isEmpty(date)) {
                LocalDate localDate = new LocalDate(date);
                query.addCriteria(Criteria.where("effectiveDate").lte(localDate).andOperator(Criteria.where("expiryDate").gte(localDate)));
            }

            if (page == 0) {
                if (httpServletResponse != null) {
                    long count = mongoRepositoryReactive.count(query, License.class).block();
                    httpServletResponse.setHeader("TotalCount", String.valueOf(count));
                }
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
            if (licenses.size() == 0 || licenses.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<LicenseDto> licenseDtos = new ArrayList<>();

            licenses.forEach(license -> {
                licenseDtos.add(license.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            String errorMsg = "Invalid date format , please use YYYY-MM-dd";
            return logAndReturnError(logger, errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all licenses";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> findLicense(String licenseId, String institutionId, String agentId, String gamingMachineId, String gameTypeId) {
        LocalDateTime dateTime = new LocalDateTime();
        dateTime = dateTime.plusDays(90);
        Query queryLicence = new Query();

        if (!StringUtils.isEmpty(institutionId) && StringUtils.isEmpty(agentId) &&
                StringUtils.isEmpty(gamingMachineId)) {
            queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryLicence.addCriteria(Criteria.where("licenseType").is("institution"));

        }
        // queryLicence.addCriteria(Criteria.where("firstPayment").is(false));

        if (!StringUtils.isEmpty(agentId) && StringUtils.isEmpty(institutionId)
                && StringUtils.isEmpty(gamingMachineId)) {
            queryLicence.addCriteria(Criteria.where("agentId").is(agentId));
            queryLicence.addCriteria(Criteria.where("licenseType").is("agent"));
        }
        if (!StringUtils.isEmpty(gamingMachineId) && StringUtils.isEmpty(institutionId) && StringUtils.isEmpty(agentId)) {
            queryLicence.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            queryLicence.addCriteria(Criteria.where("licenseType").is("gamingMachine"));

        }
        if (!StringUtils.isEmpty(gamingMachineId) && !StringUtils.isEmpty(institutionId)
                && StringUtils.isEmpty(agentId)) {
            return Mono.just(new ResponseEntity<>("Enter either agentId or gaming machineId, or institutionId", HttpStatus.OK));


        }
        if (!StringUtils.isEmpty(gamingMachineId) && !StringUtils.isEmpty(agentId)
                && StringUtils.isEmpty(institutionId)) {
            return Mono.just(new ResponseEntity<>("Enter either agentId or gaming machineId, or institutionId", HttpStatus.OK));


        }
        if (!StringUtils.isEmpty(agentId) && !StringUtils.isEmpty(institutionId)
                && StringUtils.isEmpty(gamingMachineId)) {
            return Mono.just(new ResponseEntity<>("Enter either agentId or gaming machineId, or institutionId", HttpStatus.OK));


        }
        if (!StringUtils.isEmpty(agentId) && !StringUtils.isEmpty(gamingMachineId)
                && !StringUtils.isEmpty(institutionId)) {
            return Mono.just(new ResponseEntity<>("Enter either agentId or gaming machineId, or institutionId", HttpStatus.OK));

        }


        if (!StringUtils.isEmpty(licenseId)) {
            queryLicence.addCriteria(Criteria.where("id").is(licenseId));
        }

        if (!StringUtils.isEmpty(gameTypeId)) {
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        }

        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryLicence, License.class).toStream().collect(Collectors.toList());
        List<LicenseDto> licenseDtos = new ArrayList<>();
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));

        }
        for (License license : licenses) {

            int days = Days.daysBetween(dateTime, license.getExpiryDate()).getDays();
            if (days > 0) {
                license.setRenewalStatus("true");
            } else {
                license.setRenewalStatus("false");
            }
            licenseDtos.add(license.convertToDto());
        }

        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }

    @Override
    public List<EnumeratedFactDto> getLicenseStatus() {
        Map licenseMap = Mapstore.STORE.get("LicenseStatus");
        ArrayList<LicenseStatus> licenseStatuses = new ArrayList<LicenseStatus>(licenseMap.values());
        List<EnumeratedFactDto> licenseStatusDtoLists = new ArrayList<>();
        licenseStatuses.forEach(factObject -> {
            LicenseStatus licenseStatus = factObject;
            licenseStatusDtoLists.add(licenseStatus.convertToDto());
        });
        return licenseStatusDtoLists;
    }

    @Override
    public Mono<ResponseEntity> getAllLicenseStatus() {
        return Mono.just(new ResponseEntity<>(getLicenseStatus(), HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getExpiringLicenses() {
        List<License> licenses = expirationList.getExpiringLicences(90, LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getExpiringAIPs() {
        List<License> licenses = expirationList.getExpiringLicences(14, LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.BAD_REQUEST));
    }

    @Override
    public Mono<ResponseEntity> getExpiredLicenses() {
        List<License> licenses = expirationList.getExpiredLicences(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.BAD_REQUEST));
    }

    @Override
    public Mono<ResponseEntity> getExpiredAIPs() {
        List<License> licenses = expirationList.getExpiredLicences(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.BAD_REQUEST));
    }

    @Override
    public boolean institutionIsLicensedForGameType(String institutionId, String gameTypeId) {
        Query queryForLicensedInstitutionInGameType = new Query();
        LocalDate today = LocalDate.now();
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("effectiveDate").lte(today).andOperator(Criteria.where("expiryDate").gte(today)));
        License licenseForInstitutionAndGameType = (License) mongoRepositoryReactive.find(queryForLicensedInstitutionInGameType, License.class).block();
        if (licenseForInstitutionAndGameType == null) {
            return false;
        } else {
            return StringUtils.equals(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID, licenseForInstitutionAndGameType.getLicenseStatusId());
        }
    }


    @Override
    public Mono<ResponseEntity> getInstitutionAIPs(String institutionId) {
        Query queryForInstitutionAIP = new Query();
        queryForInstitutionAIP.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID));
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseType").is(LicenseTypeReferenceData.INSTITUTION));
        List<License> aipsForInstitution = (List<License>) mongoRepositoryReactive.findAll(queryForInstitutionAIP, License.class).toStream().collect(Collectors.toList());
        ArrayList<AIPCheckDto> aipCheckDtos = new ArrayList<>();
        if (aipsForInstitution.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }
        aipsForInstitution.stream().forEach(aipForInstitution -> {
            AIPCheckDto aipCheckDto = new AIPCheckDto();
            GameType gameType = (GameType) mongoRepositoryReactive.findById(aipForInstitution.getGameTypeId(), GameType.class).block();
            if (gameType != null) {
                aipCheckDto.setGameType(gameType.convertToDto());
            }
            aipCheckDto.setLicensedId(aipForInstitution.getId());
            aipCheckDto.setLicenseStatusId(aipForInstitution.getLicenseStatusId());
            aipCheckDtos.add(aipCheckDto);

        });

        return Mono.just(new ResponseEntity<>(aipCheckDtos, HttpStatus.OK));


    }

    @Override
    public Mono<ResponseEntity> updateToDocumentAIP(String licenseId) {
        try {
            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("id").is(licenseId));
            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();

            if (license == null) {
                return Mono.just(new ResponseEntity<>("No Record Record", HttpStatus.BAD_REQUEST));
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(license);
            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Hey Something Has Broken", HttpStatus.BAD_REQUEST));

        }
    }


    @Override
    public Mono<ResponseEntity> updateAIPDocToLicense(LicenseUpdateAIPToLicenseDto licenseUpdateDto) {
        try {

            LocalDate fromDate;

            if ((licenseUpdateDto.getStartDate() != "" && !licenseUpdateDto.getStartDate().isEmpty())) {
                if (!licenseUpdateDto.getStartDate().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                    return Mono.just(new ResponseEntity("Invalid Date format. " +
                            "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
                }
                fromDate = new LocalDate(licenseUpdateDto.getStartDate());

            } else {

                return Mono.just(new ResponseEntity("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
            }

            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("institutionId").is(licenseUpdateDto.getInstitutionId()));
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(licenseUpdateDto.getGameTypeId()));
            queryLicence.addCriteria(Criteria.where("licenseType").is(LicenseTypeReferenceData.INSTITUTION));
            queryLicence.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID));
            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();
            if (license == null) {
                return Mono.just(new ResponseEntity<>("Operator have not uploaded AIP document", HttpStatus.BAD_REQUEST));

            }
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setEffectiveDate(fromDate);
            Query queryGameType = new Query();
            queryGameType.addCriteria(Criteria.where("id").is(license.getGameTypeId()));
            GameType gameType = (GameType) mongoRepositoryReactive.find(queryGameType, GameType.class).block();
            int duration = gameType.getInstitutionLicenseDurationMonths();
            license.setExpiryDate(fromDate.plusMonths(duration));
            license.setRenewalStatus("false");
            mongoRepositoryReactive.saveOrUpdate(license);

            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Hey Something Has Broken", HttpStatus.BAD_REQUEST));

        }
    }


    @Override
    public void createAIPLicenseForCompletedPayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                return;
            }
            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getAipDurationMonths());
            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setPaymentRecordId(paymentRecord.getId());
            license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            mongoRepositoryReactive.saveOrUpdate(license);
        } catch (Exception e) {
            logger.error("An error occurred while creating AIP license for institution {}", paymentRecord.getInstitutionId(), e);
        }
    }

    @Override
    public void createFirstLicenseForAgentPayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                return;
            }
            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getAgentLicenseDurationMonths());
            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setAgentId(paymentRecord.getAgentId());
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.AGENT);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setPaymentRecordId(paymentRecord.getId());
            license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            mongoRepositoryReactive.saveOrUpdate(license);
        } catch (Exception e) {
            logger.error("An error occurred while creating initial license for agent {}", paymentRecord.getAgentId(), e);
        }
    }

    @Override
    public void createFirstLicenseForGamingMachinePayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                return;
            }

            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths());
            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setGamingMachineId(paymentRecord.getGamingMachineId());
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_MACHINE);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setPaymentRecordId(paymentRecord.getId());
            license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            mongoRepositoryReactive.saveOrUpdate(license);
        } catch (Exception e) {
            logger.error("An error occurred while creating initial license for gaming machine {}", paymentRecord.getGamingMachineId(), e);
        }
    }

    @Override
    public void createRenewedLicenseForPayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                return;
            }
            GameType gameType = paymentRecord.getGameType();
            String institutionId = paymentRecord.getInstitutionId();
            String agentId = paymentRecord.getAgentId();
            String gamingMachineId = paymentRecord.getGamingMachineId();
            String gameTypeId = paymentRecord.getGameTypeId();

            List<License> licenseList = getPreviousConfirmedLicenses(institutionId, agentId, gamingMachineId, gameTypeId);
            if (licenseList == null || licenseList.isEmpty()) {
                return;
            }

            License latestLicense = licenseList.get(0);
            LocalDate newLicenseStartDate = latestLicense.getExpiryDate();
            LocalDate newLicenseEndDate = getNewLicenseEndDate(latestLicense, gameType);

            License newPendingApprovalRenewedLicense = new License();
            newPendingApprovalRenewedLicense.setId(UUID.randomUUID().toString());
            newPendingApprovalRenewedLicense.setGamingMachineId(gamingMachineId);
            newPendingApprovalRenewedLicense.setInstitutionId(institutionId);
            newPendingApprovalRenewedLicense.setAgentId(agentId);
            newPendingApprovalRenewedLicense.setEffectiveDate(newLicenseStartDate);
            newPendingApprovalRenewedLicense.setExpiryDate(newLicenseEndDate);
            newPendingApprovalRenewedLicense.setPaymentRecordId(paymentRecord.getId());
            newPendingApprovalRenewedLicense.setLicenseStatusId(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
            newPendingApprovalRenewedLicense.setLicenseTypeId(getLicenseTypeFromRevenueNameId(paymentRecord.getRevenueNameId()));
            newPendingApprovalRenewedLicense.setGameTypeId(paymentRecord.getGameTypeId());
            newPendingApprovalRenewedLicense.setParentLicenseId(latestLicense.getId());
            newPendingApprovalRenewedLicense.setLicenseNumber(latestLicense.getLicenseNumber());
            mongoRepositoryReactive.saveOrUpdate(newPendingApprovalRenewedLicense);
        } catch (Exception e) {
            logger.error("An error occurred while creating renewed license for gaming machine {}", paymentRecord.getGamingMachineId(), e);
        }
    }

    private LocalDate getNewLicenseEndDate(License latestLicense, GameType gameType) throws Exception {
        LocalDate newLicenseStartDate = latestLicense.getExpiryDate();
        switch (latestLicense.getLicenseTypeId()) {
            case LicenseTypeReferenceData.AGENT:
                return newLicenseStartDate.plusMonths(gameType.getAgentLicenseDurationMonths());
            case LicenseTypeReferenceData.INSTITUTION:
                return newLicenseStartDate.plusMonths(gameType.getInstitutionLicenseDurationMonths());
            case LicenseTypeReferenceData.GAMING_MACHINE:
                return newLicenseStartDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths());
            default:
                throw new Exception();
        }
    }

    private String getLicenseTypeFromRevenueNameId(String revenueNameId) {
        switch (revenueNameId) {
            case RevenueNameReferenceData.AGENT_REVENUE_CODE:
                return LicenseTypeReferenceData.AGENT;
            case RevenueNameReferenceData.GAMING_MACHINE_CODE:
                return LicenseTypeReferenceData.GAMING_MACHINE;
            case RevenueNameReferenceData.INSTITUTION_REVENUE_CODE:
                return LicenseTypeReferenceData.INSTITUTION;
            default:
                return null;
        }
    }

    public List<License> getPreviousConfirmedLicenses(String institutionId,
                                                      String agentId,
                                                      String gamingMachineId,
                                                      String gameTypeId) {
        Query query = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        if (!StringUtils.isEmpty(agentId)) {
            query.addCriteria(Criteria.where("agentId").is(agentId));
        }
        if (!StringUtils.isEmpty(gamingMachineId)) {
            query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
        }
        if (!StringUtils.isEmpty(gameTypeId)) {
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        }

        List<String> licenseStatuIds = new ArrayList<>();
        licenseStatuIds.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        licenseStatuIds.add(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
        query.addCriteria(Criteria.where("licenseStatusId").in(licenseStatuIds));
        Sort sort = new Sort(Sort.Direction.DESC, "effectiveDate");

        query.with(PageRequest.of(0, 10000, sort));
        query.with(sort);

        return (List<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
    }

    private String generateLicenseNumberForPaymentRecord(PaymentRecord paymentRecord) {
        String revenueNameId = paymentRecord.getRevenueNameId();
        String prefix = "LSLB-";
        switch (revenueNameId) {
            case RevenueNameReferenceData.AGENT_REVENUE_CODE:
                prefix = prefix + "AG-";
            case RevenueNameReferenceData.GAMING_MACHINE_CODE:
                prefix = prefix + "GM-";
            case RevenueNameReferenceData.INSTITUTION_REVENUE_CODE:
                prefix = prefix + "OP-";
            default:
        }
        String randomDigit = String.valueOf(getRandomNumberInRange(10000, 1000000));
        return String.format("%s%s", prefix, randomDigit);
    }

    private int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}