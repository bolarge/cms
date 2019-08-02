package com.software.finatech.lslb.cms.service.dto;


import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstitutionDto {
    protected String id;
    protected String institutionName;
    protected String emailAddress;
    protected String description;
    protected Boolean active;
    protected String licenseId;
    private List<InstitutionCategoryDetailsDto> institutionCategoryDetails = new ArrayList<>();
    private Set<String> directorsNames = new HashSet<>();
    private Set<String> shareHolderNames = new HashSet<>();
    private String address;
    private Set<String> phoneNumbers;

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getShareHolderNames() {
        return shareHolderNames;
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

    protected String tenantId;
    protected Boolean status;
    protected Set<String> gameTypeIds = new java.util.HashSet<>();


    public List<InstitutionCategoryDetailsDto> getInstitutionCategoryDetails() {
        return institutionCategoryDetails;
    }

    public void setInstitutionCategoryDetails(List<InstitutionCategoryDetailsDto> institutionCategoryDetails) {
        this.institutionCategoryDetails = institutionCategoryDetails;
    }

    @Transient
    protected Set<GameTypeDto> gameTypes = new java.util.HashSet<>();

    public Set<GameTypeDto> getGameTypes() {
        return gameTypes;
    }

    public void setGameTypes(Set<GameTypeDto> gameTypes) {
        this.gameTypes = gameTypes;
    }

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

}
