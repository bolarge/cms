package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ExistingGamingMachineLoader {

    private static final Logger logger = LoggerFactory.getLogger(ExistingGamingMachineLoader.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private GameTypeService gameTypeService;


    public void loadMachines(MultipartFile multipartFile,
                             String institutionId,
                             String gameTypeId) throws LicenseServiceException {
        GameType gameType = gameTypeService.findById(gameTypeId);
        if (gameType == null) {
            throw new LicenseServiceException("GameType with id " + gameTypeId + " not found");
        }
        License license = new License();
        license.setId(UUID.randomUUID().toString());
        license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_MACHINE_ID);
        LocalDate effectiveDate = LocalDate.now().dayOfYear().withMinimumValue();
        LocalDate expiryDate = effectiveDate.plusMonths(gameType.getGamingMachineLicenseDurationMonths());
        expiryDate = expiryDate.minusDays(1);
        license.setEffectiveDate(effectiveDate);
        license.setExpiryDate(expiryDate);
        license.setGameTypeId(gameTypeId);
        license.setInstitutionId(institutionId);
        license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
        license.setLicenseNumber(generateLicenseNumberForPaymentRecord(gameType));
        mongoRepositoryReactive.saveOrUpdate(license);
        if (!multipartFile.isEmpty()) {
            try {
                byte[] bytes = multipartFile.getBytes();
                String completeData = new String(bytes);
                String[] rows = completeData.split("\\r?\\n");
                Map<String, PendingMachine> pendingMachineMap = new HashMap<>();
                for (int i = 1; i < rows.length; i++) {
                    String[] columns = rows[i].split(",");
                    if (columns.length < 5) {
                        throw new LicenseServiceException("Line has less than required(5) fields {}" + rows[i]);
                    } else {
                        try {
                            String machineNumber = columns[0];
                            Query query = new Query();
                            query.addCriteria(Criteria.where("serialNumber").is(machineNumber));
                            Machine machine = (Machine) mongoRepositoryReactive.find(query, Machine.class).block();
                            if (machine == null) {
                                machine = new Machine();
                                machine.setId(UUID.randomUUID().toString());
                                machine.setSerialNumber(columns[0]);
                                machine.setManufacturer(columns[1]);
                                machine.setInstitutionId(institutionId);
                                machine.setMachineStatusId(MachineStatusReferenceData.ACTIVE_ID);
                                machine.setOldInstitutionId("DOXX");
                                machine.setLicenseId(license.getId());
                                machine.setGameTypeId(gameTypeId);
                                machine.setMachineTypeId(MachineTypeReferenceData.GAMING_MACHINE_ID);
                                machine.setMachineAddress(columns[2]);
                                mongoRepositoryReactive.saveOrUpdate(machine);
                                logger.info("Creating machine {}", machineNumber);
                            } else {
                                logger.info("Updating machine {}", machineNumber);
                            }
                            MachineGame machineGame = new MachineGame();
                            machineGame.setId(UUID.randomUUID().toString());
                            machineGame.setGameName(columns[3]);
                            machineGame.setGameVersion(columns[4]);
                            machineGame.setActive(true);
                            machineGame.setMachineId(machine.getId());
                            mongoRepositoryReactive.saveOrUpdate(machineGame);
                            logger.info("Saved Game {}:{} , To Machine {}", columns[3], columns[4], machineNumber);
                        } catch (Exception e) {
                            logger.error(String.format("Error parsing line %s", rows[i]), e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("An error occurred  while reading file", e);
            }
        }
    }


    private String generateLicenseNumberForPaymentRecord(GameType gameType) {
        String prefix = "LSLB-";
        prefix = prefix + "GM-";
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(100, 1000));
        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
            prefix = prefix + gameType.getShortCode() + "-";
        }
        return String.format("%s%s%s", prefix, randomDigit, LocalDateTime.now().getSecondOfMinute());
    }
}