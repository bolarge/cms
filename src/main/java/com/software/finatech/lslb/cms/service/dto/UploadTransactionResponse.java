package com.software.finatech.lslb.cms.service.dto;

import java.util.List;

public class UploadTransactionResponse {
    private int failedTransactionCount;
    private String message;
    private List<FailedLine> failedLines;

    public List<FailedLine> getFailedLines() {
        return failedLines;
    }

    public void setFailedLines(List<FailedLine> failedLines) {
        this.failedLines = failedLines;
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

    public static UploadTransactionResponse fromMessage(String message) {
        UploadTransactionResponse uploadTransactionResponse = new UploadTransactionResponse();
        uploadTransactionResponse.setMessage(message);
        return uploadTransactionResponse;
    }
}
