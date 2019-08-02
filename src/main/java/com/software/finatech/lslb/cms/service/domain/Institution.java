package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionCategoryDetailsDto;
import com.software.finatech.lslb.cms.service.dto.InstitutionDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "Institutions")
public class Institution extends AbstractFact {

    protected String institutionName;
    protected String emailAddress;
    protected String description;
    protected Boolean active;
    private String address;
    protected String vgPayCustomerCode;
    protected String website;
    private String tradeName;
    private Set<String> institutionCategoryDetailIds = new HashSet<>();
    protected Set<String> gameTypeIds = new HashSet<>();
    private Set<String> directorsNames = new HashSet<>();
    private Set<String> shareHolderNames = new HashSet<>();
    private boolean fromLiveData;
    private Set<String> phoneNumbers = new HashSet<>();

    public Institution() {}

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Set<String> getShareHolderNames() {
        return shareHolderNames;
    }

    public boolean isFromLiveData() {
        return fromLiveData;
    }

    public void setFromLiveData(boolean fromLiveData) {
        this.fromLiveData = fromLiveData;
    }

    public void setShareHolderNames(Set<String> shareHolderNames) {
        this.shareHolderNames = shareHolderNames;
    }

    public Set<String> getDirectorsNames() {
        return directorsNames;
    }

    public void setDirectorsNames(Set<String> directorsNames) {
        this.directorsNames = directorsNames;
    }

    public Set<String> getInstitutionCategoryDetailIds() {
        return institutionCategoryDetailIds;
    }

    public void setInstitutionCategoryDetailIds(Set<String> institutionCategoryDetailIds) {
        this.institutionCategoryDetailIds = institutionCategoryDetailIds;
    }

    String getTradeName() {
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

    @Override
    public String getFactName() {
        return "Institution";
    }

    public InstitutionDto convertToDto() {
        InstitutionDto institutionDto = new InstitutionDto();
        institutionDto.setActive(getActive());
        institutionDto.setId(getId());
        institutionDto.setEmailAddress(getEmailAddress());
        institutionDto.setPhoneNumbers(getPhoneNumbers());
        institutionDto.setInstitutionName(getInstitutionName());
        institutionDto.setDescription(getDescription());
        institutionDto.setActive(getActive());
        institutionDto.setGameTypes(getGameTypeDtos());
        return institutionDto;
    }

    public InstitutionDto convertToFullDto() {
        InstitutionDto dto = convertToDto();
        List<InstitutionCategoryDetailsDto> categoryDetailsDtos = new ArrayList<>();
        Set<String> shareHolderNames = new HashSet<>();
        Set<String> directorNameSet = new HashSet<>();
        if (!this.institutionCategoryDetailIds.isEmpty()) {
            for (String id : this.institutionCategoryDetailIds) {
                InstitutionCategoryDetails categoryDetails = getInstitutionCategoryDetailById(id);
                if (categoryDetails != null) {
                    categoryDetailsDtos.add(categoryDetails.convertToDto());
                    directorNameSet.addAll(categoryDetails.getDirectorsNames());
                    shareHolderNames.addAll(categoryDetails.getShareHolderNames());
                }
            }
        }
        dto.setInstitutionCategoryDetails(categoryDetailsDtos);
        dto.setDirectorsNames(directorNameSet);
        dto.setShareHolderNames(shareHolderNames);
        dto.setAddress(getAddress());
        return dto;
    }


    private InstitutionCategoryDetails getInstitutionCategoryDetailById(String id) {
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
        return (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
    }
}
