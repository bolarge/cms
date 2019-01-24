package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.AIPCheckDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseRequestDto;
import com.software.finatech.lslb.cms.service.dto.NotificationDto;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import com.software.finatech.lslb.cms.service.util.*;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.AIPMailSenderAsync;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.LoggedCaseMailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
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

import static com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID;
import static com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData.getAllowedLicensedStatusIds;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class LicenseServiceImpl implements LicenseService {
    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    @Autowired
    ExpirationList expirationList;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AuditLogHelper auditLogHelper;
    @Autowired
    protected SpringSecurityAuditorAware springSecurityAuditorAware;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    SendEmail sendEmail;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private AIPMailSenderAsync aipMailSenderAsync;

    @Autowired
    private AuthInfoServiceImpl authInfoService;

    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private GameTypeService gameTypeService;


    @Autowired
    private FrontEndPropertyHelper frontEndPropertyHelper;

    @Autowired
    private LoggedCaseMailSenderAsync loggedCaseMailSenderAsync;

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
                                               String licenseNumber,
                                               String licenseTypeId,
                                               String startDate,
                                               String endDate,
                                               String dateProperty,
                                               HttpServletResponse httpServletResponse) {

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
                query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
            }
            if (!StringUtils.isEmpty(licenseNumber)) {
                query.addCriteria(Criteria.where("licenseNumber").is(licenseNumber));
            }
            if (!StringUtils.isEmpty(date)) {
                LocalDate localDate = new LocalDate(date);
                query.addCriteria(Criteria.where("effectiveDate").lte(localDate).andOperator(Criteria.where("expiryDate").gte(localDate)));
            }
            QueryUtils.addDateToQuery(query, startDate, endDate, dateProperty);
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
                sort = new Sort(Sort.Direction.DESC, "createdAt");
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
    public Mono<ResponseEntity> getAllLicenseTypes() {
        return ReferenceDataUtil.getAllEnumeratedEntity("LicenseTypes", LicenseType.class);
    }

    @Override
    public Mono<ResponseEntity> getAllLicenseStatus() {
        return ReferenceDataUtil.getAllEnumeratedEntity("LicenseStatus", LicenseStatus.class);
    }

    @Override
    public Mono<ResponseEntity> getExpiringLicenses() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        List<License> licenses = expirationList.getExpiringLicences(90, licenseStatuses);
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getExpiringAIPs() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        List<License> licenses = expirationList.getExpiringLicences(14, licenseStatuses);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getExpiredLicenses() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);

        List<License> licenses = expirationList.getExpiredLicences(licenseStatuses);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getExpiredAIPs() {
        ArrayList<String> licenseStatuses = new ArrayList<>();
        licenseStatuses.add(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
        licenseStatuses.add(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
        List<License> licenses = expirationList.getExpiredLicences(licenseStatuses);
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
    public Mono<ResponseEntity> getInstitutionCloseToExpirationLicenses(String institutionId) {
        Query queryForLicensedInstitutionInGameType = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
//        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
//        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("renewalStatus").is("true"));
        LocalDate dateTime = LocalDate.now();
        dateTime = dateTime.plusDays(90);
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("expiryDate").lte(dateTime));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseStatusId").in(Arrays.asList(LICENSED_LICENSE_STATUS_ID, LicenseStatusReferenceData.RENEWED_ID, LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID)));

        ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(queryForLicensedInstitutionInGameType, License.class).toStream().collect(Collectors.toList());
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }
        List<LicenseDto> licenseDtos = new ArrayList<>();
        for (License license : licenses) {
            if (!StringUtils.equals("true", license.getRenewalStatus())) {
                license.setRenewalStatus("true");
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(license.getInstitutionId()));
            query.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
            query.addCriteria(Criteria.where("licenseStatusId").ne(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID));
            Sort sort = new Sort(Sort.Direction.DESC, "expiryDate");
            query.with(sort);
            License mostRecentLicense = (License) mongoRepositoryReactive.find(query, License.class).block();
            if (mostRecentLicense != null && StringUtils.equals(mostRecentLicense.getId(), license.getId())) {
                licenseDtos.add(license.convertToDto());
                continue;
            }
            if (mostRecentLicense != null &&
                    mostRecentLicense.getExpiryDate() != null &&
                    mostRecentLicense.getExpiryDate().isBefore(dateTime)) {
                licenseDtos.add(license.convertToDto());
            }
        }

        return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getAgentLicensesCloseToExpiration(String agentId) {
        Query queryForLicensedAgentInGameType = new Query();
        if (!StringUtils.isEmpty(agentId)) {
            queryForLicensedAgentInGameType.addCriteria(Criteria.where("agentId").is(agentId));
        }
        queryForLicensedAgentInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.AGENT_ID));
        LocalDateTime dateTime = new LocalDateTime();
        dateTime = dateTime.plusDays(90);
        queryForLicensedAgentInGameType.addCriteria(Criteria.where("expiryDate").lt(dateTime));

        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryForLicensedAgentInGameType, License.class).toStream().collect(Collectors.toList());
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
    public Mono<ResponseEntity> getGamingMachineLicensesCloseToExpiration(String gamingMachineId) {
        Query queryForLicensedAgentInGameType = new Query();
        if (!StringUtils.isEmpty(gamingMachineId)) {
            queryForLicensedAgentInGameType.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
        }
        queryForLicensedAgentInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_MACHINE_ID));
        LocalDateTime dateTime = new LocalDateTime();
        dateTime = dateTime.plusDays(90);
        queryForLicensedAgentInGameType.addCriteria(Criteria.where("expiryDate").lt(dateTime));

        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryForLicensedAgentInGameType, License.class).toStream().collect(Collectors.toList());
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
    public Mono<ResponseEntity> getLicensesInRenewalInReview(String institutionId) {
        Query queryForLicensedInstitutionInGameType = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_LICENSE_IN_REVIEW));
        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryForLicensedInstitutionInGameType, License.class).toStream().collect(Collectors.toList());
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
    public boolean institutionIsLicensedForGameType(String institutionId, String gameTypeId) {
        List<String> allowedLicenseStatusIds = getAllowedLicensedStatusIds();
        Query queryForLicensedInstitutionInGameType = new Query();
        LocalDate today = LocalDate.now();
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseStatusId").in(allowedLicenseStatusIds));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
        queryForLicensedInstitutionInGameType.addCriteria(new Criteria().andOperator(Criteria.where("effectiveDate").lte(today), (Criteria.where("expiryDate").gte(today))));
        License licenseForInstitutionAndGameType = (License) mongoRepositoryReactive.find(queryForLicensedInstitutionInGameType, License.class).block();
        return licenseForInstitutionAndGameType != null;
    }

    @Override
    public Mono<ResponseEntity> getInstitutionAIPs(String institutionId) {
        //        , int page, int pageSize, String sortType, String sortParam, HttpServletResponse httpServletResponse) {
        Query queryForInstitutionAIP = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            queryForInstitutionAIP.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
//        queryForInstitutionAIP.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID)
//                .orOperator(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSE_RUNNING)));
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSE_RUNNING),
                Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID));
        queryForInstitutionAIP.addCriteria(criteria);
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));

//        if (page == 0) {
//            if (httpServletResponse != null) {
//                long count = mongoRepositoryReactive.count(queryForInstitutionAIP, License.class).block();
//                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
//            }
//        }
//
//        Sort sort;
//        if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
//            sort = new Sort((sortType.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC),
//                    sortParam);
//        } else {
//            sort = new Sort(Sort.Direction.DESC, "createdAt");
//        }
//        queryForInstitutionAIP.with(PageRequest.of(page, pageSize, sort));
//        queryForInstitutionAIP.with(sort);


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
            aipCheckDto.setInstitutionId(aipForInstitution.getInstitutionId());
            aipCheckDto.setInstitutionName(aipForInstitution.getInstitution().convertToDto().getInstitutionName());
            aipCheckDto.setLicensedId(aipForInstitution.getId());
            aipCheckDto.setLicenseStatusId(aipForInstitution.getLicenseStatusId());
            aipCheckDtos.add(aipCheckDto);

        });

        return Mono.just(new ResponseEntity<>(aipCheckDtos, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> getInstitutionAIPUploaded(String institutionId, int page, int pageSize, String sortType, String sortParam, HttpServletResponse httpServletResponse) {
        Query queryForInstitutionAIP = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            queryForInstitutionAIP.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        if (page == 0) {
            if (httpServletResponse != null) {
                long count = mongoRepositoryReactive.count(queryForInstitutionAIP, License.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }
        }

        Sort sort;
        if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
            sort = new Sort((sortType.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC),
                    sortParam);
        } else {
            sort = new Sort(Sort.Direction.DESC, "createdAt");
        }
        queryForInstitutionAIP.with(PageRequest.of(page, pageSize, sort));
        queryForInstitutionAIP.with(sort);

        queryForInstitutionAIP.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID));
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
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
            Query aipFormQuery = new Query();
            aipFormQuery.addCriteria(Criteria.where("institutionId").is(aipForInstitution.getInstitutionId()));
            aipFormQuery.addCriteria(Criteria.where("gameTypeId").is(aipForInstitution.getGameTypeId()));
            AIPDocumentApproval aipDocumentApproval = (AIPDocumentApproval) mongoRepositoryReactive.find(aipFormQuery, AIPDocumentApproval.class).block();
            if (aipDocumentApproval != null) {
                aipCheckDto.setAipFormId(aipDocumentApproval.getId());
            }
            aipCheckDto.setInstitutionId(aipForInstitution.getInstitutionId());
            aipCheckDto.setInstitutionName(aipForInstitution.getInstitution().convertToDto().getInstitutionName());
            aipCheckDto.setLicensedId(aipForInstitution.getId());
            aipCheckDto.setLicenseStatusId(aipForInstitution.getLicenseStatusId());

            aipCheckDtos.add(aipCheckDto);

        });

        return Mono.just(new ResponseEntity<>(aipCheckDtos, HttpStatus.OK));
    }

    public Mono<ResponseEntity> updateToDocumentAIP(License license) {
        try {
            if (license == null) {
                return Mono.just(new ResponseEntity<>("No Record Record", HttpStatus.BAD_REQUEST));
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(license);
            List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AIP_ID);
            if (lslbAdmins.size() != 0) {
                lslbAdmins.forEach(lslbAdmin -> {
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
                    notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
                    notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ",  has uploaded " +
                            notificationDto.getGameType() + " AIP Documents.");
                    notificationDto.setTemplate("AIPUpdate");
                    notificationDto.setCallBackUrl(frontEndPropertyHelper.getFrontEndUrl() + "/all-aips");
                    notificationDto.setInstitutionEmail(lslbAdmin.getEmailAddress());
                    sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
                });
            }
            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));
        } catch (Throwable ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }


    public Mono<ResponseEntity> updateFromAIPDocToAIP(String institutionId, String gameTypeId) {
        try {

            //TODO: get all lslb admins that can recieve notification
            ArrayList<AuthInfo> lslbAdmins = new ArrayList<>();
            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();

            if (license == null) {
                return Mono.just(new ResponseEntity<>("No Record Record", HttpStatus.BAD_REQUEST));
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(license);
            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Throwable ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }


    public String updateInReviewToLicense(String paymentRecordId) {
        try {
            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            queryLicence.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_LICENSE_IN_REVIEW));
            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();

            if (license == null) {
                return "No License Record";
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.RENEWED_ID);
            license.setRenewalStatus("false");
            mongoRepositoryReactive.saveOrUpdate(license);
            List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(license.getInstitutionId());
            institutionAdmins.stream().forEach(institutionAdmin -> {
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
                notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
                notificationDto.setTemplate("LicenseUpdate");
                notificationDto.setDescription("Renewal application for " +
                        notificationDto.getGameType() + " has been approved. License is valid from " + license.getEffectiveDate().toString("dd/MM/YYY") + " to " +
                        notificationDto.getEndDate());
                notificationDto.setInstitutionEmail(institutionAdmin.getEmailAddress());
                sendEmail.sendEmailRenewalNotification(notificationDto, "Licence Renewal Notification");
            });

            return "OK";

        } catch (Throwable ex) {
            return "Error! Please contact admin";

        }

    }

    @Override
    public Mono<ResponseEntity> updateRenewalLicenseToReview(String paymentRecordId) {
        try {
            Query queryLicenceStatus = new Query();
            queryLicenceStatus.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            queryLicenceStatus.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID));
            License license = (License) mongoRepositoryReactive.find(queryLicenceStatus, License.class).block();
            if (license == null || !license.getLicenseStatusId().equals(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID)) {
                return Mono.just(new ResponseEntity<>("Invalid payment record", HttpStatus.BAD_REQUEST));
            }
            Query queryRenewalStatus = new Query();
            queryRenewalStatus.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.find(queryRenewalStatus, RenewalForm.class).block();
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.SUBMITTED);
            mongoRepositoryReactive.saveOrUpdate(renewalForm);

            license.setRenewalStatus("false");

            license.setLicenseStatusId(LicenseStatusReferenceData.RENEWAL_LICENSE_IN_REVIEW);
            mongoRepositoryReactive.saveOrUpdate(license);
            List<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_AIP_ID);
            if (lslbAdmins.size() != 0) {
                lslbAdmins.stream().forEach(lslbAdmin -> {

                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
                    notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
                    notificationDto.setTemplate("LicenseUpdate");
                    notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ",  has submitted renewal application and uploaded the requested documents for " +
                            notificationDto.getGameType());
                    //@TODO: Send email to lslb admin with permission
                    notificationDto.setInstitutionEmail(lslbAdmin.getEmailAddress());
                    sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
                });
            }
            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Throwable ex) {
            return Mono.just(new ResponseEntity<>("Error while moving to renewal license in review", HttpStatus.BAD_REQUEST));

        }
    }

    //
    public Mono<ResponseEntity> updateRenewalReviewToInProgress(RenewalForm renewalForm) {
        try {
            String verbiage;
            Query queryLicenceStatus = new Query();
            queryLicenceStatus.addCriteria(Criteria.where("paymentRecordId").is(renewalForm.getPaymentRecordId()));
            queryLicenceStatus.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_LICENSE_IN_REVIEW));
            License license = (License) mongoRepositoryReactive.find(queryLicenceStatus, License.class).block();
            if (license == null) {
                return Mono.just(new ResponseEntity<>("Invalid payment record", HttpStatus.BAD_REQUEST));
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(license);
            verbiage = "Moved : " + getInstitution(license.getInstitutionId()).getInstitutionName() + " license status from Renewal In Review back to Renewal In Progress";
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.RENEWAL_ID,
                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
//            List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(license.getInstitutionId());
//            institutionAdmins.stream().forEach(institutionAdmin -> {
//                NotificationDto notificationDto = new NotificationDto();
//                notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
//                notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
//                notificationDto.setTemplate("LicenseUpdate");
//                notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ", " + renewalForm.getComment()
//                );
//                notificationDto.setInstitutionEmail(institutionAdmin.getEmailAddress());
//                sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
//            });

            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));
        } catch (Throwable ex) {
            return Mono.just(new ResponseEntity<>("Error while moving to renewal license in progress", HttpStatus.BAD_REQUEST));

        }
    }

    public Mono<ResponseEntity> updateAIPDocToLicense(String institutionId, String gameTypeId) {
        try {
            Query queryAIPFormApproval = new Query();
            queryAIPFormApproval.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryAIPFormApproval.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            queryAIPFormApproval.addCriteria(Criteria.where("formStatusId").is(ApplicationFormStatusReferenceData.APPROVED_STATUS_ID));
            AIPDocumentApproval aipDocumentApproval = (AIPDocumentApproval) mongoRepositoryReactive.find(queryAIPFormApproval, AIPDocumentApproval.class).block();
            if (aipDocumentApproval == null) {
                return Mono.just(new ResponseEntity<>("AIP FORM NOT APPROVED", HttpStatus.BAD_REQUEST));
            }
            String verbiage;
            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            queryLicence.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSE_RUNNING),
                    Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID));
            queryLicence.addCriteria(criteria);

            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();
            if (license == null) {
                return Mono.just(new ResponseEntity<>("Operator has not uploaded AIP document", HttpStatus.BAD_REQUEST));

            }
            // LicenseTransfer licenseTransfer = license.getLicenseTransfer();
            License createLicense = new License();
            String licenseNumber = "";

            Query queryGameType = new Query();

            queryGameType.addCriteria(Criteria.where("id").is(license.getGameTypeId()));
            GameType gameType = (GameType) mongoRepositoryReactive.find(queryGameType, GameType.class).block();
            int duration = gameType.getInstitutionLicenseDurationMonths();


            if (!StringUtils.isEmpty(license.getLicenseTransferId())) {
                license.setLicenseStatusId(LicenseStatusReferenceData.AIP_COMPLETED);
                mongoRepositoryReactive.saveOrUpdate(license);
                return Mono.just(new ResponseEntity<>("Updated Successfully", HttpStatus.OK));
                /**
                 License transferorLicense = licenseTransfer.getLicense();
                 transferorLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_TRANSFERED);
                 mongoRepositoryReactive.saveOrUpdate(transferorLicense);

                 createLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
                 licenseNumber = transferorLicense.getLicenseNumber();
                 createLicense.setLicenseNumber(licenseNumber);
                 createLicense.setExpiryDate(transferorLicense.getExpiryDate());
                 createLicense.setEffectiveDate(LocalDate.now());
                 createLicense.setPaymentRecordId(license.getPaymentRecordId());
                 createLicense.setRenewalStatus("false");
                 createLicense.setInstitutionId(license.getInstitutionId());
                 createLicense.setGameTypeId(license.getGameTypeId());
                 createLicense.setParentLicenseId(license.getId());
                 createLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);

                 */
            } else {

                license.setLicenseStatusId(LicenseStatusReferenceData.AIP_COMPLETED);
             /* int days_diff = 0;
            LocalDate licenseEndDate = LocalDate.now();
            if (license.getExpiryDate().isAfter(LocalDate.now())) {
                days_diff = Days.daysBetween(LocalDate.now(), license.getExpiryDate()).getDays();
                licenseEndDate = licenseEndDate.plusMonths(duration);
                licenseEndDate = licenseEndDate.plusDays(days_diff);
            } else {*/
                LocalDate licenseEndDate = license.getExpiryDate().plusMonths(duration);
                // }
                PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.findById(license.getPaymentRecordId(), PaymentRecord.class).block();
                if (paymentRecord != null) {
                    licenseNumber = generateLicenseNumberForPaymentRecord(paymentRecord);
                } else {
                    licenseNumber = generateLicenseNumberForOperator(license.getGameTypeId());
                }
                createLicense.setLicenseNumber(licenseNumber);
                createLicense.setId(UUID.randomUUID().toString());
                createLicense.setEffectiveDate(license.getExpiryDate().plusDays(1));
                createLicense.setExpiryDate(licenseEndDate);
                createLicense.setRenewalStatus("false");
                createLicense.setInstitutionId(license.getInstitutionId());
                createLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
                createLicense.setGameTypeId(license.getGameTypeId());
                createLicense.setParentLicenseId(license.getId());
                createLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
            }
            mongoRepositoryReactive.saveOrUpdate(license);
            verbiage = "UPDATED : " + getInstitution(license.getInstitutionId()).getInstitutionName() + " license status from AIP DOC UPLOADED to AIP COMPLETED";
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.AIP_ID,
                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            Institution institution = getInstitution(institutionId);
            if (institution.isFromLiveData() && license.getExpiryDate().isBefore(LocalDate.now())) {
                generateLegacyLicenses(license, duration);

            } else {
                mongoRepositoryReactive.saveOrUpdate(createLicense);

            }
            verbiage = getInstitution(license.getInstitutionId()).getInstitutionName() + " is Licensed ";
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.LICENCE_ID,
                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
            notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
            notificationDto.setTemplate("LicenseUpdate");
            notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ",  License for " +
                    notificationDto.getGameType() + " has been approved.\n License Number is: " + licenseNumber + ". \nKindly pick up the original hard copy of this license at LSLB Office.");

            ArrayList<AuthInfo> authInfos = authInfoService.getAllActiveGamingOperatorUsersForInstitution(license.getInstitutionId());
            for (AuthInfo authInfo : authInfos) {
                notificationDto.setInstitutionEmail(authInfo.getEmailAddress());
                sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
            }
            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Throwable ex) {
            return Mono.just(new ResponseEntity<>("Error occurred. Please contact admin", HttpStatus.BAD_REQUEST));
        }
    }

    public Institution getInstitution(String institutionId) {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public GameType getGameType(String gameTypeId) {
        return (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
    }


    @Override
    public License findRenewalLicense(String institutionId, String agentId, String gamingMachineId, String gameTypeId, String licenseTypeId) {
        Query queryLicense = new Query();
        if (!StringUtils.isEmpty(agentId)) {
            queryLicense.addCriteria(Criteria.where("agentId").is(agentId));
        }
        if (!StringUtils.isEmpty(gamingMachineId)) {
            queryLicense.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
        }
        if (!StringUtils.isEmpty(institutionId)) {
            queryLicense.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        queryLicense.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryLicense.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        queryLicense.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID));

        License licenses = (License) mongoRepositoryReactive.find(queryLicense, License.class).block();
        if (licenses != null) {
            Query queryRenewal = new Query();
            queryRenewal.addCriteria(Criteria.where("paymentRecordId").is(licenses.getPaymentRecordId()));
            RenewalForm renewalFormCheck = (RenewalForm) mongoRepositoryReactive.find(queryRenewal, RenewalForm.class).block();
            if (renewalFormCheck == null) {
                return licenses;
            }
        }

        return null;

    }


    @Override
    public void createAIPLicenseForCompletedPayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                logger.info("payment record with id {} is not completed, skipping creation of AIP institution", paymentRecord.getId());
                return;
            }

            Institution paymentInitiatingInstitution = paymentRecord.getInstitution();
            if (paymentRecord.isLicenseTransferPayment()) {
                LicenseTransfer licenseTransfer = paymentRecord.getLicenseTransfer();
                licenseTransfer.setPaymentRecordId(paymentRecord.getId());
                License transferredLicense = licenseTransfer.getLicense();
                transferredLicense.setLicenseTransferId(licenseTransfer.getId());

                License newLicenseForTransferee = new License();

                newLicenseForTransferee.setId(UUID.randomUUID().toString());
                newLicenseForTransferee.setLicenseStatusId(transferredLicense.getLicenseStatusId());
                newLicenseForTransferee.setRenewalStatus(transferredLicense.getRenewalStatus());
                newLicenseForTransferee.setGameTypeId(transferredLicense.getGameTypeId());
                newLicenseForTransferee.setLicenseNumber(transferredLicense.getLicenseNumber());
                newLicenseForTransferee.setInstitutionId(paymentRecord.getInstitutionId());
                newLicenseForTransferee.setExpiryDate(transferredLicense.getExpiryDate());
                newLicenseForTransferee.setEffectiveDate(transferredLicense.getEffectiveDate());
                newLicenseForTransferee.setLicenseTypeId(transferredLicense.getLicenseTypeId());
                newLicenseForTransferee.setPaymentRecordId(paymentRecord.getId());
                newLicenseForTransferee.setParentLicenseId(transferredLicense.getId());
                newLicenseForTransferee.setLicenseStatusId(transferredLicense.getLicenseStatusId());

                transferredLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_TRANSFERRED);

//                transferredLicense.setInstitutionId(paymentRecord.getInstitutionId());
                //             transferredLicense.setPaymentRecordId(paymentRecord.getId());
                mongoRepositoryReactive.saveOrUpdate(transferredLicense);
                mongoRepositoryReactive.saveOrUpdate(licenseTransfer);
                mongoRepositoryReactive.saveOrUpdate(newLicenseForTransferee);


                //expire all operators terminals and machines
                institutionService.expireAllOperatorTerminalsAndMachines(licenseTransfer);
                String verbiage = String.format("Transferred License Number -> %s , Category ->%s, Transferor -> %s, Transfereree -> %s",
                        transferredLicense.getLicenseNumber(), transferredLicense.getGameType(), licenseTransfer.getFromInstitution(), licenseTransfer.getToInstitution());
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.LICENCE_ID, "System Admin",
                        String.valueOf(licenseTransfer.getToInstitution()), LocalDateTime.now(), LocalDate.now(), true, null, verbiage));
            }

            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getAipDurationMonths()).minusDays(1);
            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setLicenseTransferId(paymentRecord.getLicenseTransferId());
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setPaymentRecordId(paymentRecord.getId());
            //     license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            mongoRepositoryReactive.saveOrUpdate(license);
            String tradeName = getApprovedApplicationTradeNameForOperator(paymentRecord.getInstitutionId(), paymentRecord.getGameTypeId());
            if (tradeName != null) {
                InstitutionCategoryDetails institutionCategoryDetails = institutionService.findInstitutionCategoryDetailsByInstitutionIdAndGameTypeId(paymentRecord.getInstitutionId(), paymentRecord.getGameTypeId());
                if (institutionCategoryDetails == null) {
                    institutionCategoryDetails = new InstitutionCategoryDetails();
                    institutionCategoryDetails.setId(UUID.randomUUID().toString());
                    institutionCategoryDetails.setInstitutionId(paymentRecord.getInstitutionId());
                    institutionCategoryDetails.setGameTypeId(gameType.getId());
                    paymentInitiatingInstitution.getInstitutionCategoryDetailIds().add(institutionCategoryDetails.getId());
                }
                institutionCategoryDetails.setTradeName(tradeName);
                institutionCategoryDetails.setFirstCommencementDate(LocalDate.now());
                mongoRepositoryReactive.saveOrUpdate(institutionCategoryDetails);
                mongoRepositoryReactive.saveOrUpdate(paymentInitiatingInstitution);
            }
//           String verbiage = "Moved : " + getInstitution(license.getInstitutionId()).getInstitutionName() + " license status to AIP";
//            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.AIP_ID,
//                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
//                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            AIPDocumentApproval aipDocumentApproval = new AIPDocumentApproval();
            aipDocumentApproval.setFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
            aipDocumentApproval.setGameTypeId(license.getGameTypeId());
            aipDocumentApproval.setInstitutionId(license.getInstitutionId());
            aipDocumentApproval.setId(UUID.randomUUID().toString());
            aipDocumentApproval.setReadyForApproval(false);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);

            String verbiage = String.format("Created application form : %s ->  Category :%s",
                    aipDocumentApproval.getFormStatusId(), aipDocumentApproval.getGameTypeName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.AIP_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), aipDocumentApproval.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            aipMailSenderAsync.sendAipNotificationToInstitutionAdmins(paymentRecord);
            paymentRecord.setLicenseId(license.getId());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        } catch (Exception e) {
            logger.error("An error occurred while creating AIP license for institution {}", paymentRecord.getInstitutionId(), e);
        }
    }

    @Override
    public void createFirstLicenseForAgentPayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                logger.info("payment record with id {} is not completed, skipping creation of License for agent", paymentRecord.getId());
                return;
            }
            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getAgentLicenseDurationMonths()).minusDays(1);
            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setAgentId(paymentRecord.getAgentId());
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.AGENT_ID);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setPaymentRecordId(paymentRecord.getId());
            license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            mongoRepositoryReactive.saveOrUpdate(license);
            paymentRecord.setLicenseId(license.getId());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        } catch (Exception e) {
            logger.error("An error occurred while creating initial license for agent {}", paymentRecord.getAgentId(), e);
        }
    }

    @Override
    public void createLicenseForGamingMachinePayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                logger.info("payment record with id {} is not completed, skipping creation of License for gaming machine", paymentRecord.getId());
                return;
            }

            ///add license to payment record
            Set<Machine> gamingMachines = paymentRecord.getGamingMachines();
            License license = findExistingGamingMachineLicenseInPresentYear(paymentRecord);
            if (license != null) {
                addLicenseToMachines(license, gamingMachines);
                paymentRecord.setLicenseId(license.getId());
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                return;
            }

            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now().dayOfYear().withMinimumValue();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths());
            expiryDate = expiryDate.minusDays(1);
            license = new License();
            license.setId(UUID.randomUUID().toString());
            License mostRecentLicense = findMostRecentGamingMachineLicense(paymentRecord);
            if (mostRecentLicense == null) {
                license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            } else {
                license.setLicenseNumber(mostRecentLicense.getLicenseNumber());
            }
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_MACHINE_ID);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setPaymentRecordId(paymentRecord.getId());
            mongoRepositoryReactive.saveOrUpdate(license);
            addLicenseToMachines(license, gamingMachines);
            paymentRecord.setLicenseId(license.getId());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        } catch (Exception e) {
            logger.error("An error occurred while creating license for gaming machines", e);
        }
    }

    @Override
    public void createLicenseForGamingTerminalPayment(PaymentRecord paymentRecord) {
        try {
            if (!StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecord.getPaymentStatusId())) {
                logger.info("payment record with id {} is not completed, skipping creation of License for gaming machine", paymentRecord.getId());
                return;
            }

            //add licence to payment record
            Set<Machine> gamingTerminals = paymentRecord.getGamingTerminals();
            License license = findExistingGamingTerminalLicenseInPresentYear(paymentRecord);
            if (license != null) {
                addLicenseToMachines(license, gamingTerminals);
                paymentRecord.setLicenseId(license.getId());
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                return;
            }

            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now().dayOfYear().withMinimumValue();
            //LocalDate expiryDate = LocalDate.now().dayOfYear().withMaximumValue();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getGamingTerminalLicenseDurationMonths());
            expiryDate = expiryDate.minusDays(1);
            license = new License();
            license.setId(UUID.randomUUID().toString());
            License mostRecentLicense = findMostRecentGamingTerminalLicense(paymentRecord);
            if (mostRecentLicense == null) {
                license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            } else {
                license.setLicenseNumber(mostRecentLicense.getLicenseNumber());
            }
            license.setAgentId(paymentRecord.getAgentId());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_TERMINAL_ID);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setPaymentRecordId(paymentRecord.getId());
            mongoRepositoryReactive.saveOrUpdate(license);
            addLicenseToMachines(license, gamingTerminals);
            paymentRecord.setLicenseId(license.getId());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
        } catch (Exception e) {
            logger.error("An error occurred while creating license for gaming machines", e);
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
            String gameTypeId = paymentRecord.getGameTypeId();
            String licenseTypeId = paymentRecord.getLicenseTypeId();

            License latestLicense = getPreviousConfirmedLicense(institutionId, agentId, gameTypeId, licenseTypeId);
            if (latestLicense == null) {
                logger.info("There is no previous license found for the payment record with id {}", paymentRecord.getId());
                return;
            }

            LocalDate newLicenseStartDate;
            LocalDate today = LocalDate.now();
            if (today.isBefore(latestLicense.getExpiryDate())) {
                newLicenseStartDate = latestLicense.getExpiryDate();
            } else {
                newLicenseStartDate = LocalDate.now();
            }
            LocalDate newLicenseEndDate = getNewLicenseEndDate(latestLicense, gameType);

            License newPendingApprovalRenewedLicense = new License();
            newPendingApprovalRenewedLicense.setId(UUID.randomUUID().toString());
            newPendingApprovalRenewedLicense.setInstitutionId(institutionId);
            newPendingApprovalRenewedLicense.setAgentId(agentId);
            newPendingApprovalRenewedLicense.setEffectiveDate(newLicenseStartDate.plusDays(1));
            newPendingApprovalRenewedLicense.setExpiryDate(newLicenseEndDate);
            newPendingApprovalRenewedLicense.setPaymentRecordId(paymentRecord.getId());
            if (latestLicense.isInstitutionLicense()) {
                newPendingApprovalRenewedLicense.setLicenseStatusId(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
            }
            if (latestLicense.isAgentLicense()) {
                newPendingApprovalRenewedLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            }
            newPendingApprovalRenewedLicense.setLicenseTypeId(paymentRecord.getLicenseTypeId());
            newPendingApprovalRenewedLicense.setGameTypeId(paymentRecord.getGameTypeId());
            newPendingApprovalRenewedLicense.setParentLicenseId(latestLicense.getId());
            newPendingApprovalRenewedLicense.setRenewalStatus("false");
            newPendingApprovalRenewedLicense.setLicenseNumber(latestLicense.getLicenseNumber());
            mongoRepositoryReactive.saveOrUpdate(newPendingApprovalRenewedLicense);
            paymentRecord.setLicenseId(newPendingApprovalRenewedLicense.getId());
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);


            latestLicense.setRenewalPaymentMade(true);
            latestLicense.setRenewalPaymentRecordId(paymentRecord.getId());
            mongoRepositoryReactive.saveOrUpdate(latestLicense);

//            verbiage = "UPDATED : " + getInstitution(license.getInstitutionId()).getInstitutionName() + " license status from AIP DOC UPLOADED to AIP COMPLETED";
//            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.AIP_ID,
//                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
//                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
        } catch (Exception e) {
            logger.error("An error occurred while creating renewed license for payment record {}", paymentRecord.getId(), e);
        }
    }

    @Override
    public License findLicenseById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return (License) mongoRepositoryReactive.findById(id, License.class).block();
    }

    @Override
    public License findInstitutionActiveLicenseInGameType(String institutionId, String gameTypeId) {
        Query queryForLicensedInstitutionInGameType = new Query();
        LocalDate today = LocalDate.now();
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
        queryForLicensedInstitutionInGameType.addCriteria(new Criteria().andOperator(Criteria.where("effectiveDate").lte(today), (Criteria.where("expiryDate").gte(today))));
        return (License) mongoRepositoryReactive.find(queryForLicensedInstitutionInGameType, License.class).block();
    }

    @Override
    public void changeLicenseStatusForCaseOutcome(License license, String newLicenseStatusId) {
        license.setOldLicenseStatusId(license.getLicenseStatusId());
        license.setLicenseStatusId(newLicenseStatusId);
        mongoRepositoryReactive.saveOrUpdate(license);
        ArrayList<License> otherOwnerLicensesInCategory = findAllFutureLicensesAfterLicense(license);
        for (License futureLicense : otherOwnerLicensesInCategory) {
            futureLicense.setOldLicenseStatusId(futureLicense.getLicenseStatusId());
            futureLicense.setLicenseStatusId(newLicenseStatusId);
            mongoRepositoryReactive.saveOrUpdate(futureLicense);
        }
    }

    @Override
    public License findPresentLicenseForCase(LoggedCase loggedCase) {
        Query query = new Query();
        LocalDate today = LocalDate.now();
        if (!StringUtils.isEmpty(loggedCase.getInstitutionId())) {
            query.addCriteria(Criteria.where("InstitutionId").is(loggedCase.getInstitutionId()));
        }
        if (!StringUtils.isEmpty(loggedCase.getAgentId())) {
            query.addCriteria(Criteria.where("agentId").is(loggedCase.getAgentId()));
        }
        query.addCriteria(Criteria.where("gameTypeId").is(loggedCase.getGameTypeId()));
        query.addCriteria(new Criteria().andOperator(Criteria.where("effectiveDate").lte(today), (Criteria.where("expiryDate").gte(today))));
        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    private ArrayList<License> findAllFutureLicensesAfterLicense(License license) {
        Query query = new Query();
        query.addCriteria(Criteria.where("effectiveDate").gt(license.getEffectiveDate()));
        query.addCriteria(Criteria.where("InstitutionId").is(license.getInstitutionId()));
        query.addCriteria(Criteria.where("agentId").is(license.getAgentId()));
        query.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
        return (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
    }


    @Override
    public Mono<ResponseEntity> licenseLicense(LicenseRequestDto licenseRequestDto, HttpServletRequest request) {
        String licenseId = licenseRequestDto.getLicenseId();
        try {
            License license = findLicenseById(licenseId);
            if (license == null) {
                return Mono.just(new ResponseEntity<>(String.format("Licence with id %s not found", licenseId), HttpStatus.BAD_REQUEST));
            }

            if (license.isTerminatedLicence()) {
                return Mono.just(new ResponseEntity<>("License already terminated", HttpStatus.BAD_REQUEST));
            }

            LicenseStatus oldLicenseStatus = license.getLicenseStatus();
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(license);

            String licenseOwner = license.getOwnerName();
            String verbiage = String.format("Re licenced , Owner -> %s,license Number -> %s , Category -> %s, Old Status  -> %s, New Status -> LICENSED",
                    licenseOwner, license.getLicenseNumber(), license.getGameType(), oldLicenseStatus);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.LICENCE_ID, "System Admin",
                    String.valueOf(license.getOwnerName()), LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            //send email to owner
            loggedCaseMailSenderAsync.sendRelicenseMailToLicense(license, oldLicenseStatus);
            return Mono.just(new ResponseEntity<>(license.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while licencing license", e);
        }
    }

    private LocalDate getNewLicenseEndDate(License latestLicense, GameType gameType) throws Exception {
        LocalDate newLicenseStartDate = latestLicense.getExpiryDate();
        String licenseTypeId = latestLicense.getLicenseTypeId();
        if (StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, licenseTypeId)) {
            return newLicenseStartDate.plusMonths(gameType.getAgentLicenseDurationMonths()).minusDays(1);
        }
        if (StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, licenseTypeId)) {
            return newLicenseStartDate.plusMonths(gameType.getInstitutionLicenseDurationMonths()).minusDays(1);
        }
        if (StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, licenseTypeId)) {
            return newLicenseStartDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths()).minusDays(1);
        }
        throw new Exception();
    }


    /**
     * Gets previous confirmed license for renewal payment
     *
     * @param institutionId
     * @param agentId
     * @param gameTypeId
     * @param licenseTypeId
     * @return
     */
    @Override
    public License getPreviousConfirmedLicense(String institutionId,
                                               String agentId,
                                               String gameTypeId,
                                               String licenseTypeId) {
        Query query = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        if (!StringUtils.isEmpty(agentId)) {
            query.addCriteria(Criteria.where("agentId").is(agentId));
        }
        if (!StringUtils.isEmpty(gameTypeId)) {
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        }
        if (!StringUtils.isEmpty(licenseTypeId)) {
            query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        }

        List<String> licenseStatusIds = new ArrayList<>();
        licenseStatusIds.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        licenseStatusIds.add(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
        licenseStatusIds.add(LicenseStatusReferenceData.RENEWED_ID);
        licenseStatusIds.add(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);

        query.addCriteria(Criteria.where("licenseStatusId").in(licenseStatusIds));
        Sort sort = new Sort(Sort.Direction.DESC, "effectiveDate");

        query.with(PageRequest.of(0, 10000, sort));
        query.with(sort);

        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    private String generateLicenseNumberForPaymentRecord(PaymentRecord paymentRecord) {
        String prefix = "LSLB-";
        if (paymentRecord.isAgentPayment()) {
            prefix = prefix + "AG-";
        }
        if (paymentRecord.isGamingMachinePayment()) {
            prefix = prefix + "GM-";
        }
        if (paymentRecord.isGamingTerminalPayment()) {
            prefix = prefix + "GT-";
        }
        if (paymentRecord.isInstitutionPayment()) {
            prefix = prefix + "OP-";
        }
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(100, 1000));
        GameType gameType = paymentRecord.getGameType();
        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
            prefix = prefix + gameType.getShortCode() + "-";
        }
        return String.format("%s%s%s", prefix, randomDigit, LocalDateTime.now().getSecondOfMinute());
    }

    private String generateLicenseNumberForOperator(String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        LocalDateTime time = LocalDateTime.now();
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10, 1000));
        return String.format("LSLB-OP-%s-%s%s", gameType.getShortCode(), randomDigit, time.getSecondOfMinute());
    }

    private License findExistingGamingMachineLicenseInPresentYear(PaymentRecord paymentRecord) {
        LocalDate firstDayOfYear = LocalDate.now().dayOfYear().withMinimumValue();
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(paymentRecord.getInstitutionId()));
        query.addCriteria(Criteria.where("gameTypeId").is(paymentRecord.getGameTypeId()));
        query.addCriteria(Criteria.where("effectiveDate").gte(firstDayOfYear));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_MACHINE_ID));
        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    private License findExistingGamingTerminalLicenseInPresentYear(PaymentRecord paymentRecord) {
        LocalDate firstDayOfYear = LocalDate.now().dayOfYear().withMinimumValue();
        Query query = new Query();
        query.addCriteria(Criteria.where("agentId").is(paymentRecord.getAgentId()));
        query.addCriteria(Criteria.where("gameTypeId").is(paymentRecord.getGameTypeId()));
        query.addCriteria(Criteria.where("effectiveDate").gte(firstDayOfYear));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_TERMINAL_ID));
        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    private void addLicenseToMachines(License license, Collection<Machine> machines) {
        for (Machine machine : machines) {
            try {
                machine.setLicenseId(license.getId());
                mongoRepositoryReactive.saveOrUpdate(machine);
            } catch (Exception e) {
                logger.error("An error occurred while adding license {} to machine {}", license.getLicenseNumber(), machine.getSerialNumber(), e);
            }
        }
    }

    private License findMostRecentGamingTerminalLicense(PaymentRecord paymentRecord) {
        Query query = new Query();
        query.addCriteria(Criteria.where("agentId").is(paymentRecord.getAgentId()));
        query.addCriteria(Criteria.where("gameTypeId").is(paymentRecord.getGameTypeId()));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_TERMINAL_ID));
        Sort sort = new Sort(Sort.Direction.DESC, "expiryDate");
        query.with(sort);
        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    private License findMostRecentGamingMachineLicense(PaymentRecord paymentRecord) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(paymentRecord.getInstitutionId()));
        query.addCriteria(Criteria.where("gameTypeId").is(paymentRecord.getGameTypeId()));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_MACHINE_ID));
        Sort sort = new Sort(Sort.Direction.DESC, "expiryDate");
        query.with(sort);
        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }


    public String getApprovedApplicationTradeNameForOperator(String institutionId, String gameTypeId) {
        ApplicationForm applicationForm = getApprovedApplicationFormForInstitution(institutionId, gameTypeId);
        if (applicationForm != null) {
            ApplicantDetails applicantDetails = applicationForm.getApplicantDetails();
            if (applicantDetails != null) {
                return applicantDetails.getTradingName();
            }
        }
        return null;
    }

    @Override
    public ApplicationForm getApprovedApplicationFormForInstitution(String institutionId, String gameTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("applicationFormStatusId").is(ApplicationFormStatusReferenceData.APPROVED_STATUS_ID));
        return (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
    }


    protected void generateLegacyLicenses(License license, int duration) {
        int days_diff;
        days_diff = Days.daysBetween(license.getExpiryDate(), LocalDate.now()).getDays();
        String licenseNumber = "";
        int count = 0;
        licenseNumber = generateLicenseNumberForOperator(license.getGameTypeId());
        while (days_diff > 0) {
            count++;
            License createLicense = new License();

            createLicense.setLicenseNumber(licenseNumber);
            createLicense.setId(UUID.randomUUID().toString());
            createLicense.setRenewalStatus("false");
            createLicense.setInstitutionId(license.getInstitutionId());
            if (count == 1) {
                createLicense.setEffectiveDate(license.getExpiryDate().plusDays(1));
                LocalDate licenseEndDate = createLicense.getEffectiveDate().plusMonths(duration);
                createLicense.setExpiryDate(licenseEndDate.minusDays(1));
                createLicense.setParentLicenseId(license.getId());
                createLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            } else {
                Query query = new Query();
                query.addCriteria(Criteria.where("institutionId").is(license.getInstitutionId()));
                query.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
                Sort sort = new Sort(Sort.Direction.DESC, "expiryDate");
                query.with(PageRequest.of(0, 1, sort));
                query.with(sort);
                License currentLicense = (License) mongoRepositoryReactive.find(query, License.class).block();
                createLicense.setEffectiveDate(currentLicense.getExpiryDate().plusDays(1));
                createLicense.setExpiryDate(createLicense.getEffectiveDate().plusMonths(duration).minusDays(1));
                createLicense.setLicenseStatusId(LicenseStatusReferenceData.RENEWED_ID);
                createLicense.setParentLicenseId(currentLicense.getId());
            }
            createLicense.setGameTypeId(license.getGameTypeId());
            createLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
            days_diff = Days.daysBetween(createLicense.getExpiryDate(), LocalDate.now()).getDays();
            if (days_diff > 0) {
                createLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);
            }
            mongoRepositoryReactive.saveOrUpdate(createLicense);
        }
    }
}