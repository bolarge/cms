package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.domain.DocumentPurpose;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.Arrays;

public class DocumentPurposeReferenceData {
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        DocumentPurpose purpose1 = (DocumentPurpose) mongoRepositoryReactive.findById("1",DocumentPurpose.class).block();
        if(purpose1==null){
            purpose1 = new DocumentPurpose();
            purpose1.setId("1");

        }
        purpose1.setDescription("Application Form");
        purpose1.setName("Application Form");

        mongoRepositoryReactive.save(purpose1);
    }
}
