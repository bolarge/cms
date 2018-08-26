package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.FeeDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.MapValues;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "Fees")
public class Fee extends AbstractFact {
    @Autowired
    MapValues mapValues;

    protected double amount;
    protected String gameTypeId;
    protected String feePaymentTypeId;
    protected String revenueName;
    protected String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getRevenueName() {
        return revenueName;
    }

    public void setRevenueName(String revenueName) {
        this.revenueName = revenueName;
    }




    public FeeDto convertToDto() {
        FeeDto feeDto = new FeeDto();
        feeDto.setAmount(getAmount());
        feeDto.setId(getId());
        feeDto.setDuration(getDuration());
        feeDto.setRevenueName(getRevenueName());
        GameType gameType = mapValues.getGameType(gameTypeId);
        if (gameType != null) {
            feeDto.setGameType(gameType.convertToDto());
        }
        FeePaymentType feePaymentType = mapValues.getFeePaymentType(feePaymentTypeId);
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
