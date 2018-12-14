package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RenewalFormStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.beans.Transient;
import java.util.Arrays;
import java.util.List;
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
    protected LocalDate lastSentExpiryEmailDate;
    protected String licenseTransferId;
    protected String licenseChangeReason;
    protected String oldLicenseStatusId;
    private String renewalFormId;
    private boolean renewalInProgress;


    public boolean isRenewalInProgress() {
        return renewalInProgress;
    }

    public void setRenewalInProgress(boolean renewalInProgress) {
        this.renewalInProgress = renewalInProgress;
    }

    public String getRenewalFormId() {
        return renewalFormId;
    }

    public void setRenewalFormId(String renewalFormId) {
        this.renewalFormId = renewalFormId;
    }

    public String getLicenseChangeReason() {
        return licenseChangeReason;
    }

    public String getOldLicenseStatusId() {
        return oldLicenseStatusId;
    }

    public void setOldLicenseStatusId(String oldLicenseStatusId) {
        this.oldLicenseStatusId = oldLicenseStatusId;
    }

    public void setLicenseChangeReason(String licenseChangeReason) {
        this.licenseChangeReason = licenseChangeReason;
    }

    public String getLicenseTransferId() {
        return licenseTransferId;
    }

    public void setLicenseTransferId(String licenseTransferId) {
        this.licenseTransferId = licenseTransferId;
    }

    public LocalDate getLastSentExpiryEmailDate() {
        return lastSentExpiryEmailDate;
    }

    public void setLastSentExpiryEmailDate(LocalDate lastSentExpiryEmailDate) {
        this.lastSentExpiryEmailDate = lastSentExpiryEmailDate;
    }

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

    public PaymentRecord getPaymentRecord() {
        if (StringUtils.isEmpty(this.paymentRecordId)) {
            return null;
        }
        return (PaymentRecord) mongoRepositoryReactive.findById(this.paymentRecordId, PaymentRecord.class).block();
    }

    public LicenseType getLicenseType() {
        if (licenseTypeId == null) {
            return null;
        }
        Map licenseTypeMap = Mapstore.STORE.get("LicenseType");

        LicenseType licenseType = null;
        if (licenseTypeMap != null) {
            licenseType = (LicenseType) licenseTypeMap.get(licenseTypeId);
        }
        if (licenseType == null) {
            licenseType = (LicenseType) mongoRepositoryReactive.findById(licenseTypeId, LicenseType.class).block();
            if (licenseType != null && licenseTypeMap != null) {
                licenseTypeMap.put(licenseTypeId, licenseType);
            }
        }
        return licenseType;
    }

    public LicenseDto convertToDto() {
        LicenseDto licenseDto = new LicenseDto();
        String ownerName = null;
        licenseDto.setId(getId());
        licenseDto.setGamingMachineId(getGamingMachineId());
        licenseDto.setPaymentRecordId(getPaymentRecordId());
        licenseDto.setLicenseTypeId(getLicenseTypeId());
        licenseDto.setLicenseTypeName(String.valueOf(getLicenseType()));
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
            ownerName = agent.getFullName();
            licenseDto.setAgentNumber(agent.getAgentId());
        }
        Institution institution = getInstitution();
        if (institution != null) {
            InstitutionCategoryDetails institutionCategoryDetails = getInstitutionCategoryDetails();
            if (institutionCategoryDetails != null) {
                ownerName = institutionCategoryDetails.getTradeName();
            }
            if (StringUtils.isEmpty(ownerName)) {
                ownerName = institution.getInstitutionName();
            }
            licenseDto.setInstitutionId(getInstitutionId());
        }
        LicenseStatus licenseStatus = getLicenseStatus();
        if (licenseStatus != null) {
            licenseDto.setLicenseStatusId(licenseStatus.getId());
            licenseDto.setLicenseStatusName(licenseStatus.getName());
        }
        PaymentRecord paymentRecord = getPaymentRecord();
        if (paymentRecord != null) {
            licenseDto.setAmountPaid(paymentRecord.getAmountPaid());
        }
        licenseDto.setOwnerName(ownerName);
        licenseDto.setRenewalStatus(getRenewalStatus());
        licenseDto.setRenewalFormId(getRenewalFormId());
        licenseDto.setRenewalInProgress(isRenewalInProgress());
        return licenseDto;
    }

    private InstitutionCategoryDetails getInstitutionCategoryDetails() {
        if (!StringUtils.isEmpty(this.institutionId) && !StringUtils.isEmpty(this.gameTypeId)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(this.institutionId));
            query.addCriteria(Criteria.where("gameTypeId").is(this.gameTypeId));
            return (InstitutionCategoryDetails) mongoRepositoryReactive.find(query, InstitutionCategoryDetails.class).block();
        }
        return null;
    }

    public boolean isInstitutionLicense() {
        return StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, this.licenseTypeId);
    }

    public boolean isGamingMachineLicense() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, this.licenseTypeId);
    }

    public boolean isGamingTerminalLicense() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, this.licenseTypeId);
    }

    public boolean isAgentLicense() {
        return StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, this.licenseTypeId);
    }

    public String getEndDateString() {
        LocalDate date = getExpiryDate();
        if (date != null) {
            return date.toString("dd-MM-yyyy");
        }
        return null;
    }

    public String getStartDateString() {
        LocalDate date = getEffectiveDate();
        if (date != null) {
            return date.toString("dd-MM-yyyy");
        }
        return null;
    }

    public String getLicenseStatusName() {
        LicenseStatus status = getLicenseStatus();
        if (status != null) {
            return status.getName();
        }
        return null;
    }

    public LicenseTransfer getLicenseTransfer() {
        if (StringUtils.isEmpty(this.licenseTransferId)) {
            return null;
        }
        return (LicenseTransfer) mongoRepositoryReactive.findById(this.licenseTransferId, LicenseTransfer.class).block();
    }

    public boolean isSuspendedLicence() {
        return StringUtils.equals(LicenseStatusReferenceData.LICENSE_SUSPENDED_ID, this.licenseStatusId);
    }

    public boolean isTerminatedLicence() {
        return StringUtils.equals(LicenseStatusReferenceData.LICENSE_TERMINATED_ID, this.licenseStatusId);
    }

    public boolean isRevokedLicence() {
        return StringUtils.equals(LicenseStatusReferenceData.LICENSE_REVOKED_ID, this.licenseStatusId);
    }

    public boolean isExpiredLicence() {
        return StringUtils.equals(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID, this.licenseStatusId);
    }

    public String getOwnerName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        Agent agent = getAgent();
        if (agent != null) {
            return agent.getFullName();
        }
        return null;
    }


    public boolean isAIPRelatedLicense() {
        return LicenseStatusReferenceData.getAIPLicenseStatues().contains(this.licenseStatusId);
    }

    @Override
    public String getFactName() {
        return "License";
    }
}
