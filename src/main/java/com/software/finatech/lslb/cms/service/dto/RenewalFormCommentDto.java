package com.software.finatech.lslb.cms.service.dto;

public class RenewalFormCommentDto {
    protected String paymentRecordId;
    protected String comment;

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
