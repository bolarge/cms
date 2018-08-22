package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ApplicationFormStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;


public class ApplicationFormStatusReferenceData {

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        ApplicationFormStatus status1 = (ApplicationFormStatus) mongoRepositoryReactive.findById("1", ApplicationFormStatus.class).block();
        if (status1 == null) {
            status1 = new ApplicationFormStatus();
            status1.setId("1");
        }
        status1.setName("REGISTERED");
        status1.setDescription("Institution has been initially created");

        ApplicationFormStatus status2 = (ApplicationFormStatus) mongoRepositoryReactive.findById("2", ApplicationFormStatus.class).block();
        if (status2 == null) {
            status2 = new ApplicationFormStatus();
            status2.setId("2");
        }
        status2.setName("PENDING APPLICATION FEE PAYMENT");
        status2.setDescription("Institution needs to pay application fee to proceed");
        status2.setNextStep("Pay application fees");

        ApplicationFormStatus status3 = (ApplicationFormStatus) mongoRepositoryReactive.findById("3", ApplicationFormStatus.class).block();
        if (status3 == null) {
            status3 = new ApplicationFormStatus();
            status3.setId("3");
        }
        status3.setName("PENDING APPLICATION FORM SUBMISSION");
        status3.setDescription("Institution has made application fee payment but needs to fill and submit application form");
        status3.setNextStep("Fill and submit application form");

        ApplicationFormStatus status4 = (ApplicationFormStatus) mongoRepositoryReactive.findById("4", ApplicationFormStatus.class).block();
        if (status4 == null) {
            status4 = new ApplicationFormStatus();
            status4.setId("4");
        }
        status4.setName("PENDING REQUIRED DOCUMENT UPLOAD");
        status4.setDescription("Institution has successfully uploaded/filled application form needs to upload required documents for category");
        status4.setNextStep("Upload required documents for category");


        ApplicationFormStatus status5 = (ApplicationFormStatus) mongoRepositoryReactive.findById("5", ApplicationFormStatus.class).block();
        if (status5 == null) {
            status5 = new ApplicationFormStatus();
            status5.setId("5");
        }
        status5.setName("PHYSICAL MEETING/PRESENTATION SCHEDULED");
        status5.setDescription("Institution physical meeting/presentations scheduled");
        status5.setNextStep("Close physical meeting");

        ApplicationFormStatus status6 = (ApplicationFormStatus) mongoRepositoryReactive.findById("6", ApplicationFormStatus.class).block();
        if (status6 == null) {
            status6 = new ApplicationFormStatus();
            status6.setId("6");
        }
        status6.setName("PENDING  UPLOAD ADDITIONAL DOCUMENTS SPECIFIED");
        status6.setDescription("LSLB requires upload of additional documents by institution");
        status6.setNextStep("Upload additional documents required by LSLB");

        ApplicationFormStatus status7 = (ApplicationFormStatus) mongoRepositoryReactive.findById("7", ApplicationFormStatus.class).block();
        if (status7 == null) {
            status7 = new ApplicationFormStatus();
            status7.setId("7");
        }
        status7.setName("PENDING LISCENCE FEE PAYMENT");
        status7.setDescription("Institution has met criteria for application but need to pay license fee to get to AIP");
        status7.setNextStep("Pay license fee");

        ApplicationFormStatus status8 = (ApplicationFormStatus) mongoRepositoryReactive.findById("8", ApplicationFormStatus.class).block();
        if (status8 == null) {
            status8 = new ApplicationFormStatus();
            status8.setId("8");
        }

        status8.setName("APPROVAL IN PRINCIPLE");
        status8.setDescription("Institution has paid license fee and is now in AIP");

        ApplicationFormStatus status9 = (ApplicationFormStatus) mongoRepositoryReactive.findById("9", ApplicationFormStatus.class).block();
        if (status9 == null) {
            status9 = new ApplicationFormStatus();
            status9.setId("9");
        }
        status9.setName("LICENSED");
        status9.setDescription("Institution has been licensed");

        ApplicationFormStatus status10 = (ApplicationFormStatus) mongoRepositoryReactive.findById("10", ApplicationFormStatus.class).block();
        if (status10 == null) {
            status10 = new ApplicationFormStatus();
            status10.setId("10");
        }
        status10.setName("COMPLETED");
        status10.setDescription("Application form has been completed and has served its purpose");

        mongoRepositoryReactive.saveOrUpdate(status1);
        mongoRepositoryReactive.saveOrUpdate(status2);
        mongoRepositoryReactive.saveOrUpdate(status3);
        mongoRepositoryReactive.saveOrUpdate(status4);
        mongoRepositoryReactive.saveOrUpdate(status5);
        mongoRepositoryReactive.saveOrUpdate(status6);
        mongoRepositoryReactive.saveOrUpdate(status7);
        mongoRepositoryReactive.saveOrUpdate(status8);
        mongoRepositoryReactive.saveOrUpdate(status9);
        mongoRepositoryReactive.saveOrUpdate(status10);
    }
}