package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ExistingOperatorLoader {
    @Autowired
    private GameTypeService gameTypeService;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    private DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd/MM/yyyy");

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
                if (columns.length < 11) {
                    throw new LicenseServiceException("File is less than 11 columns");
                } else {
                    try {
                        String institutionName = columns[1];
                        if (!StringUtils.isEmpty(institutionName)) {
                            InstitutionUpload institutionUpload = institutionUploadMap.get(institutionName);
                            if (institutionUpload == null) {
                                institutionUpload = new InstitutionUpload();
                                institutionUpload.setLine(rows[i]);
                                institutionUpload.setInstitutionName(institutionName);
                                institutionUpload.setDescription(columns[3]);
                                institutionUpload.setEmailAddress(columns[5]);
                                institutionUpload.setPhoneNumber(columns[6]);
                                String address = columns[7].replace("\"", "").replace("?", "");
                                institutionUpload.setAddress(address);
                            }
                            String gameTypeSearchKey = columns[4];
                            GameType gameType = gameTypeService.findGameTypeBySearchKey(gameTypeSearchKey);
                            if (gameType == null) {
                                throw new LicenseServiceException(String.format("Game type with search key %s not found", gameTypeSearchKey));
                            }
                            InstitutionLoadDetails loadDetails = new InstitutionLoadDetails();

                            if (!StringUtils.isEmpty(columns[8])) {
                                LocalDate licenseStartDate = dateTimeFormat.parseLocalDate(columns[8]);
                                loadDetails.setLicenseStartDate(licenseStartDate);
                            }
                            if (!StringUtils.isEmpty(columns[9])) {
                                LocalDate licenseEndDate = dateTimeFormat.parseLocalDate(columns[9]);
                                loadDetails.setLicenseEndDate(licenseEndDate);
                            }
                            if (!StringUtils.isEmpty(columns[10])) {
                                LocalDate licenseFirstDate = dateTimeFormat.parseLocalDate(columns[10]);
                                loadDetails.setFirstCommencementDate(licenseFirstDate);
                            }
                            loadDetails.setGameTypeId(gameType.getId());
                            loadDetails.setTradeName(columns[2]);
                            institutionUpload.getInstitutionLoadDetails().add(loadDetails);
                            institutionUploadMap.put(institutionName, institutionUpload);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new LicenseServiceException("Error occurred while parsing date ");
                    }
                }
            }
            loadInstitutionsFromMap(institutionUploadMap);
        } catch (IOException e) {
            throw new LicenseServiceException("An error occurred while parsing the file");
        } catch (Exception e) {
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
            pendingInstitution.setPhoneNumber(institutionUpload.getPhoneNumber());
            pendingInstitution.setAddress(institutionUpload.getAddress());
            for (InstitutionLoadDetails institutionLoadDetails : institutionUpload.getInstitutionLoadDetails()) {
                GameType gameType = gameTypeService.findById(institutionLoadDetails.getGameTypeId());
                InstitutionCategoryDetails institutionCategoryDetails = new InstitutionCategoryDetails();
                institutionCategoryDetails.setFirstCommencementDate(institutionLoadDetails.getFirstCommencementDate());
                institutionCategoryDetails.setGameTypeId(gameType.getId());
                institutionCategoryDetails.setGameTypeName(gameType.toString());
                institutionCategoryDetails.setTradeName(institutionLoadDetails.getTradeName());
                pendingInstitution.getInstitutionCategoryDetailsList().add(institutionCategoryDetails);
                License pendingLicense = new License();
                pendingLicense.setId(UUID.randomUUID().toString());
                pendingLicense.setInstitutionId(pendingInstitution.getId());
                pendingLicense.setEffectiveDate(institutionLoadDetails.getLicenseStartDate());
                pendingLicense.setExpiryDate(institutionLoadDetails.getLicenseEndDate());
                pendingLicense.setGameTypeId(institutionLoadDetails.getGameTypeId());
                pendingLicense.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
                pendingLicense.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
                pendingLicense.setLicenseNumber(generateLicenseNumber(institutionLoadDetails.getGameTypeId()));
                mongoRepositoryReactive.saveOrUpdate(pendingLicense);
            }
            mongoRepositoryReactive.saveOrUpdate(pendingInstitution);
        }
    }

    private String generateLicenseNumber(String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        LocalDateTime time = LocalDateTime.now();
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10, 1000));
        return String.format("LSLB-OP-%s-%s%s", gameType.getShortCode(), randomDigit, time.getSecondOfMinute());
    }
}
