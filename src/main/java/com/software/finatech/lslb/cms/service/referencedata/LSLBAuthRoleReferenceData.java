package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.Arrays;
import java.util.Set;

public class LSLBAuthRoleReferenceData {
    public static final String LSLB_ADMIN_ID = "4";
    public static final String LSLB_USER_ID = "5";
    public static final String GAMING_OPERATOR_ADMIN_ROLE_ID = "6";
    public static final String GAMING_OPERATOR_USER_ROLE_ID = "7";
    public static final String AGENT_ROLE_ID = "8";
    public static final String APPLICANT_ROLE_ID = "9";

    public static final String SSO_CLIENT_ADMIN = "clientadmin";
    private static final String SSO_CLIENT_USER = "clientuser";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        AuthRole role4 = (AuthRole) mongoRepositoryReactive.findById(LSLB_ADMIN_ID, AuthRole.class).block();
        if (role4 == null) {
            role4 = new AuthRole();
            role4.setId(LSLB_ADMIN_ID);
        }
        role4.setDescription("LSLB Admin");
        role4.setName("LSLB ADMIN");
        role4.setSsoRoleMapping(SSO_CLIENT_ADMIN);
        Set<String> permissionIds = LSLBAuthPermissionReferenceData.getLSLBAdminPermissions();
        role4.setAuthPermissionIds(permissionIds);

        AuthRole role5 = (AuthRole) mongoRepositoryReactive.findById(LSLB_USER_ID, AuthRole.class).block();
        if (role5 == null) {
            role5 = new AuthRole();
            role5.setId(LSLB_USER_ID);
        }
        role5.setDescription("LSLB user");
        role5.setName("LSLB USER");
        role5.setSsoRoleMapping(SSO_CLIENT_USER);
        permissionIds = LSLBAuthPermissionReferenceData.getLSLBUserPermissions();
        role5.setAuthPermissionIds(permissionIds);


        AuthRole role6 = (AuthRole) mongoRepositoryReactive.findById(GAMING_OPERATOR_ADMIN_ROLE_ID, AuthRole.class).block();
        if (role6 == null) {
            role6 = new AuthRole();
            role6.setId(GAMING_OPERATOR_ADMIN_ROLE_ID);
        }
        role6.setDescription("GAMING OPERATOR ADMIN (Is in charge of the registration of the gaming operator)");
        role6.setName("GAMING OPERATOR ADMIN");
        role6.setSsoRoleMapping(SSO_CLIENT_USER);
        permissionIds = LSLBAuthPermissionReferenceData.getGamingOperatorAdminPermissions();
        role6.setAuthPermissionIds(permissionIds);


        AuthRole role7 = (AuthRole) mongoRepositoryReactive.findById(GAMING_OPERATOR_USER_ROLE_ID, AuthRole.class).block();
        if (role7 == null) {
            role7 = new AuthRole();
            role7.setId(GAMING_OPERATOR_USER_ROLE_ID);
        }
        role7.setDescription("GAMING OPERATOR USER(Is in charge of managing gaming operator agents)");
        role7.setName("GAMING OPERATOR USER");
        role7.setSsoRoleMapping(SSO_CLIENT_USER);
        permissionIds = LSLBAuthPermissionReferenceData.getGamingOperatorUserPermissions();
        role7.setAuthPermissionIds(permissionIds);

        AuthRole role8 = (AuthRole) mongoRepositoryReactive.findById(AGENT_ROLE_ID, AuthRole.class).block();
        if (role8 == null) {
            role8 = new AuthRole();
            role8.setId(AGENT_ROLE_ID);
        }
        role8.setDescription("AGENT");
        role8.setName("AGENT");
        role8.setSsoRoleMapping(SSO_CLIENT_USER);
        permissionIds = LSLBAuthPermissionReferenceData.getAllAgentPermissions();
        role8.setAuthPermissionIds(permissionIds);


        AuthRole role9 = (AuthRole) mongoRepositoryReactive.findById(APPLICANT_ROLE_ID, AuthRole.class).block();
        if (role9 == null) {
            role9 = new AuthRole();
            role9.setId(APPLICANT_ROLE_ID);
        }
        role9.setDescription("Applicant user for gaming operators");
        role9.setName("APPLICANT");
        role9.setSsoRoleMapping(SSO_CLIENT_USER);
        permissionIds = LSLBAuthPermissionReferenceData.getApplicantPermissions();
        role9.setAuthPermissionIds(permissionIds);



        mongoRepositoryReactive.saveOrUpdate(role4);
        mongoRepositoryReactive.saveOrUpdate(role5);
        mongoRepositoryReactive.saveOrUpdate(role6);
        mongoRepositoryReactive.saveOrUpdate(role7);
        mongoRepositoryReactive.saveOrUpdate(role8);
        mongoRepositoryReactive.saveOrUpdate(role9);
    }
}

