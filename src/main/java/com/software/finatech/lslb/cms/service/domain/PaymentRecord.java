package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecords")
public class PaymentRecord extends AbstractFact {

    private String institutionId;
    private String approverId;
    private String paymentStatusId;
    private String feeId;
    private String parentLicenseId;
    private String agentId;
    private String gamingMachineId;
    private String startYear;
    private String endYear;
    private double amount;
    private double amountPaid;
    private double amountOutstanding;
    private List<String> paymentRecordDetailIds = new ArrayList<>();
    private String gameTypeId;

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGamingMachineId() {
        return gamingMachineId;
    }

    public void setGamingMachineId(String gamingMachineId) {
        this.gamingMachineId = gamingMachineId;
    }

    public String getParentLicenseId() {
        return parentLicenseId;
    }

    public void setParentLicenseId(String parentLicenseId) {
        this.parentLicenseId = parentLicenseId;
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

    public Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountOutstanding() {
        return amountOutstanding;
    }

    public void setAmountOutstanding(double amountOutstanding) {
        this.amountOutstanding = amountOutstanding;
    }

    public List<String> getPaymentRecordDetailIds() {
        return paymentRecordDetailIds;
    }

    public void setPaymentRecordDetailIds(List<String> paymentRecordDetailIds) {
        this.paymentRecordDetailIds = paymentRecordDetailIds;
    }

    public PaymentRecordDto convertToDto() {
        PaymentRecordDto paymentRecordDto = new PaymentRecordDto();
        Fee fee = getFee();
        paymentRecordDto.setId(getId());
        if (fee != null) {
            paymentRecordDto.setFee(fee.convertToDto());
        }
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
        if (paymentStatus != null) {
            paymentRecordDto.setPaymentStatus(paymentStatus.convertToDto());
        }
        //paymentRecordDto.setAgentId(getAgentId());
        Agent agent =(Agent) mongoRepositoryReactive.findById(getAgentId(), Agent.class).block();
        if(agent!=null){
            paymentRecordDto.setAgent(agent.convertToDto());
        }
        GamingMachine gamingMachine =(GamingMachine) mongoRepositoryReactive.findById(getGamingMachineId(), GamingMachine.class).block();
        if(gamingMachine!=null){
            paymentRecordDto.setGamingMachine(gamingMachine.convertToDto());
        }
        //paymentRecordDto.setGamingMachineId(getGamingMachineId());
        paymentRecordDto.setStartYear(getStartYear());
        paymentRecordDto.setEndYear(getEndYear());
        paymentRecordDto.setApproverName(getApproverFullName());
        paymentRecordDto.setInstitutionId(getInstitutionId());
        paymentRecordDto.setInstitutionName(getInstitution().institutionName);

        return paymentRecordDto;
    }

    @Override
    public String getFactName() {
        return "PaymentRecord";
    }
}
