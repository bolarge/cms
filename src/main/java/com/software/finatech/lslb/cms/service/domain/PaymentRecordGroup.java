package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecordGroup")
public class PaymentRecordGroup extends AbstractFact{

    private double totalAmount;
    private String institutionId;
    private List<String> paymentRecordIds = new ArrayList<>();
    private LocalDateTime dateTimeCreated;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public List<String> getPaymentRecordIds() {
        return paymentRecordIds;
    }

    public void setPaymentRecordIds(List<String> paymentRecordIds) {
        this.paymentRecordIds = paymentRecordIds;
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    @Override
    public String getFactName() {
        return "PaymentRecordGroup";
    }
}
