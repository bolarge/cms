package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionCategoryDetailsDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import com.software.finatech.lslb.cms.service.model.applicantMembers.OperatorMemberDetails;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@SuppressWarnings("serial")
@Document(collection = "Institutions")
public class Institution extends AbstractFact {

    protected String institutionName;
    protected String emailAddress;
    protected String description;
    protected Boolean active;
    private String address;
    protected String phoneNumber;
    protected String vgPayCustomerCode;
    protected String website;
    private String tradeName;
    private List<String> institutionCategoryDetailIds = new ArrayList<>();
    protected Set<String> gameTypeIds = new HashSet<>();
    private OperatorMemberDetails operatorMemberDetails;


    public List<String> getInstitutionCategoryDetailIds() {
        return institutionCategoryDetailIds;
    }

    public OperatorMemberDetails getOperatorMemberDetails() {
        return operatorMemberDetails;
    }

    public void setOperatorMemberDetails(OperatorMemberDetails operatorMemberDetails) {
        this.operatorMemberDetails = operatorMemberDetails;
    }

    public void setInstitutionCategoryDetailIds(List<String> institutionCategoryDetailIds) {
        this.institutionCategoryDetailIds = institutionCategoryDetailIds;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVgPayCustomerCode() {
        return vgPayCustomerCode;
    }

    public void setVgPayCustomerCode(String vgPayCustomerCode) {
        this.vgPayCustomerCode = vgPayCustomerCode;
    }

    @Transient
    protected Set<GameType> gameTypes = new java.util.HashSet<>();

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

    public Set<GameType> getGameTypes() {
        return gameTypes;
    }

    public void setGameTypes(Set<GameType> gameTypes) {
        this.gameTypes = gameTypes;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getFactName() {
        return "Institution";
    }


    public InstitutionDto convertToDto() {
        InstitutionDto institutionDto = new InstitutionDto();
        institutionDto.setActive(getActive());
        institutionDto.setId(getId());
        institutionDto.setEmailAddress(getEmailAddress());
        institutionDto.setPhoneNumber(getPhoneNumber());
        institutionDto.setTenantId(getTenantId());
        institutionDto.setInstitutionName(getInstitutionName());
        institutionDto.setDescription(getDescription());
        institutionDto.setActive(getActive());
        institutionDto.setInstitutionCategoryDetails(getInstitutionCategoryDetailsList());
        institutionDto.setGameTypes(getGameTypeDtos());
        return institutionDto;
    }


    public List<InstitutionCategoryDetailsDto> getInstitutionCategoryDetailsList() {
        List<InstitutionCategoryDetailsDto> dtos = new ArrayList<>();
        if (!this.institutionCategoryDetailIds.isEmpty()) {
            for (String id : this.institutionCategoryDetailIds) {
                InstitutionCategoryDetails categoryDetails = getInstitutionCategoryDetailById(id);
                if (categoryDetails != null) {
                    dtos.add(categoryDetails.convertToDto());
                }
            }
        }
        return dtos;
    }

    public InstitutionCategoryDetails getInstitutionCategoryDetailById(String id) {
        return (InstitutionCategoryDetails) mongoRepositoryReactive.findById(id, InstitutionCategoryDetails.class).block();
    }


    private Set<GameTypeDto> getGameTypeDtos() {
        Set<GameTypeDto> dtos = new HashSet<>();
        for (String gameTypeId : this.gameTypeIds) {
            GameType type = getGameType(gameTypeId);
            if (type != null) {
                dtos.add(type.convertToDto());
            }
        }
        return dtos;
    }

    @Override
    public String toString() {
        return this.getInstitutionName();
    }

    public GameType getGameType(String gameTypeId) {
        if (StringUtils.isEmpty(gameTypeId)) {
            return null;
        }
        Map<String, FactObject> gameTypeMap = Mapstore.STORE.get("GameType");
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


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Institution == false) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Institution that = (Institution) obj;

        Object thisObject = this.getId();
        Object thatObject = that.getId();

        if ((thisObject != null) && (thatObject != null)) {
            return thisObject.equals(thatObject);
        } else {
            return false;
        }
    }
}
