package com.software.finatech.lslb.cms.service.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData.*;

public class FullPaymentConfirmationRequest {
    private String agentId;
    private String institutionId;
    private String feeId;
    private String licenseTypeId;
    private double amountPaid;
    private String gameTypeId;
    private String licenseTransferId;
    private Set<String> gamingMachineIds = new HashSet<>();
    private Set<String> gamingTerminalIds = new HashSet<>();
    /*
   Added to meet implementation logic of Offline Payment Processing
    */
    private String invoiceNumber;
    private String modeOfPaymentId;
    private String paymentConfirmationApprovalRequestType;
    private boolean forIncompleteOfflineLicenceRenewal;
    private boolean forOutsideSystemPayment;
    private boolean isFullPayment;
    private String paymentRecordId;

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

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
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

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    //
    public boolean isLicenseTransferPayment() {
        return StringUtils.equals(LICENSE_TRANSFER_FEE_TYPE_ID, this.feeId);
    }

    public boolean isLicenseFeePayment() {
        return StringUtils.equals(LICENSE_FEE_TYPE_ID, this.feeId);
    }

    public boolean isLicenseRenewalPayment() {
        return StringUtils.equals(LICENSE_RENEWAL_FEE_TYPE_ID, this.feeId);
    }

    public boolean isApplicationFeePayment() {
        return StringUtils.equals(APPLICATION_FEE_TYPE_ID, this.feeId);
    }

    public boolean isTaxPayment() {
        return StringUtils.equals(TAX_FEE_TYPE_ID, this.feeId);
    }

    public boolean isBeingPaidByOperator() {
        return StringUtils.isNotEmpty(this.institutionId);
    }

    public boolean isBeingPaidByAgent() {
        return StringUtils.isNotEmpty(this.agentId);
    }

    //Added to support logic processing for Offline Payments
    public boolean isForOutsideSystemPayment() {
        return forOutsideSystemPayment;
    }

    public void setForOutsideSystemPayment(boolean forOutsideSystemPayment) {
        this.forOutsideSystemPayment = forOutsideSystemPayment;
    }

    public boolean isForIncompleteOfflineLicenceRenewal() {
        return forIncompleteOfflineLicenceRenewal;
    }

    public void setForIncompleteOfflineLicenceRenewal(boolean forIncompleteOfflineLicenceRenewal) {
        this.forIncompleteOfflineLicenceRenewal = forIncompleteOfflineLicenceRenewal;
    }

    public boolean isFullPayment() { return isFullPayment; }

    public void setFullPayment(boolean fullPayment) { isFullPayment = fullPayment; }

    public String getInvoiceNumber() { return invoiceNumber; }

    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getModeOfPaymentId() { return modeOfPaymentId; }

    public void setModeOfPaymentId(String modeOfPaymentId) { this.modeOfPaymentId = modeOfPaymentId; }

    public String getPaymentConfirmationApprovalRequestType() { return paymentConfirmationApprovalRequestType; }

    public void setPaymentConfirmationApprovalRequestType(String paymentConfirmationApprovalRequestType) { this.paymentConfirmationApprovalRequestType = paymentConfirmationApprovalRequestType; }

    public boolean isInstitutionPayment() {
        return StringUtils.isEmpty(this.getAgentId())
                && this.gamingMachineIds.isEmpty()
                && this.gamingTerminalIds.isEmpty()
                && !StringUtils.isEmpty(this.getInstitutionId());
    }

    public boolean isAgentPayment() {
        return !StringUtils.isEmpty(this.getAgentId())
                && this.gamingTerminalIds.isEmpty()
                && this.gamingMachineIds.isEmpty()
                && StringUtils.isEmpty(this.getInstitutionId());
    }

    public boolean isGamingMachinePayment() {
        return StringUtils.isEmpty(this.getAgentId())
                && !this.gamingMachineIds.isEmpty()
                && this.gamingTerminalIds.isEmpty()
                && !StringUtils.isEmpty(this.getInstitutionId())
                && StringUtils.isEmpty(this.licenseTransferId);
    }

    public boolean isGamingTerminalPayment() {
        return StringUtils.isEmpty(this.institutionId)
                && !StringUtils.isEmpty(this.agentId)
                && this.gamingMachineIds.isEmpty()
                && !this.gamingTerminalIds.isEmpty()
                && StringUtils.isEmpty(this.licenseTransferId);
    }

    public boolean isFirstPayment() {
        return StringUtils.isEmpty(this.invoiceNumber);
    }
}
