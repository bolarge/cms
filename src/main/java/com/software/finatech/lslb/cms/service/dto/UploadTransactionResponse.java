package com.software.finatech.lslb.cms.service.dto;

import java.util.List;

public class UploadTransactionResponse {
    private List<String> failedTransactions;
    private int failedTransactionCount;
    private String message;

    public List<String> getFailedTransactions() {
        return failedTransactions;
    }

    public void setFailedTransactions(List<String> failedTransactions) {
        this.failedTransactions = failedTransactions;
    }

    public int getFailedTransactionCount() {
        return failedTransactionCount;
    }

    public void setFailedTransactionCount(int failedTransactionCount) {
        this.failedTransactionCount = failedTransactionCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
