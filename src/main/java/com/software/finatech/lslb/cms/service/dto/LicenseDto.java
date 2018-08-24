package com.software.finatech.lslb.cms.service.dto;

public class LicenseDto {
    private LicenseRecordDto licenseRecordDto;
    private EnumeratedFactDto licenseStatus;
    private String institutionId;

    public LicenseRecordDto getLicenseRecordDto() {
        return licenseRecordDto;
    }

    public void setLicenseRecordDto(LicenseRecordDto licenseRecordDto) {
        this.licenseRecordDto = licenseRecordDto;
    }

    public EnumeratedFactDto getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(EnumeratedFactDto licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

}
