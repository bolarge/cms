package com.software.finatech.lslb.cms.service.dto;

public class FailedLine {
    private String line;
    private String reason;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static FailedLine fromLineAndReason(String line, String reason) {
        FailedLine failedLine = new FailedLine();
        failedLine.setReason(reason);
        failedLine.setLine(line);
        return failedLine;
    }
}
