package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

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
