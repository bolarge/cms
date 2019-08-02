package com.software.finatech.lslb.cms.service.dto;

public class AuditTrailDto {
    protected String id;
    protected String auditDate;
    protected String auditDateTime;
    protected String auditActionId;
    protected String performedBy;
    protected String actionPerformed;
    protected Boolean viaAPI;
    protected String remoteAddress;
    protected String auditAction;
    protected String owner;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditDateTime() {
        return auditDateTime;
    }

    public void setAuditDateTime(String auditDateTime) {
        this.auditDateTime = auditDateTime;
    }

    public void setAuditAction(String auditAction) {
        this.auditAction = auditAction;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAuditActionId() {
        return auditActionId;
    }

    public void setAuditActionId(String auditActionId) {
        this.auditActionId = auditActionId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(String actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public Boolean getViaAPI() {
        return viaAPI;
    }

    public void setViaAPI(Boolean viaAPI) {
        this.viaAPI = viaAPI;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

}
