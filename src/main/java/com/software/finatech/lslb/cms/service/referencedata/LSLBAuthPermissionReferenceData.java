package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LSLBAuthPermissionReferenceData {
    public static final String RECEIVE_APPLICATION_ID = "31";
    public static final String RECEIVE_PAYMENT_NOTIFICATION_ID = "32";
    public static final String RECEIVE_CUSTOMER_COMPLAIN_ID = "33";
    public static final String RECEIVE_AIP_ID = "71";
    public static final String RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID = "48";
    public static final String RECEIVE_CASE_NOTIFICATION_ID = "51";
    public static final String APPROVE_APPLICATION_FORM_ID = "57";
    public static final String RECEIVE_MACHINE_APPLICATION_NOTIFICATION_ID = "64";
    public static final String RECEIVE_FEE_EXPIRIY_NOTIFICATION_ID = "65";
    public static final String RECEIVE_LICENSE_TRANSFER_NOTIFICATION_ID = "66";
    public static final String FINAL_LICENSE_TRANSFER_APPROVER_ID = "93";
    public static final String INITIAL_LICENSE_TRANSFER_APPROVER_ID = "95";
    public static final String RECEIVE_INSPECTION_FORM_NOTIFICATION_ID = "94";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        deleteAllPermissions(mongoRepositoryReactive);
        AuthPermission permission = (AuthPermission) mongoRepositoryReactive.findById("1", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("1");
        }
        permission.setName("CREATE USER PROFILE");
        permission.setDescription("Create user and update users email, phone, role etc");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("2", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("2");
        }
        permission.setName("CHANGE USER STATUS");
        permission.setDescription("Change user from enabled to disabled and the other way round");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("3", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("3");
        }
        permission.setName("CREATE AGENT");
        permission.setDescription("Creates an agent or add self to agent");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("4", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("4");
        }
        permission.setName("APPROVE AGENT APPROVAL REQUEST");
        permission.setDescription("Approve and reject agent approval request");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);


        permission = (AuthPermission) mongoRepositoryReactive.findById("5", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("5");
        }
        permission.setName("SCHEDULE PRESENTATION WITH APPLICANT");
        permission.setDescription("Create a scheduled presentation with an applicant , view and reschedule a presentation with the applicant");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("6", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("6");
        }
        permission.setName("VIEW APPLICATION FORMS");
        permission.setDescription("View all application forms on the system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);


        permission = (AuthPermission) mongoRepositoryReactive.findById("7", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("7");
        }
        permission.setName("APPROVE APPLICATION FORM");
        permission.setDescription("Approve and reject application forms");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.delete(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("8", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("8");
        }
        permission.setName("VIEW AIPs");
        permission.setDescription("View AIP required documents and upload");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("9", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("9");
        }
        permission.setName("APPROVE AIPs");
        permission.setDescription("Approve AIPs on the system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("10", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("10");
        }
        permission.setName("VIEW GAMING CATEGORIES");
        permission.setDescription("View all game types on the system  and their details");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("11", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("11");
        }
        permission.setName("CREATE GAMING CATEGORIES");
        permission.setDescription("Create and update properties of categories");
        // removed this permission because we dont create from UI
        //        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("12", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("12");
        }
        permission.setName("VIEW FEES");
        permission.setDescription("View fees configured on system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("13", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("13");
        }
        permission.setName("CREATE FEES");
        permission.setDescription("Create and update fees configured on system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("14", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("14");
        }
        permission.setName("VIEW REVENUE LINES");
        permission.setDescription("View revenue lines configured on system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("15", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("15");
        }
        permission.setName("CREATE REVENUE LINES");
        permission.setDescription("Create and update Revenue lines configured on system");
        //commented out because we cannot create revenue lines
        // mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("16", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("16");
        }
        permission.setName("VIEW PAYMENT RECORDS");
        permission.setDescription("View payment records on system based on property of user");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("17", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("17");
        }
        permission.setName("MAKE NEW PAYMENT");
        permission.setDescription("Make a new payment on the system");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("18", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("18");
        }
        permission.setName("VIEW DASHBOARD");
        permission.setDescription("View system dashboard based on property of user logged in");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("19", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("19");
        }
        permission.setName("VIEW DOCUMENT TYPES");
        permission.setDescription("View document types configured on system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("20", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("20");
        }
        permission.setName("CREATE DOCUMENT TYPES");
        permission.setDescription("Create and update document types configured on system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("21", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("21");
        }
        permission.setName("VIEW DOCUMENT PURPOSES");
        permission.setDescription("View Document purposes");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("22", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("22");
        }
        permission.setName("CREATE DOCUMENT PURPOSES");
        permission.setDescription("Create and update Document purposes");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("23", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("23");
        }
        permission.setName("VIEW DOCUMENTS");
        permission.setDescription("View documents on system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("24", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("24");
        }
        permission.setName("VIEW LICENCES");
        permission.setDescription("View Licences on system based on user logged in params");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("25", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("25");
        }
        permission.setName("VIEW LOGGED REPORTS");
        permission.setDescription("View Logged reports from inspection and audit");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("26", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("26");
        }
        permission.setName("UPLOAD REPORT");
        permission.setDescription("Upload Report");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("27", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("27");
        }
        permission.setName("VIEW LOGGED CASES");
        permission.setDescription("View Logged cases based on user logged in");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("28", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("28");
        }
        permission.setName("CREATE LOGGED CASE");
        permission.setDescription("Create logged case and add more details to logged case");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("29", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("29");
        }
        permission.setName("UPDATE LOGGED CASE");
        permission.setDescription("Update logged case status");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("30", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("30");
        }
        permission.setName("VIEW AUDIT TRAILS");
        permission.setDescription("View Audit Trails based on institution logged in");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_APPLICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_APPLICATION_ID);

        }
        permission.setDescription("Can receive application form submissions notifications");
        permission.setName("CAN RECEIVE APPLICATION SUBMISSION NOTIFICATION");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_PAYMENT_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_PAYMENT_NOTIFICATION_ID);
        }

        permission.setDescription("Can receive payment notification");
        permission.setName("RECEIVE PAYMENT NOTIFICATION");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_CUSTOMER_COMPLAIN_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_CUSTOMER_COMPLAIN_ID);
        }
        permission.setDescription("Can receive customer complains  notifications (new and reminder)");
        permission.setName("RECEIVE CUSTOMER COMPLAIN NOTIFICATION");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("34", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("34");
        }
        permission.setName("CREATE APPLICATION FORMS");
        permission.setDescription("Create an application form on the system");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("35", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("35");
        }
        permission.setName("VIEW AGENT APPROVAL REQUEST");
        permission.setDescription("View all agent approval requests");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("36", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("36");
        }
        permission.setName("VIEW USER");
        permission.setDescription("View users on platform based on user logged in");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("37", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("37");
        }
        permission.setName("VIEW AGENTS");
        permission.setDescription("View all agents on platform");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("38", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("38");
        }
        permission.setName("VIEW SCHEDULED PRESENTATIONS");
        permission.setDescription("View all scheduled presentations with applicant");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("39", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("39");
        }
        permission.setName("CREATE INSTITUTIONS");
        permission.setDescription("Can Create an institution on the system");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("40", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("40");
        }
        permission.setName("VIEW INSTITUTIONS");
        permission.setDescription("Can View institutions on the system");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("41", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("41");
        }
        permission.setName("CREATE GAMING MACHINE");
        permission.setDescription("Create gaming machine");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("42", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("42");
        }
        permission.setName("VIEW GAMING MACHINES");
        permission.setDescription("View gaming machine");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("43", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("43");
        }
        permission.setName("ADD PERMISSION_TO USER");
        permission.setDescription("Add permission to user");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("44", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("44");
        }
        permission.setName("START LICENCE RENEWAL");
        permission.setDescription("Create and update licence renewals");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("45", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("45");
        }
        permission.setName("VIEW LICENCE RENEWALS");
        permission.setDescription("View Licence Renewals");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("46", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("46");
        }
        permission.setName("APPROVE LICENCE RENEWALS");
        permission.setDescription("Approve licence renewals");
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("47", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("47");
        }
        permission.setName("RECEIVE NEW CASE NOTIFICATION");
        permission.setDescription("Receive new case Notification");
        permission.setUsedBySystem(true);
        //Removed this because it is duplicate
        // mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID);
        }
        permission.setName("RECEIVE NEW AGENT APPROVAL REQUEST NOTIFICATION");
        permission.setDescription("Receive new agent approval request Notification");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);


        permission = (AuthPermission) mongoRepositoryReactive.findById("49", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("49");
        }
        permission.setName("VIEW ROLES");
        permission.setDescription("View roles on system");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("50", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("50");
        }
        permission.setName("ADD PERMISSIONS TO ROLE");
        permission.setDescription("Can add permissions to roles and update roles");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_CASE_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_CASE_NOTIFICATION_ID);
        }
        permission.setName("RECEIVE NEW CASE NOTIFICATION");
        permission.setDescription("Can receive case notification (new and reminder)");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("52", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("52");
        }
        permission.setName("VIEW USER APPROVAL REQUESTS");
        permission.setDescription("Can view user approval requests");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("53", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("53");
        }
        permission.setName("VIEW CUSTOMER COMPLAINTS");
        permission.setDescription("Can view customer complaints");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("54", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("54");
        }
        permission.setName("UPDATE CUSTOMER COMPLAINTS");
        permission.setDescription("Can update customer complaints");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("55", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("55");
        }
        permission.setName("VIEW FEE APPROVAL REQUESTS");
        permission.setDescription("Can view fee approval requests");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("56", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("56");
        }
        permission.setName("APPROVE FEE APPROVAL REQUESTS");
        permission.setDescription("Can Approve / Reject fee approval request");
        permission.setUsedBySystem(false);
        permission.setAuthRoleId(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(APPROVE_APPLICATION_FORM_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(APPROVE_APPLICATION_FORM_ID);
        }
        permission.setName("APPROVE APPLICATION FORM");
        permission.setDescription("Can Approve / Reject application forms");
        permission.setUsedBySystem(false);
        permission.setAuthRoleId(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("58", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("58");
        }
        permission.setName("VIEW USER APPROVAL REQUESTS");
        permission.setDescription("Can View User Approval Requests");
        permission.setUsedBySystem(false);
        //Not loaded because its a duplicate
        // mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("59", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("59");
        }
        permission.setName("APPROVE USER APPROVAL REQUESTS");
        permission.setDescription("Can Approve/Reject  Approval Requests");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("60", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("60");
        }
        permission.setName("VIEW MACHINE APPROVAL REQUESTS");
        permission.setDescription("View Machine Approval Requests");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("61", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("61");
        }
        permission.setName("APPROVE MACHINE APPROVAL REQUESTS");
        permission.setDescription("Can Approve/Reject Machine Approval Requests");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("62", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("62");
        }
        permission.setName("VIEW AGENT APPROVAL REQUESTS");
        permission.setDescription("Can View Agent Approval Requests");
        permission.setUsedBySystem(false);
        //Not loaded because its a duplicate
//        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("63", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("63");
        }
        permission.setName("APPROVE AGENT APPROVAL REQUESTS");
        permission.setDescription("Can Approve/Reject AGENT Approval Requests");
        permission.setUsedBySystem(false);
        //Not loaded because its a duplicate
        //mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_MACHINE_APPLICATION_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_MACHINE_APPLICATION_NOTIFICATION_ID);
        }
        permission.setName("RECEIVE MACHINE APPROVAL REQUEST NOTIFICATION");
        permission.setDescription("Receive Machine approval Request Notification");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_FEE_EXPIRIY_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_FEE_EXPIRIY_NOTIFICATION_ID);
        }
        permission.setName("RECEIVE FEE EXPIRY NOTIFICATION");
        permission.setDescription("Receive Fee Expiry Notification");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_LICENSE_TRANSFER_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_LICENSE_TRANSFER_NOTIFICATION_ID);
        }
        permission.setName("RECEIVE LICENCE TRANSFER NOTIFICATION");
        permission.setDescription("Receive Licence Transfer Notification");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_AIP_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_AIP_ID);
        }
        permission.setName("RECEIVE AIP REQUEST NOTIFICATION");
        permission.setDescription("Receive AIP request Notification");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("72", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("72");
        }
        permission.setName("ASSIGN TERMINALS TO AGENT");
        permission.setDescription("ASSIGN TERMINALS TO AGENT");
        permission.setUsedBySystem(false);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("73", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("73");
        }
        permission.setName("EDIT AGENT DETAILS");
        permission.setDescription("EDIT AGENT DETAILS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("74", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("74");
        }
        permission.setName("CREATE GAMING TERMINALS");
        permission.setDescription("CREATE GAMING TERMINALS");
        permission.setUsedBySystem(false);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("75", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("75");
        }
        permission.setName("VIEW GAMING TERMINALS");
        permission.setDescription("VIEW GAMING TERMINALS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("76", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("76");
        }
        permission.setName("VIEW GAMING TERMINAL APPROVAL REQUESTS");
        permission.setDescription("VIEW GAMING TERMINAL APPROVAL REQUESTS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("77", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("77");
        }
        permission.setName("APPROVE/REJECT GAMING TERMINAL APPROVAL REQUESTS");
        permission.setDescription("APPROVE/REJECT GAMING TERMINAL APPROVAL REQUESTS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("78", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("78");
        }
        permission.setName("UPLOAD OPERATORS LEGACY DOCUMENTS");
        permission.setDescription("UPLOAD OPERATORS LEGACY DOCUMENTS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("79", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("79");
        }
        permission.setName("VIEW LICENCE TRANSFERS");
        permission.setDescription("VIEW LICENCE TRANSFERS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("80", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("80");
        }
        permission.setName("APPROVE/REJECT LICENCE TRANSFER (Transferror)");
        permission.setDescription("APPROVE LICENCE TRANSFER");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("81", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("81");
        }
        permission.setName("APPROVE/REJECT FINAL APPROVAL LICENCE TRANSFER");
        permission.setDescription("APPROVE/REJECT FINAL APPROVAL LICENCE TRANSFER");
        permission.setAuthRoleId(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        permission.setUsedBySystem(false);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("82", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("82");
        }
        permission.setName("SCHEDULE PRESENTATION FOR LICENCE TRANSFER");
        permission.setDescription("SCHEDULE PRESENTATION FOR LICENCE TRANSFER");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("83", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("83");
        }
        permission.setName("VIEW DOCUMENT APPROVAL REQUESTS");
        permission.setDescription("VIEW DOCUMENT APPROVAL REQUESTS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("84", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("84");
        }
        permission.setName("APPROVE/REJECT DOCUMENT APPROVAL REQUESTS");
        permission.setDescription("APPROVE/REJECT DOCUMENT APPROVAL REQUESTS");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("85", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("85");
        }
        permission.setName("SET APPROVER TO DOCUMENT TYPE");
        permission.setDescription("SET APPROVER TO DOCUMENT TYPE");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("86", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("86");
        }
        permission.setName("EFFECT OUTCOME ON LOGGED CASES");
        permission.setDescription("EFFECT OUTCOME ON LOGGED CASES");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("87", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("87");
        }
        permission.setName("VIEW AGENT BVN");
        permission.setDescription("CAN VIEW AGENT BVN");
        permission.setUsedBySystem(false);
        permission.getAuthRoleIds().addAll(Arrays.asList("4", "5"));
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("88", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("88");
        }
        permission.setName("VIEW CATEGORIES");
        permission.setDescription("VIEW CATEGORIES");
        permission.setUsedBySystem(false);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("89", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("89");
        }
        permission.setName("CREATE/UPDATE  CATEGORIES");
        permission.setDescription("CREATE/UPDATE CATEGORIES");
        permission.setUsedBySystem(false);
        //no more loaded because
        //  mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("90", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("90");
        }
        permission.setName("REVERSE SUSPENDED/TERMINATED/REVOKED LICENCE");
        permission.setDescription("Reverse suspended/terminated/revoked licence");
        permission.setUsedBySystem(false);
        permission.setAuthRoleId(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        mongoRepositoryReactive.saveOrUpdate(permission);


        permission = (AuthPermission) mongoRepositoryReactive.findById("91", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("91");
        }
        permission.setName("CREATE ROLE");
        permission.setDescription("can create roles");
        permission.setUsedBySystem(false);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById("92", AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId("92");
        }
        permission.setName("UPDATE USER ROLE");
        permission.setDescription("update user role");
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(FINAL_LICENSE_TRANSFER_APPROVER_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(FINAL_LICENSE_TRANSFER_APPROVER_ID);
        }
        permission.setName("APPROVE FINAL LICENSE TRANSFER DECISION");
        permission.setDescription("Approves final license transfer decision");
        permission.setAuthRoleId(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        mongoRepositoryReactive.saveOrUpdate(permission);


        permission = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_INSPECTION_FORM_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(RECEIVE_INSPECTION_FORM_NOTIFICATION_ID);
        }
        permission.setName("RECEIVE INSPECTION FORMS NOTIFICATION");
        permission.setDescription("Receive inspection form  Notification");
        permission.setUsedBySystem(true);
        mongoRepositoryReactive.saveOrUpdate(permission);

        permission = (AuthPermission) mongoRepositoryReactive.findById(INITIAL_LICENSE_TRANSFER_APPROVER_ID, AuthPermission.class).block();
        if (permission == null) {
            permission = new AuthPermission();
            permission.setId(INITIAL_LICENSE_TRANSFER_APPROVER_ID);
        }
        permission.setName("APPROVE INITIAL /EXIT LICENCE TRANSFER");
        permission.setDescription("Approve Initial / Exit License Transfer");
        permission.setAuthRoleId(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        mongoRepositoryReactive.saveOrUpdate(permission);
    }


    private static List<String> getCodeUsedPermissions() {
        List<String> codePermissions = new ArrayList<>();
        codePermissions.add(RECEIVE_APPLICATION_ID);
        codePermissions.add(RECEIVE_AIP_ID);
        codePermissions.add(RECEIVE_CUSTOMER_COMPLAIN_ID);
        codePermissions.add(RECEIVE_PAYMENT_NOTIFICATION_ID);
        codePermissions.add(RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID);
        codePermissions.add(RECEIVE_CASE_NOTIFICATION_ID);
        codePermissions.add(RECEIVE_MACHINE_APPLICATION_NOTIFICATION_ID);
        codePermissions.add(RECEIVE_FEE_EXPIRIY_NOTIFICATION_ID);
        codePermissions.add(RECEIVE_LICENSE_TRANSFER_NOTIFICATION_ID);
        return codePermissions;
    }


    private static void deleteAllPermissions(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        ArrayList<AuthPermission> authPermissions = (ArrayList<AuthPermission>) mongoRepositoryReactive.findAll(new Query(), AuthPermission.class).toStream().collect(Collectors.toList());
        for (AuthPermission authPermission : authPermissions) {
            mongoRepositoryReactive.delete(authPermission);
        }
    }
}
