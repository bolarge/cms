package com.software.finatech.lslb.cms.service.dto;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GamingMachineMultiplePaymentRequest {
    private Set<String> gamingMachineIdList = new HashSet<>();
    private double totalAmount ;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<String> getGamingMachineIdList() {
        return gamingMachineIdList;
    }

    public void setGamingMachineIdList(Set<String> gamingMachineIdList) {
        this.gamingMachineIdList = gamingMachineIdList;
    }
}
