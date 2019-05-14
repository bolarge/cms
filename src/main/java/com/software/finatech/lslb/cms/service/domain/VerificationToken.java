package com.software.finatech.lslb.cms.service.domain;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VerificationTokens")
public class VerificationToken  extends AbstractFact{
    private String confirmationToken;
    private String authInfoId;
    private Boolean activated;
    private Boolean expired;
    private String healthInstitutionId;
    //Expires in 24hrs
    protected DateTime expiryDate;
    private  boolean forResourceOwnerUserCreation;

    public boolean isForResourceOwnerUserCreation() {
        return forResourceOwnerUserCreation;
    }

    public void setForResourceOwnerUserCreation(boolean forResourceOwnerUserCreation) {
        this.forResourceOwnerUserCreation = forResourceOwnerUserCreation;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String getAuthInfoId() {
        return authInfoId;
    }

    public void setAuthInfoId(String authInfoId) {
        this.authInfoId = authInfoId;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getHealthInstitutionId() {
        return healthInstitutionId;
    }

    public void setHealthInstitutionId(String healthInstitutionId) {
        this.healthInstitutionId = healthInstitutionId;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String getFactName() {
        return "VerificationToken";
    }

}
