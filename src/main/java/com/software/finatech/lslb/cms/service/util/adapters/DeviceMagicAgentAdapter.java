package com.software.finatech.lslb.cms.service.util.adapters;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.GameTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DeviceMagicAgentAdapter {
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    public Agent convertDeviceMagicAgentToAgent(DeviceMagicAgent deviceMagicAgent) {
        Agent agent = new Agent();
        agent.setId(UUID.randomUUID().toString());
        agent.setFullName(String.format("%s %s", deviceMagicAgent.getFirstname(), deviceMagicAgent.getLastname()));
        agent.setFirstName(deviceMagicAgent.getFirstname());
        agent.setLastName(deviceMagicAgent.getLastname());
        agent.setEmailAddress(deviceMagicAgent.getEmail());
        agent.setPhoneNumber(deviceMagicAgent.getPhonenumber1());
        agent.getPhoneNumbers().add(deviceMagicAgent.getPhonenumber1());
        agent.getPhoneNumbers().add(deviceMagicAgent.getPhonenumber2());
        agent.setMeansOfId(deviceMagicAgent.getMeansofid());
        agent.setTitle(deviceMagicAgent.getTitle());
        agent.setIdNumber(deviceMagicAgent.getIdnumber());
        String address = buildAddress(deviceMagicAgent.getResidentialaddressstreet(),
                deviceMagicAgent.getResindetialaddresscity(), deviceMagicAgent.getResidentialaddressstate());
        if (!StringUtils.isEmpty(address)) {
            agent.setResidentialAddress(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet1(),
                deviceMagicAgent.getBusinessaddresscity1(), deviceMagicAgent.getBusinessaddressstate1());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet2(),
                deviceMagicAgent.getBusinessaddresscity2(), deviceMagicAgent.getBusinessaddressstate2());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet3(),
                deviceMagicAgent.getBusinessaddresscity3(), deviceMagicAgent.getBusinessaddressstate3());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        address = buildAddress(deviceMagicAgent.getBusinessaddressstreet4(),
                deviceMagicAgent.getBusinessaddresscity4(), deviceMagicAgent.getBusinessaddressstate4());
        if (!StringUtils.isEmpty(address)) {
            agent.getBusinessAddresses().add(address);
        }
        Institution institution = findInstitutionByName(deviceMagicAgent.getOperatorid(), mongoRepositoryReactive);
        if (institution == null) {
            institution = new Institution();
            institution.setId("1234");
        }
        //  if (institution != null) {
        agent.getInstitutionIds().add(institution.getId());
        List<String> gameTypeStrings = Arrays.asList(deviceMagicAgent.getGamingcategopry().split(","));
        for (String gameTypeString : gameTypeStrings) {
            GameType gameType = getGameTypeFromDeviceMagicName(gameTypeString);
            if (gameType != null) {
                agent.getGameTypeIds().add(gameType.getId());
                AgentInstitution agentInstitution = new AgentInstitution();
                agentInstitution.setGameTypeId(gameType.getId());
                agentInstitution.setBusinessAddressList(agent.getBusinessAddresses());
                agentInstitution.setInstitutionId(institution.getId());
            }
        }
        //    }
        return agent;
    }

    private String buildAddress(String streetAddress, String city, String state) {
        if (!StringUtils.isEmpty(streetAddress) && !StringUtils.isEmpty(city)
                && !StringUtils.isEmpty(state)) {
            return String.format("%s, %s, %s", streetAddress.replace(".", ""), city, state);
        }
        return null;
    }

    private Institution findInstitutionByName(String institutionName, MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        if (StringUtils.isEmpty(institutionName)) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionName").regex(institutionName, "i"));
        return (Institution) mongoRepositoryReactive.find(query,
                Institution.class).block();
    }

    private GameType getGameTypeFromDeviceMagicName(String gameTypeString) {
        Map<String, FactObject> gameTypeMap = Mapstore.STORE.get("GameType");
        if (gameTypeMap != null) {
            if (StringUtils.equals("Sports Bet", gameTypeString)) {
                return (GameType) gameTypeMap.get(GameTypeReferenceData.OSB_GAME_TYPE_ID);
            }
            if (StringUtils.equals("Gaming Machine", gameTypeString)) {
                return (GameType) gameTypeMap.get(GameTypeReferenceData.GAMING_MACHINE_ID);
            }
        }
        return null;
    }
}
