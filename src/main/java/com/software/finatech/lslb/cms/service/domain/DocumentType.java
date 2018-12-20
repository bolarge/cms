package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.DocumentTypeDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Document(collection = "DocumentTypes")
public class DocumentType extends EnumeratedFact {
    protected Set<String> gameTypeIds = new HashSet<>();
    protected String documentPurposeId;
    protected boolean active;
    protected boolean required;
    protected String approverId;

    public Set<String> getGameTypeIds() {
        return gameTypeIds;
    }

    public void setGameTypeIds(Set<String> gameTypeIds) {
        this.gameTypeIds = gameTypeIds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDocumentPurposeId() {
        return documentPurposeId;
    }

    public void setDocumentPurposeId(String documentPurposeId) {
        this.documentPurposeId = documentPurposeId;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    @Override
    public String getFactName() {
        return "DocumentType";
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private GameType getGameType(String gameTypeId) {
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

    private Set<String> getGameTypeNames() {
        Set<String> gameTypeNames = new HashSet<>();
        for (String gameTypeId : gameTypeIds) {
            GameType gameType = getGameType(gameTypeId);
            if (gameType != null) {
                gameTypeNames.add(gameType.getName());
            }
        }
        return gameTypeNames;
    }

    public AuthInfo getApprover() {
        if (StringUtils.isEmpty(this.approverId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(this.approverId, AuthInfo.class).block();
    }

    public DocumentTypeDto convertToDto() {
        DocumentTypeDto dto = new DocumentTypeDto();
        dto.setName(getName());
        dto.setId(getId());
        dto.setDocumentPurposeId(getDocumentPurposeId());
        dto.setDescription(getDescription());
        dto.setDocumentPurpose(getDocumentPurpose() == null ? null : getDocumentPurpose().convertToDto());
        dto.setActive(isActive());
        dto.setRequired(isRequired());
        dto.setGameTypeNames(getGameTypeNames());
        AuthInfo approver = getApprover();
        if (approver != null) {
            dto.setApproverName(approver.getFullName());
            dto.setApproverId(this.approverId);
        }
        return dto;
    }

    public DocumentPurpose getDocumentPurpose() {
        if (StringUtils.isEmpty(this.documentPurposeId)) {
            return null;
        }
        DocumentPurpose documentPurpose = null;
        Map<String, FactObject> documentPurposeMap = Mapstore.STORE.get("DocumentPurpose");
        if (documentPurposeMap != null) {
            documentPurpose = (DocumentPurpose) documentPurposeMap.get(documentPurposeId);
        }
        if (documentPurpose == null) {
            documentPurpose = (DocumentPurpose) mongoRepositoryReactive.findById(documentPurposeId, DocumentPurpose.class).block();
            if (documentPurpose != null && documentPurposeMap != null) {
                documentPurposeMap.put(documentPurposeId, documentPurpose);
            }
        }
        return documentPurpose;
    }
}
