package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecords")
public class PaymentRecord extends AbstractFact {

    private String institutionId;
    private String paymentStatusId;
    private String feeId;
    private String agentId;
    private String gamingMachineId;
    private double amount;
    private double amountPaid;
    private double amountOutstanding;
    private List<String> paymentRecordDetailIds = new ArrayList<>();
    private String gameTypeId;
    private String feePaymentTypeId;
    private String revenueNameId;
    private String paymentReference;
    private String batchPaymentId;

    public String getBatchPaymentId() {
        return batchPaymentId;
    }

    public void setBatchPaymentId(String batchPaymentId) {
        this.batchPaymentId = batchPaymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getRevenueNameId() {
        return revenueNameId;
    }

    public void setRevenueNameId(String revenueNameId) {
        this.revenueNameId = revenueNameId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
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

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
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
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return "";
    }

    public Fee getFee() {
        if (feeId == null) {
            return null;
        }
        return (Fee) mongoRepositoryReactive.findById(feeId, Fee.class).block();
    }

    public Double getAmount() {
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

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType != null) {
            return gameType.getName();
        }
        return "";
    }

    public GameType getGameType() {
        if (gameTypeId == null) {
            return null;
        }
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


    public PaymentStatus getPaymentStatus() {
        if (paymentStatusId == null) {
            return null;
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
        return paymentStatus;
    }

    public Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(getAgentId(), Agent.class).block();
    }

    public GamingMachine getGamingMachine() {
        if (StringUtils.isEmpty(this.gamingMachineId)) {
            return null;
        }
        return (GamingMachine) mongoRepositoryReactive.findById(getGamingMachineId(), GamingMachine.class).block();
    }

    public String getRevenueNameName() {
        RevenueName revenueName = getRevenueName();
        if (revenueName != null) {
            return revenueName.getName();
        }
        return "";
    }

    public RevenueName getRevenueName() {
        if (revenueNameId == null) {
            return null;
        }
        Map revenueNameMap = Mapstore.STORE.get("RevenueName");

        RevenueName revenueName = null;
        if (revenueNameMap != null) {
            revenueName = (RevenueName) revenueNameMap.get(revenueNameId);
        }
        if (revenueName == null) {
            revenueName = (RevenueName) mongoRepositoryReactive.findById(revenueNameId, RevenueName.class).block();
            if (revenueName != null && revenueNameMap != null) {
                revenueNameMap.put(revenueNameId, revenueName);
            }
        }
        return revenueName;
    }

    public String getFeePaymentTypeName() {
        FeePaymentType feePaymentType = getFeePaymentType();
        if (feePaymentType != null) {
            return feePaymentType.getName();
        }
        return "";
    }

    public FeePaymentType getFeePaymentType() {
        if (feePaymentTypeId == null) {
            return null;
        }
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
        paymentRecordDto.setId(getId());
        paymentRecordDto.setFeeId(getFeeId());
        String ownerName = "";
        RevenueName revenueName = getRevenueName();
        if (revenueName != null) {
            paymentRecordDto.setRevenueName(revenueName.getName());
            paymentRecordDto.setRevenueNameId(revenueName.getId());
        }
        FeePaymentType feePaymentType = getFeePaymentType();
        if (feePaymentType != null) {
            paymentRecordDto.setFeePaymentTypeId(feePaymentType.getId());
            paymentRecordDto.setFeePaymentTypeName(feePaymentType.getName());
        }

        PaymentStatus paymentStatus = getPaymentStatus();
        if (paymentStatus != null) {
            paymentRecordDto.setPaymentStatusId(getPaymentStatusId());
            paymentRecordDto.setPaymentStatusName(paymentStatus.getName());
        }
        Agent agent = getAgent();
        if (agent != null) {
            paymentRecordDto.setAgentId(getAgentId());
            ownerName = agent.getFullName();
        }
        GamingMachine gamingMachine = getGamingMachine();
        if (gamingMachine != null) {
            paymentRecordDto.setGamingMachineId(getGamingMachineId());
            paymentRecordDto.setMachineNumber(gamingMachine.getMachineNumber());
            paymentRecordDto.setInstitutionId(gamingMachine.getInstitutionId());
            Institution institution = gamingMachine.getInstitution();
            if (institution != null) {
                ownerName = institution.getInstitutionName();
            }
        }

        paymentRecordDto.setAmount(getAmount());
        paymentRecordDto.setAmountPaid(getAmountPaid());
        paymentRecordDto.setAmountOutstanding(getAmountOutstanding());
        Institution institution = getInstitution();
        if (institution != null) {
            paymentRecordDto.setInstitutionId(getInstitutionId());
            ownerName = institution.getInstitutionName();
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            paymentRecordDto.setGameTypeId(getGameTypeId());
            paymentRecordDto.setGameTypeName(gameType.getName());
        }
        paymentRecordDto.setOwnerName(ownerName);
        paymentRecordDto.setPaymentReference(getPaymentReference());
        return paymentRecordDto;
    }


    public boolean isInstitutionPayment() {
        return StringUtils.equals(RevenueNameReferenceData.INSTITUTION_REVENUE_ID, this.revenueNameId);
    }

    public boolean isGamingMachinePayment() {
        return StringUtils.equals(RevenueNameReferenceData.GAMING_MACHINE_ID, this.revenueNameId);
    }

    public boolean isAgentPayment() {
        return StringUtils.equals(RevenueNameReferenceData.AGENT_REVENUE_ID, this.revenueNameId);
    }


    public License getLicense() {
        return (License) mongoRepositoryReactive.find(Query.query(Criteria.where("paymentRecordId").is(this.id)), License.class).block();
    }

    public String getOwnerName() {
        if (isGamingMachinePayment() || isInstitutionPayment()) {
            Institution institution = getInstitution();
            if (institution != null) {
                return institution.getInstitutionName();
            }
        }
        if (isAgentPayment()) {
            Agent agent = getAgent();
            if (agent != null) {
                return agent.getFullName();
            }
        }
        return null;
    }

    public boolean isCompletedPayment() {
        return StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, this.paymentStatusId);
    }

    @Override
    public String getFactName() {
        return "PaymentRecord";
    }
}
