package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordStatusDto {
    protected String _id;
    protected String paymentStatusId;
    protected long paymentStatusCount;
    protected double paymentTotalSum;
    protected double paymentOutstandingTotalSum;

    public double getPaymentOutstandingTotalSum() {
        return paymentOutstandingTotalSum;
    }

    public void setPaymentOutstandingTotalSum(double paymentOutstandingTotalSum) {
        this.paymentOutstandingTotalSum = paymentOutstandingTotalSum;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public long getPaymentStatusCount() {
        return paymentStatusCount;
    }

    public void setPaymentStatusCount(long paymentStatusCount) {
        this.paymentStatusCount = paymentStatusCount;
    }

    public double getPaymentTotalSum() {
        return paymentTotalSum;
    }

    public void setPaymentTotalSum(double paymentTotalSum) {
        this.paymentTotalSum = paymentTotalSum;
    }
}
