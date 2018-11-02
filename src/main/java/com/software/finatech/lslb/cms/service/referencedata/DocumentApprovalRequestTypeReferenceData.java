package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class DocumentApprovalRequestTypeReferenceData {

    public static final String CREATE_DOCUMENT_TYPE_ID = "1";
    public static final String SET_APPROVER_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        loadForIdNameAndDesc(CREATE_DOCUMENT_TYPE_ID, mongoRepositoryReactive, "CREATE DOCUMENT TYPE", "Create a new document type , with an approver set");
        loadForIdNameAndDesc(SET_APPROVER_ID, mongoRepositoryReactive, "SET APPROVER", null);
    }

    private static void loadForIdNameAndDesc(String id, MongoRepositoryReactiveImpl mongoRepositoryReactive, String name, String description) {
        DocumentApprovalRequestType requestType = (DocumentApprovalRequestType) mongoRepositoryReactive.findById(id, DocumentApprovalRequestType.class).block();
        if (requestType == null) {
            requestType = new DocumentApprovalRequestType();
            requestType.setId(id);
        }
        requestType.setName(name);
        requestType.setDescription(description);
        mongoRepositoryReactive.saveOrUpdate(requestType);
    }
}
