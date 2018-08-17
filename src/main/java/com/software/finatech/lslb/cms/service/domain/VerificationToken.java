package com.software.finatech.lslb.cms.userservice.domain;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VerificationTokens")
public class VerificationToken  extends AbstractFact{
    protected String confirmationToken;
    protected String authInfoId;
    protected Boolean activated;
    protected Boolean expired;
    protected String healthInstitutionId;
    //Expires in 24hrs
    protected DateTime expiryDate;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VerificationToken == false) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        VerificationToken that = (VerificationToken) obj;

        Object thisObject = this.getId();
        Object thatObject = that.getId();

        if ((thisObject != null) && (thatObject != null)) {
            return thisObject.equals(thatObject);
        } else {
            return false;
        }
    }
}
