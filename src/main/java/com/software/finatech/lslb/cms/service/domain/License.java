package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.util.MapValues;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import java.beans.Transient;
import java.util.Map;


@SuppressWarnings("serial")
@Document(collection = "Licenses")
public class License extends AbstractFact {

    protected String licenseStatusId;
    protected String paymentRecordId;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;
    protected String renewalStatus;


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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }


    private static final Logger logger = LoggerFactory.getLogger(License.class);

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLicenseStatusId() {
        return licenseStatusId;
    }

    public void setLicenseStatusId(String licenseStatusId) {
        this.licenseStatusId = licenseStatusId;
    }



    public PaymentRecord getPaymentRecord() {
        return (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
    }


    public LicenseDto convertToDto() {
        LicenseDto licenseDto = new LicenseDto();
        licenseDto.setId(getId());
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
        licenseDto.setLicenseStatus(licenseStatus.convertToDto());
        licenseDto.setPaymentRecord(getPaymentRecord().convertToDto());
        licenseDto.setStartDate(startDate.toString("dd/MM/yyyy HH:mm:ss"));
        licenseDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
        licenseDto.setRenewalStatus(getRenewalStatus());

        licenseDto.setId(id);
        logger.error(licenseDto.toString());
   return licenseDto;
    }


    @Override
    public String getFactName() {
        return "License";
    }
}
