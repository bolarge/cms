package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentConfirmationApprovalRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import static com.software.finatech.lslb.cms.service.referencedata.PaymentConfirmationApprovalRequestTypeReferenceData.*;

@SuppressWarnings("serial")
@Document(collection = "PaymentConfirmationApprovalRequests")
public class PaymentConfirmationApprovalRequest extends AbstractApprovalRequest {
    private String paymentRecordId;
    private String approvalRequestTypeId;
    private String paymentRecordDetailId;
    private String paymentOwnerName;

    public String getPaymentOwnerName() {
        return paymentOwnerName;
    }

    public void setPaymentOwnerName(String paymentOwnerName) {
        this.paymentOwnerName = paymentOwnerName;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public String getApprovalRequestTypeId() {
        return approvalRequestTypeId;
    }

    public void setApprovalRequestTypeId(String approvalRequestTypeId) {
        this.approvalRequestTypeId = approvalRequestTypeId;
    }

    public String getPaymentRecordDetailId() {
        return paymentRecordDetailId;
    }

    public void setPaymentRecordDetailId(String paymentRecordDetailId) {
        this.paymentRecordDetailId = paymentRecordDetailId;
    }

    public PaymentConfirmationApprovalRequestDto convertToDto() {
        PaymentConfirmationApprovalRequestDto dto = new PaymentConfirmationApprovalRequestDto();
        dto.setId(getId());
        dto.setInitiatorId(getInitiatorId());
        dto.setDateCreated(getDateCreatedString());
        dto.setInitiatorName(getAuthInfoName(getInitiatorId()));
        dto.setPaymentOwnerName(getPaymentOwnerName());
        dto.setRequestStatusId(getApprovalRequestStatusId());
        dto.setRequestStatusName(getApprovalRequestStatus() != null ? getApprovalRequestStatus().getName() : "");
        dto.setRequestTypeName(getTypeNameById(mongoRepositoryReactive, getApprovalRequestTypeId()));
        dto.setRequestTypeId(getApprovalRequestTypeId());
        return dto;
    }

    public PaymentConfirmationApprovalRequestDto convertToFullDto() {
        PaymentConfirmationApprovalRequestDto dto = convertToDto();
        PaymentRecord paymentRecord = getPaymentRecord();
        if (paymentRecord != null) {
            dto.setPaymentRecord(paymentRecord.convertToDto());
        }
        PaymentRecordDetail detail = getPaymentRecordDetail();
        if (detail != null) {
            dto.setPaymentRecordDetail(detail.convertToDto());
        }
        dto.setApproverId(getApproverId());
        dto.setApproverName(getAuthInfoName(getApproverId()));
        dto.setRejectorId(getRejectorId());
        dto.setRejectorName(getAuthInfoName(getRejectorId()));
        return dto;
    }

    public PaymentRecord getPaymentRecord() {
        if (StringUtils.isEmpty(this.paymentRecordId)) {
            return null;
        }
        return (PaymentRecord) mongoRepositoryReactive.findById(this.paymentRecordId, PaymentRecord.class).block();
    }

    public PaymentRecordDetail getPaymentRecordDetail() {
        if (StringUtils.isEmpty(this.paymentRecordDetailId)) {
            return null;
        }
        return (PaymentRecordDetail) mongoRepositoryReactive.findById(this.paymentRecordDetailId, PaymentRecordDetail.class).block();
    }

    public boolean isConfirmFullPayment() {
        return StringUtils.equals(CONFIRM_FULL_PAYMENT_ID, this.approvalRequestTypeId);
    }

    public boolean isConfirmPartialPayment() {
        return StringUtils.equals(CONFIRM_PARTIAL_PAYMENT_ID, this.approvalRequestTypeId);
    }

    @Override
    public String getFactName() {
        return "---";
    }
}
