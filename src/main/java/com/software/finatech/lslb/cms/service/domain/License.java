package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "Licenses")
public class License extends AbstractFact {

    protected String paymentRecordId;
    protected String licenseStatusId;
    protected String institutionId;
    protected DateTime startDate;
    protected DateTime endDate;
    protected String parentLicenseId;
    protected String gameTypeId;

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    private GameType getGameType() {
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

    public Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public PaymentRecord getPaymentRecord() {
        return (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
    }

    private LicenseStatus getLicenseStatus() {
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


    public LicenseDto convertToDto() {
        LicenseDto licenseDto = new LicenseDto();
        Institution institution = getInstitution();
        if (institution != null) {
            licenseDto.setInstitutionId(institutionId);
            licenseDto.setInstitutionName(institution.getInstitutionName());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            licenseDto.setGameType(gameType.convertToDto());
        }
        LicenseStatus licenseStatus = getLicenseStatus();
        if (licenseStatus != null) {
            licenseDto.setLicenseStatus(licenseStatus.convertToDto());
        }
        licenseDto.setStartDate(startDate.toString("dd/MM/yyyy HH:mm:ss"));
        licenseDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
        PaymentRecord paymentRecord = getPaymentRecord();
        if (paymentRecord != null) {
            licenseDto.setPaymentRecord(paymentRecord.convertToDto());
        }
        return licenseDto;
    }


    @Override
    public String getFactName() {
        return "License";
    }
}
