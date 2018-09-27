package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.RenewalFormStatus;
import com.software.finatech.lslb.cms.service.domain.RenewalFormStatus;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class RenewalFormStatusReferenceData {

    public static final String PENDING_DOCUMENT_UPLOAD = "01";
    public static  final String  SUBMITTED= "02";

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




        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus1);
        mongoRepositoryReactive.saveOrUpdate(renewalFormStatus2);


    }
}
