package com.software.finatech.lslb.cms.service.model.vigipay;

public class VigipayInBranchNotification {
    private String MessageType;
    private VigiPayMessage Message;

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public VigiPayMessage getMessage() {
        return Message;
    }

    public void setMessage(VigiPayMessage message) {
        Message = message;
    }
}
