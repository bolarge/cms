package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class FeeEndDateUpdateDto {

    @NotEmpty(message = "Please provide fee id")
   private String feeId;
    @NotEmpty(message = "please provide end date")
   private String endDate;

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
