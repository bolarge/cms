package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.DocumentTypeDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Document(collection = "DocumentTypes")
public class DocumentType extends EnumeratedFact{

    protected Set<String> gameTypeIds = new HashSet<>();
    protected String documentPurposeId;
    @Transient
    protected DocumentPurpose documentPurpose;
    protected boolean active;
    protected boolean required;
    protected String institutionId;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

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

    public DocumentPurpose getDocumentPurpose() {
        return documentPurpose;
    }

    public void setDocumentPurpose(DocumentPurpose documentPurpose) {
        this.documentPurpose = documentPurpose;
    }

    @Override
    public String getFactName() {
        return "DocumentType";
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private GameType getGameType(String gameTypeId){
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

    private Set<String> getGameTypeNames(){
       Set<String> gameTypeNames = new HashSet<>();
        for (String gameTypeId : gameTypeIds) {
            GameType gameType = getGameType(gameTypeId);
            if (gameType != null){
                gameTypeNames.add(gameType.getName());
            }
        }
        return gameTypeNames;
    }


    public void setAssociatedProperties() throws FactNotFoundException {
        if (documentPurposeId != null) {
            DocumentPurpose DocumentPurpose = (DocumentPurpose) Mapstore.STORE.get("DocumentPurpose").get(documentPurposeId);
            if (DocumentPurpose == null) {
                DocumentPurpose = (DocumentPurpose) mongoRepositoryReactive.findById(documentPurposeId, DocumentPurpose.class).block();
                if (DocumentPurpose == null) {
                    throw new FactNotFoundException("DocumentPurpose", documentPurposeId);
                } else {
                    Mapstore.STORE.get("DocumentPurpose").put(DocumentPurpose.getId(), DocumentPurpose);
                }
            }
            setDocumentPurpose(DocumentPurpose);
        }
    }

    public DocumentTypeDto convertToDto() {
        DocumentTypeDto dto = new DocumentTypeDto();
        dto.setName(getName());
        dto.setId(getId());
        dto.setDocumentPurposeId(getDocumentPurposeId());
        dto.setDescription(getDescription());
        dto.setDocumentPurpose(getDocumentPurpose()==null?null:getDocumentPurpose().convertToDto());
        dto.setActive(isActive());
        dto.setRequired(isRequired());
        dto.setGameTypeNames(getGameTypeNames());
        return dto;
    }
}
