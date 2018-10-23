package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class GamingTerminalMultiplePaymentResponse {
    private double amountTotal;
    private List<ValidGamingTerminalPayment> validGamingTerminalPaymentList = new ArrayList<>();
    private List<InvalidGamingTerminalPayment> invalidGamingTerminalPaymentList = new ArrayList<>();
    private List<String> validGamingTerminalsList = new ArrayList<>();

    public List<String> getValidGamingTerminalsList() {
        return validGamingTerminalsList;
    }

    public void setValidGamingTerminalsList(List<String> validGamingTerminalsList) {
        this.validGamingTerminalsList = validGamingTerminalsList;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public List<ValidGamingTerminalPayment> getValidGamingTerminalPaymentList() {
        return validGamingTerminalPaymentList;
    }

    public void setValidGamingTerminalPaymentList(List<ValidGamingTerminalPayment> validGamingTerminalPaymentList) {
        this.validGamingTerminalPaymentList = validGamingTerminalPaymentList;
    }

    public List<InvalidGamingTerminalPayment> getInvalidGamingTerminalPaymentList() {
        return invalidGamingTerminalPaymentList;
    }

    public void setInvalidGamingTerminalPaymentList(List<InvalidGamingTerminalPayment> invalidGamingTerminalPaymentList) {
        this.invalidGamingTerminalPaymentList = invalidGamingTerminalPaymentList;
    }
}
