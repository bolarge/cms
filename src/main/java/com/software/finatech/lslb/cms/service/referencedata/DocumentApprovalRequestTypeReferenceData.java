package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class DocumentApprovalRequestTypeReferenceData {

    public static final String CREATE_DOCUMENT_TYPE_ID = "1";
    public static final String SET_APPROVER_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        DocumentApprovalRequestType requestType = (DocumentApprovalRequestType) mongoRepositoryReactive.findById(CREATE_DOCUMENT_TYPE_ID, DocumentApprovalRequestType.class).block();
        if (requestType == null) {
            requestType = new DocumentApprovalRequestType();
            requestType.setId(CREATE_DOCUMENT_TYPE_ID);
        }
        requestType.setName("CREATE DOCUMENT TYPE");
        requestType.setDescription("Create a new document type , with an approver set");
        mongoRepositoryReactive.saveOrUpdate(requestType);


        requestType = (DocumentApprovalRequestType) mongoRepositoryReactive.findById(SET_APPROVER_ID, DocumentApprovalRequestType.class).block();
        if (requestType == null) {
            requestType = new DocumentApprovalRequestType();
            requestType.setId(SET_APPROVER_ID);
        }
        requestType.setName("SET APPROVER");
        mongoRepositoryReactive.saveOrUpdate(requestType);
    }
}
