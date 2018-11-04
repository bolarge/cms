package com.software.finatech.lslb.cms.service.dto;

import org.joda.time.LocalDateTime;

public class LicenseTransferDecision {
    private String userFullName;
    private String oldStatus;
    private String newStatus;
    private String decisionTime;
    private String decisionDate;

    public LicenseTransferDecision() {
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getDecisionTime() {
        return decisionTime;
    }

    public void setDecisionTime(String decisionTime) {
        this.decisionTime = decisionTime;
    }

    public String getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(String decisionDate) {
        this.decisionDate = decisionDate;
    }

    public static LicenseTransferDecision fromNameNewAndOldStatus(String name, String newStatus, String oldStatus) {
        LicenseTransferDecision licenseTransferDecision = new LicenseTransferDecision();
        licenseTransferDecision.setDecisionDate(LocalDateTime.now().toString("dd-MM-yyyy"));
        licenseTransferDecision.setDecisionTime(LocalDateTime.now().toString("HH:mm:ss a"));
        licenseTransferDecision.setNewStatus(newStatus);
        licenseTransferDecision.setOldStatus(oldStatus);
        licenseTransferDecision.setUserFullName(name);
        return licenseTransferDecision;
    }
}
