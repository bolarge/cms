package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.InstitutionCategoryDetails;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.InstitutionLoadDetails;
import com.software.finatech.lslb.cms.service.dto.InstitutionUpload;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExistingOperatorLoader {
    private static final Logger logger = LoggerFactory.getLogger(ExistingOperatorLoader.class);
    @Autowired
    private GameTypeService gameTypeService;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    private DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd/MM/yyyy");



    @PostConstruct
    public void init(){
        Query query = new Query();
        query.addCriteria(Criteria.where("fromLiveData").is(true));
        ArrayList<Institution> operators = (ArrayList<Institution>)mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
        for (Institution institution: operators) {
            query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(institution.getId()));
            ArrayList<License> licenses = (ArrayList<License>)mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
            for (License license:licenses) {
                mongoRepositoryReactive.delete(license);
            }
            ArrayList<InstitutionCategoryDetails> institutionCategoryDetails = (ArrayList<InstitutionCategoryDetails>)mongoRepositoryReactive.findAll(query, InstitutionCategoryDetails.class).toStream().collect(Collectors.toList());
            for (InstitutionCategoryDetails categoryDetails: institutionCategoryDetails) {
                mongoRepositoryReactive.delete(categoryDetails);
            }
            mongoRepositoryReactive.delete(institution);
        }
    }



    public void loadFromCsv(MultipartFile multipartFile) throws LicenseServiceException {
        if (multipartFile.isEmpty()) {
            throw new LicenseServiceException("File is empty");
        }
        Map<String, InstitutionUpload> institutionUploadMap = new HashMap<>();
        try {
            byte[] bytes = multipartFile.getBytes();
            String completeData = new String(bytes);
            String[] rows = completeData.split("\\r?\\n");
            for (int i = 2; i < rows.length; i++) {
                // String[] columns = rows[i].split(",");
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (columns.length < 15) {
                    throw new LicenseServiceException("File is less than 15 columns");
                }
                try {
                    String institutionName = columns[3];
                    if (!StringUtils.isEmpty(institutionName)) {
                        InstitutionUpload institutionUpload = institutionUploadMap.get(institutionName);
                        if (institutionUpload == null) {
                            institutionUpload = new InstitutionUpload();
                            institutionUpload.setLine(rows[i]);
                            institutionUpload.setInstitutionName(institutionName);
                            institutionUpload.setDescription(columns[5]);
                            institutionUpload.setEmailAddress(columns[8]);
                            institutionUpload.setPhoneNumber(columns[9]);
                            String address = columns[10].replace("\"", "").replace("?", "");
                            institutionUpload.setAddress(address);
                        }
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
                        institutionUpload.getInstitutionLoadDetails().add(loadDetails);
                        institutionUploadMap.put(institutionName, institutionUpload);
                    }
                } catch (IllegalArgumentException e) {
                    throw new LicenseServiceException("Error occurred while parsing date ");
                }
            }
            loadInstitutionsFromMap(institutionUploadMap);
        } catch (IOException e) {
            logger.error("IO Exception ", e);
            throw new LicenseServiceException("An error occurred while parsing the file");
        } catch (Exception e) {
            logger.error("An error occurred ", e);
            throw new LicenseServiceException("An error occurred while parsing file");
        }
    }

    private void loadInstitutionsFromMap(Map<String, InstitutionUpload> institutionUploadMap) {
        for (InstitutionUpload institutionUpload : institutionUploadMap.values()) {
            Institution pendingInstitution = new Institution();
            pendingInstitution.setId(UUID.randomUUID().toString());
            pendingInstitution.setInstitutionName(institutionUpload.getInstitutionName());
            pendingInstitution.setActive(true);
            pendingInstitution.setDescription(institutionUpload.getDescription());
            pendingInstitution.setEmailAddress(institutionUpload.getEmailAddress());
            pendingInstitution.setPhoneNumber(String.format("0%s", institutionUpload.getPhoneNumber()));
            pendingInstitution.setFromLiveData(true);
            pendingInstitution.setAddress(institutionUpload.getAddress());
            for (InstitutionLoadDetails institutionLoadDetails : institutionUpload.getInstitutionLoadDetails()) {
                InstitutionCategoryDetails institutionCategoryDetails = new InstitutionCategoryDetails();
                institutionCategoryDetails.setId(UUID.randomUUID().toString());
                institutionCategoryDetails.setFirstCommencementDate(institutionLoadDetails.getFirstCommencementDate());
                institutionCategoryDetails.setGameTypeId(institutionLoadDetails.getGameTypeId());
                institutionCategoryDetails.setTradeName(institutionLoadDetails.getTradeName());
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
                    continue;
                }
                pendingLicense.setLicenseStatusId(licenseStatusId);
                //    pendingLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
                pendingLicense.setLicenseNumber(generateLicenseNumber(institutionLoadDetails.getGameTypeId()));
                mongoRepositoryReactive.saveOrUpdate(pendingLicense);
                mongoRepositoryReactive.saveOrUpdate(institutionCategoryDetails);
            }
            mongoRepositoryReactive.saveOrUpdate(pendingInstitution);

        }
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
}
