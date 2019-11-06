package com.software.finatech.lslb.cms.service.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import static com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData.*;

public class PaymentConfirmationRequest {
    private String agentId;
    private String institutionId;
    private String feePaymentTypeId;
    private String licenseTypeId;
    private double amountPaid;
    private String gameTypeId;
    private String licenseTransferId;
    private Set<String> gamingMachineIds;
    private Set<String> gamingTerminalIds;
    private String paymentRecordId;
    /*
     *   Added to meet implementation logic of Offline Payment Processing
     */
    private String bankName;
    private String tellerNumber;
    private String tellerDate;
    private String modeOfPayment;
    private String paymentConfirmationApprovalRequestType;
    private double amountToBePaid;


    public String getPaymentRecordId(){
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getLicenseTransferId() {
        return licenseTransferId;
    }

    public void setLicenseTransferId(String licenseTransferId) {
        this.licenseTransferId = licenseTransferId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public Set<String> getGamingMachineIds() {
        return gamingMachineIds;
    }

    public void setGamingMachineIds(Set<String> gamingMachineIds) {
        this.gamingMachineIds = gamingMachineIds;
    }

    public Set<String> getGamingTerminalIds() {
        return gamingTerminalIds;
    }

    public void setGamingTerminalIds(Set<String> gamingTerminalIds) {
        this.gamingTerminalIds = gamingTerminalIds;
    }

    public double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }

    public boolean isLicenseTransferPayment() {
        return StringUtils.equals(LICENSE_TRANSFER_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isLicenseFeePayment() {
        return StringUtils.equals(LICENSE_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isLicenseRenewalPayment() {
        return StringUtils.equals(LICENSE_RENEWAL_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isApplicationFeePayment() {
        return StringUtils.equals(APPLICATION_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isTaxPayment() {
        return StringUtils.equals(TAX_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isBeingPaidByOperator() {
        return StringUtils.isNotEmpty(this.institutionId);
    }

    public boolean isBeingPaidByAgent() {
        return StringUtils.isNotEmpty(this.agentId);
    }

    public boolean isForExistingPayment() {
        return StringUtils.isNotEmpty(this.paymentRecordId);
    }

    public String getModeOfPayment() { return modeOfPayment; }

    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }

    public String getPaymentConfirmationApprovalRequestType() { return paymentConfirmationApprovalRequestType; }

    public void setPaymentConfirmationApprovalRequestType(String paymentConfirmationApprovalRequestType) {
        this.paymentConfirmationApprovalRequestType = paymentConfirmationApprovalRequestType;
    }

    public String getBankName() { return bankName; }

    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getTellerNumber() { return tellerNumber; }

    public void setTellerNumber(String tellerNumber) { this.tellerNumber = tellerNumber; }

    public String getTellerDate() { return tellerDate; }

    public void setTellerDate(String tellerDate) {
        this.tellerDate = tellerDate;
    }
}
