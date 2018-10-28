package com.software.finatech.lslb.cms.service.dto;

import java.util.ArrayList;
import java.util.List;

public class MachineMultiplePayment {
    private  double totalAmount;
    private String gameTypeId;
    private List<MachinePaymentDetail> machinePaymentDetailList = new ArrayList<>();


    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<MachinePaymentDetail> getMachinePaymentDetailList() {
        return machinePaymentDetailList;
    }

    public void setMachinePaymentDetailList(List<MachinePaymentDetail> machinePaymentDetailList) {
        this.machinePaymentDetailList = machinePaymentDetailList;
    }
}
