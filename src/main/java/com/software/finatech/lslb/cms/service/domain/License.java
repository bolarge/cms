package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.LocalDate;
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
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String renewalStatus;
    protected String licenseTypeId;
    protected String agentId;
    protected String gamingMachineId;
    protected boolean firstPayment;

    public boolean isFirstPayment() {
        return firstPayment;
    }

    public void setFirstPayment(boolean firstPayment) {
        this.firstPayment = firstPayment;
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

    public void setLicenseType(String licenseTypeId) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }


    private static final Logger logger = LoggerFactory.getLogger(License.class);

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
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
        Map licenseTypeMap = Mapstore.STORE.get("LicenseTypes");

        LicenseStatus licenseStatus = null;
        LicenseType licenseType = null;
        if (licenseStatusMap != null) {
            licenseStatus = (LicenseStatus) licenseStatusMap.get(licenseStatusId);
        }
        if (licenseStatus == null) {
            licenseStatus = (LicenseStatus) mongoRepositoryReactive.findById(licenseStatusId, LicenseStatus.class).block();
            if (licenseStatus != null && licenseStatusMap != null) {
                licenseStatusMap.put(licenseStatusId, licenseStatus);
            }
        }
        if (licenseType == null) {
            licenseType = (LicenseType) mongoRepositoryReactive.findById(licenseTypeId, LicenseType.class).block();
            if (licenseType != null && licenseTypeMap != null) {
                licenseTypeMap.put(licenseStatusId, licenseType);
            }
        }
        licenseDto.setLicenseStatus(licenseStatus.convertToDto());
        licenseDto.setLicenseType(licenseType.convertToDto());
        //licenseDto.setGamingMachineId(getGamingMachineId());
       // licenseDto.setAgentId(getAgentId());
        if(getPaymentRecord()!=null){
            licenseDto.setPaymentRecord(getPaymentRecord().convertToDto());
        }
            if(getStartDate()!=null){
                licenseDto.setStartDate(getStartDate().toString("dd-MM-yyyy"));
                licenseDto.setEndDate(getStartDate().toString("dd-MM-yyyy"));
            }

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
