package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.model.migrations.BaseInstitutionUpload;
import com.software.finatech.lslb.cms.service.model.migrations.MigratedInstitutionUpload;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.EnvironmentUtils;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.OKResponseUtil.OKResponse;

@Component
public class ExistingOperatorLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExistingOperatorLoader.class);
    @Autowired
    private GameTypeService gameTypeService;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private EnvironmentUtils environmentUtils;
    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;

    private DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd/MM/yyyy");

    public void loadFromCsv(MultipartFile multipartFile) throws LicenseServiceException {
        if (multipartFile.isEmpty()) {
            throw new LicenseServiceException("File is empty");
        }
        List<InstitutionUpload> institutionUploads = new ArrayList<>();
        try {
            byte[] bytes = multipartFile.getBytes();
            String completeData = new String(bytes);
            String[] rows = completeData.split("\\r?\\n");

            for (int i = 2; i < rows.length; i++) {
                // String[] columns = rows[i].split(",");
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (columns.length < 15) {
                    throw new LicenseServiceException("File is less than 15 columns =>" + rows[i]);
                }
                try {
                    String institutionName = columns[3];
                    if (!StringUtils.isEmpty(institutionName)) {
                        InstitutionUpload institutionUpload = new InstitutionUpload();
                        institutionUpload.setLine(rows[i]);
                        institutionUpload.setInstitutionName(institutionName);
                        institutionUpload.setDescription(columns[5]);
                        institutionUpload.setEmailAddress(columns[8]);
                        institutionUpload.setPhoneNumber(columns[9]);
                        String address = columns[10].replace("\"", "").replace("?", "");
                        institutionUpload.setAddress(address);

                        String gameTypeSearchKey = columns[6];
                        GameType gameType = gameTypeService.findGameTypeBySearchKey(gameTypeSearchKey);
                        if (gameType == null) {
                            throw new LicenseServiceException(String.format("Game type with search key %s not found", gameTypeSearchKey));
                        }
                        InstitutionLoadDetails loadDetails = new InstitutionLoadDetails();

                        if (!StringUtils.isEmpty(columns[11])) {
                            LocalDate licenseStartDate = dateTimeFormat.parseLocalDate(columns[11]);
                            loadDetails.setLicenseStartDate(licenseStartDate);
                        }
                        if (!StringUtils.isEmpty(columns[12])) {
                            LocalDate licenseEndDate = dateTimeFormat.parseLocalDate(columns[12]);
                            loadDetails.setLicenseEndDate(licenseEndDate);
                        }
                        if (!StringUtils.isEmpty(columns[13])) {
                            LocalDate licenseFirstDate = dateTimeFormat.parseLocalDate(columns[13]);
                            loadDetails.setFirstCommencementDate(licenseFirstDate);
                        }
                        loadDetails.setGameTypeId(gameType.getId());
                        loadDetails.setTradeName(columns[4]);
                        loadDetails.setDirector(columns[7]);
                        loadDetails.setStatus(columns[14]);
                        String status = columns[14];
                        if (StringUtils.isEmpty(status)) {
                            logger.info("{} has no licence status for category {}, Skipping", institutionName, gameTypeSearchKey);
                            continue;
                        }
                        //institutionUpload.getInstitutionLoadDetails().add(loadDetails);
                        institutionUpload.setLoadDetails(loadDetails);
                        institutionUploads.add(institutionUpload);
                    }
                } catch (IllegalArgumentException e) {
                    throw new LicenseServiceException("Error occurred while parsing date ");
                }
            }
            loadInstitutionsFromUploads(institutionUploads);
        } catch (IOException e) {
            logger.error("IO Exception ", e);
            throw new LicenseServiceException("An error occurred while parsing the file");
        } catch (Exception e) {
            logger.error("An error occurred ", e);
            throw new LicenseServiceException("An error occurred while parsing file");
        }
    }


    public void loadAIPOrSuspendedFromCsv(MultipartFile multipartFile) throws LicenseServiceException {
        if (multipartFile.isEmpty()) {
            throw new LicenseServiceException("File is empty");
        }
        Map<String, InstitutionUpload> institutionUploadMap = new HashMap<>();
        List<InstitutionUpload> institutionUploads = new ArrayList<>();
        try {
            byte[] bytes = multipartFile.getBytes();
            String completeData = new String(bytes);
            String[] rows = completeData.split("\\r?\\n");

            for (int i = 2; i < rows.length; i++) {
                // String[] columns = rows[i].split(",");
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (columns.length < 13) {
                    throw new LicenseServiceException("File is less than 13 columns" + rows[i]);
                }
                try {

                    String institutionName = columns[1];
                    if (!StringUtils.isEmpty(institutionName)) {
                        InstitutionUpload institutionUpload = institutionUploadMap.get(institutionName);
                        if (institutionUpload == null) {
                            institutionUpload = new InstitutionUpload();
                            institutionUpload.setLine(rows[i]);
                            institutionUpload.setInstitutionName(institutionName);
                            institutionUpload.setDescription(columns[3]);
                            institutionUpload.setEmailAddress(columns[6]);
                            institutionUpload.setPhoneNumber(columns[7]);
                            String address = columns[8].replace("\"", "").replace("?", "");
                            institutionUpload.setAddress(address);
                        }
                        String gameTypeSearchKey = columns[4];
                        GameType gameType = gameTypeService.findGameTypeBySearchKey(gameTypeSearchKey);
                        if (gameType == null) {
                            throw new LicenseServiceException(String.format("Game type with search key %s not found", gameTypeSearchKey));
                        }
                        InstitutionLoadDetails loadDetails = new InstitutionLoadDetails();

                        if (!StringUtils.isEmpty(columns[9])) {
                            LocalDate licenseStartDate = dateTimeFormat.parseLocalDate(columns[9]);
                            loadDetails.setLicenseStartDate(licenseStartDate);
                        }
                        if (!StringUtils.isEmpty(columns[10])) {
                            LocalDate licenseEndDate = dateTimeFormat.parseLocalDate(columns[10]);
                            loadDetails.setLicenseEndDate(licenseEndDate);
                        }
                        if (!StringUtils.isEmpty(columns[11])) {
                            LocalDate licenseFirstDate = dateTimeFormat.parseLocalDate(columns[11]);
                            loadDetails.setFirstCommencementDate(licenseFirstDate);
                        }
                        loadDetails.setGameTypeId(gameType.getId());
                        loadDetails.setTradeName(columns[2]);
                        loadDetails.setDirector(columns[5]);
                        loadDetails.setStatus(columns[12]);
                        String status = columns[12];
                        if (StringUtils.isEmpty(status)) {
                            logger.info("{} has no licence status for category {}, Skipping", institutionName, gameTypeSearchKey);
                            continue;
                        }
                        //institutionUpload.getInstitutionLoadDetails().add(loadDetails);
                        institutionUpload.setLoadDetails(loadDetails);
                        institutionUploads.add(institutionUpload);
                        //  institutionUploadMap.put(institutionName, institutionUpload);
                    }
                } catch (IllegalArgumentException e) {
                    throw new LicenseServiceException("Error occurred while parsing date ");
                }
            }
            loadInstitutionsFromUploads(institutionUploads);
        } catch (IOException e) {
            logger.error("IO Exception ", e);
            throw new LicenseServiceException("An error occurred while parsing the file");
        } catch (Exception e) {
            logger.error("An error occurred ", e);
            throw new LicenseServiceException("An error occurred while parsing file");
        }
    }


    private void loadInstitutionsFromUploads(List<InstitutionUpload> institutionUploads) {
        for (InstitutionUpload institutionUpload : institutionUploads) {
            loadInstitutionUpload(institutionUpload);
        }
    }


    private void loadInstitutionUpload(InstitutionUpload institutionUpload) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionName").regex(institutionUpload.getInstitutionName(), "i"));
        Institution pendingInstitution = (Institution) mongoRepositoryReactive.find(query, Institution.class).block();

        if (pendingInstitution == null) {
            pendingInstitution = new Institution();
            pendingInstitution.setId(UUID.randomUUID().toString());
            pendingInstitution.setInstitutionName(institutionUpload.getInstitutionName());
            pendingInstitution.setActive(true);
            pendingInstitution.setDescription(institutionUpload.getDescription());
            pendingInstitution.setEmailAddress(getInstitutionAddressBasedOnEnvironment(institutionUpload));
            pendingInstitution.getPhoneNumbers().addAll(getPhoneNumbersFromUpload(institutionUpload));
            pendingInstitution.setFromLiveData(true);
            pendingInstitution.setAddress(institutionUpload.getAddress());
        }
        InstitutionLoadDetails institutionLoadDetails = institutionUpload.getLoadDetails();
        InstitutionCategoryDetails institutionCategoryDetails = new InstitutionCategoryDetails();
        institutionCategoryDetails.setId(UUID.randomUUID().toString());
        institutionCategoryDetails.setFirstCommencementDate(institutionLoadDetails.getFirstCommencementDate());
        institutionCategoryDetails.setGameTypeId(institutionLoadDetails.getGameTypeId());
        institutionCategoryDetails.setTradeName(institutionLoadDetails.getTradeName());
        institutionCategoryDetails.getPhoneNumbers().addAll(getPhoneNumbersFromUpload(institutionUpload));
        Set<String> directorNames = directorNamesFromString(institutionLoadDetails.getDirector());
        institutionCategoryDetails.getDirectorsNames().addAll(directorNames);
        pendingInstitution.getDirectorsNames().addAll(directorNames);
        institutionCategoryDetails.setInstitutionId(pendingInstitution.getId());
        pendingInstitution.getGameTypeIds().add(institutionLoadDetails.getGameTypeId());
        pendingInstitution.getInstitutionCategoryDetailIds().add(institutionCategoryDetails.getId());

        License pendingLicense = new License();
        pendingLicense.setId(UUID.randomUUID().toString());
        pendingLicense.setInstitutionId(pendingInstitution.getId());
        pendingLicense.setEffectiveDate(institutionLoadDetails.getLicenseStartDate());
        pendingLicense.setExpiryDate(institutionLoadDetails.getLicenseEndDate());
        pendingLicense.setGameTypeId(institutionLoadDetails.getGameTypeId());
        pendingLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
        String licenseStatusId = findLicenseStatusIdByKey(institutionLoadDetails.getStatus());
        if (licenseStatusId == null) {
            logger.info("{} has no license status ", pendingInstitution);
            return;
        }
        pendingLicense.setLicenseStatusId(licenseStatusId);
        if (pendingLicense.isAIPRelatedLicense()) {
            AIPDocumentApproval aipDocumentApproval = new AIPDocumentApproval();
            aipDocumentApproval.setFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
            aipDocumentApproval.setGameTypeId(pendingLicense.getGameTypeId());
            aipDocumentApproval.setInstitutionId(pendingLicense.getInstitutionId());
            aipDocumentApproval.setId(UUID.randomUUID().toString());
            aipDocumentApproval.setReadyForApproval(false);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
        } else {
            pendingLicense.setLicenseNumber(generateLicenseNumber(institutionLoadDetails.getGameTypeId()));
        }
        mongoRepositoryReactive.saveOrUpdate(pendingLicense);
        if (pendingLicense.isSuspendedLicence()) {
            institutionCategoryDetails.setFirstCommencementDate(null);
        }
        mongoRepositoryReactive.saveOrUpdate(institutionCategoryDetails);
        mongoRepositoryReactive.saveOrUpdate(pendingInstitution);
    }

    public Institution loadMigratedInstitutionUpload(MigratedInstitutionUpload migratedInstitutionUpload) {
        String gameTypeId = migratedInstitutionUpload.getGameTypeId();
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionName").regex(migratedInstitutionUpload.getInstitutionName(), "i"));
        Institution pendingInstitution;
        if (migratedInstitutionUpload.isUseInstitutionId()) {
            pendingInstitution = (Institution) mongoRepositoryReactive.findById(migratedInstitutionUpload.getInstitutionId(), Institution.class).block();
        } else {
            pendingInstitution = (Institution) mongoRepositoryReactive.find(query, Institution.class).block();
        }
        if (pendingInstitution == null) {
            pendingInstitution = new Institution();
            pendingInstitution.setId(UUID.randomUUID().toString());
            pendingInstitution.setInstitutionName(migratedInstitutionUpload.getInstitutionName());
            pendingInstitution.setActive(true);
            pendingInstitution.setDescription(migratedInstitutionUpload.getDescription());
            pendingInstitution.setEmailAddress(getInstitutionAddressBasedOnEnvironment(migratedInstitutionUpload));
            pendingInstitution.getPhoneNumbers().addAll(migratedInstitutionUpload.getPhoneNumbers());
            pendingInstitution.setFromLiveData(true);
            pendingInstitution.setAddress(migratedInstitutionUpload.getAddress());
        }
        String insitutionId = pendingInstitution.getId();
        InstitutionCategoryDetails institutionCategoryDetails;
        query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(insitutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(migratedInstitutionUpload.getGameTypeId()));
        institutionCategoryDetails = (InstitutionCategoryDetails) mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
        if (institutionCategoryDetails == null) {
            institutionCategoryDetails = new InstitutionCategoryDetails();
            institutionCategoryDetails.setId(UUID.randomUUID().toString());
        }
        if (StringUtils.isNotEmpty(migratedInstitutionUpload.getFirstCommencementDate())) {
            institutionCategoryDetails.setFirstCommencementDate(new LocalDate(migratedInstitutionUpload.getFirstCommencementDate()));
        }
        institutionCategoryDetails.setGameTypeId(migratedInstitutionUpload.getGameTypeId());
        institutionCategoryDetails.setTradeName(migratedInstitutionUpload.getTradeName());
        institutionCategoryDetails.getPhoneNumbers().addAll(migratedInstitutionUpload.getPhoneNumbers());
        institutionCategoryDetails.getDirectorsNames().addAll(migratedInstitutionUpload.getDirectors());
        pendingInstitution.getDirectorsNames().addAll(migratedInstitutionUpload.getDirectors());
        institutionCategoryDetails.setInstitutionId(pendingInstitution.getId());
        pendingInstitution.getGameTypeIds().add(migratedInstitutionUpload.getGameTypeId());
        pendingInstitution.getInstitutionCategoryDetailIds().add(institutionCategoryDetails.getId());

        query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(insitutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));

        License pendingLicense;
        pendingLicense = (License) mongoRepositoryReactive.find(query, License.class).block();
        if (pendingLicense == null) {
            pendingLicense = new License();
            pendingLicense.setId(UUID.randomUUID().toString());
        }
        pendingLicense.setInstitutionId(pendingInstitution.getId());
        pendingLicense.setEffectiveDate(new LocalDate(migratedInstitutionUpload.getLicenseStartDate()));
        pendingLicense.setExpiryDate(new LocalDate(migratedInstitutionUpload.getLicenseEndDate()));
        pendingLicense.setGameTypeId(migratedInstitutionUpload.getGameTypeId());
        pendingLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
        pendingLicense.setLicenseStatusId(migratedInstitutionUpload.getLicenseStatusId());
        if (pendingLicense.isAIPRelatedLicense()) {
            query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(insitutionId));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));

            AIPDocumentApproval aipDocumentApproval;
            aipDocumentApproval = (AIPDocumentApproval) mongoRepositoryReactive.find(query, AIPDocumentApproval.class).block();
            if (aipDocumentApproval == null) {
                aipDocumentApproval = new AIPDocumentApproval();
                aipDocumentApproval.setId(UUID.randomUUID().toString());
            }
            aipDocumentApproval.setFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
            aipDocumentApproval.setGameTypeId(pendingLicense.getGameTypeId());
            aipDocumentApproval.setInstitutionId(pendingLicense.getInstitutionId());
            aipDocumentApproval.setReadyForApproval(false);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
        } else {
            pendingLicense.setLicenseNumber(generateLicenseNumber(gameTypeId));
        }
        mongoRepositoryReactive.saveOrUpdate(pendingLicense);
        if (pendingLicense.isSuspendedLicence()) {
            institutionCategoryDetails.setFirstCommencementDate(null);
        }
        mongoRepositoryReactive.saveOrUpdate(institutionCategoryDetails);
        mongoRepositoryReactive.saveOrUpdate(pendingInstitution);
        return pendingInstitution;
    }


    private Set<String> getPhoneNumbersFromUpload(InstitutionUpload institutionUpload) {
        Set<String> phoneNumber = new HashSet<>();
        String[] phoneNumbers = institutionUpload.getPhoneNumber().split("\\s+");
        for (String number : phoneNumbers) {
            if (StringUtils.isNotEmpty(number)) {
                number = number.trim().replace("\"", "");
                if (!number.startsWith("0")) {
                    number = String.format("0%s", number);
                }
                phoneNumber.add(number);
            }
        }
        return phoneNumber;
    }

    private Set<String> directorNamesFromString(String director) {
        Set<String> directorNames = new HashSet<>();
        String[] names = director.split(" {3}");
        for (String name : names) {
            if (!StringUtils.isEmpty(name)) {
                name = name.trim();
                if (!StringUtils.isEmpty(name)) {
                    name = name.replace("\"", "");
                    directorNames.add(name);
                }
            }
        }
        return directorNames;
    }


    private String findLicenseStatusIdByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        if (StringUtils.equalsIgnoreCase("Licenced", key)) {
            return LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID;
        }
        if (StringUtils.equalsIgnoreCase("AIP", key)) {
            return LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID;
        }
        if (StringUtils.equalsIgnoreCase("Voluntary Suspension", key)) {
            return LicenseStatusReferenceData.LICENSE_SUSPENDED_ID;
        }
        if (StringUtils.equalsIgnoreCase("Suspension", key)) {
            return LicenseStatusReferenceData.LICENSE_SUSPENDED_ID;
        }
        if (StringUtils.equalsIgnoreCase("Licence Expired", key)) {
            return LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID;
        }
        return null;
    }

    private String generateLicenseNumber(String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        LocalDateTime time = LocalDateTime.now();
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10, 1000));
        return String.format("LSLB-OP-%s-%s%s", gameType.getShortCode(), randomDigit, time.getSecondOfMinute());
    }


    private String getInstitutionAddressBasedOnEnvironment(BaseInstitutionUpload institutionUpload) {
        if (environmentUtils.isProductionEnvironment()) {
            return institutionUpload.getEmailAddress();
        }
        return "test@mailinator.com";
    }


    public void deleteMigratedOperatorsData() {
        logger.info("<------------- Start of Delete ------>");
        Query query = new Query();
        query.addCriteria(Criteria.where("fromLiveData").is(true));
        ArrayList<Institution> migratedInstitutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
        List<String> institutionIds = new ArrayList<>();
        for (Institution migratedInstitution : migratedInstitutions) {
            institutionIds.add(migratedInstitution.getId());
        }

        logger.info("---- Deleting Institution Category Details---------");
        query = new Query();
        query.addCriteria(Criteria.where("institutionId").in(institutionIds));
        ArrayList<InstitutionCategoryDetails> institutionCategoryDetails = (ArrayList<InstitutionCategoryDetails>) mongoRepositoryReactive.findAll(query, InstitutionCategoryDetails.class).toStream().collect(Collectors.toList());
        for (InstitutionCategoryDetails categoryDetails : institutionCategoryDetails) {
            mongoRepositoryReactive.delete(categoryDetails);
        }

        logger.info("---- Deleting Agents --------");
        ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
        for (Agent agent : agents) {
            mongoRepositoryReactive.delete(agent);
        }

        logger.info("---- Deleting Machines --------");
        ArrayList<Machine> machines = (ArrayList<Machine>) mongoRepositoryReactive.findAll(query, Machine.class).toStream().collect(Collectors.toList());
        for (Machine machine : machines) {
            mongoRepositoryReactive.delete(machine);
        }

        logger.info("---- Deleting AIP Document Approvals --------");
        ArrayList<AIPDocumentApproval> aipDocumentApprovals = (ArrayList<AIPDocumentApproval>) mongoRepositoryReactive.findAll(query, AIPDocumentApproval.class).toStream().collect(Collectors.toList());
        for (AIPDocumentApproval aipDocumentApproval : aipDocumentApprovals) {
            mongoRepositoryReactive.delete(aipDocumentApproval);
        }

        logger.info("---- Deleting Licenses --------");
        ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
        for (License license : licenses) {
            mongoRepositoryReactive.delete(license);
        }

    }

    public Mono<ResponseEntity> createExpiredLicenseForOperator(CreateExpiredLicensePaymentDto createExpiredLicensePaymentDto) {
        String institutionId = createExpiredLicensePaymentDto.getInstitutionId();
        String gameTypeId = createExpiredLicensePaymentDto.getGameTypeId();
        double amountPaid = createExpiredLicensePaymentDto.getAmountPaid();
        double amountOutstanding = createExpiredLicensePaymentDto.getAmountOutstanding();
        String paymentDateString = createExpiredLicensePaymentDto.getPaymentDate();
        boolean operatorHasMadeSomePayment = amountPaid > 0;

        try {

            LocalDate paymentDate = new LocalDate(paymentDateString);
            LocalDateTime paymentDateTime = paymentDate.toLocalDateTime(LocalTime.MIDNIGHT);
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            License license = (License) mongoRepositoryReactive.find(query, License.class).block();
            if (license != null) {
                if (createExpiredLicensePaymentDto.isUpdateLicense()) {
                    license.setEffectiveDate(new LocalDate(createExpiredLicensePaymentDto.getLicenseStartDate()));
                    license.setExpiryDate(new LocalDate(createExpiredLicensePaymentDto.getLicenseEndDate()));
                }
                license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);
                mongoRepositoryReactive.saveOrUpdate(license);
            }
            PaymentRecord record = new PaymentRecord();
            record.setId(UUID.randomUUID().toString());
            record.setGameTypeId(gameTypeId);
            record.setAmount(amountPaid + amountOutstanding);
            record.setAmountPaid(amountPaid);
            record.setAmountOutstanding(amountOutstanding);
            record.setCreationDate(LocalDate.now());
            record.setForIncompleteOfflineLicenceRenewal(true);
            record.setFeePaymentTypeId(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID);
            record.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
            record.setInstitutionId(institutionId);
            if (operatorHasMadeSomePayment) {
                record.setPaymentStatusId(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID);
            } else {
                record.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
            }

            if (operatorHasMadeSomePayment) {
                PaymentRecordDetail recordDetail = new PaymentRecordDetail();
                recordDetail.setId(UUID.randomUUID().toString());
                recordDetail.setPaymentRecordId(record.getId());
                recordDetail.setAmount(amountPaid);
                recordDetail.setInvoiceNumber(NumberUtil.generateTransactionReferenceForPaymentRecord());
                recordDetail.setPaymentDate(paymentDateTime);
                recordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.LSLB_OFFLINE_ID);
                recordDetail.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
                record.getPaymentRecordDetailIds().add(recordDetail.getId());
                mongoRepositoryReactive.saveOrUpdate(recordDetail);
            }

            mongoRepositoryReactive.saveOrUpdate(record);
            return OKResponse("Payment Updated Successfully");
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        }
    }


    public Mono<ResponseEntity> updateDirectorDetails(DirectorsUpdateDto shareHoldersUpdateDto) {
        String institutionId = shareHoldersUpdateDto.getInstitutionId();
        String gameTypeId = shareHoldersUpdateDto.getGameTypeId();
        Institution institution = (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
        if (institution == null) {
            return ErrorResponseUtil.BadRequestResponse("Invalid institution");
        }
        institution.getDirectorsNames().clear();
        institution.getDirectorsNames().addAll(shareHoldersUpdateDto.getNames());

        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        InstitutionCategoryDetails categoryDetails = (InstitutionCategoryDetails) mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
        if (categoryDetails != null) {
            categoryDetails.getDirectorsNames().clear();
            categoryDetails.getDirectorsNames().addAll(shareHoldersUpdateDto.getNames());
            mongoRepositoryReactive.saveOrUpdate(categoryDetails);
        }
        mongoRepositoryReactive.saveOrUpdate(institution);
        return OKResponse("Updated successfully");
    }

    public Mono<ResponseEntity> changeExistingOperatorCategory(MigrateCategoryDto migrateCategoryDto) {
        String institutionId = migrateCategoryDto.getInstitutionId();
        String oldGameTypeId = migrateCategoryDto.getOldGameTypeId();
        String newGameTypeId = migrateCategoryDto.getNewGameTypeId();
        Institution institution = (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
        GameType oldGameType = gameTypeService.findById(oldGameTypeId);
        GameType newGameType = gameTypeService.findById(newGameTypeId);

        StringBuilder builder = new StringBuilder();
        builder.append("Affected Entities ==> ");

        if (institution != null && oldGameType != null && newGameType != null) {
            if (institution.getGameTypeIds().contains(oldGameTypeId)) {
                institution.getGameTypeIds().remove(oldGameTypeId);
            }
            institution.getGameTypeIds().add(newGameTypeId);
            mongoRepositoryReactive.saveOrUpdate(institution);
            builder.append("\n Institution Category ");

            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
            query.addCriteria(Criteria.where("gameTypeId").is(oldGameTypeId));

            //update operators licencse
            ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
            if (!licenses.isEmpty()) {
                for (License license : licenses) {
                    logger.info(" updating license with number {}", license.getLicenseNumber());
                    license.setGameTypeId(newGameTypeId);
                    mongoRepositoryReactive.saveOrUpdate(license);
                }
                builder.append("\n Licences ");
            }

            //update operators category details
            InstitutionCategoryDetails institutionCategoryDetails = (InstitutionCategoryDetails) mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
            if (institutionCategoryDetails != null) {
                logger.info("Updating Category Details");
                institutionCategoryDetails.setGameTypeId(newGameTypeId);
                mongoRepositoryReactive.saveOrUpdate(institutionCategoryDetails);
                builder.append("\n Institution Category Details ");
            }

            //update operators AIP document approval
            AIPDocumentApproval aipDocumentApproval = (AIPDocumentApproval) mongoRepositoryReactive.find(query, AIPDocumentApproval.class).block();
            if (aipDocumentApproval != null) {
                logger.info("Updating Aip Document Approval");
                aipDocumentApproval.setGameTypeId(newGameTypeId);
                mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
                builder.append("\n AIP Document Approval ");
            }

            //update operators payment records
            ArrayList<PaymentRecord> paymentRecords = (ArrayList<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());
            if (!paymentRecords.isEmpty()) {
                for (PaymentRecord paymentRecord : paymentRecords) {
                    logger.info("updating payment record with id ---- {}", paymentRecord.getId());
                    paymentRecord.setGameTypeId(newGameTypeId);
                    mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                }
                builder.append("\n Payment Records ");
            }

            //update operators application form
            ApplicationForm applicationForm = (ApplicationForm) mongoRepositoryReactive.find(query, ApplicationForm.class).block();
            if (applicationForm != null) {
                logger.info("Updating Application form");
                applicationForm.setGameTypeId(newGameTypeId);
                mongoRepositoryReactive.saveOrUpdate(applicationForm);
                builder.append("\n Application Form ");
            }

            //udpdate AGents belonging to operator in the category
            query = new Query();
            query.addCriteria(Criteria.where("institutionIds").in(Collections.singletonList(institutionId)));
            query.addCriteria(Criteria.where("gameTypeIds").in(Collections.singletonList(oldGameTypeId)));
            ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
            if (!agents.isEmpty()) {
                for (Agent agent : agents) {
                    logger.info("Updating Agent {}", agent.getFullName());

                    //change the detail that binds them
                    agent.getGameTypeIds().remove(oldGameTypeId);
                    agent.getGameTypeIds().add(newGameTypeId);
                    List<AgentInstitution> agentInstitutions = agent.getAgentInstitutions();
                    for (AgentInstitution agentInstitution : agentInstitutions) {
                        if (StringUtils.equals(agentInstitution.getInstitutionId(), institutionId)
                                && agentInstitution.getGameTypeIds().contains(oldGameTypeId)) {
                            agentInstitution.getGameTypeIds().remove(oldGameTypeId);
                            agent.getGameTypeIds().add(newGameTypeId);
                        }
                    }
                    agent.setAgentInstitutions(agentInstitutions);

                    //update Agent payment records in old category
                    query = new Query();
                    query.addCriteria(Criteria.where("agentId").is(agent.getId()));
                    query.addCriteria(Criteria.where("gameTypeId").is(oldGameTypeId));
                    paymentRecords = (ArrayList<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());
                    if (!paymentRecords.isEmpty()) {
                        for (PaymentRecord paymentRecord : paymentRecords) {
                            logger.info("updating payment record with id ---- {}", paymentRecord.getId());
                            paymentRecord.setGameTypeId(newGameTypeId);
                            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                        }
                    }
                    //update agents licences
                    licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                    if (!licenses.isEmpty()) {
                        for (License license : licenses) {
                            logger.info(" updating license with number {}", license.getLicenseNumber());
                            license.setGameTypeId(newGameTypeId);
                            mongoRepositoryReactive.saveOrUpdate(license);
                        }
                    }
                }
                builder.append("\n Agent");
            }

            query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
            query.addCriteria(Criteria.where("initiatedByLslb").is(false));
            query.addCriteria(Criteria.where("gameTypeId").is(oldGameTypeId));
            ArrayList<AgentApprovalRequest> agentApprovalRequests = (ArrayList<AgentApprovalRequest>) mongoRepositoryReactive.findAll(query, AgentApprovalRequest.class).toStream().collect(Collectors.toList());
            if (!agentApprovalRequests.isEmpty()) {
                for (AgentApprovalRequest agentApprovalRequest : agentApprovalRequests) {
                    agentApprovalRequest.setGameTypeId(newGameTypeId);
                    mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
                }
                builder.append(" \n Agent Approval Requests");
            }

            OperatorCategoryChangeHistory operatorChangeHistory = new OperatorCategoryChangeHistory();
            operatorChangeHistory.setAuditorName(springSecurityAuditorAware.getCurrentAuditorNotNull());
            operatorChangeHistory.setId(UUID.randomUUID().toString());
            operatorChangeHistory.setNewGameTypeId(newGameTypeId);
            operatorChangeHistory.setOldGameTypeId(oldGameTypeId);
            operatorChangeHistory.setAffectedEntities(builder.toString());
            mongoRepositoryReactive.saveOrUpdate(operatorChangeHistory);
            return OKResponse(String.format("Successfully changed \n\n\n => %s", builder.toString()));
        } else {
            return ErrorResponseUtil.BadRequestResponse("Invalid Parameters");
        }
    }


    public void clearAllVigipayCustomerCodes() {
        Query query = new Query();
        query.addCriteria(Criteria.where("vgPayCustomerCode").ne(null));
        ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
        for (Institution institution : institutions) {
            institution.setVgPayCustomerCode(null);
            mongoRepositoryReactive.saveOrUpdate(institution);
        }
    }
}