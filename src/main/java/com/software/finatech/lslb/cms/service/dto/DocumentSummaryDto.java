package com.software.finatech.lslb.cms.service.dto;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class DocumentSummaryDto extends AbstractApprovalRequestDto {

    private String id;
    private String documentTypeId;
    private LocalDate nextReminderDate;


    @Override
    public String getId() {
        return id;
    }

    @Override
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
