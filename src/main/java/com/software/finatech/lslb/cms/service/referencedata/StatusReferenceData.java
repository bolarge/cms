package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.Status;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;


public class StatusReferenceData {

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        Status status1 = (Status) mongoRepositoryReactive.findById("1", Status.class).block();
        if (status1 == null) {
            status1 = new Status();
            status1.setId("1");
        }
        status1.setName("REGISTERED");
        status1.setDescription("Institution has been initially created");

        Status status2 = (Status) mongoRepositoryReactive.findById("2", Status.class).block();
        if (status2 == null) {
            status2 = new Status();
            status2.setId("2");
        }
        status2.setName("PENDING APPLICATION FEE PAYMENT");
        status2.setDescription("Institution needs to pay application fee to proceed");
        status2.setNextStep("Pay application fees");

        Status status3 = (Status) mongoRepositoryReactive.findById("3", Status.class).block();
        if (status3 == null) {
            status3 = new Status();
            status3.setId("3");
        }
        status3.setName("PENDING APPLICATION FORM SUBMISSION");
        status3.setDescription("Institution has made application fee payment but needs to fill and submit application form");
        status3.setNextStep("Fill and submit application form");

        Status status4 = (Status) mongoRepositoryReactive.findById("4", Status.class).block();
        if (status4 == null) {
            status4 = new Status();
            status4.setId("4");
        }
        status4.setName("PENDING REQUIRED DOCUMENT UPLOAD");
        status4.setDescription("Institution has successfully uploaded/filled application form needs to upload required documents for category");
        status4.setNextStep("Upload required documents for category");


        Status status5 = (Status) mongoRepositoryReactive.findById("5", Status.class).block();
        if (status5 == null) {
            status5 = new Status();
            status5.setId("5");
        }
        status5.setName("PHYSICAL MEETING/PRESENTATION SCHEDULED");
        status5.setDescription("Institution physical meeting/presentations scheduled");
        status5.setNextStep("Close physical meeting");

        Status status6 = (Status) mongoRepositoryReactive.findById("6", Status.class).block();
        if (status6 == null) {
            status6 = new Status();
            status6.setId("6");
        }
        status6.setName("PENDING  UPLOAD ADDITIONAL DOCUMENTS SPECIFIED");
        status6.setDescription("LSLB requires upload of additional documents by institution");
        status6.setNextStep("Upload additional documents required by LSLB");

        Status status7 = (Status) mongoRepositoryReactive.findById("7", Status.class).block();
        if (status7 == null) {
            status7 = new Status();
            status7.setId("7");
        }
        status7.setName("PENDING LISCENCE FEE PAYMENT");
        status7.setDescription("Institution has met criteria for application but need to pay license fee to get to AIP");
        status7.setNextStep("Pay license fee");

        Status status8 = (Status) mongoRepositoryReactive.findById("8", Status.class).block();
        if (status8 == null) {
            status8 = new Status();
            status8.setId("8");
        }

        status8.setName("APPROVAL IN PRINCIPLE");
        status8.setDescription("Institution has paid license fee and is now in AIP");

        Status status9 = (Status) mongoRepositoryReactive.findById("9", Status.class).block();
        if (status9 == null) {
            status9 = new Status();
            status9.setId("9");
        }
        status9.setName("LICENSED");
        status9.setDescription("Institution has been licensed");

        mongoRepositoryReactive.saveOrUpdate(status1);
        mongoRepositoryReactive.saveOrUpdate(status2);
        mongoRepositoryReactive.saveOrUpdate(status3);
        mongoRepositoryReactive.saveOrUpdate(status4);
        mongoRepositoryReactive.saveOrUpdate(status5);
        mongoRepositoryReactive.saveOrUpdate(status6);
        mongoRepositoryReactive.saveOrUpdate(status7);
        mongoRepositoryReactive.saveOrUpdate(status8);
        mongoRepositoryReactive.saveOrUpdate(status9);
    }
}