package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecords")
public class PaymentRecord extends AbstractFact {

    private String institutionId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String approverId;
    private String paymentStatusId;
    private String feeId;
    private String feePaymentTypeId;
    private String parentLicenseId;
    private String gameTypeId;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
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
    public Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }
    private GameType getGameType() {
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }
    private PaymentStatus getPaymentStatus() {
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

    private Fee getFee() {
        return (Fee) mongoRepositoryReactive.findById(feeId, Fee.class).block();
    }

    private String getApproverFullName() {
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(approverId, AuthInfo.class).block();
        if (authInfo == null) {
            return null;
        } else {
            return authInfo.getFullName();
        }
    }

    private FeePaymentType getFeePaymentType() {
        Map feePaymentTypeMap = Mapstore.STORE.get("FeePaymentType");
        FeePaymentType feePaymentType = null;
        if (feePaymentTypeMap != null) {
            feePaymentType = (FeePaymentType) feePaymentTypeMap.get(feePaymentTypeId);
        }
        if (feePaymentType == null) {
            feePaymentType = (FeePaymentType) mongoRepositoryReactive.findById(feePaymentTypeId, FeePaymentType.class).block();
            if (feePaymentType != null && feePaymentTypeMap != null) {
                feePaymentTypeMap.put(feePaymentTypeId, feePaymentType);
            }
        }
        return feePaymentType;
    }

    public PaymentRecordDto convertToDto() {
        PaymentRecordDto paymentRecordDto = new PaymentRecordDto();
        Fee fee = getFee();
        paymentRecordDto.setId(getId());
        paymentRecordDto.setStartDate(startDate.toString("dd/MM/yyyy HH:mm:ss"));
        paymentRecordDto.setEndDate(endDate.toString("dd/MM/yyyy HH:mm:ss"));
        if (fee != null) {
            paymentRecordDto.setFee(fee.convertToDto());
        }
        PaymentStatus paymentStatus = getPaymentStatus();
        if (paymentStatus != null) {
            paymentRecordDto.setPaymentStatus(paymentStatus.convertToDto());
        }
        paymentRecordDto.setApproverName(getApproverFullName());
        FeePaymentType feePaymentType = getFeePaymentType();
        if (feePaymentType != null) {
            paymentRecordDto.setFeePaymentType(feePaymentType.convertToDto());
        }
        paymentRecordDto.setInstitutionId(getInstitutionId());
        paymentRecordDto.setInstitutionName(getInstitution().institutionName);




        return paymentRecordDto;
    }

    @Override
    public String getFactName() {
        return "PaymentRecord";
    }
}
