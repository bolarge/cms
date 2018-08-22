package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.ApplicationFormStatus;

import java.util.Set;

public class ApplicationFormDto {
    private GameTypeDto gameType;
    private EnumeratedFactDto status;
    private Set<PaymentRecordDto> paymentRecordDtoSet;
    private EnumeratedFactDto applicationFormType;
    private String formName;
    private String institutionId;
    private String institutionName;

    public GameTypeDto getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeDto gameType) {
        this.gameType = gameType;
    }

    public EnumeratedFactDto getStatus() {
        return status;
    }

    public void setStatus(EnumeratedFactDto status) {
        this.status = status;
    }

    public Set<PaymentRecordDto> getPaymentRecordDtoSet() {
        return paymentRecordDtoSet;
    }

    public void setPaymentRecordDtoSet(Set<PaymentRecordDto> paymentRecordDtoSet) {
        this.paymentRecordDtoSet = paymentRecordDtoSet;
    }

    public EnumeratedFactDto getApplicationFormType() {
        return applicationFormType;
    }

    public void setApplicationFormType(EnumeratedFactDto applicationFormType) {
        this.applicationFormType = applicationFormType;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
