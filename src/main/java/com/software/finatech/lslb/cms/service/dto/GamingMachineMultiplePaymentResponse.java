package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class GamingMachineMultiplePaymentResponse {
    private double amountTotal;
    private List<ValidGamingMachinePayment> validGamingMachinePaymentList = new ArrayList<>();
    private List<InvalidGamingMachinePayment> invalidGamingMachinePaymentList = new ArrayList<>();
    private List<String> validGamingMachinesList = new ArrayList<>();

    public List<String> getValidGamingMachinesList() {
        return validGamingMachinesList;
    }

    public void setValidGamingMachinesList(List<String> validGamingMachinesList) {
        this.validGamingMachinesList = validGamingMachinesList;
    }

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public List<ValidGamingMachinePayment> getValidGamingMachinePaymentList() {
        return validGamingMachinePaymentList;
    }

    public void setValidGamingMachinePaymentList(List<ValidGamingMachinePayment> validGamingMachinePaymentList) {
        this.validGamingMachinePaymentList = validGamingMachinePaymentList;
    }

    public List<InvalidGamingMachinePayment> getInvalidGamingMachinePaymentList() {
        return invalidGamingMachinePaymentList;
    }

    public void setInvalidGamingMachinePaymentList(List<InvalidGamingMachinePayment> invalidGamingMachinePaymentList) {
        this.invalidGamingMachinePaymentList = invalidGamingMachinePaymentList;
    }
}
