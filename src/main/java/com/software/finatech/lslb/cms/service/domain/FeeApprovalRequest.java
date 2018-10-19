package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.FeeApprovalRequestDto;
import com.software.finatech.lslb.cms.service.referencedata.FeeApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "FeeApprovalRequests")
public class FeeApprovalRequest extends AbstractApprovalRequest {
    private String pendingFeeId;
    private String feeId;
    private String feeApprovalRequestTypeId;
    private LocalDate endDate;

    public String getPendingFeeId() {
        return pendingFeeId;
    }

    public void setPendingFeeId(String pendingFeeId) {
        this.pendingFeeId = pendingFeeId;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getFeeApprovalRequestTypeId() {
        return feeApprovalRequestTypeId;
    }

    public void setFeeApprovalRequestTypeId(String feeApprovalRequestTypeId) {
        this.feeApprovalRequestTypeId = feeApprovalRequestTypeId;
    }

    public FeeApprovalRequestType getFeeApprovalRequestType() {
        FeeApprovalRequestType feeApprovalRequestType = null;
        Map feeApprovalRequestTypeMap = Mapstore.STORE.get("FeeApprovalRequestType");
        if (feeApprovalRequestTypeMap != null) {
            feeApprovalRequestType = (FeeApprovalRequestType) feeApprovalRequestTypeMap.get(this.feeApprovalRequestTypeId);
        }
        if (feeApprovalRequestType == null) {
            feeApprovalRequestType = (FeeApprovalRequestType) mongoRepositoryReactive.findById(this.feeApprovalRequestTypeId, FeeApprovalRequestType.class).block();
            if (feeApprovalRequestType != null && feeApprovalRequestTypeMap != null) {
                feeApprovalRequestTypeMap.put(this.feeApprovalRequestTypeId, feeApprovalRequestType);
            }
        }
        return feeApprovalRequestType;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public FeeApprovalRequestDto convertToDto() {
        FeeApprovalRequestDto dto = convertToHalfDto();
        PendingFee pendingFee = getPendingFee();
        if (pendingFee != null) {
            dto.setPendingFee(pendingFee.convertToDto());
        }
        Fee fee = getFee();
        if (fee != null) {
            dto.setPendingFee(fee.convertToDto());
        }
        LocalDate endDate = getEndDate();
        if (endDate != null) {
            dto.setNewEndDate(endDate.toString("dd-MM-yyyy"));
        }
        AuthInfo rejector = getRejector();
        if (rejector != null) {
            dto.setRejectorId(this.rejectorId);
            dto.setRejectorName(rejector.getFullName());
        }
        return dto;
    }

    public FeeApprovalRequestDto convertToHalfDto() {
        FeeApprovalRequestDto dto = new FeeApprovalRequestDto();
        dto.setId(getId());
        AuthInfo initiator = getAuthInfo(this.initiatorId);
        if (initiator != null) {
            dto.setInitiatorId(this.initiatorId);
            dto.setInitiatorName(initiator.getFullName());
        }
        FeeApprovalRequestType approvalRequestType = getFeeApprovalRequestType();
        if (approvalRequestType != null) {
            dto.setApprovalRequestType(approvalRequestType.getName());
            dto.setApprovalRequestTypeId(this.feeApprovalRequestTypeId);
        }
        ApprovalRequestStatus approvalRequestStatus = getApprovalRequestStatus();
        if (approvalRequestStatus != null) {
            dto.setApprovalRequestStatusId(this.approvalRequestStatusId);
            dto.setApprovalRequestStatus(approvalRequestStatus.toString());
        }
        LocalDateTime dateCreated = getDateCreated();
        if (dateCreated != null) {
            dto.setDateCreated(dateCreated.toString("dd-MM-yyyy HH:mm:ss"));
        }
        return dto;
    }

    public boolean isCreateFee() {
        return StringUtils.equals(FeeApprovalRequestTypeReferenceData.CREATE_FEE_ID, this.feeApprovalRequestTypeId);
    }

    public boolean isSetFeeEndDate() {
        return StringUtils.equals(FeeApprovalRequestTypeReferenceData.SET_FEE_END_DATE_ID, this.feeApprovalRequestTypeId);
    }

    public PendingFee getPendingFee() {
        if (StringUtils.isEmpty(this.pendingFeeId)) {
            return null;
        }
        return (PendingFee) mongoRepositoryReactive.findById(this.pendingFeeId, PendingFee.class).block();
    }

    public Fee getFee() {
        if (StringUtils.isEmpty(this.feeId)) {
            return null;
        }
        return (Fee) mongoRepositoryReactive.findById(this.feeId, Fee.class).block();
    }

    @Override
    public String getFactName() {
        return "FeeApprovalRequests";
    }
}
