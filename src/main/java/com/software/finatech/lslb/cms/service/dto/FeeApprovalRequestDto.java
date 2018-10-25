package com.software.finatech.lslb.cms.service.dto;

public class FeeApprovalRequestDto extends AbstractApprovalRequestDto {
    private FeeDto pendingFee;
    private String newEndDate;

    public FeeDto getPendingFee() {
        return pendingFee;
    }

    public void setPendingFee(FeeDto pendingFee) {
        this.pendingFee = pendingFee;
    }

    public String getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(String newEndDate) {
        this.newEndDate = newEndDate;
    }
}
