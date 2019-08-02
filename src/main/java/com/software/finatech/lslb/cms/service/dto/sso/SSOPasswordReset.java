package com.software.finatech.lslb.cms.service.dto.sso;

import io.advantageous.boon.json.annotations.SerializedName;

public class SSOPasswordReset {
    @SerializedName("Exists")
    protected Boolean exists ;
    @SerializedName("TotalRecords")
    protected int totalRecords ;
    @SerializedName("CustomValue")
    protected String customValue;
    @SerializedName("Response")
    protected SSOPasswordResetResponse response ;

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getCustomValue() {
        return customValue;
    }

    public void setCustomValue(String customValue) {
        this.customValue = customValue;
    }

    public SSOPasswordResetResponse getResponse() {
        return response;
    }

    public void setResponse(SSOPasswordResetResponse response) {
        this.response = response;
    }
}
