package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    protected GameType getGameType() {
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
    private static final Logger logger = LoggerFactory.getLogger(License.class);

    @Transient
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Transient
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
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

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
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
        licenseDto.setId(getId());
        licenseDto.setLicenseStatus(getLicenseStatus().convertToDto());
        licenseDto.setPaymentRecord(getPaymentRecord().convertToDto());
        licenseDto.setId(id);
        logger.error(licenseDto.toString());
   return licenseDto;
    }


    @Override
    public String getFactName() {
        return "License";
    }
}
