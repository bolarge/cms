package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentPurpose;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class DocumentPurposeReferenceData {

    public static final String APPLICATION_FORM_DOCUMENT_PURPOSE_ID = "1";
    public static final String AGENT_REGISTRATION_ID = "2";
    public static final String AIP_LICENSE_ID = "3";
    public static final String RENEWAL_LICENSE_ID = "4";
    public static final String AGENT_UPLOADS = "5";
    public static final String CUSTOMER_COMPLAIN_ID = "6";
    public static final String LOGGED_CASE_ID = "7";
    public static final String INSPECTION_ID = "8";
    public static final String ADD_GAMES_TO_MACHINES_ID = "9";
    public static final String LICENSE_TRANSFER_ID = "10";
    public static final String AIP_REPORT_ID = "11";


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
            purpose4.setId(RENEWAL_LICENSE_ID);

        }
        purpose4.setDescription("RENEWAL LICENSE Documents");
        purpose4.setName("RENEWAL LICENSE Documents");

        DocumentPurpose purpose5 = (DocumentPurpose) mongoRepositoryReactive.findById(AGENT_UPLOADS, DocumentPurpose.class).block();
        if (purpose5 == null) {
            purpose5 = new DocumentPurpose();
            purpose5.setId(AGENT_UPLOADS);

        }
        purpose5.setDescription("AGENTS UPLOAD IMAGES");
        purpose5.setName("AGENTS UPLOAD IMAGES");


        DocumentPurpose purpose6 = (DocumentPurpose) mongoRepositoryReactive.findById(CUSTOMER_COMPLAIN_ID, DocumentPurpose.class).block();
        if (purpose6 == null) {
            purpose6 = new DocumentPurpose();
            purpose6.setId(CUSTOMER_COMPLAIN_ID);

        }
        purpose6.setDescription("Customer complains");
        purpose6.setName("CUSTOMER COMPLAIN FILES");

        DocumentPurpose purpose7 = (DocumentPurpose) mongoRepositoryReactive.findById(LOGGED_CASE_ID, DocumentPurpose.class).block();
        if (purpose7 == null) {
            purpose7 = new DocumentPurpose();
            purpose7.setId(LOGGED_CASE_ID);

        }
        purpose7.setDescription("Logged case");
        purpose7.setName("LOGGED CASE FILES");


        DocumentPurpose purpose8 = (DocumentPurpose) mongoRepositoryReactive.findById(INSPECTION_ID, DocumentPurpose.class).block();
        if (purpose8 == null) {
            purpose8 = new DocumentPurpose();
            purpose8.setId(INSPECTION_ID);

        }
        purpose8.setDescription("Inspection");
        purpose8.setName("Inspection");


        DocumentPurpose purpose9 = (DocumentPurpose) mongoRepositoryReactive.findById(ADD_GAMES_TO_MACHINES_ID, DocumentPurpose.class).block();
        if (purpose9 == null) {
            purpose9 = new DocumentPurpose();
            purpose9.setId(ADD_GAMES_TO_MACHINES_ID);

        }
        purpose9.setDescription("Add Games to Machines");
        purpose9.setName("Add Games to machines");

        DocumentPurpose purpose10 = (DocumentPurpose) mongoRepositoryReactive.findById(LICENSE_TRANSFER_ID, DocumentPurpose.class).block();
        if (purpose10 == null) {
            purpose10 = new DocumentPurpose();
            purpose10.setId(LICENSE_TRANSFER_ID);

        }
        purpose10.setDescription("License Transfer");
        purpose10.setName("License Transfer");

        DocumentPurpose purpose11 = (DocumentPurpose) mongoRepositoryReactive.findById(AIP_REPORT_ID, DocumentPurpose.class).block();
        if (purpose11 == null) {
            purpose11 = new DocumentPurpose();
            purpose11.setId(AIP_REPORT_ID);

        }
        purpose11.setDescription("AIP REPORT Documents");
        purpose11.setName("AIP REPORT Documents");

        mongoRepositoryReactive.saveOrUpdate(purpose1);
        mongoRepositoryReactive.saveOrUpdate(purpose2);
        mongoRepositoryReactive.saveOrUpdate(purpose3);
        mongoRepositoryReactive.saveOrUpdate(purpose4);
        mongoRepositoryReactive.saveOrUpdate(purpose5);
        mongoRepositoryReactive.saveOrUpdate(purpose6);
        mongoRepositoryReactive.saveOrUpdate(purpose7);
        mongoRepositoryReactive.saveOrUpdate(purpose8);
        mongoRepositoryReactive.saveOrUpdate(purpose9);
        mongoRepositoryReactive.saveOrUpdate(purpose10);
        mongoRepositoryReactive.saveOrUpdate(purpose11);
    }
}
