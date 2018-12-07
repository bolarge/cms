package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.MachineDto;
import com.software.finatech.lslb.cms.service.dto.MachineMultiplePayment;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

@SuppressWarnings("serial")
@Document(collection = "PaymentRecords")
public class PaymentRecord extends AbstractFact {
    private String institutionId;
    private String paymentStatusId;
    private String feeId;
    private String agentId;
    private double amount;
    private double amountPaid;
    private double amountOutstanding;
    private List<String> paymentRecordDetailIds = new ArrayList<>();
    private String gameTypeId;
    private String feePaymentTypeId;
    private String licenseTypeId;
    private String paymentReference;
    private Set<String> gamingMachineIds = new HashSet<>();
    private Set<String> gamingTerminalIds = new HashSet<>();
    private String licenseTransferId;
    private MachineMultiplePayment machineMultiplePayment;
    private String licenseId;
    private LocalDate creationDate;
    private LocalDate completionDate;

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getLicenseTransferId() {
        return licenseTransferId;
    }

    public MachineMultiplePayment getMachineMultiplePayment() {
        return machineMultiplePayment;
    }

    public void setMachineMultiplePayment(MachineMultiplePayment machineMultiplePayment) {
        this.machineMultiplePayment = machineMultiplePayment;
    }

    public void setLicenseTransferId(String licenseTransferId) {
        this.licenseTransferId = licenseTransferId;
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

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(String licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
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
        return null;
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

    public LicenseType getLicenseType() {
        if (licenseTypeId == null) {
            return null;
        }
        Map licenseTypeMap = Mapstore.STORE.get("LicenseType");

        LicenseType licenseType = null;
        if (licenseTypeMap != null) {
            licenseType = (LicenseType) licenseTypeMap.get(licenseTypeId);
        }
        if (licenseType == null) {
            licenseType = (LicenseType) mongoRepositoryReactive.findById(licenseTypeId, LicenseType.class).block();
            if (licenseType != null && licenseTypeMap != null) {
                licenseTypeMap.put(licenseTypeId, licenseType);
            }
        }
        return licenseType;
    }


    public String getLicenseTypeName(){
        LicenseType licenseType = getLicenseType();
        if (licenseType != null){
            return licenseType.getName();
        }
    return null;
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
        LicenseType licenseType = getLicenseType();
        if (licenseType != null) {
            paymentRecordDto.setRevenueName(licenseType.toString());
            paymentRecordDto.setRevenueNameId(licenseType.getId());
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
        paymentRecordDto.setCreationDate(getCreationDateString());
        paymentRecordDto.setCompletionDate(getCompletionDateString());
        return paymentRecordDto;
    }

    public PaymentRecordDto convertToFullDto() {
        PaymentRecordDto dto = convertToDto();
        dto.setGamingMachines(getGamingMachineDtos());
        dto.setGamingTerminals(getGamingTerminalDtos());
        dto.setMachineMultiplePayment(getMachineMultiplePayment());
        return dto;
    }

    private String getCreationDateString() {
        LocalDate localDateTime = getCreationDate();
        if (localDateTime != null) {
            return localDateTime.toString("dd-MM-yyyy HH:mm a");
        }
        return null;
    }

    private String getCompletionDateString() {
        LocalDate localDateTime = getCompletionDate();
        if (localDateTime != null) {
            return localDateTime.toString("dd-MM-yyyy HH:mm a");
        }
        return null;
    }

    public License getLicense() {
        License license = (License) mongoRepositoryReactive.findById(this.licenseId, License.class).block();
        if (license != null) {
            return license;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("paymentRecordId").is(this.id));
        return (License) mongoRepositoryReactive.find(query, License.class).block();
    }

    public String getOwnerName() {
        if (isGamingMachinePayment() || isInstitutionPayment()) {
            Institution institution = getInstitution();
            if (institution != null) {
                return institution.getInstitutionName();
            }
        }
        if (isAgentPayment() || isGamingTerminalPayment()) {
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

    public boolean isLicensePayment() {
        return StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isLicenseTransferPayment() {
        return StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_TRANSFER_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isApplicationPayment() {
        return StringUtils.equals(FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isLicenseRenewalPayment() {
        return StringUtils.equals(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public boolean isInstitutionPayment() {
        return StringUtils.equals(LicenseTypeReferenceData.INSTITUTION_ID, this.licenseTypeId);
    }

    public boolean isGamingMachinePayment() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_MACHINE_ID, this.licenseTypeId);
    }

    public boolean isGamingTerminalPayment() {
        return StringUtils.equals(LicenseTypeReferenceData.GAMING_TERMINAL_ID, this.licenseTypeId);
    }

    public boolean isAgentPayment() {
        return StringUtils.equals(LicenseTypeReferenceData.AGENT_ID, this.licenseTypeId);
    }

    public boolean isTaxPayment() {
        return StringUtils.equals(FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID, this.feePaymentTypeId);
    }

    public Set<Machine> getGamingMachines() {
        Set<Machine> machines = new HashSet<>();
        for (String machineId : this.gamingMachineIds) {
            Machine machine = findMachineById(machineId);
            if (machine != null && machine.isGamingMachine()) {
                machines.add(machine);
            }
        }
        return machines;
    }

    public Set<Machine> getGamingTerminals() {
        Set<Machine> machines = new HashSet<>();
        for (String machineId : this.gamingTerminalIds) {
            Machine machine = findMachineById(machineId);
            if (machine != null && machine.isGamingTerminal()) {
                machines.add(machine);
            }
        }
        return machines;
    }

    public List<MachineDto> getGamingMachineDtos() {
        List<MachineDto> dtos = new ArrayList<>();
        for (String machineId : this.gamingMachineIds) {
            Machine machine = findMachineById(machineId);
            if (machine != null) {
                MachineDto dto = new MachineDto();
                dto.setId(machine.getId());
                dto.setSerialNumber(machine.getSerialNumber());
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<MachineDto> getGamingTerminalDtos() {
        List<MachineDto> dtos = new ArrayList<>();
        for (String machineId : this.gamingTerminalIds) {
            Machine machine = findMachineById(machineId);
            if (machine != null) {
                MachineDto dto = new MachineDto();
                dto.setId(machine.getId());
                dto.setSerialNumber(machine.getSerialNumber());
                dtos.add(dto);
            }
        }
        return dtos;
    }

    private Machine findMachineById(String id) {
        return (Machine) mongoRepositoryReactive.findById(id, Machine.class).block();
    }

    public LicenseTransfer getLicenseTransfer() {
        if (StringUtils.isEmpty(this.licenseTransferId)) {
            return null;
        }
        return (LicenseTransfer) mongoRepositoryReactive.findById(this.licenseTransferId, LicenseTransfer.class).block();
    }

    @Override
    public String getFactName() {
        return "PaymentRecord";
    }
}
