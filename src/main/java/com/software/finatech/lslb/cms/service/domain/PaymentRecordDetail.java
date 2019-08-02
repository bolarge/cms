package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentInvoiceResponse;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecordDetail")
public class PaymentRecordDetail extends AbstractFact {
    private String invoiceNumber;
    private LocalDateTime paymentDate;
    private String paymentStatusId;
    private double amount;
    private String modeOfPaymentId;
    private String paymentRecordId;
    private String vigiPayTransactionReference;

    public String getVigiPayTransactionReference() {
        return vigiPayTransactionReference;
    }

    public void setVigiPayTransactionReference(String vigiPayTransactionReference) {
        this.vigiPayTransactionReference = vigiPayTransactionReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getModeOfPaymentId() {
        return modeOfPaymentId;
    }

    public void setModeOfPaymentId(String modeOfPaymentId) {
        this.modeOfPaymentId = modeOfPaymentId;
    }


    public PaymentRecordDetailDto convertToDto() {
        PaymentRecordDetailDto paymentRecordDetailDto = new PaymentRecordDetailDto();
        paymentRecordDetailDto.setAmount(getAmount());
        paymentRecordDetailDto.setId(getId());
        paymentRecordDetailDto.setPaymentRecordId(getPaymentRecordId());
        ModeOfPayment modeOfPayment = getModeOfPayment();
        if (modeOfPayment != null) {
            paymentRecordDetailDto.setModeOfPaymentName(modeOfPayment.getName());
            paymentRecordDetailDto.setModeOfPaymentId(getModeOfPaymentId());
        }
        PaymentStatus paymentStatus = getPaymentStatus();
        if (paymentStatus != null) {
            paymentRecordDetailDto.setPaymentStatus(paymentStatus.getName());
            paymentRecordDetailDto.setPaymentStatusId(getPaymentStatusId());
        }

        paymentRecordDetailDto.setCreationDate(getCreatedAt() != null ? getCreatedAt().toString("dd-MM-yyyy") : null);
        paymentRecordDetailDto.setPaymentDate(getPaymentDate() != null ? getPaymentDate().toString("dd-MM-yyyy HH:mm:ss a") : null);
        paymentRecordDetailDto.setInvoiceNumber(getInvoiceNumber());
        paymentRecordDetailDto.setVigiPayReference(getVigiPayTransactionReference());
        return paymentRecordDetailDto;
    }


    public PaymentStatus getPaymentStatus() {
        Map paymentStatusMap = Mapstore.STORE.get("PaymentStatus");
        PaymentStatus paymentStatus = null;
        if (paymentStatusMap != null) {
            paymentStatus = (PaymentStatus) paymentStatusMap.get(paymentStatusId);
        }
        if (paymentStatus == null) {
            paymentStatus = (PaymentStatus) mongoRepositoryReactive.findById(paymentStatusId, PaymentStatus.class).block();
            if (paymentStatus != null && paymentStatusMap != null) {
                paymentStatusMap.put(paymentStatusId, paymentStatus);
            }
        }
        return paymentStatus;
    }

    public String getPaymentStatusName() {
        PaymentStatus paymentStatus = getPaymentStatus();
        if (paymentStatus != null) {
            return paymentStatus.toString();
        }
        return null;
    }

    public ModeOfPayment getModeOfPayment() {
        Map modeOfPaymentMap = Mapstore.STORE.get("ModeOfPayment");
        ModeOfPayment modeOfPayment = null;
        if (modeOfPaymentMap != null) {
            modeOfPayment = (ModeOfPayment) modeOfPaymentMap.get(modeOfPaymentId);
        }
        if (modeOfPayment == null) {
            modeOfPayment = (ModeOfPayment) mongoRepositoryReactive.findById(modeOfPaymentId, ModeOfPayment.class).block();
            if (modeOfPayment != null && modeOfPaymentMap != null) {
                modeOfPaymentMap.put(modeOfPaymentId, modeOfPayment);
            }
        }
        return modeOfPayment;
    }

    public String getModeOfPaymentName() {
        ModeOfPayment modeOfPayment = getModeOfPayment();
        if (modeOfPayment != null) {
            return modeOfPayment.getName();
        }
        return "";
    }

    public PaymentRecord getPaymentRecord() {
        if (StringUtils.isEmpty(this.paymentRecordId)) {
            return null;
        }
        return (PaymentRecord) mongoRepositoryReactive.findById(this.paymentRecordId, PaymentRecord.class).block();
    }

    public boolean isSuccessfulPayment() {
        return StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, this.paymentStatusId);
    }

    public boolean isFailedPayment() {
        return StringUtils.equals(PaymentStatusReferenceData.FAILED_PAYMENT_STATUS_ID, this.paymentStatusId);
    }

    public boolean isPendingVigiPayConfirmation() {
        return StringUtils.equals(PaymentStatusReferenceData.PENDING_VIGIPAY_CONFIRMATION_STATUS_ID, this.paymentStatusId);
    }


    public String getPaymentDateString() {
        if (this.paymentDate != null) {
            return this.paymentDate.toString("dd-MM-yyyy");
        }
        return null;
    }

    public String getPaymentTimeString() {
        if (this.paymentDate != null) {
            return this.paymentDate.toString("HH:mm:ss a");
        }
        return null;
    }

    public PaymentInvoiceResponse convertToPaymentInvoice() {
        PaymentInvoiceResponse paymentInvoiceResponse = new PaymentInvoiceResponse();
        paymentInvoiceResponse.setAmount(getAmount());
        PaymentRecord paymentRecord = getPaymentRecord();
        PaymentRecordDto paymentRecordDto = paymentRecord.convertToDto();
        paymentInvoiceResponse.setCreationDate(getCreatedAt().toString("dd-MM-yyyy"));
        paymentInvoiceResponse.setFeePaymentTypeName(paymentRecordDto.getFeePaymentTypeName());
        paymentInvoiceResponse.setGameTypeName(paymentRecordDto.getGameTypeName());
        paymentInvoiceResponse.setOwnerName(paymentRecordDto.getOwnerName());
        paymentInvoiceResponse.setRevenueName(paymentRecordDto.getRevenueName());
        paymentInvoiceResponse.setInvoiceNumber(getInvoiceNumber());
        paymentInvoiceResponse.setModeOfPaymentName(getModeOfPaymentName());
        if (this.amount < paymentRecord.getAmount()) {
            paymentInvoiceResponse.setPaymentType("Partial Payment");
        } else {
            paymentInvoiceResponse.setPaymentType("Full Payment");
        }
        return paymentInvoiceResponse;
    }

    @Override
    public String getFactName() {
        return "PaymentRecordDetails";
    }
}
