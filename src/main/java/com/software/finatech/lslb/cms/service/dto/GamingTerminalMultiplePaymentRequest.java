package com.software.finatech.lslb.cms.service.dto;


import java.util.HashSet;
import java.util.Set;

public class GamingTerminalMultiplePaymentRequest {
    private Set<String> gamingTerminalIdList = new HashSet<>();
    private double totalAmount ;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<String> getGamingTerminalIdList() {
        return gamingTerminalIdList;
    }

    public void setGamingTerminalIdList(Set<String> gamingTerminalIdList) {
        this.gamingTerminalIdList = gamingTerminalIdList;
    }
}
