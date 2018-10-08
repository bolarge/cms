package com.software.finatech.lslb.cms.service.dto;


import java.util.ArrayList;
import java.util.List;

public class GamingMachineMultiplePaymentRequest {
    private List<String> gamingMachineIdList = new ArrayList<>();
    private double totalAmount ;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<String> getGamingMachineIdList() {
        return gamingMachineIdList;
    }

    public void setGamingMachineIdList(List<String> gamingMachineIdList) {
        this.gamingMachineIdList = gamingMachineIdList;
    }
}
