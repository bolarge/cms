package com.software.finatech.lslb.cms.service.referencedata;


import com.software.finatech.lslb.cms.service.domain.AuditAction;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class AuditActionReferenceData {
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        AuditAction action = (AuditAction) mongoRepositoryReactive.findById("1",AuditAction.class).block();
        if(action==null){
            action = new AuditAction();
            action.setId("1");
        }
        action.setDescription("Login");
        action.setName("Login");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById("2",AuditAction.class).block();
        if(action==null){
            action = new AuditAction();
            action.setId("2");
        }
        action.setDescription("User");
        action.setName("User");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById("3",AuditAction.class).block();
        if(action==null){
            action = new AuditAction();
            action.setId("3");
        }
        action.setDescription("File Upload");
        action.setName("File Upload");
        mongoRepositoryReactive.saveOrUpdate(action);

    }
}