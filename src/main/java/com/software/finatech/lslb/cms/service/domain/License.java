package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.beans.Transient;
import java.util.Map;


@SuppressWarnings("serial")
@Document(collection = "Licenses")
public class License extends AbstractFact {

    protected String licenseStatusId;
    protected String institutionId;
    protected String gameTypeId;
    protected String paymentRecordId;
    protected LocalDate effectiveDate;
    protected LocalDate expiryDate;
    protected String renewalStatus;
    protected String licenseTypeId;
    protected String agentId;
    protected String gamingMachineId;
    protected String parentLicenseId;
    protected String licenseNumber;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    @Transient
    public void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public PaymentRecord getPaymentRecord() {
        return (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
    }

    public LicenseStatus getLicenseStatus() {
        if (StringUtils.isEmpty(this.licenseStatusId)) {
            return null;
        }
        Map licenseStatusMap = Mapstore.STORE.get("LicenseStatus");
        LicenseStatus licenseStatus = null;
        if (licenseStatusMap != null) {
            licenseStatus = (LicenseStatus) licenseStatusMap.get(licenseStatusId);
        }
        if (licenseStatus == null) {
            licenseStatus = (LicenseStatus) mongoRepositoryReactive.findById(licenseStatusId, LicenseStatus.class).block();
            if (licenseStatus != null && licenseStatusMap != null) {
                licenseStatusMap.put(licenseStatusId, licenseStatus);
            }
        }
        return licenseStatus;
    }


    public GameType getGameType() {
        if (gameTypeId == null) {
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

    public Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(getAgentId(), Agent.class).block();
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public LicenseDto convertToDto() {
        LicenseDto licenseDto = new LicenseDto();
        licenseDto.setId(getId());
        licenseDto.setGamingMachineId(getGamingMachineId());
        licenseDto.setPaymentRecordId(getPaymentRecordId());
        licenseDto.setLicenseTypeId(getLicenseTypeId());
        licenseDto.setLicenseTypeName(getLicenseTypeId());
        licenseDto.setId(id);
        licenseDto.setLicenseNumber(getLicenseNumber());
        if (getEffectiveDate() != null && getExpiryDate() != null) {
            licenseDto.setStartDate(getEffectiveDate().toString("dd-MM-yyyy"));
            licenseDto.setEndDate(getExpiryDate() == null ? null : getExpiryDate().toString("dd-MM-yyyy"));
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            licenseDto.setGameTypeId(getGameTypeId());
            licenseDto.setGameTypeName(gameType.getName());
        }
        Agent agent = getAgent();
        if (agent != null) {
            licenseDto.setAgentId(getAgentId());
            licenseDto.setAgentName(agent.getFullName());
        }
        Institution institution = getInstitution();
        if (institution != null) {
            licenseDto.setInstitutionName(institution.getInstitutionName());
            licenseDto.setInstitutionId(getInstitutionId());
        }
        LicenseStatus licenseStatus = getLicenseStatus();
        if (licenseStatus != null) {
            licenseDto.setLicenseStatusId(licenseStatus.getId());
            licenseDto.setLicenseStatusName(licenseStatus.getName());
        }
        return licenseDto;
    }

    @Override
    public String getFactName() {
        return "License";
    }
}
