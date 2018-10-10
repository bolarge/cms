package com.software.finatech.lslb.cms.service.referencedata;


import com.software.finatech.lslb.cms.service.domain.AuditAction;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class AuditActionReferenceData {
    public static final String LOGIN_ID = "1";
    public static final String USER_ID = "2";
    public static final String APPLICATION_ID = "4";
    public static final String FEE_ID = "5";
    public static final String SCHEDULED_MEETING_ID = "6";
    public static final String LICENCE_ID = "7";
    public static final String CASE_ID = "8";
    public static final String PAYMENT_ID = "9";
    public static final String CUSTOMER_COMPLAIN = "10";
    public static final String GAMING_MACHINE_ID = "11";
    public static final String AGENT_ID = "12";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AuditAction action = (AuditAction) mongoRepositoryReactive.findById(LOGIN_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(LOGIN_ID);
        }
        action.setDescription("Login");
        action.setName("Login");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(USER_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(USER_ID);
        }
        action.setDescription("User");
        action.setName("User");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById("3", AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId("3");
        }
        action.setDescription("File Upload");
        action.setName("File Upload");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(APPLICATION_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(APPLICATION_ID);
        }
        action.setDescription("Application for licence");
        action.setName("Application");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(FEE_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(FEE_ID);
        }
        action.setDescription("Fee Configurations");
        action.setName("Fee");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(SCHEDULED_MEETING_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(SCHEDULED_MEETING_ID);
        }
        action.setDescription("Scheduled meetings");
        action.setName("Scheduled Meeting");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(LICENCE_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(LICENCE_ID);
        }
        action.setDescription("Licence");
        action.setName("Licence");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(CASE_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(CASE_ID);
        }
        action.setDescription("Logged Case");
        action.setName("Case");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(PAYMENT_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(PAYMENT_ID);
        }
        action.setDescription("Payments");
        action.setName("Payment");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(CUSTOMER_COMPLAIN, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(CUSTOMER_COMPLAIN);
        }
        action.setDescription("Customer Complaints");
        action.setName("Customer Complain");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(GAMING_MACHINE_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(GAMING_MACHINE_ID);
        }
        action.setDescription("Gaming Machine");
        action.setName("Gaming Machine");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(AGENT_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(AGENT_ID);
        }
        action.setDescription("AGENT");
        action.setName("AGENT");
        mongoRepositoryReactive.saveOrUpdate(action);
    }
}
