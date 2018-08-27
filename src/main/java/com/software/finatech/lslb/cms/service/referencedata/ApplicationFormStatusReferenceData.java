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
        status1.setName("CREATED");
        status1.setDescription("Application form has been created ");

        ApplicationFormStatus status2 = (ApplicationFormStatus) mongoRepositoryReactive.findById("2", ApplicationFormStatus.class).block();
        if (status2 == null) {
            status2 = new ApplicationFormStatus();
            status2.setId("2");
        }
        status2.setName("FORM IN PROGRESS");
        status2.setDescription("A form in the application form has been filled and submitted");

        ApplicationFormStatus status3 = (ApplicationFormStatus) mongoRepositoryReactive.findById("3", ApplicationFormStatus.class).block();
        if (status3 == null) {
            status3 = new ApplicationFormStatus();
            status3.setId("3");
        }
        status3.setName("IN REVIEW");
        status3.setDescription("Institution has filled all the forms and uploaded all the documents required for application");

        ApplicationFormStatus status4 = (ApplicationFormStatus) mongoRepositoryReactive.findById("4", ApplicationFormStatus.class).block();
        if (status4 == null) {
            status4 = new ApplicationFormStatus();
            status4.setId("4");
        }
        status4.setName("APPROVED");
        status4.setDescription("Application has been approved by an LSLB admin");

        ApplicationFormStatus status5 = (ApplicationFormStatus) mongoRepositoryReactive.findById("5", ApplicationFormStatus.class).block();
        if (status5 == null) {
            status5 = new ApplicationFormStatus();
            status5.setId("5");
        }
        status5.setName("REJECTED");
        status5.setDescription("Application has been rejected by an LSLB admin");

        mongoRepositoryReactive.saveOrUpdate(status1);
        mongoRepositoryReactive.saveOrUpdate(status2);
        mongoRepositoryReactive.saveOrUpdate(status3);
        mongoRepositoryReactive.saveOrUpdate(status4);
        mongoRepositoryReactive.saveOrUpdate(status5);
    }
}