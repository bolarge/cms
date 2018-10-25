package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AgentDto;
import com.software.finatech.lslb.cms.service.dto.AgentInstitutionDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.adapters.AgentInstitutionAdapter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
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
    protected List<AgentInstitution> agentInstitutions = new ArrayList<>();
    protected LocalDate dateOfBirth;
    protected String residentialAddress;
    protected List<String> businessAddresses = new ArrayList<>();
    protected String meansOfId;
    protected String idNumber;
    protected String bvn;
    protected Set<String> institutionIds;
    protected Set<String> gameTypeIds;
    protected String vgPayCustomerCode;
    private String title;
    private String authInfoId;
    private String agentId;
    private boolean enabled;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAuthInfoId() {
        return authInfoId;
    }

    public void setAuthInfoId(String authInfoId) {
        this.authInfoId = authInfoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVgPayCustomerCode() {
        return vgPayCustomerCode;
    }

    public void setVgPayCustomerCode(String vgPayCustomerCode) {
        this.vgPayCustomerCode = vgPayCustomerCode;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
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

    public List<AgentInstitution> getAgentInstitutions() {
        return agentInstitutions;
    }

    public void setAgentInstitutions(List<AgentInstitution> agentInstitutions) {
        this.agentInstitutions = agentInstitutions;
    }

    public List<String> getBusinessAddresses() {
        return businessAddresses;
    }

    public void setBusinessAddresses(List<String> businessAddresses) {
        this.businessAddresses = businessAddresses;
    }

    public AgentDto convertToDto() {
        AgentDto agentDto = new AgentDto();
        agentDto.setEmailAddress(getEmailAddress());
        agentDto.setFullName(getFullName());
        agentDto.setPhoneNumber(getPhoneNumber());
        agentDto.setId(getId());
        agentDto.setEnabled(isEnabled());
        agentDto.setTitle(getTitle());
        agentDto.setAgentId(getAgentId());
        agentDto.setId(getId());
        return agentDto;
    }

    public AgentDto convertToFullDetailDto() {
        AgentDto agentDto = convertToDto();
        agentDto.setMeansOfId(getMeansOfId());
        agentDto.setIdNumber(getIdNumber());
        agentDto.setLastName(getLastName());
        agentDto.setFirstName(getFirstName());
        agentDto.setResidentialAddress(getResidentialAddress());
        agentDto.setBusinessAddresses(getBusinessAddresses());
        agentDto.setBvn(getBvn());
        LocalDate dateOfBirth = getDateOfBirth();
        if (dateOfBirth != null) {
            agentDto.setDateOfBirth(dateOfBirth.toString("yyyy-MM-dd"));
        }
        agentDto.setAgentId(getAgentId());
        agentDto.setInstitutions(getInstitutions());
        agentDto.setGameTypes(getGameTypes());
        agentDto.setBusinessAddresses(getBusinessAddresses());
        agentDto.setAgentInstitutions(convertAgentInstitutions());
        return agentDto;
    }


    private List<AgentInstitutionDto> convertAgentInstitutions() {
        List<AgentInstitutionDto> agentInstitutionDtos = new ArrayList<>();
        for (AgentInstitution agentInstitution : getAgentInstitutions()) {
            agentInstitutionDtos.add(AgentInstitutionAdapter.convertAgentInstitutionToDto(agentInstitution, mongoRepositoryReactive));
        }
        return agentInstitutionDtos;
    }


    public List<EnumeratedFactDto> getGameTypes() {
        List<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
        for (String gameTypeId : getGameTypeIds()) {
            GameType gameType = getGameType(gameTypeId);
            if (gameType != null) {
                EnumeratedFactDto enumeratedFactDto = new EnumeratedFactDto();
                enumeratedFactDto.setId(gameType.getId());
                enumeratedFactDto.setName(gameType.getName());
                enumeratedFactDtos.add(enumeratedFactDto);
            }
        }

        return enumeratedFactDtos;
    }

    public List<InstitutionDto> getInstitutions() {
        List<InstitutionDto> institutionList = new ArrayList<>();
        for (String institutionId : getInstitutionIds()) {
            Institution institution = getInstitution(institutionId);
            if (institution != null) {
                InstitutionDto temp = new InstitutionDto();
                temp.setId(institution.getId());
                temp.setInstitutionName(institution.getInstitutionName());
                institutionList.add(temp);
            }
        }
        return institutionList;
    }

    @Override
    public String getFactName() {
        return "Agent";
    }

    private GameType getGameType(String gameTypeId) {
        if (StringUtils.isEmpty(gameTypeId)) {
            return null;
        }
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
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public AuthInfo getAuthInfo() {
        if (StringUtils.isEmpty(this.authInfoId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(this.authInfoId, AuthInfo.class).block();
    }
}
