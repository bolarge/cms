package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.LicenseTransferDecision;
import com.software.finatech.lslb.cms.service.dto.LicenseTransferDto;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTransferStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "LicenseTransfers")
public class LicenseTransfer extends AbstractFact {
    private String fromInstitutionId;
    private String toInstitutionId;
    private String licenseId;
    private String gameTypeId;
    private String licenseTransferStatusId;
    private List<LicenseTransferDecision> transferDecisions = new ArrayList<>();

    public List<LicenseTransferDecision> getTransferDecisions() {
        return transferDecisions;
    }

    public void setTransferDecisions(List<LicenseTransferDecision> transferDecisions) {
        this.transferDecisions = transferDecisions;
    }

    public String getFromInstitutionId() {
        return fromInstitutionId;
    }

    public void setFromInstitutionId(String fromInstitutionId) {
        this.fromInstitutionId = fromInstitutionId;
    }

    public String getToInstitutionId() {
        return toInstitutionId;
    }

    public void setToInstitutionId(String toInstitutionId) {
        this.toInstitutionId = toInstitutionId;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getLicenseTransferStatusId() {
        return licenseTransferStatusId;
    }

    public void setLicenseTransferStatusId(String licenseTransferStatusId) {
        this.licenseTransferStatusId = licenseTransferStatusId;
    }

    public LicenseTransferStatus getLicenseTransferStatus() {
        if (StringUtils.isEmpty(this.licenseTransferStatusId)) {
            return null;
        }
        Map licenseTransferStatusMap = Mapstore.STORE.get("LicenseTransferStatus");
        LicenseTransferStatus licenseTransferStatus = null;
        if (licenseTransferStatus != null) {
            licenseTransferStatus = (LicenseTransferStatus) licenseTransferStatusMap.get(licenseTransferStatusId);
        }
        if (licenseTransferStatus == null) {
            licenseTransferStatus = (LicenseTransferStatus) mongoRepositoryReactive.findById(licenseTransferStatusId, LicenseTransferStatus.class).block();
            if (licenseTransferStatus != null && licenseTransferStatusMap != null) {
                licenseTransferStatusMap.put(licenseTransferStatusId, licenseTransferStatus);
            }
        }
        return licenseTransferStatus;
    }


    public LicenseTransferDto convertToDto() {
        LicenseTransferDto dto = new LicenseTransferDto();
        Institution fromInstitution = getFromInstitution();
        if (fromInstitution != null) {
            dto.setFromInstitutionId(this.fromInstitutionId);
            dto.setFromInstitutionName(fromInstitution.getInstitutionName());
        }
        Institution toInstitution = getToInstitution();
        if (toInstitution != null) {
            dto.setToInstitutionId(this.toInstitutionId);
            dto.setToInstitutionName(toInstitution.getInstitutionName());
        }
        LicenseTransferStatus transferStatus = getLicenseTransferStatus();
        if (transferStatus != null) {
            dto.setStatusName(transferStatus.getName());
            dto.setStatusId(transferStatus.getId());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            dto.setGameTypeId(this.gameTypeId);
            dto.setGameTypeName(gameType.getName());
        }
        return dto;
    }

    public LicenseTransferDto convertToFullDto() {
        LicenseTransferDto dto = convertToDto();
        License license = getLicense();
        if (license != null) {
            dto.setLicenseNumber(license.getLicenseNumber());
        }
        dto.setTransferDecisions(getTransferDecisions());
        return dto;
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


    public boolean isPendingNewInstitutionAddition() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_NEW_INSTITUTION_ADDITION_ID, this.licenseTransferStatusId);
    }

    public boolean isPendingInitialApproval() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_INITIAL_APPROVAL_ID, this.licenseTransferStatusId);
    }

    public boolean isPendingAddInstitutionApproval() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_ADD_INSTITUTION_APPROVAL_ID, this.licenseTransferStatusId);
    }

    public boolean isPendingFinalApproval() {
        return StringUtils.equals(LicenseTransferStatusReferenceData.PENDING_FINAL_APPROVAL_ID, this.licenseTransferStatusId);
    }

    private Institution getInstitution(String institutionId) {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public Institution getToInstitution() {
        return getInstitution(this.toInstitutionId);
    }

    public Institution getFromInstitution() {
        return getInstitution(this.fromInstitutionId);
    }

    public License getLicense() {
        if (StringUtils.isEmpty(this.licenseId)) {
            return null;
        }
        return (License) mongoRepositoryReactive.findById(this.licenseId, License.class).block();
    }

    public String getLicenseNumber() {
        License license = getLicense();
        if (license != null) {
            return license.getLicenseNumber();
        }
        return null;
    }

    @Override
    public String getFactName() {
        return "LicenseTransfers";
    }
}
