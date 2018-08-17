package com.software.finatech.lslb.cms.userservice.domain;

import com.software.finatech.lslb.cms.userservice.dto.GameTypeDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "GameType")
public class GameType extends EnumeratedFact {
    public GameTypeDto convertToDto() {
        GameTypeDto gameType = new GameTypeDto();
        gameType.setName(getName());
        gameType.setId(getId());
        gameType.setDescription(getDescription());
        return gameType;
    }

    @Override
    public String getFactName() {
        return "GameType";
    }
}
