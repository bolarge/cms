package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.InstitutionCategoryDetailsDto;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "InstitutionCategoryDetails")
public class InstitutionCategoryDetails extends AbstractFact {
    private String institutionId;
    private String gameTypeId;
    private String tradeName;
    private LocalDate firstCommencementDate;
    private Set<String> directorsNames = new HashSet<>();
    private Set<String> shareHolderNames = new HashSet<>();
    private Set<String> phoneNumbers = new HashSet<>();


    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Set<String> getShareHolderNames() {
        return shareHolderNames;
    }

    public void setShareHolderNames(Set<String> shareHolderNames) {
        this.shareHolderNames = shareHolderNames;
    }

    public Set<String> getDirectorsNames() {
        return directorsNames;
    }

    public void setDirectorsNames(Set<String> directorsNames) {
        this.directorsNames = directorsNames;
    }

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

    public InstitutionCategoryDetailsDto convertToDto() {
        InstitutionCategoryDetailsDto dto = new InstitutionCategoryDetailsDto();
        GameType gameType = getGameType();
        if (gameType != null) {
            dto.setGameTypeName(gameType.toString());
        }
        LocalDate date = getFirstCommencementDate();
        if (date != null) {
            dto.setFirstCommencementDate(date.toString("dd-MM-yyyy"));
        }
        dto.setTradeName(getTradeName());
        dto.setDirectorsNames(getDirectorsNames());
        return dto;
    }

    public GameType getGameType() {
        if (StringUtils.isEmpty(this.gameTypeId)) {
            return null;
        }
        return (GameType) mongoRepositoryReactive.findById(this.gameTypeId, GameType.class).block();
    }

    @Override
    public String getFactName() {
        return "InstitutionCategoryDetails";
    }
}
