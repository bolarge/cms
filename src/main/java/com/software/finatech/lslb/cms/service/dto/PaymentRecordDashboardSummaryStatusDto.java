package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDashboardSummaryStatusDto {
    protected long partPaymentTotalCount;
    protected double partPaymentTotalAmount;
    protected long fullPaymentTotalCount;
    protected double fullPaymentTotalAmount;
    protected double unPaidTotalAmount;
    protected long  unPaidTotalCount;
    protected long totalInvoices;


    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public double getUnPaidTotalAmount() {
        return unPaidTotalAmount;
    }

    public void setUnPaidTotalAmount(double unPaidTotalAmount) {
        this.unPaidTotalAmount = unPaidTotalAmount;
    }

    public long getUnPaidTotalCount() {
        return unPaidTotalCount;
    }

    public void setUnPaidTotalCount(long unPaidTotalCount) {
        this.unPaidTotalCount = unPaidTotalCount;
    }

    public long getPartPaymentTotalCount() {
        return partPaymentTotalCount;
    }

    public void setPartPaymentTotalCount(long partPaymentTotalCount) {
        this.partPaymentTotalCount = partPaymentTotalCount;
    }

    public double getPartPaymentTotalAmount() {
        return partPaymentTotalAmount;
    }

    public void setPartPaymentTotalAmount(double partPaymentTotalAmount) {
        this.partPaymentTotalAmount = partPaymentTotalAmount;
    }

    public long getFullPaymentTotalCount() {
        return fullPaymentTotalCount;
    }

    public void setFullPaymentTotalCount(long fullPaymentTotalCount) {
        this.fullPaymentTotalCount = fullPaymentTotalCount;
    }

    public double getFullPaymentTotalAmount() {
        return fullPaymentTotalAmount;
    }

    public void setFullPaymentTotalAmount(double fullPaymentTotalAmount) {
        this.fullPaymentTotalAmount = fullPaymentTotalAmount;
    }


}
