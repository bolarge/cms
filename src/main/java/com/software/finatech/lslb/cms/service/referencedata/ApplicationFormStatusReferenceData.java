package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.ApplicationFormStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;


public class ApplicationFormStatusReferenceData {

    public static final String CREATED_STATUS_ID = "1";
    public static final String IN_PROGRESS_STATUS_ID = "2";
    public static final String IN_REVIEW_STATUS_ID = "3";
    public static final String APPROVED_STATUS_ID = "4";
    public static final String REJECTED_STATUS_ID = "5";
    public static final String PENDING_RESUBMISSON_ID = "6";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        ApplicationFormStatus status1 = (ApplicationFormStatus) mongoRepositoryReactive.findById(CREATED_STATUS_ID, ApplicationFormStatus.class).block();
        if (status1 == null) {
            status1 = new ApplicationFormStatus();
            status1.setId(CREATED_STATUS_ID);
        }
        status1.setName("CREATED");
        status1.setDescription("Application form has been created ");

        ApplicationFormStatus status2 = (ApplicationFormStatus) mongoRepositoryReactive.findById(IN_PROGRESS_STATUS_ID, ApplicationFormStatus.class).block();

        if (status2 == null) {
            status2 = new ApplicationFormStatus();
            status2.setId(IN_PROGRESS_STATUS_ID);
        }
        status2.setName("IN PROGRESS");
        status2.setDescription("A form in the application form has been filled and submitted");

        ApplicationFormStatus status3 = (ApplicationFormStatus) mongoRepositoryReactive.findById(IN_REVIEW_STATUS_ID, ApplicationFormStatus.class).block();
        if (status3 == null) {
            status3 = new ApplicationFormStatus();
            status3.setId(IN_REVIEW_STATUS_ID);
        }
        status3.setName("IN REVIEW");
        status3.setDescription("Institution has filled all the forms and uploaded all the documents required for application");

        ApplicationFormStatus status4 = (ApplicationFormStatus) mongoRepositoryReactive.findById(APPROVED_STATUS_ID, ApplicationFormStatus.class).block();
        if (status4 == null) {
            status4 = new ApplicationFormStatus();
            status4.setId(APPROVED_STATUS_ID);
        }
        status4.setName("APPROVED");
        status4.setDescription("Application has been approved by an LSLB admin");

        ApplicationFormStatus status5 = (ApplicationFormStatus) mongoRepositoryReactive.findById(REJECTED_STATUS_ID, ApplicationFormStatus.class).block();
        if (status5 == null) {
            status5 = new ApplicationFormStatus();
            status5.setId(REJECTED_STATUS_ID);
        }
        status5.setName("REJECTED");
        status5.setDescription("Application has been rejected by an LSLB admin");

        ApplicationFormStatus status6 = (ApplicationFormStatus) mongoRepositoryReactive.findById(PENDING_RESUBMISSON_ID, ApplicationFormStatus.class).block();
        if (status6 == null) {
            status6 = new ApplicationFormStatus();
            status6.setId(PENDING_RESUBMISSON_ID);
        }
        status6.setName("PENDING RESUBMISSION");
        status6.setDescription("LSLB admin has made a comment to the application, either for the institution to submit an additional document");


        mongoRepositoryReactive.saveOrUpdate(status1);
        mongoRepositoryReactive.saveOrUpdate(status2);
        mongoRepositoryReactive.saveOrUpdate(status3);
        mongoRepositoryReactive.saveOrUpdate(status4);
        mongoRepositoryReactive.saveOrUpdate(status5);
        mongoRepositoryReactive.saveOrUpdate(status6);
    }
}