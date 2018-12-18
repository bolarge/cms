package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.Machine;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.MachineStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.adapters.model.LslbGamingTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LslbGamingTerminalAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LslbGamingTerminalAdapter.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public void saveLslbGamingTerminalToDb(LslbGamingTerminal lslbGamingTerminal) {
        Machine machine = new Machine();
        Agent agent = lslbGamingTerminal.getAgent();
        machine.setId(UUID.randomUUID().toString());
        machine.setGameTypeId(lslbGamingTerminal.getGameTypeId());
        machine.setMachineAddress(lslbGamingTerminal.getAgentAddress());
        machine.setInstitutionId(lslbGamingTerminal.getInstitutionId());
        machine.setMachineStatusId(MachineStatusReferenceData.ACTIVE_ID);
        machine.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
        machine.setAgentId(agent.getId());
        machine.setSerialNumber(lslbGamingTerminal.getMachineId());
        machine.setFromLiveData(true);
        mongoRepositoryReactive.saveOrUpdate(machine);

        logger.info("Saving Gaming Terminal {}", lslbGamingTerminal.getMachineId());
    }
}
