package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AuditTrailDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "AuditTrails")
public class AuditTrail extends AbstractFact {

    protected LocalDate auditDate;
    protected LocalDateTime auditDateTime;
    protected String healthInstitutionId;
    protected String auditActionId;
    protected String performedBy;
    protected String actionPerformed;
    protected Boolean viaAPI;
    protected String remoteAddress;
    protected String auditAction;
    protected String owner;

    public void setAuditAction(String auditAction) {
        this.auditAction = auditAction;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDate getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }

    public LocalDateTime getAuditDateTime() {
        return auditDateTime;
    }

    public void setAuditDateTime(LocalDateTime auditDateTime) {
        this.auditDateTime = auditDateTime;
    }

    public String getHealthInstitutionId() {
        return healthInstitutionId;
    }

    public void setHealthInstitutionId(String healthInstitutionId) {
        this.healthInstitutionId = healthInstitutionId;
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

    public AuditTrailDto convertToDto() {
        AuditTrailDto auditTrailDto = new AuditTrailDto();
        auditTrailDto.setAuditDate(getAuditDate().toString("dd-MM-yyyy"));
        auditTrailDto.setAuditDateTime((getAuditDateTime().toString(DateTimeFormat.longDateTime())));
        auditTrailDto.setActionPerformed(getActionPerformed());
        auditTrailDto.setPerformedBy(getPerformedBy());
        auditTrailDto.setOwner(getOwner());
        auditTrailDto.setRemoteAddress(getRemoteAddress());
        auditTrailDto.setViaAPI(getViaAPI());
        auditTrailDto.setId(getId());

        if (auditActionId != null && !auditActionId.isEmpty()) {
            Map<String, FactObject> auditActionMap = Mapstore.STORE.get("AuditAction");
            AuditAction auditAction = null;
            if (auditActionMap != null) {
                auditAction = (AuditAction) auditActionMap.get(auditActionId);
            }
            if (auditAction == null) {
                auditAction = (AuditAction) mongoRepositoryReactive.findById(auditActionId, AuditAction.class).block();
                if (auditAction != null && auditActionMap != null) {
                    auditActionMap.put(auditAction.getId(), auditAction);
                }
            }
            auditTrailDto.setAuditAction(auditAction == null ? null : auditAction.getName());
        }
        return auditTrailDto;
    }

    @Override
    public String getFactName() {
        return "AuditTrail";
    }
}
