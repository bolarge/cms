package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.adapters.model.LslbGamingTerminal;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LslbGamingTerminalAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LslbGamingTerminalAdapter.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public void saveLslbGamingTerminalToDb(LslbGamingTerminal lslbGamingTerminal) {
        Agent agent = lslbGamingTerminal.getAgent();
        GameType gameType = lslbGamingTerminal.getGameType();
        Query query = new Query();
        query.addCriteria(Criteria.where("gameTypeId").is(gameType.getId()));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_TERMINAL_ID));
        query.addCriteria(Criteria.where("effectiveDate").is(LocalDate.now().withDayOfYear(1)));
        query.addCriteria(Criteria.where("agentId").is(agent.getId()));

        License license = (License) mongoRepositoryReactive.find(query, License.class).block();
        if (license == null) {
            license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_TERMINAL_ID);
            LocalDate effectiveDate = LocalDate.now().dayOfYear().withMinimumValue();
            LocalDate expiryDate = effectiveDate.plusMonths(gameType.getGamingTerminalLicenseDurationMonths());
            expiryDate = expiryDate.minusDays(1);
            license.setEffectiveDate(effectiveDate);
            license.setExpiryDate(expiryDate);
            license.setGameTypeId(gameType.getId());
            license.setAgentId(agent.getId());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setLicenseNumber(generateLicenseNumberForPaymentRecord(gameType));
            mongoRepositoryReactive.saveOrUpdate(license);
        }
        Machine machine;
        machine = findMachineBySerialNumber(lslbGamingTerminal.getMachineId());
        if (machine == null) {
            machine = new Machine();
            machine.setId(UUID.randomUUID().toString());
        }
        Institution institution = lslbGamingTerminal.getInstitution();
        machine.setGameTypeId(gameType.getId());
        String address = lslbGamingTerminal.getAgentAddress().replace("\"", "").trim();
        //address = address.replace("\"", "");
        // address = address.trim();
        machine.setMachineAddress(address);
        machine.setInstitutionId(institution.getId());
        machine.setMachineStatusId(MachineStatusReferenceData.ACTIVE_ID);
        machine.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
        machine.setAgentId(agent.getId());
        machine.setSerialNumber(lslbGamingTerminal.getMachineId());
        machine.setFromLiveData(true);
        machine.setLicenseId(license.getId());
        machine.setLicenseNumber(license.getLicenseNumber());
        mongoRepositoryReactive.saveOrUpdate(machine);
        logger.info("Saving Gaming Terminal {}", lslbGamingTerminal.getMachineId());
    }

    private Machine findMachineBySerialNumber(String machineId) {
        return (Machine) mongoRepositoryReactive.find(Query.query(Criteria.where("serialNumber").is(machineId)), Machine.class).block();
    }

    private String generateLicenseNumberForPaymentRecord(GameType gameType) {
        String prefix = "LSLB-";
        prefix = prefix + "GT-";
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(100, 1000));
        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
            prefix = prefix + gameType.getShortCode() + "-";
        }
        return String.format("%s%s%s", prefix, randomDigit, LocalDateTime.now().getSecondOfMinute());
    }
}
