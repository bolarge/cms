package com.software.finatech.lslb.cms.service.dto;

/**
 * @author adeyi.adebolu
 * created on 19/06/2019
 */
public class CreateExpiredLicensePaymentDto {
    private double amountPaid;
    private double amountOutstanding;
    private String institutionId;
    private String gameTypeId;
    private String paymentDate;
    private String licenseStartDate;
    private String licenseEndDate;
    private boolean updateLicense;

    public String getLicenseStartDate() {
        return licenseStartDate;
    }

    public void setLicenseStartDate(String licenseStartDate) {
        this.licenseStartDate = licenseStartDate;
    }

    public String getLicenseEndDate() {
        return licenseEndDate;
    }

    public void setLicenseEndDate(String licenseEndDate) {
        this.licenseEndDate = licenseEndDate;
    }

    public boolean isUpdateLicense() {
        return updateLicense;
    }

    public void setUpdateLicense(boolean updateLicense) {
        this.updateLicense = updateLicense;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountOutstanding() {
        return amountOutstanding;
    }

    public void setAmountOutstanding(double amountOutstanding) {
        this.amountOutstanding = amountOutstanding;
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

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}
