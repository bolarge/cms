package com.software.finatech.lslb.cms.service.dto;


import com.software.finatech.lslb.cms.service.domain.InstitutionCategoryDetails;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InstitutionDto {
    protected String id;
    protected String institutionName;
    protected String emailAddress;
    protected String description;
    protected Boolean active;
    protected String phoneNumber;
    protected String sectorId;
    protected String bankAccountId;
    protected String billCycleId;
    protected String dataSourceId;
    protected String licenseId;
    protected String vatFormulaId;
    protected String settlementCycleId;
    private List<InstitutionCategoryDetailsDto> institutionCategoryDetails = new ArrayList<>();

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

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSectorId() {
        return sectorId;
    }

    public void setSectorId(String sectorId) {
        this.sectorId = sectorId;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getBillCycleId() {
        return billCycleId;
    }

    public void setBillCycleId(String billCycleId) {
        this.billCycleId = billCycleId;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSoureId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getVatFormulaId() {
        return vatFormulaId;
    }

    public void setVatFormulaId(String vatFormulaId) {
        this.vatFormulaId = vatFormulaId;
    }

    public String getSettlementCycleId() {
        return settlementCycleId;
    }

    public void setSettlementCycleId(String settlementCycleId) {
        this.settlementCycleId = settlementCycleId;
    }
}
