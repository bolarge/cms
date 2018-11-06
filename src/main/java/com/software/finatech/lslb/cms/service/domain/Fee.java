package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.FeeDto;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "Fees")
public class Fee extends AbstractFact {
    protected boolean active;
    protected double amount;
    protected String gameTypeId;
    protected String feePaymentTypeId;
    protected String licenseTypeId;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private LocalDate nextNotificationDate;

    public LocalDate getNextNotificationDate() {
        return nextNotificationDate;
    }

    public void setNextNotificationDate(LocalDate nextNotificationDate) {
        this.nextNotificationDate = nextNotificationDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    private String revenueNameId;

    public String getRevenueNameId() {
        return revenueNameId;
    }

    public void setRevenueNameId(String revenueNameId) {
        this.revenueNameId = revenueNameId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
    }

    public LicenseType getLicenseType() {
        if (this.licenseTypeId == null) {
            return null;
        }
        Map licenseTypeMap = Mapstore.STORE.get("LicenseType");

        LicenseType licenseType = null;
        if (licenseTypeMap != null) {
            licenseType = (LicenseType) licenseTypeMap.get(this.licenseTypeId);
        }
        if (licenseType == null) {
            licenseType = (LicenseType) mongoRepositoryReactive.findById(this.licenseTypeId, LicenseType.class).block();
            if (licenseType != null && licenseTypeMap != null) {
                licenseTypeMap.put(this.licenseTypeId, licenseType);
            }
        }
        return licenseType;
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

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        }
        return gameType.getName();
    }


    public FeePaymentType getFeePaymentType() {
        if (feePaymentTypeId == null) {
            return null;
        }
        Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
        FeePaymentType feePaymentType = null;
        if (feePaymentTypeMap != null) {
            feePaymentType = (FeePaymentType) feePaymentTypeMap.get(feePaymentTypeId);
        }
        if (feePaymentType == null) {
            feePaymentType = (FeePaymentType) mongoRepositoryReactive.findById(feePaymentTypeId, FeePaymentType.class).block();
            if (feePaymentType != null && feePaymentTypeMap != null) {
                feePaymentTypeMap.put(feePaymentTypeId, feePaymentType);
            }
        }
        return feePaymentType;
    }

    public String getFeePaymentTypeName() {
        FeePaymentType feePaymentType = getFeePaymentType();
        if (feePaymentType != null) {
            return feePaymentType.getName();
        }
        return null;
    }

    public FeeDto convertToDto() {
        FeeDto feeDto = new FeeDto();
        feeDto.setAmount(getAmount());
        feeDto.setId(getId());
        feeDto.setActive(isActive());
        LicenseType licenseType = getLicenseType();
        if (licenseType != null) {
            feeDto.setRevenueName(licenseType.toString());
            feeDto.setRevenueId(getLicenseTypeId());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            feeDto.setGameTypeName(gameType.getName());
            feeDto.setGameTypeId(getGameTypeId());
        }
        FeePaymentType feePaymentType = getFeePaymentType();
        if (feePaymentType != null) {
            feeDto.setFeePaymentTypeName(feePaymentType.getName());
            feeDto.setFeePaymentTypeId(getFeePaymentTypeId());
        }

        LocalDate startDate = getEffectiveDate();
        if (startDate != null) {
            feeDto.setEffectiveDate(startDate.toString("dd-MM-yyyy"));
        }
        LocalDate endDate = getEndDate();
        if (endDate != null) {
            feeDto.setEndDate(endDate.toString("dd-MM-yyyy"));
        }
        return feeDto;
    }

    public boolean isLicenseRenewalFee() {
        return StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isLicenseFee() {
        return StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isApplicationFee() {
        return StringUtils.equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isLicenseTransferFee() {
        return StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_TRANSFER_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    @Override
    public String getFactName() {
        return "Fee";
    }
}
