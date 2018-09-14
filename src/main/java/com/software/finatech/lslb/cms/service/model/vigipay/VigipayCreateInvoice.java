package com.software.finatech.lslb.cms.service.model.vigipay;

import java.util.List;

public class VigipayCreateInvoice {
    private String CustomerCode;
    private List<VigipayRecipient> Recipients;
    private String CorporateRevenueCode;
    private String LocationCode;
    private String CurrencyCode;
    private String Note;
    private List<VigipayInvoiceItem> InvoiceItems;
    private String CorporateCode;
    private String InvoiceReference;
    private String InvoiceDate;
    private String DueDate;
    private boolean EnforceDueDate;
    private int InvoiceType;
    private int InvoiceAction;
    private boolean CreateContacts;

    public boolean isCreateContacts() {
        return CreateContacts;
    }

    public void setCreateContacts(boolean createContacts) {
        CreateContacts = createContacts;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public List<VigipayRecipient> getRecipients() {
        return Recipients;
    }

    public void setRecipients(List<VigipayRecipient> recipients) {
        Recipients = recipients;
    }

    public String getCorporateRevenueCode() {
        return CorporateRevenueCode;
    }

    public void setCorporateRevenueCode(String corporateRevenueCode) {
        CorporateRevenueCode = corporateRevenueCode;
    }

    public String getLocationCode() {
        return LocationCode;
    }

    public void setLocationCode(String locationCode) {
        LocationCode = locationCode;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public List<VigipayInvoiceItem> getInvoiceItems() {
        return InvoiceItems;
    }

    public void setInvoiceItems(List<VigipayInvoiceItem> invoiceItems) {
        InvoiceItems = invoiceItems;
    }

    public String getCorporateCode() {
        return CorporateCode;
    }

    public void setCorporateCode(String corporateCode) {
        CorporateCode = corporateCode;
    }

    public String getInvoiceReference() {
        return InvoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        InvoiceReference = invoiceReference;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public boolean isEnforceDueDate() {
        return EnforceDueDate;
    }

    public void setEnforceDueDate(boolean enforceDueDate) {
        EnforceDueDate = enforceDueDate;
    }

    public int getInvoiceType() {
        return InvoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        InvoiceType = invoiceType;
    }

    public int getInvoiceAction() {
        return InvoiceAction;
    }

    public void setInvoiceAction(int invoiceAction) {
        InvoiceAction = invoiceAction;
    }
}
