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
    public static final String APPROVE_AGENT_REQUEST_ID = "4";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AuthPermission permission1 = (AuthPermission) mongoRepositoryReactive.findById("1", AuthPermission.class).block();
        if (permission1 == null) {
            permission1 = new AuthPermission();
            permission1.setId("1");
        }
        permission1.setName("UPDATE USER PROFILE");
        permission1.setDescription("Update a users email, phone, role etc");

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
        permission10.setName("VIEW CATEGORIES");
        permission10.setDescription("View all game types on the system  and their details");

        AuthPermission permission11 = (AuthPermission) mongoRepositoryReactive.findById("11", AuthPermission.class).block();
        if (permission11 == null) {
            permission11 = new AuthPermission();
            permission11.setId("11");
        }
        permission11.setName("UPDATE CATEGORIES");
        permission11.setDescription("Update properties of categories");

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
        permission13.setName("UPDATE FEES");
        permission13.setDescription("Update fees configured on system");


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
        permission15.setName("UPDATE REVENUE LINES");
        permission15.setDescription("Update Revenue lines configured on system");

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
        permission20.setName("UPDATE DOCUMENT TYPES");
        permission20.setDescription("Update document types configured on system");

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
        permission22.setName("UPDATE DOCUMENT PURPOSES");
        permission22.setDescription("Update Document purposes");

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

        AuthPermission permission32 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_PAYMENT_NOTIFICATION_ID, AuthPermission.class).block();
        if (permission32 == null) {
            permission32 = new AuthPermission();
            permission32.setId(RECEIVE_PAYMENT_NOTIFICATION_ID);
        }

        permission32.setDescription("Can receive payment notification");
        permission32.setName("RECEIVE PAYMENT NOTIFICATION");

        AuthPermission permission33 = (AuthPermission) mongoRepositoryReactive.findById(RECEIVE_CUSTOMER_COMPLAIN_ID, AuthPermission.class).block();
        if (permission33 == null) {
            permission33 = new AuthPermission();
            permission33.setId(RECEIVE_CUSTOMER_COMPLAIN_ID);
        }
        permission33.setDescription("Can receive customer complains  notifications (new and reminder)");
        permission33.setName("RECEIVE CUSTOMER COMPLAIN NOTIFICATION");

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
        return permissions;
    }

    public  static  Set<String> getAllAgentPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("10");
        permissions.add("12");
        permissions.add("14");
        permissions.add("16");
        permissions.add("17");
        permissions.add("24");
        return permissions;
    }

    public static Set<String> getAllVGGSuperAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        List<String> codeGeneratedPermissionIds = getCodeUsedPermissions();
        for (int i = 1; i <= 35; i++) {
            if (!codeGeneratedPermissionIds.contains(String.valueOf(i))) {
                permissions.add(String.valueOf(i));
            }
        }
        permissions.add("34");
        permissions.add("35");

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
        return permissions;
    }

    public static Set<String> getAllVGGUserPermissions() {
        return getAllVGGAdminPermissions();
    }

    private static List<String> getCodeUsedPermissions() {
        List<String> codePermissions = new ArrayList<>();
        codePermissions.add(RECEIVE_APPLICATION_ID);
        codePermissions.add(RECEIVE_CUSTOMER_COMPLAIN_ID);
        codePermissions.add(RECEIVE_PAYMENT_NOTIFICATION_ID);
        codePermissions.add(APPROVE_AGENT_REQUEST_ID);
        return codePermissions;
    }
}
