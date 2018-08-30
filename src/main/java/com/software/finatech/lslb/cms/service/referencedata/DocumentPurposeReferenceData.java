package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentPurpose;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class DocumentPurposeReferenceData {

    public static final String APPLICATION_FORM_DOCUMENT_PURPOSE_ID = "1";
    public static final String AGENT_REGISTRATION_ID = "2";
    public static final String AIP_LICENSE_ID = "3";
    public static final String RENEWAL_LICENSE_ID = "4";

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


        DocumentPurpose purpose3 = (DocumentPurpose) mongoRepositoryReactive.findById(AIP_LICENSE_ID, DocumentPurpose.class).block();
        if (purpose3 == null) {
            purpose3 = new DocumentPurpose();
            purpose3.setId(AIP_LICENSE_ID);

        }
        purpose3.setDescription("AIP Documents");
        purpose3.setName("AIP Documents");

        DocumentPurpose purpose4 = (DocumentPurpose) mongoRepositoryReactive.findById(RENEWAL_LICENSE_ID, DocumentPurpose.class).block();
        if (purpose4 == null) {
            purpose4 = new DocumentPurpose();
            purpose4.setId(AIP_LICENSE_ID);

        }
        purpose4.setDescription("RENEWAL LICENSE Documents");
        purpose4.setName("RENEWAL LICENSE Documents");

        mongoRepositoryReactive.saveOrUpdate(purpose1);
        mongoRepositoryReactive.saveOrUpdate(purpose2);
        mongoRepositoryReactive.saveOrUpdate(purpose3);
        mongoRepositoryReactive.saveOrUpdate(purpose4);
    }
}
