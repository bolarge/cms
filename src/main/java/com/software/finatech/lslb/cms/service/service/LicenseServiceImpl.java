package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import com.software.finatech.lslb.cms.service.util.*;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.AIPMailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Value("${email-username}")
    String adminEmail;
    @Autowired
    private FrontEndPropertyHelper frontEndPropertyHelper;

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
                query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
            }
            if (!StringUtils.isEmpty(licenseNumber)) {
                query.addCriteria(Criteria.where("licenseNumber").is(licenseNumber));
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
    public List<EnumeratedFactDto> getAllLicenseTypes() {
        Map licenseMap = Mapstore.STORE.get("LicenseTypes");
        ArrayList<LicenseType> licenseTypes = new ArrayList<LicenseType>(licenseMap.values());
        List<EnumeratedFactDto> licenseTypesDtoLists = new ArrayList<>();
        licenseTypes.forEach(factObject -> {
            LicenseType licenseType = factObject;
            licenseTypesDtoLists.add(licenseType.convertToDto());
        });
        return licenseTypesDtoLists;
    }

    @Override
    public Mono<ResponseEntity> getAllLicenseStatus() {
        return Mono.just(new ResponseEntity<>(getLicenseStatus(), HttpStatus.OK));
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
        licenses.stream().forEach(license -> {
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
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION));
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("renewalStatus").is("true"));
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
    public Mono<ResponseEntity> getAgentLicensesCloseToExpiration(String agentId) {
        Query queryForLicensedAgentInGameType = new Query();
        if (!StringUtils.isEmpty(agentId)) {
            queryForLicensedAgentInGameType.addCriteria(Criteria.where("agentId").is(agentId));
        }
        queryForLicensedAgentInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.AGENT));
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
        queryForLicensedAgentInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_MACHINE));
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
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION));
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
        queryForLicensedInstitutionInGameType.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION));
        queryForLicensedInstitutionInGameType.addCriteria(new Criteria().andOperator(Criteria.where("effectiveDate").lte(today), (Criteria.where("expiryDate").gte(today))));
        License licenseForInstitutionAndGameType = (License) mongoRepositoryReactive.find(queryForLicensedInstitutionInGameType, License.class).block();
        return licenseForInstitutionAndGameType != null;
    }

    @Override
    public Mono<ResponseEntity> getInstitutionAIPUploaded(String institutionId) {
        Query queryForInstitutionAIP = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            queryForInstitutionAIP.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID));
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION));
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
    public Mono<ResponseEntity> getInstitutionAIPs(String institutionId) {
        Query queryForInstitutionAIP = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            queryForInstitutionAIP.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID));
        queryForInstitutionAIP.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION));
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
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
            notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
            notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ",  has uploaded " +
                    notificationDto.getGameType() + " AIP Documents.");
            notificationDto.setTemplate("AIPUpdate");
            notificationDto.setCallBackUrl(frontEndPropertyHelper.getFrontEndUrl() + "/all-aips");
            notificationDto.setInstitutionEmail(adminEmail);
            sendEmail.sendEmailLicenseApplicationNotification(notificationDto);

            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @Override
    public Mono<ResponseEntity> updateInReviewToLicense(String licenseId) {
        try {
            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("id").is(licenseId));
            queryLicence.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_LICENSE_IN_REVIEW));
            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();

            if (license == null) {
                return Mono.just(new ResponseEntity<>("No License Record", HttpStatus.BAD_REQUEST));
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.RENEWED_ID);
            mongoRepositoryReactive.saveOrUpdate(license);
            List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(license.getInstitutionId());
            institutionAdmins.stream().forEach(institutionAdmin->{
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
                notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
                notificationDto.setTemplate("LicenseUpdate");
                notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ", renewal application for " +
                        notificationDto.getGameType()+" have been approved. License is valid from "+license.getEffectiveDate().toString("dd/MM/YYY")+" to" +
                        notificationDto.getEndDate());
                notificationDto.setInstitutionEmail(institutionAdmin.getEmailAddress());
                sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
            });

            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

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

            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
            notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
            notificationDto.setTemplate("LicenseUpdate");
            notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ",  has submitted renewal application and uploaded the requested documents for " +
                    notificationDto.getGameType());
            notificationDto.setInstitutionEmail(adminEmail);
            sendEmail.sendEmailLicenseApplicationNotification(notificationDto);

            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error while moving to renewal license in review", HttpStatus.BAD_REQUEST));

        }
    }

    @Override
    public Mono<ResponseEntity> updateRenewalReviewToInProgress(RenewalFormCommentDto renewalFormCommentDto) {
        try {
            String verbiage;
            Query queryLicenceStatus = new Query();
            queryLicenceStatus.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCommentDto.getPaymentRecordId()));
            queryLicenceStatus.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.RENEWAL_LICENSE_IN_REVIEW));
            License license = (License) mongoRepositoryReactive.find(queryLicenceStatus, License.class).block();
            if (license == null) {
                return Mono.just(new ResponseEntity<>("Invalid payment record", HttpStatus.BAD_REQUEST));
            }
            Query queryRenewalStatus = new Query();
            queryRenewalStatus.addCriteria(Criteria.where("paymentRecordId").is(renewalFormCommentDto.getPaymentRecordId()));
            RenewalForm renewalForm = (RenewalForm) mongoRepositoryReactive.find(queryRenewalStatus, RenewalForm.class).block();
            renewalForm.setFormStatusId(RenewalFormStatusReferenceData.PENDING);
            renewalForm.setComment(renewalFormCommentDto.getComment());
            mongoRepositoryReactive.saveOrUpdate(renewalForm);
//            verbiage = "Moved : " + getInstitution(license.getInstitutionId()).getInstitutionName() + " renewal form  status from submitted to pending";
//            //auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.U,
//                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
//                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));



            license.setLicenseStatusId(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
            mongoRepositoryReactive.saveOrUpdate(license);
//            verbiage = "Moved : " + getInstitution(license.getInstitutionId()).getInstitutionName() + " license status from Renewal In Review to Renewal In Progress";
//             auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.RENEWAL_FORM_UPDATE,
//                    springSecurityAuditorAware.getCurrentAuditor().get(), getInstitution(license.getInstitutionId()).getInstitutionName(),
//                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage);

            List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(license.getInstitutionId());
            institutionAdmins.stream().forEach(institutionAdmin -> {
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
                notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
                notificationDto.setTemplate("LicenseUpdate");
                notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ", " + renewalFormCommentDto.getComment()
                );
                notificationDto.setInstitutionEmail(institutionAdmin.getEmailAddress());
                sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
            });


            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error while moving to renewal license in progress", HttpStatus.BAD_REQUEST));

        }
    }


    @Override
    public Mono<ResponseEntity> updateAIPDocToLicense(LicenseUpdateAIPToLicenseDto licenseUpdateDto) {
        try {

            Query queryLicence = new Query();
            queryLicence.addCriteria(Criteria.where("institutionId").is(licenseUpdateDto.getInstitutionId()));
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(licenseUpdateDto.getGameTypeId()));
            queryLicence.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION));
            queryLicence.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_DOCUMENT_STATUS_ID));
            License license = (License) mongoRepositoryReactive.find(queryLicence, License.class).block();
            if (license == null) {
                return Mono.just(new ResponseEntity<>("Operator has not uploaded AIP document", HttpStatus.BAD_REQUEST));

            }
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_COMPLETED);
            License createLicense = new License();

            Query queryGameType = new Query();

            queryGameType.addCriteria(Criteria.where("id").is(license.getGameTypeId()));
            GameType gameType = (GameType) mongoRepositoryReactive.find(queryGameType, GameType.class).block();
            int duration = gameType.getInstitutionLicenseDurationMonths();
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
            String licenseNumber = "";
            if (paymentRecord != null) {
                licenseNumber = generateLicenseNumberForPaymentRecord(paymentRecord);
                createLicense.setLicenseNumber(licenseNumber);
            }
            createLicense.setId(UUID.randomUUID().toString());
            createLicense.setEffectiveDate(license.getExpiryDate().plusDays(1));
            createLicense.setExpiryDate(licenseEndDate);
            createLicense.setRenewalStatus("false");
            createLicense.setInstitutionId(license.getInstitutionId());
            createLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            createLicense.setGameTypeId(license.getGameTypeId());
            createLicense.setParentLicenseId(license.getId());
            createLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION);
            createLicense.setPaymentRecordId(license.getPaymentRecordId());
            mongoRepositoryReactive.saveOrUpdate(license);
            mongoRepositoryReactive.saveOrUpdate(createLicense);

            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setGameType(getGameType(license.getGameTypeId()).getName());
            notificationDto.setEndDate(license.getExpiryDate().toString("dd/MM/YYY"));
            notificationDto.setTemplate("LicenseUpdate");
            notificationDto.setDescription(getInstitution(license.getInstitutionId()).getInstitutionName() + ",  License for " +
                    notificationDto.getGameType() + " has been approved.\n License Number is: " + licenseNumber);

            ArrayList<AuthInfo> authInfos = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(license.getInstitutionId());
            for (AuthInfo authInfo : authInfos) {
                notificationDto.setInstitutionEmail(authInfo.getEmailAddress());
                sendEmail.sendEmailLicenseApplicationNotification(notificationDto);
            }
            return Mono.just(new ResponseEntity<>("OK", HttpStatus.OK));

        } catch (Exception ex) {
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
            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getAipDurationMonths()).minusDays(1);
            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setGameTypeId(paymentRecord.getGameTypeId());
            license.setLicenseStatusId(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
            license.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setPaymentRecordId(paymentRecord.getId());
            //     license.setLicenseNumber(generateLicenseNumberForPaymentRecord(paymentRecord));
            mongoRepositoryReactive.saveOrUpdate(license);
            aipMailSenderAsync.sendAipNotificationToInstitutionAdmins(paymentRecord);
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
                logger.info("payment record with id {} is not completed, skipping creation of License for gaming machine", paymentRecord.getId());
                return;
            }

            GameType gameType = paymentRecord.getGameType();
            LocalDate effectiveDate = LocalDate.now();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths()).minusDays(1);
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
            String licenseTypeId = getLicenseTypeIdFromRevenueNameId(paymentRecord.getRevenueNameId());

            License latestLicense = getPreviousConfirmedLicenses(institutionId, agentId, gamingMachineId, gameTypeId, licenseTypeId);
            if (latestLicense == null) {
                logger.info("There is no previous license found for the payment record with id {}", paymentRecord.getId());
                return;
            }

            LocalDate newLicenseStartDate = latestLicense.getExpiryDate();
            LocalDate newLicenseEndDate = getNewLicenseEndDate(latestLicense, gameType);

            License newPendingApprovalRenewedLicense = new License();
            newPendingApprovalRenewedLicense.setId(UUID.randomUUID().toString());
            newPendingApprovalRenewedLicense.setGamingMachineId(gamingMachineId);
            newPendingApprovalRenewedLicense.setInstitutionId(institutionId);
            newPendingApprovalRenewedLicense.setAgentId(agentId);
            newPendingApprovalRenewedLicense.setEffectiveDate(newLicenseStartDate.plusDays(1));
            newPendingApprovalRenewedLicense.setExpiryDate(newLicenseEndDate);
            newPendingApprovalRenewedLicense.setPaymentRecordId(paymentRecord.getId());
            if (latestLicense.isInstitutionLicense()) {
                newPendingApprovalRenewedLicense.setLicenseStatusId(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
            }
            if (latestLicense.isAgentLicense() || latestLicense.isGamingMachineLicense()) {
                newPendingApprovalRenewedLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            }
            newPendingApprovalRenewedLicense.setLicenseTypeId(getLicenseTypeIdFromRevenueNameId(paymentRecord.getRevenueNameId()));
            newPendingApprovalRenewedLicense.setGameTypeId(paymentRecord.getGameTypeId());
            newPendingApprovalRenewedLicense.setParentLicenseId(latestLicense.getId());
            newPendingApprovalRenewedLicense.setLicenseNumber(latestLicense.getLicenseNumber());
            mongoRepositoryReactive.saveOrUpdate(newPendingApprovalRenewedLicense);
        } catch (Exception e) {
            logger.error("An error occurred while creating renewed license for payment record {}", paymentRecord.getId(), e);
        }
    }

    private List<String> getAllowedLicensedStatusIds() {
        List<String> allowedLicenseStatusIds = new ArrayList<>();
        allowedLicenseStatusIds.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        allowedLicenseStatusIds.add(LicenseStatusReferenceData.AIP_COMPLETED);
        allowedLicenseStatusIds.add(LicenseStatusReferenceData.RENEWED_ID);
        return allowedLicenseStatusIds;
    }

    private LocalDate getNewLicenseEndDate(License latestLicense, GameType gameType) throws Exception {
        LocalDate newLicenseStartDate = latestLicense.getExpiryDate();
        String licenseTypeId = latestLicense.getLicenseTypeId();
        if (StringUtils.equals(LicenseTypeReferenceData.AGENT, licenseTypeId)) {
            return newLicenseStartDate.plusMonths(gameType.getAgentLicenseDurationMonths()).minusDays(1);
        }
        if (StringUtils.equals(LicenseTypeReferenceData.INSTITUTION, licenseTypeId)) {
            return newLicenseStartDate.plusMonths(gameType.getInstitutionLicenseDurationMonths()).minusDays(1);
        }
        if (StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE, licenseTypeId)) {
            return newLicenseStartDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths()).minusDays(1);
        }
        throw new Exception();
    }

    private String getLicenseTypeIdFromRevenueNameId(String revenueNameId) {
        if (StringUtils.equals(RevenueNameReferenceData.AGENT_REVENUE_ID, revenueNameId)) {
            return LicenseTypeReferenceData.AGENT;
        }
        if (StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, revenueNameId)) {
            return LicenseTypeReferenceData.GAMING_MACHINE;
        }
        if (StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, revenueNameId)) {
            return LicenseTypeReferenceData.INSTITUTION;
        }
        return null;
    }

    public License getPreviousConfirmedLicenses(String institutionId,
                                                String agentId,
                                                String gamingMachineId,
                                                String gameTypeId,
                                                String licenseTypeId) {
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
        if (!StringUtils.isEmpty(licenseTypeId)) {
            query.addCriteria(Criteria.where("licenseTypeId").is(licenseTypeId));
        }

        List<String> licenseStatusIds = new ArrayList<>();
        licenseStatusIds.add(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        licenseStatusIds.add(LicenseStatusReferenceData.RENEWAL_IN_PROGRESS_LICENSE_STATUS_ID);
        query.addCriteria(Criteria.where("licenseStatusId").in(licenseStatusIds));
        Sort sort = new Sort(Sort.Direction.DESC, "effectiveDate");

        query.with(PageRequest.of(0, 10000, sort));
        query.with(sort);

        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    private String generateLicenseNumberForPaymentRecord(PaymentRecord paymentRecord) {
        String revenueNameId = paymentRecord.getRevenueNameId();
        String prefix = "LSLB-";
        if (StringUtils.equals(RevenueNameReferenceData.AGENT_REVENUE_ID, revenueNameId)) {
            prefix = prefix + "AG-";
        }
        if (StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, revenueNameId)) {
            prefix = prefix + "GM-";
        }
        if (StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, revenueNameId)) {
            prefix = prefix + "OP-";
        }
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10000, 1000000));
        GameType gameType = paymentRecord.getGameType();
        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
            prefix = prefix + gameType.getShortCode() + "-";
        }
        return String.format("%s%s", prefix, randomDigit);
    }
}