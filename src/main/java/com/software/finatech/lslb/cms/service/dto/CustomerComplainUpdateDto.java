package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.referencedata.CustomerComplainStatusReferenceData;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;

public class CustomerComplainUpdateDto {
    @NotEmpty(message = "user id cannot be empty")
    private String userId;
    @NotEmpty(message = "customer complain id cannot be empty")
    private String customerComplainId;
    @NotEmpty(message = "customer complain status id cannot be empty")
    private String customerComplainStatusId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerComplainId() {
        return customerComplainId;
    }

    public void setCustomerComplainId(String customerComplainId) {
        this.customerComplainId = customerComplainId;
    }

    public String getCustomerComplainStatusId() {
        return customerComplainStatusId;
    }

    public void setCustomerComplainStatusId(String customerComplainStatusId) {
        this.customerComplainStatusId = customerComplainStatusId;
    }

    public boolean isClosedUpdate() {
        return StringUtils.equals(CustomerComplainStatusReferenceData.CLOSED_ID, this.customerComplainStatusId);
    }

    public boolean isResolvedUpdate() {
        return StringUtils.equals(CustomerComplainStatusReferenceData.RESOLVED_ID, this.customerComplainStatusId);
    }
}
