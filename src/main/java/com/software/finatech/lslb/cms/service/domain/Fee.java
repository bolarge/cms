package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.FeeDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "Fees")
public class Fee extends AbstractFact {
    protected boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    protected double amount;
    protected String gameTypeId;
    protected String feePaymentTypeId;
    protected String revenueNameId;

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



    public FeeDto convertToDto() {
        FeeDto feeDto = new FeeDto();
        feeDto.setAmount(getAmount());
        feeDto.setId(getId());
        feeDto.setActive(isActive());
      //  feeDto.setRevenueName(getRevenueName());
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
        if (revenueName != null) {
            feeDto.setRevenueName(revenueName.convertToDto());
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
        if (gameType != null) {
            feeDto.setGameType(gameType.convertToDto());
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
        if (feePaymentType != null) {
            feeDto.setFeePaymentType(feePaymentType.convertToDto());
        }
        return feeDto;
    }


    @Override
    public String getFactName() {
        return "Fee";
    }
}
