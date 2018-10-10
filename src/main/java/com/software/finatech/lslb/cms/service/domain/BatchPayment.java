package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.BatchPaymentDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "BatchPayment")
public class BatchPayment extends AbstractFact {
    private String institutionId;
    private String agentId;
    private Set<String> gamingTerminalIds = new HashSet<>();
    private Set<String> gamingMachineIds = new HashSet<>();
    private Set<String> paymentRecordIds = new HashSet<>();
    private String paymentStatusId;
    private double amountTotal;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Set<String> getGamingTerminalIds() {
        return gamingTerminalIds;
    }

    public void setGamingTerminalIds(Set<String> gamingTerminalIds) {
        this.gamingTerminalIds = gamingTerminalIds;
    }

    public Set<String> getGamingMachineIds() {
        return gamingMachineIds;
    }

    public void setGamingMachineIds(Set<String> gamingMachineIds) {
        this.gamingMachineIds = gamingMachineIds;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public Set<String> getPaymentRecordIds() {
        return paymentRecordIds;
    }

    public void setPaymentRecordIds(Set<String> paymentRecordIds) {
        this.paymentRecordIds = paymentRecordIds;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public BatchPaymentDto convertToDto() {
        BatchPaymentDto dto = new BatchPaymentDto();
        dto.setId(getId());
        dto.setAmountTotal(getAmountTotal());
        Institution institution = getInstitution(this.institutionId);
        if (institution != null) {
            dto.setInstitutionId(this.institutionId);
            dto.setOwnerName(institution.getInstitutionName());
        }

        Agent agent = getAgent(this.agentId);
        if (agent != null) {
            dto.setAgentId(this.agentId);
            dto.setOwnerName(agent.getFullName());
        }
        return dto;
    }

    public BatchPaymentDto convertToFullDto() {
        BatchPaymentDto dto = convertToDto();
        dto.setPaymentRecords(getPaymentRecordDtos());
        return dto;
    }


    public Set<PaymentRecord> getPaymentRecords(){
        Set<PaymentRecord> paymentRecords = new HashSet<>();
        if (getPaymentRecordIds().isEmpty()) {
            return paymentRecords;
        }
        for (String paymentRecordId : getPaymentRecordIds()) {
            PaymentRecord paymentRecord = getPaymentRecord(paymentRecordId);
            if (paymentRecord != null) {
                paymentRecords.add(paymentRecord);
            }
        }
        return paymentRecords;
    }

    private Set<PaymentRecordDto> getPaymentRecordDtos() {
        HashSet<PaymentRecordDto> paymentRecordDtos = new HashSet<>();
        if (getPaymentRecordIds().isEmpty()) {
            return paymentRecordDtos;
        }
        for (String paymentRecordId : getPaymentRecordIds()) {
            PaymentRecord paymentRecord = getPaymentRecord(paymentRecordId);
            if (paymentRecord != null) {
                paymentRecordDtos.add(paymentRecord.convertToDto());
            }
        }
        return paymentRecordDtos;
    }

    private PaymentRecord getPaymentRecord(String paymentRecordId) {
        if (StringUtils.isEmpty(paymentRecordId)) {
            return null;
        }
        return (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
    }

    private Institution getInstitution(String institutionId) {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    private Agent getAgent(String agentId) {
        if (StringUtils.isEmpty(agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
    }

    @Override
    public String getFactName() {
        return "BatchPayment";
    }
}
