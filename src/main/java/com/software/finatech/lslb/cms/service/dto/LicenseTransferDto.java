package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class LicenseTransferDto {
    private String fromInstitutionName;
    private String fromInstitutionId;
    private String toInstitutionName;
    private String toInstitutionId;
    private String statusName;
    private String statusId;
    private String gameTypeName;
    private String gameTypeId;
    private String licenseNumber;
    private List<LicenseTransferDecision> transferDecisions = new ArrayList<>();

    public String getFromInstitutionName() {
        return fromInstitutionName;
    }

    public void setFromInstitutionName(String fromInstitutionName) {
        this.fromInstitutionName = fromInstitutionName;
    }

    public String getFromInstitutionId() {
        return fromInstitutionId;
    }

    public void setFromInstitutionId(String fromInstitutionId) {
        this.fromInstitutionId = fromInstitutionId;
    }

    public String getToInstitutionName() {
        return toInstitutionName;
    }

    public void setToInstitutionName(String toInstitutionName) {
        this.toInstitutionName = toInstitutionName;
    }

    public String getToInstitutionId() {
        return toInstitutionId;
    }

    public void setToInstitutionId(String toInstitutionId) {
        this.toInstitutionId = toInstitutionId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public List<LicenseTransferDecision> getTransferDecisions() {
        return transferDecisions;
    }

    public void setTransferDecisions(List<LicenseTransferDecision> transferDecisions) {
        this.transferDecisions = transferDecisions;
    }
}
