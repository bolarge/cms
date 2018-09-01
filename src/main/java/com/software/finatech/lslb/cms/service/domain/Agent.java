package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AgentDto;
import com.software.finatech.lslb.cms.service.dto.AgentInstitutionDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "Agents")
public class Agent extends AbstractFact {

    protected String passportId;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String emailAddress;
    protected String phoneNumber;
    protected Set<AgentInstitution> agentInstitutions = new HashSet<>();
    protected DateTime dateOfBirth;
    protected String residentialAddress;
    protected Set<String> businessAddresses = new HashSet<>();
    protected String meansOfId;
    protected String idNumber;
    protected String bvn;
    protected Set<String> institutionIds;
    protected Set<String> gameTypeIds;

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getMeansOfId() {
        return meansOfId;
    }

    public void setMeansOfId(String meansOfId) {
        this.meansOfId = meansOfId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public Set<String> getInstitutionIds() {
        return institutionIds;
    }

    public void setInstitutionIds(Set<String> institutionIds) {
        this.institutionIds = institutionIds;
    }

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<AgentInstitution> getAgentInstitutions() {
        return agentInstitutions;
    }

    public void setAgentInstitutions(Set<AgentInstitution> agentInstitutions) {
        this.agentInstitutions = agentInstitutions;
    }

    public Set<String> getBusinessAddresses() {
        return businessAddresses;
    }

    public void setBusinessAddresses(Set<String> businessAddresses) {
        this.businessAddresses = businessAddresses;
    }

    public AgentDto convertToDto() {
        AgentDto agentDto = new AgentDto();
        agentDto.setDateOfBirth(getDateOfBirth().toString("dd/MM/yyyy"));
        agentDto.setEmailAddress(getEmailAddress());
        agentDto.setResidentialAddress(getResidentialAddress());
        agentDto.setBusinessAddresses(getBusinessAddresses());
        agentDto.setFirstName(getFirstName());
        agentDto.setFullName(getFullName());
        agentDto.setLastName(getLastName());
        agentDto.setMeansOfId(getMeansOfId());
        agentDto.setIdNumber(getIdNumber());
        agentDto.setAgentInstitutions(convertAgentInstitutions(getAgentInstitutions()));
        return agentDto;
    }


    private Set<AgentInstitutionDto> convertAgentInstitutions(Set<AgentInstitution> agentInstitutions){
       Set<AgentInstitutionDto> agentInstitutionDtos = new HashSet<>();
        for (AgentInstitution agentInstitution: agentInstitutions) {
           AgentInstitutionDto agentInstitutionDto = new AgentInstitutionDto();
            GameType gameType = getGameType(agentInstitution.getGameTypeId());
            if (gameType != null){
                agentInstitutionDto.setGameTypeDescription(gameType.getDescription());
                agentInstitutionDto.setGameTypeId(gameType.getId());
                agentInstitutionDto.setGameTypeName(gameType.getName());
            }

            Institution institution = getInstitution(agentInstitution.getInstitutionId());
            if (institution != null){
                agentInstitutionDto.setInstitutionName(institution.getInstitutionName());
                agentInstitutionDto.setInstitutionId(institution.getId());
            }
            agentInstitutionDtos.add(agentInstitutionDto);
        }
        return agentInstitutionDtos;
    }

    @Override
    public String getFactName() {
        return "Agent";
    }

    private GameType getGameType(String gameTypeId) {
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }

    public Institution getInstitution(String institutionId) {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }
}
