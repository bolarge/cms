package com.software.finatech.lslb.cms.service.referencedata;


import com.software.finatech.lslb.cms.service.domain.AuditAction;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class AuditActionReferenceData {
    public static final String LOGIN_ID = "1";
    public static final String USER_ID = "2";
    public static final String APPLICATION_ID = "4";
    public static final String CONFIGURATIONS_ID = "5";
    public static final String SCHEDULED_MEETING_ID = "6";
    public static final String LICENCE_ID = "7";
    public static final String CASE_ID = "8";
    public static final String PAYMENT_ID = "9";
    public static final String CUSTOMER_COMPLAIN = "10";
    public static final String GAMING_MACHINE_ID = "11";
    public static final String AGENT_ID = "12";
    public static final String ROLE_ID = "13";
    public static final String INSTITUTION = "14";
    public static final String AIP_ID = "15";
    public static final String RENEWAL_ID = "16";
    public static final String GAMING_TERMINAL_ID = "17";

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

        action = (AuditAction) mongoRepositoryReactive.findById(CONFIGURATIONS_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(CONFIGURATIONS_ID);
        }
        action.setDescription("Fee and Category Configurations");
        action.setName("Configurations");
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
        action.setDescription("Agent");
        action.setName("Agent");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(ROLE_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(ROLE_ID);
        }
        action.setDescription("Role");
        action.setName("Role");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(INSTITUTION, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(INSTITUTION);
        }
        action.setDescription("Institution");
        action.setName("Institution");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(AIP_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(AIP_ID);
        }
        action.setDescription("AIP");
        action.setName("AIP");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(RENEWAL_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(RENEWAL_ID);
        }
        action.setDescription("RENEWAL");
        action.setName("RENEWAL");
        mongoRepositoryReactive.saveOrUpdate(action);

        action = (AuditAction) mongoRepositoryReactive.findById(GAMING_TERMINAL_ID, AuditAction.class).block();
        if (action == null) {
            action = new AuditAction();
            action.setId(GAMING_TERMINAL_ID);
        }
        action.setDescription("GAMING TERMINAL");
        action.setName("GAMING TERMINAL");
        mongoRepositoryReactive.saveOrUpdate(action);
    }
}
