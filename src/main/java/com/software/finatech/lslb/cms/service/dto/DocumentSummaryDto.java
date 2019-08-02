package com.software.finatech.lslb.cms.service.dto;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class DocumentSummaryDto {

    private String id;
    private String documentTypeId;
    private String approvalRequestStatusId;
    private LocalDate nextReminderDate;

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public LocalDate getNextReminderDate() {
        return nextReminderDate;
    }

    public void setNextReminderDate(LocalDate nextReminderDate) {
        this.nextReminderDate = nextReminderDate;
    }
}
