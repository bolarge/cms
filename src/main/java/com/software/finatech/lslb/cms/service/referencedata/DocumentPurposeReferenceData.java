package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentPurpose;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class DocumentPurposeReferenceData {

    public static final String APPLICATION_FORM_DOCUMENT_PURPOSE_ID = "1";
    public static final String AGENT_REGISTRATION_ID = "2";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        DocumentPurpose purpose1 = (DocumentPurpose) mongoRepositoryReactive.findById(APPLICATION_FORM_DOCUMENT_PURPOSE_ID, DocumentPurpose.class).block();
        if (purpose1 == null) {
            purpose1 = new DocumentPurpose();
            purpose1.setId(APPLICATION_FORM_DOCUMENT_PURPOSE_ID);

        }
        purpose1.setDescription("Application Form");
        purpose1.setName("Application Form");

        DocumentPurpose purpose2 = (DocumentPurpose) mongoRepositoryReactive.findById(AGENT_REGISTRATION_ID, DocumentPurpose.class).block();
        if (purpose2 == null) {
            purpose2 = new DocumentPurpose();
            purpose2.setId(AGENT_REGISTRATION_ID);

        }
        purpose2.setDescription("Agent Registration");
        purpose2.setName("Agent Registration");

        mongoRepositoryReactive.saveOrUpdate(purpose1);
        mongoRepositoryReactive.saveOrUpdate(purpose2);
    }
}
