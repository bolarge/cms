package com.software.finatech.lslb.cms.service.domain;

import java.util.HashMap;
import java.util.Map;

public class FormDocumentApproval {
    private int supposedLength;
    private Map<String, Boolean> approvalMap = new HashMap<>();

    public int getSupposedLength() {
        return supposedLength;
    }

    public void setSupposedLength(int supposedLength) {
        this.supposedLength = supposedLength;
    }

    public Map<String, Boolean> getApprovalMap() {
        return approvalMap;
    }

    public void setApprovalMap(Map<String, Boolean> approvalMap) {
        this.approvalMap = approvalMap;
    }
}
