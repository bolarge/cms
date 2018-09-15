package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.FeeDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "Fees")
public class Fee extends AbstractFact {
    protected boolean active;
    protected double amount;
    protected String gameTypeId;
    protected String feePaymentTypeId;
    protected String revenueNameId;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRevenueNameId() {
        return revenueNameId;
    }

    public void setRevenueNameId(String revenueNameId) {
        this.revenueNameId = revenueNameId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public void setFeePaymentTypeId(String feePaymentTypeId) {
        this.feePaymentTypeId = feePaymentTypeId;
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

    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        }
        return gameType.getName();
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

    public FeeDto convertToDto() {
        FeeDto feeDto = new FeeDto();
        feeDto.setAmount(getAmount());
        feeDto.setId(getId());
        feeDto.setActive(isActive());
        RevenueName revenueName = getRevenueName();
        if (revenueName != null) {
            feeDto.setRevenueName(revenueName.getName());
            feeDto.setRevenueId(getRevenueNameId());
        }
        GameType gameType = getGameType();
        if (gameType != null) {
            feeDto.setGameTypeName(gameType.getName());
            feeDto.setGameTypeId(getGameTypeId());
        }
        FeePaymentType feePaymentType = getFeePaymentType();
        if (feePaymentType != null) {
            feeDto.setFeePaymentTypeName(feePaymentType.getName());
            feeDto.setFeePaymentTypeId(getFeePaymentTypeId());
        }
        return feeDto;
    }


    @Override
    public String getFactName() {
        return "Fee";
    }
}
