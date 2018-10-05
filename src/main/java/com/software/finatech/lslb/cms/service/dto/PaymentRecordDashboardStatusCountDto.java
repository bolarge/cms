package com.software.finatech.lslb.cms.service.dto;

public class PaymentRecordDashboardStatusCountDto {
    protected long partPaymentTotalCount;
    protected long fullPaymentTotalCount;
    protected long unPaidTotalCount;
    protected long totalInvoices;


    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(long totalInvoices) {
        this.totalInvoices = totalInvoices;
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

    public long getFullPaymentTotalCount() {
        return fullPaymentTotalCount;
    }

    public void setFullPaymentTotalCount(long fullPaymentTotalCount) {
        this.fullPaymentTotalCount = fullPaymentTotalCount;
    }




}
