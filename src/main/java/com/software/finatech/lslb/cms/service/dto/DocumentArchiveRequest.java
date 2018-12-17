package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class DocumentArchiveRequest {
    @NotEmpty
    private Set<String> documentIds = new HashSet<>();

    public Set<String> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(Set<String> documentIds) {
        this.documentIds = documentIds;
    }
}
