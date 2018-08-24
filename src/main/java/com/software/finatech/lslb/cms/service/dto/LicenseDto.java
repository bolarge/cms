package com.software.finatech.lslb.cms.service.dto;

public class LicenseDto {
    private LicenseRecordDto licenseRecord;
    private EnumeratedFactDto licenseStatus;
    private String institutionId;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LicenseRecordDto getLicenseRecordDto() {
        return licenseRecord;
    }

    public void setLicenseRecordDto(LicenseRecordDto licenseRecordDto) {
        this.licenseRecord = licenseRecordDto;
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
