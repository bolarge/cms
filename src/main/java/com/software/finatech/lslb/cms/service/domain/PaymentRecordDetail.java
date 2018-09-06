package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecordsDetail")
public class PaymentRecordDetail extends AbstractFact {
    private String invoiceNumber;
    private DateTime paymentDate;
    private String paymentStatusId;
    private double amount;
    private String modeOfPaymentId;
    private String id;
    private String paymentRecordId;
    private boolean paymentAddedToParent;


    public boolean isPaymentAddedToParent() {
        return paymentAddedToParent;
    }

    public void setPaymentAddedToParent(boolean paymentAddedToParent) {
        this.paymentAddedToParent = paymentAddedToParent;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public DateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(DateTime paymentDate) {
        this.paymentDate = paymentDate;
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
        }
        PaymentStatus paymentStatus = getPaymentStatus();
        if (paymentStatus != null) {
            paymentRecordDetailDto.setPaymentStatus(paymentStatus.getName());
        }
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

    @Override
    public String getFactName() {
        return "PaymentRecordDetails";
    }
}
