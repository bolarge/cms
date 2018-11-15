package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.InstitutionCategoryDetailsDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "InstitutionCategoryDetails")
public class InstitutionCategoryDetails extends AbstractFact {
    private String institutionId;
    private String gameTypeId;
    private String tradeName;
    private LocalDate firstCommencementDate;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public LocalDate getFirstCommencementDate() {
        return firstCommencementDate;
    }

    public void setFirstCommencementDate(LocalDate firstCommencementDate) {
        this.firstCommencementDate = firstCommencementDate;
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(this.institutionId, Institution.class).block();
    }

    public InstitutionCategoryDetailsDto convertToDto(){
        InstitutionCategoryDetailsDto dto = new InstitutionCategoryDetailsDto();
        GameType gameType = getGameType();
        if (gameType != null){
            dto.setGameTypeName(gameType.toString());
        }
        LocalDate date = getFirstCommencementDate();
        if (date != null){
            dto.setFirstCommencementDate(date.toString("dd-MM-yyyy"));
        }
        dto.setTradeName(getTradeName());
        return dto;
    }

    public GameType getGameType() {
        Map<String,FactObject> gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(this.gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(this.gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameType.getId(), gameType);
            }
        }
        return gameType;
    }

    @Override
    public String getFactName() {
        return "InstitutionCategoryDetails";
    }
}
