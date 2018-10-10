package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LSLBAuthPermissionReferenceData {
    public static final String RECEIVE_APPLICATION_ID = "31";
    public static final String RECEIVE_PAYMENT_NOTIFICATION_ID = "32";
    public static final String RECEIVE_CUSTOMER_COMPLAIN_ID = "33";
    public static final String RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID = "48";
    public static final String RECEIVE_CASE_NOTIFICATION_ID = "51";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AuthPermission permission1 = (AuthPermission) mongoRepositoryReactive.findById("1", AuthPermission.class).block();
        if (permission1 == null) {
            permission1 = new AuthPermission();
            permission1.setId("1");
        }
        permission1.setName("CREATE USER PROFILE");
        permission1.setDescription("Create user and update users email, phone, role etc");

        AuthPermission permission2 = (AuthPermission) mongoRepositoryReactive.findById("2", AuthPermission.class).block();
        if (permission2 == null) {
            permission2 = new AuthPermission();
            permission2.setId("2");
        }
        permission2.setName("CHANGE USER STATUS");
        permission2.setDescription("Change user from enabled to disabled and the other way round");

        AuthPermission permission3 = (AuthPermission) mongoRepositoryReactive.findById("3", AuthPermission.class).block();
        if (permission3 == null) {
            permission3 = new AuthPermission();
            permission3.setId("3");
        }
        permission3.setName("CREATE AGENT");
        permission3.setDescription("Creates an agent or add self to agent");

        AuthPermission permission4 = (AuthPermission) mongoRepositoryReactive.findById("4", AuthPermission.class).block();
        if (permission4 == null) {
            permission4 = new AuthPermission();
            permission4.setId("4");
        }
        permission4.setName("APPROVE AGENT APPROVAL REQUEST");
        permission4.setDescription("Approve and reject agent approval request");

        AuthPermission permission5 = (AuthPermission) mongoRepositoryReactive.findById("5", AuthPermission.class).block();
        if (permission5 == null) {
            permission5 = new AuthPermission();
            permission5.setId("5");
        }
        permission5.setName("SCHEDULE PRESENTATION WITH APPLICANT");
        permission5.setDescription("Create a scheduled presentation with an applicant , view and reschedule a presentation with the applicant");


        AuthPermission permission6 = (AuthPermission) mongoRepositoryReactive.findById("6", AuthPermission.class).block();
        if (permission6 == null) {
            permission6 = new AuthPermission();
            permission6.setId("6");
        }
        permission6.setName("VIEW APPLICATION FORMS");
        permission6.setDescription("View all application forms on the system");

        AuthPermission permission7 = (AuthPermission) mongoRepositoryReactive.findById("7", AuthPermission.class).block();
        if (permission7 == null) {
            permission7 = new AuthPermission();
            permission7.setId("7");
        }
        permission7.setName("APPROVE APPLICATION");
        permission7.setDescription("Approve and reject application forms");


        AuthPermission permission8 = (AuthPermission) mongoRepositoryReactive.findById("8", AuthPermission.class).block();
        if (permission8 == null) {
            permission8 = new AuthPermission();
            permission8.setId("8");
        }
        permission8.setName("VIEW AIPs");
        permission8.setDescription("View AIP required documents and upload");

        AuthPermission permission9 = (AuthPermission) mongoRepositoryReactive.findById("9", AuthPermission.class).block();
        if (permission9 == null) {
            permission9 = new AuthPermission();
            permission9.setId("9");
        }
        permission9.setName("APPROVE AIPs");
        permission9.setDescription("Approve AIPs on the system");

        AuthPermission permission10 = (AuthPermission) mongoRepositoryReactive.findById("10", AuthPermission.class).block();
        if (permission10 == null) {
            permission10 = new AuthPermission();
            permission10.setId("10");
        }
        permission10.setName("VIEW GAMING CATEGORIES");
        permission10.setDescription("View all game types on the system  and their details");

        AuthPermission permission11 = (AuthPermission) mongoRepositoryReactive.findById("11", AuthPermission.class).block();
        if (permission11 == null) {
            permission11 = new AuthPermission();
            permission11.setId("11");
        }
        permission11.setName("CREATE GAMING CATEGORIES");
        permission11.setDescription("Create and update properties of categories");

        AuthPermission permission12 = (AuthPermission) mongoRepositoryReactive.findById("12", AuthPermission.class).block();
        if (permission12 == null) {
            permission12 = new AuthPermission();
            permission12.setId("12");
        }
        permission12.setName("VIEW FEES");
        permission12.setDescription("View fees configured on system");

        AuthPermission permission13 = (AuthPermission) mongoRepositoryReactive.findById("13", AuthPermission.class).block();
        if (permission13 == null) {
            permission13 = new AuthPermission();
            permission13.setId("13");
        }
        permission13.setName("CREATE FEES");
        permission13.setDescription("Create and update fees configured on system");


        AuthPermission permission14 = (AuthPermission) mongoRepositoryReactive.findById("14", AuthPermission.class).block();
        if (permission14 == null) {
            permission14 = new AuthPermission();
            permission14.setId("14");
        }
        permission14.setName("VIEW REVENUE LINES");
        permission14.setDescription("View revenue lines configured on system");


        AuthPermission permission15 = (AuthPermission) mongoRepositoryReactive.findById("15", AuthPermission.class).block();
        if (permission15 == null) {
            permission15 = new AuthPermission();
            permission15.setId("15");
        }
        permission15.setName("CREATE REVENUE LINES");
        permission15.setDescription("Create and update Revenue lines configured on system");

        AuthPermission permission16 = (AuthPermission) mongoRepositoryReactive.findById("16", AuthPermission.class).block();
        if (permission16 == null) {
            permission16 = new AuthPermission();
            permission16.setId("16");
        }
        permission16.setName("VIEW PAYMENT RECORDS");
        permission16.setDescription("View payment records on system based on property of user");

        AuthPermission permission17 = (AuthPermission) mongoRepositoryReactive.findById("17", AuthPermission.class).block();
        if (permission17 == null) {
            permission17 = new AuthPermission();
            permission17.setId("17");
        }
        permission17.setName("MAKE NEW PAYMENT");
        permission17.setDescription("Make a new payment on the system");

        AuthPermission permission18 = (AuthPermission) mongoRepositoryReactive.findById("18", AuthPermission.class).block();
        if (permission18 == null) {
            permission18 = new AuthPermission();
            permission18.setId("18");
        }
        permission18.setName("VIEW DASHBOARD");
        permission18.setDescription("View system dashboard based on property of user logged in");

        AuthPermission permission19 = (AuthPermission) mongoRepositoryReactive.findById("19", AuthPermission.class).block();
        if (permission19 == null) {
            permission19 = new AuthPermission();
            permission19.setId("19");
        }
        permission19.setName("VIEW DOCUMENT TYPES");
        permission19.setDescription("View document types configured on system");

        AuthPermission permission20 = (AuthPermission) mongoRepositoryReactive.findById("20", AuthPermission.class).block();
        if (permission20 == null) {
            permission20 = new AuthPermission();
            permission20.setId("20");
        }
        permission20.setName("CREATE DOCUMENT TYPES");
        permission20.setDescription("Create and update document types configured on system");

        AuthPermission permission21 = (AuthPermission) mongoRepositoryReactive.findById("21", AuthPermission.class).block();
        if (permission21 == null) {
            permission21 = new AuthPermission();
            permission21.setId("21");
        }
        permission21.setName("VIEW DOCUMENT PURPOSES");
        permission21.setDescription("View Document purposes");

        AuthPermission permission22 = (AuthPermission) mongoRepositoryReactive.findById("22", AuthPermission.class).block();
        if (permission22 == null) {
            permission22 = new AuthPermission();
            permission22.setId("22");
        }
        permission22.setName("CREATE DOCUMENT PURPOSES");
        permission22.setDescription("Create and update Document purposes");

        AuthPermission permission23 = (AuthPermission) mongoRepositoryReactive.findById("23", AuthPermission.class).block();
        if (permission23 == null) {
            permission23 = new AuthPermission();
            permission23.setId("23");
        }
        permission23.setName("VIEW DOCUMENTS");
        permission23.setDescription("View documents on system");

        AuthPermission permission24 = (AuthPermission) mongoRepositoryReactive.findById("24", AuthPermission.class).block();
        if (permission24 == null) {
            permission24 = new AuthPermission();
            permission24.setId("24");
        }
        permission24.setName("VIEW LICENCES");
        permission24.setDescription("View Licences on system based on user logged in params");

        AuthPermission permission25 = (AuthPermission) mongoRepositoryReactive.findById("25", AuthPermission.class).block();
        if (permission25 == null) {
            permission25 = new AuthPermission();
            permission25.setId("25");
        }
        permission25.setName("VIEW LOGGED REPORTS");
        permission25.setDescription("View Logged reports from inspection and audit");

        AuthPermission permission26 = (AuthPermission) mongoRepositoryReactive.findById("26", AuthPermission.class).block();
        if (permission26 == null) {
            permission26 = new AuthPermission();
            permission26.setId("26");
        }
        permission26.setName("UPLOAD REPORT");
        permission26.setDescription("View Licences on system based on user logged in params");

        AuthPermission permission27 = (AuthPermission) mongoRepositoryReactive.findById("27", AuthPermission.class).block();
        if (permission27 == null) {
            permission27 = new AuthPermission();
            permission27.setId("27");
        }
        permission27.setName("VIEW LOGGED CASES");
        permission27.setDescription("View Logged cases based on user logged in");

        AuthPermission permission28 = (AuthPermission) mongoRepositoryReactive.findById("28", AuthPermission.class).block();
        if (permission28 == null) {
            permission28 = new AuthPermission();
            permission28.setId("28");
        }
        permission28.setName("CREATE LOGGED CASE");
        permission28.setDescription("Create logged case and add more details to logged case");

        AuthPermission permission29 = (AuthPermission) mongoRepositoryReactive.findById("29", AuthPermission.class).block();
        if (permission29 == null) {
            permission29 = new AuthPermission();
            permission29.setId("29");
        }
        permission29.setName("UPDATE LOGGED CASE");
        permission29.setDescription("Update logged case status");

        AuthPermission permission30 = (AuthPermission) mongoRepositoryReactive.findById("30", AuthPermission.class).block();
        if (permission30 == null) {
            permission30 = new AuthPermission();
            permission30.setId("30");
        }
        permission30.setName("VIEW AUDIT TRAILS");
        permission30.setDescription("View Audit Trails based on institution logged in");

        AuthPermission permission31 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_APPLICATION_ID, AuthPermission.class).block();
        if (permission31 == null) {
            permission31 = new AuthPermission();
            permission31.setId(RECEIVE_APPLICATION_ID);

        }
        permission31.setDescription("Can receive application form submissions notifications");
        permission31.setName("CAN RECEIVE APPLICATION SUBMISSION NOTIFICATION");
        permission31.setUsedBySystem(true);

        AuthPermission permission32 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_PAYMENT_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission32 == null) {
            permission32 = new AuthPermission();
            permission32.setId(RECEIVE_PAYMENT_NOTIFICATION_ID);
        }

        permission32.setDescription("Can receive payment notification");
        permission32.setName("RECEIVE PAYMENT NOTIFICATION");
        permission32.setUsedBySystem(true);

        AuthPermission permission33 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_CUSTOMER_COMPLAIN_ID, AuthPermission.class).block();
        if (permission33 == null) {
            permission33 = new AuthPermission();
            permission33.setId(RECEIVE_CUSTOMER_COMPLAIN_ID);
        }
        permission33.setDescription("Can receive customer complains  notifications (new and reminder)");
        permission33.setName("RECEIVE CUSTOMER COMPLAIN NOTIFICATION");
        permission33.setUsedBySystem(true);

        AuthPermission permission34 = (AuthPermission) mongoRepositoryReactive.findById("34", AuthPermission.class).block();
        if (permission34 == null) {
            permission34 = new AuthPermission();
            permission34.setId("34");
        }
        permission34.setName("CREATE APPLICATION FORMS");
        permission34.setDescription("Create an application form on the system");

        AuthPermission permission35 = (AuthPermission) mongoRepositoryReactive.findById("35", AuthPermission.class).block();
        if (permission35 == null) {
            permission35 = new AuthPermission();
            permission35.setId("35");
        }
        permission35.setName("VIEW AGENT APPROVAL REQUEST");
        permission35.setDescription("View all agent approval requests");


        AuthPermission permission36 = (AuthPermission) mongoRepositoryReactive.findById("36", AuthPermission.class).block();
        if (permission36 == null) {
            permission36 = new AuthPermission();
            permission36.setId("36");
        }
        permission36.setName("VIEW USER");
        permission36.setDescription("View users on platform based on user logged in");

        AuthPermission permission37 = (AuthPermission) mongoRepositoryReactive.findById("37", AuthPermission.class).block();
        if (permission37 == null) {
            permission37 = new AuthPermission();
            permission37.setId("37");
        }
        permission37.setName("VIEW AGENTS");
        permission37.setDescription("View all agents on platform");

        AuthPermission permission38 = (AuthPermission) mongoRepositoryReactive.findById("38", AuthPermission.class).block();
        if (permission38 == null) {
            permission38 = new AuthPermission();
            permission38.setId("38");
        }
        permission38.setName("VIEW SCHEDULED PRESENTATIONS");
        permission38.setDescription("View all scheduled presentations with applicant");

        AuthPermission permission39 = (AuthPermission) mongoRepositoryReactive.findById("39", AuthPermission.class).block();
        if (permission39 == null) {
            permission39 = new AuthPermission();
            permission39.setId("39");
        }
        permission39.setName("CREATE INSTITUTIONS");
        permission39.setDescription("Can Create an institution on the system");

        AuthPermission permission40 = (AuthPermission) mongoRepositoryReactive.findById("40", AuthPermission.class).block();
        if (permission40 == null) {
            permission40 = new AuthPermission();
            permission40.setId("40");
        }
        permission40.setName("VIEW INSTITUTIONS");
        permission40.setDescription("Can View institutions on the system");

        AuthPermission permission41 = (AuthPermission) mongoRepositoryReactive.findById("41", AuthPermission.class).block();
        if (permission41 == null) {
            permission41 = new AuthPermission();
            permission41.setId("41");
        }
        permission41.setName("CREATE GAMING MACHINE");
        permission41.setDescription("Create gaming machine");

        AuthPermission permission42 = (AuthPermission) mongoRepositoryReactive.findById("42", AuthPermission.class).block();
        if (permission42 == null) {
            permission42 = new AuthPermission();
            permission42.setId("42");
        }
        permission42.setName("VIEW GAMING MACHINES");
        permission42.setDescription("View gaming machine");

        AuthPermission permission43 = (AuthPermission) mongoRepositoryReactive.findById("43", AuthPermission.class).block();
        if (permission43 == null) {
            permission43 = new AuthPermission();
            permission43.setId("43");
        }
        permission43.setName("ADD PERMISSION_TO USER");
        permission43.setDescription("Add permission to user ");

        AuthPermission permission44 = (AuthPermission) mongoRepositoryReactive.findById("44", AuthPermission.class).block();
        if (permission44 == null) {
            permission44 = new AuthPermission();
            permission44.setId("44");
        }
        permission44.setName("START LICENCE RENEWAL");
        permission44.setDescription("Create and update licence renewals");

        AuthPermission permission45 = (AuthPermission) mongoRepositoryReactive.findById("45", AuthPermission.class).block();
        if (permission45 == null) {
            permission45 = new AuthPermission();
            permission45.setId("45");
        }
        permission45.setName("VIEW LICENCE RENEWALS");
        permission45.setDescription("Add permission to user ");

        AuthPermission permission46 = (AuthPermission) mongoRepositoryReactive.findById("46", AuthPermission.class).block();
        if (permission46 == null) {
            permission46 = new AuthPermission();
            permission46.setId("46");
        }
        permission46.setName("APPROVE LICENCE RENEWALS");
        permission46.setDescription("Approve licence renewals");

        AuthPermission permission47 = (AuthPermission) mongoRepositoryReactive.findById("47", AuthPermission.class).block();
        if (permission47 == null) {
            permission47 = new AuthPermission();
            permission47.setId("47");
        }
        permission47.setName("RECEIVE NEW CASE NOTIFICATION");
        permission47.setDescription("Receive new case Notification");

        AuthPermission permission48 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID, AuthPermission.class).block();
        if (permission48 == null) {
            permission48 = new AuthPermission();
            permission48.setId(RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID);
        }
        permission48.setName("RECEIVE NEW AGENT APPROVAL REQUEST NOTIFICATION");
        permission48.setDescription("Receive new agent approval request Notification");
        permission48.setUsedBySystem(true);

        AuthPermission permission49 = (AuthPermission) mongoRepositoryReactive.findById("49", AuthPermission.class).block();
        if (permission49 == null) {
            permission49 = new AuthPermission();
            permission49.setId("49");
        }
        permission49.setName("VIEW ROLES");
        permission49.setDescription("View roles on system");

        AuthPermission permission50 = (AuthPermission) mongoRepositoryReactive.findById("50", AuthPermission.class).block();
        if (permission50 == null) {
            permission50 = new AuthPermission();
            permission50.setId("50");
        }
        permission50.setName("ADD PERMISSIONS TO ROLE");
        permission50.setDescription("Can add permissions to roles and update roles");

        AuthPermission permission51 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_CASE_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission51 == null) {
            permission51 = new AuthPermission();
            permission51.setId(RECEIVE_CASE_NOTIFICATION_ID);
        }
        permission51.setName("RECEIVE NEW CASE NOTIFICATION");
        permission51.setDescription("Can receive case notification (new and reminder)");
        permission51.setUsedBySystem(true);

        mongoRepositoryReactive.saveOrUpdate(permission1);
        mongoRepositoryReactive.saveOrUpdate(permission2);
        mongoRepositoryReactive.saveOrUpdate(permission3);
        mongoRepositoryReactive.saveOrUpdate(permission4);
        mongoRepositoryReactive.saveOrUpdate(permission5);
        mongoRepositoryReactive.saveOrUpdate(permission6);
        mongoRepositoryReactive.saveOrUpdate(permission7);
        mongoRepositoryReactive.saveOrUpdate(permission8);
        mongoRepositoryReactive.saveOrUpdate(permission9);
        mongoRepositoryReactive.saveOrUpdate(permission10);
        mongoRepositoryReactive.saveOrUpdate(permission11);
        mongoRepositoryReactive.saveOrUpdate(permission12);
        mongoRepositoryReactive.saveOrUpdate(permission13);
        mongoRepositoryReactive.saveOrUpdate(permission14);
        mongoRepositoryReactive.saveOrUpdate(permission15);
        mongoRepositoryReactive.saveOrUpdate(permission16);
        mongoRepositoryReactive.saveOrUpdate(permission17);
        mongoRepositoryReactive.saveOrUpdate(permission18);
        mongoRepositoryReactive.saveOrUpdate(permission19);
        mongoRepositoryReactive.saveOrUpdate(permission20);
        mongoRepositoryReactive.saveOrUpdate(permission21);
        mongoRepositoryReactive.saveOrUpdate(permission22);
        mongoRepositoryReactive.saveOrUpdate(permission23);
        mongoRepositoryReactive.saveOrUpdate(permission24);
        mongoRepositoryReactive.saveOrUpdate(permission25);
        mongoRepositoryReactive.saveOrUpdate(permission26);
        mongoRepositoryReactive.saveOrUpdate(permission27);
        mongoRepositoryReactive.saveOrUpdate(permission28);
        mongoRepositoryReactive.saveOrUpdate(permission29);
        mongoRepositoryReactive.saveOrUpdate(permission30);
        mongoRepositoryReactive.saveOrUpdate(permission31);
        mongoRepositoryReactive.saveOrUpdate(permission32);
        mongoRepositoryReactive.saveOrUpdate(permission33);
        mongoRepositoryReactive.saveOrUpdate(permission34);
        mongoRepositoryReactive.saveOrUpdate(permission35);
        mongoRepositoryReactive.saveOrUpdate(permission36);
        mongoRepositoryReactive.saveOrUpdate(permission37);
        mongoRepositoryReactive.saveOrUpdate(permission38);
        mongoRepositoryReactive.saveOrUpdate(permission39);
        mongoRepositoryReactive.saveOrUpdate(permission40);
        mongoRepositoryReactive.saveOrUpdate(permission41);
        mongoRepositoryReactive.saveOrUpdate(permission42);
        mongoRepositoryReactive.saveOrUpdate(permission43);
        mongoRepositoryReactive.saveOrUpdate(permission44);
        mongoRepositoryReactive.saveOrUpdate(permission45);
        mongoRepositoryReactive.saveOrUpdate(permission46);
        mongoRepositoryReactive.saveOrUpdate(permission47);
        mongoRepositoryReactive.saveOrUpdate(permission48);
        mongoRepositoryReactive.saveOrUpdate(permission49);
        mongoRepositoryReactive.saveOrUpdate(permission50);
        mongoRepositoryReactive.saveOrUpdate(permission51);
    }


    public static Set<String> getLSLBAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("1");
        permissions.add("2");
        permissions.add("4");
        permissions.add("5");
        permissions.add("6");
        permissions.add("7");
        permissions.add("8");
        permissions.add("9");
        permissions.add("10");
        permissions.add("11");
        permissions.add("12");
        permissions.add("13");
        permissions.add("14");
        permissions.add("15");
        permissions.add("16");
        permissions.add("18");
        permissions.add("19");
        permissions.add("20");
        permissions.add("21");
        permissions.add("22");
        permissions.add("23");
        permissions.add("24");
        permissions.add("25");
        permissions.add("26");
        permissions.add("27");
        permissions.add("28");
        permissions.add("29");
        permissions.add("30");
        permissions.add("35");
        permissions.add("36");
        permissions.add("37");
        permissions.add("38");
        permissions.add("40");
        permissions.add("42");
        permissions.add("43");
        permissions.add("45");
        permissions.add("46");
        return permissions;
    }

    public static Set<String> getLSLBUserPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("6");
        permissions.add("8");
        permissions.add("10");
        permissions.add("12");
        permissions.add("14");
        permissions.add("16");
        permissions.add("18");
        permissions.add("19");
        permissions.add("21");
        permissions.add("23");
        permissions.add("24");
        permissions.add("25");
        permissions.add("26");
        permissions.add("27");
        permissions.add("28");
        permissions.add("30");
        permissions.add("35");
        permissions.add("36");
        permissions.add("37");
        permissions.add("38");
        permissions.add("40");
        permissions.add("42");
        permissions.add("45");
        return permissions;
    }

    public static Set<String> getGamingOperatorAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("3");
        permissions.add("6");
        permissions.add("8");
        permissions.add("10");
        permissions.add("12");
        permissions.add("14");
        permissions.add("16");
        permissions.add("17");
        permissions.add("18");
        permissions.add("19");
        permissions.add("21");
        permissions.add("23");
        permissions.add("24");
        permissions.add("30");
        permissions.add("34");
        permissions.add("36");
        permissions.add("37");
        permissions.add("38");
        permissions.add("40");
        permissions.add("42");
        permissions.add("41");
        permissions.add("44");
        permissions.add("45");
        return permissions;
    }

    public static Set<String> getGamingOperatorUserPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("6");
        permissions.add("8");
        permissions.add("10");
        permissions.add("12");
        permissions.add("14");
        permissions.add("16");
        permissions.add("18");
        permissions.add("19");
        permissions.add("21");
        permissions.add("23");
        permissions.add("24");
        permissions.add("30");
        permissions.add("34");
        permissions.add("36");
        permissions.add("37");
        permissions.add("38");
        permissions.add("40");
        permissions.add("42");
        permissions.add("45");
        return permissions;
    }

    public static Set<String> getAllAgentPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("10");
        permissions.add("12");
        permissions.add("14");
        permissions.add("16");
        permissions.add("17");
        permissions.add("24");
        permissions.add("36");
        permissions.add("37");
        return permissions;
    }

    public static Set<String> getAllVGGSuperAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        List<String> codeGeneratedPermissionIds = getCodeUsedPermissions();
        for (int i = 1; i <= 51; i++) {
            if (!codeGeneratedPermissionIds.contains(String.valueOf(i))) {
                permissions.add(String.valueOf(i));
            }
        }
        return permissions;
    }

    public static Set<String> getAllVGGAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("1");
        permissions.add("2");
        permissions.add("6");
        permissions.add("8");
        permissions.add("10");
        permissions.add("12");
        permissions.add("14");
        permissions.add("16");
        permissions.add("18");
        permissions.add("19");
        permissions.add("21");
        permissions.add("23");
        permissions.add("24");
        permissions.add("25");
        permissions.add("27");
        permissions.add("30");
        permissions.add("35");
        permissions.add("36");
        permissions.add("37");
        permissions.add("38");
        permissions.add("40");
        permissions.add("42");
        permissions.add("43");
        permissions.add("45");
        permissions.add("49");
        permissions.add("50");
        return permissions;
    }

    public static Set<String> getAllVGGUserPermissions() {
        return getAllVGGAdminPermissions();
    }

    public static Set<String> getApplicantPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("39");
        return permissions;
    }

    public static List<String> getCodeUsedPermissions() {
        List<String> codePermissions = new ArrayList<>();
        codePermissions.add(RECEIVE_APPLICATION_ID);
        codePermissions.add(RECEIVE_CUSTOMER_COMPLAIN_ID);
        codePermissions.add(RECEIVE_PAYMENT_NOTIFICATION_ID);
        codePermissions.add(RECEIVE_AGENT_APPROVAL_AGENT_REQUEST_ID);
        codePermissions.add(RECEIVE_CASE_NOTIFICATION_ID);
        return codePermissions;
    }
}
