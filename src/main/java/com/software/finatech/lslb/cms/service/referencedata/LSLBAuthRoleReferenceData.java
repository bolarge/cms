package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.Arrays;

public class LSLBAuthRoleReferenceData {
    public static final String LSLB_GM_ROLE_ID = "4";
    public static final String LSLB_LEGAL_ADMIN_ID = "5";
    public static final String LSLB_LEGAL_USER_ID = "6";
    public static final String LSLB_FINANCE_ADMIN_ID = "7";
    public static final String LSLB_FINANCE_USER_ID = "8";
    public static final String LSLB_IT_ADMIN_ID = "9";
    public static final String LSLB_IT_USER_ID = "10";
    public static final String GAMING_OPERATOR_ADMIN_ROLE_ID = "11";
    public static final String GAMING_OPERATOR_USER_ROLE_ID = "12";
    public static final String AGENT_ROLE_ID = "13";
    public static final String APPLICANT_ROLE_ID = "14";

    private static final String SSO_CLIENT_ADMIN = "clientadmin";
    private static final String SSO_CLIENT_USER = "clientuser";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        AuthRole role4 = (AuthRole) mongoRepositoryReactive.findById(LSLB_GM_ROLE_ID, AuthRole.class).block();
        if (role4 == null) {
            role4 = new AuthRole();
            role4.setId(LSLB_GM_ROLE_ID);
        }
        role4.setDescription("LSLB General Manager");
        role4.setName("LSLB General Manager");
        role4.setSsoRoleMapping(SSO_CLIENT_ADMIN);
        role4.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));

        AuthRole role5 = (AuthRole) mongoRepositoryReactive.findById(LSLB_LEGAL_ADMIN_ID, AuthRole.class).block();
        if (role5 == null) {
            role5 = new AuthRole();
            role5.setId(LSLB_LEGAL_ADMIN_ID);
        }
        role5.setDescription("LSLB Legal department admin");
        role5.setName("LSLB LEGAL DEPARTMENT ADMIN");
        role5.setSsoRoleMapping(SSO_CLIENT_ADMIN);
        role5.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));


        AuthRole role6 = (AuthRole) mongoRepositoryReactive.findById(LSLB_LEGAL_USER_ID, AuthRole.class).block();
        if (role6 == null) {
            role6 = new AuthRole();
            role6.setId(LSLB_LEGAL_USER_ID);
        }

        role6.setDescription("LSLB Legal department user");
        role6.setName("LSLB LEGAL DEPARTMENT USER");
        role6.setSsoRoleMapping(SSO_CLIENT_USER);
        role6.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));


        AuthRole role7 = (AuthRole) mongoRepositoryReactive.findById(LSLB_FINANCE_ADMIN_ID, AuthRole.class).block();
        if (role7 == null) {
            role7 = new AuthRole();
            role7.setId(LSLB_FINANCE_ADMIN_ID);
        }
        role7.setDescription("LSLB Finance department admin");
        role7.setName("LSLB FINANCE DEPARTMENT ADMIN");
        role7.setSsoRoleMapping(SSO_CLIENT_ADMIN);
        role7.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));


        AuthRole role8 = (AuthRole) mongoRepositoryReactive.findById(LSLB_FINANCE_USER_ID, AuthRole.class).block();
        if (role8 == null) {
            role8 = new AuthRole();
            role8.setId(LSLB_FINANCE_USER_ID);
        }
        role8.setDescription("LSLB Finance department user");
        role8.setName("LSLB FINANCE DEPARTMENT USER");
        role8.setSsoRoleMapping(SSO_CLIENT_USER);
        role8.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));


        AuthRole role9 = (AuthRole) mongoRepositoryReactive.findById(LSLB_IT_ADMIN_ID, AuthRole.class).block();
        if (role9 == null) {
            role9 = new AuthRole();
            role9.setId(LSLB_IT_ADMIN_ID);
        }
        role9.setDescription("LSLB Information Technology Department Admin");
        role9.setName("LSLB IT DEPARTMENT ADMIN");
        role9.setSsoRoleMapping(SSO_CLIENT_ADMIN);
        role9.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));

        AuthRole role10 = (AuthRole) mongoRepositoryReactive.findById(LSLB_IT_USER_ID, AuthRole.class).block();
        if (role10 == null) {
            role10 = new AuthRole();
            role10.setId(LSLB_IT_USER_ID);
        }
        role10.setDescription("LSLB Information Technology User");
        role10.setName("LSLB IT USER");
        role10.setSsoRoleMapping(SSO_CLIENT_USER);
        role10.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));

        AuthRole role11 = (AuthRole) mongoRepositoryReactive.findById(GAMING_OPERATOR_ADMIN_ROLE_ID, AuthRole.class).block();
        if (role11 == null) {
            role11 = new AuthRole();
            role11.setId(GAMING_OPERATOR_ADMIN_ROLE_ID);
        }
        role11.setDescription("GAMING OPERATOR ADMIN (Is in charge of the registration of the gaming operator)");
        role11.setName("GAMING OPERATOR ADMIN");
        role11.setSsoRoleMapping(SSO_CLIENT_USER);
        role11.getAuthPermissionIds().addAll(Arrays.asList("1", "2"));


        AuthRole role12 = (AuthRole) mongoRepositoryReactive.findById(GAMING_OPERATOR_USER_ROLE_ID, AuthRole.class).block();
        if (role12 == null) {
            role12 = new AuthRole();
            role12.setId(GAMING_OPERATOR_USER_ROLE_ID);
        }
        role12.setDescription("GAMING OPERATOR USER(Is in charge of managing gaming operator agents)");
        role12.setName("GAMING OPERATOR USER");
        role12.setSsoRoleMapping(SSO_CLIENT_USER);
        role12.getAuthPermissionIds().addAll(Arrays.asList("1", "2"));

        AuthRole role13 = (AuthRole) mongoRepositoryReactive.findById(AGENT_ROLE_ID, AuthRole.class).block();
        if (role13 == null) {
            role13 = new AuthRole();
            role13.setId(AGENT_ROLE_ID);
        }
        role13.setDescription("AGENT");
        role13.setName("AGENT");
        role13.setSsoRoleMapping(SSO_CLIENT_USER);
        role13.getAuthPermissionIds().addAll(Arrays.asList("1", "2"));


        AuthRole role14 = (AuthRole) mongoRepositoryReactive.findById(APPLICANT_ROLE_ID, AuthRole.class).block();
        if (role14 == null) {
            role14 = new AuthRole();
            role14.setId(APPLICANT_ROLE_ID);
        }
        role14.setDescription("Applicant user for gaming operators");
        role14.setName("APPLICANT");
        role14.setSsoRoleMapping(SSO_CLIENT_USER);
        role14.getAuthPermissionIds().addAll(Arrays.asList("1", "2"));


        mongoRepositoryReactive.saveOrUpdate(role4);
        mongoRepositoryReactive.saveOrUpdate(role5);
        mongoRepositoryReactive.saveOrUpdate(role6);
        mongoRepositoryReactive.saveOrUpdate(role7);
        mongoRepositoryReactive.saveOrUpdate(role8);
        mongoRepositoryReactive.saveOrUpdate(role9);
        mongoRepositoryReactive.saveOrUpdate(role10);
        mongoRepositoryReactive.saveOrUpdate(role11);
        mongoRepositoryReactive.saveOrUpdate(role12);
        mongoRepositoryReactive.saveOrUpdate(role13);
        mongoRepositoryReactive.saveOrUpdate(role14);
    }
}

