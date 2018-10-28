package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.RenewalFormStatus;
import com.software.finatech.lslb.cms.service.domain.RenewalFormStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class RenewalFormStatusReferenceData {

    public static final String PENDING_DOCUMENT_UPLOAD = "01";
    public static  final String  SUBMITTED= "02";
    public static  final String  PENDING= "03";
    public static  final String  APPROVED= "04";
    public static  final String  REJECT= "05";
    public static  final String  IN_REVIEW= "06";
    public static  final String  PENDING_RESUBMISION= "07";
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        RenewalFormStatus renewalFormStatus1 = (RenewalFormStatus)mongoRepositoryReactive.findById(PENDING_DOCUMENT_UPLOAD, RenewalFormStatus.class).block();
        if (renewalFormStatus1 == null){
            renewalFormStatus1 = new RenewalFormStatus();
            renewalFormStatus1.setId(PENDING_DOCUMENT_UPLOAD);
        }
        renewalFormStatus1.setName("PENDING DOCUMENT UPLOAD");


        RenewalFormStatus renewalFormStatus2 = (RenewalFormStatus)mongoRepositoryReactive.findById(SUBMITTED, RenewalFormStatus.class).block();
        if (renewalFormStatus2 == null){
            renewalFormStatus2 = new RenewalFormStatus();
            renewalFormStatus2.setId(SUBMITTED);
        }
        renewalFormStatus2.setName("SUBMITTED");
        RenewalFormStatus renewalFormStatus3 = (RenewalFormStatus)mongoRepositoryReactive.findById(PENDING, RenewalFormStatus.class).block();
        if (renewalFormStatus3 == null){
            renewalFormStatus3= new RenewalFormStatus();
            renewalFormStatus3.setId(PENDING);
        }
        renewalFormStatus3.setName("PENDING");


        RenewalFormStatus renewalFormStatus4 = (RenewalFormStatus)mongoRepositoryReactive.findById(APPROVED, RenewalFormStatus.class).block();
        if (renewalFormStatus4 == null){
            renewalFormStatus4= new RenewalFormStatus();
            renewalFormStatus4.setId(APPROVED);
        }
        renewalFormStatus4.setName("APPROVED");

        RenewalFormStatus renewalFormStatus5 = (RenewalFormStatus)mongoRepositoryReactive.findById(REJECT, RenewalFormStatus.class).block();
        if (renewalFormStatus5 == null){
            renewalFormStatus5= new RenewalFormStatus();
            renewalFormStatus5.setId(REJECT);
        }
        renewalFormStatus5.setName("REJECT");

        RenewalFormStatus renewalFormStatus6 = (RenewalFormStatus)mongoRepositoryReactive.findById(IN_REVIEW, RenewalFormStatus.class).block();
        if (renewalFormStatus6 == null){
            renewalFormStatus6= new RenewalFormStatus();
            renewalFormStatus6.setId(IN_REVIEW);
        }
        renewalFormStatus6.setName("IN REVIEW");

        RenewalFormStatus renewalFormStatus7 = (RenewalFormStatus)mongoRepositoryReactive.findById(PENDING_RESUBMISION, RenewalFormStatus.class).block();
        if (renewalFormStatus7 == null){
            renewalFormStatus7= new RenewalFormStatus();
            renewalFormStatus7.setId(PENDING_RESUBMISION);
        }
        renewalFormStatus7.setName("PENDING RESUBMISSION");




        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus1);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus2);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus3);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus4);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus5);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus6);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus7);
    }
}
