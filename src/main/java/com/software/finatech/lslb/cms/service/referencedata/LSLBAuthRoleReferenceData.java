package com.software.finatech.lslb.cms.userservice.referencedata;

import com.software.finatech.lslb.cms.userservice.domain.AuthRole;
import com.software.finatech.lslb.cms.userservice.persistence.MongoRepositoryReactiveImpl;

import java.util.Arrays;

public class LSLBAuthRoleReferenceData {
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        AuthRole role4 = (AuthRole) mongoRepositoryReactive.findById("4", AuthRole.class).block();
        if (role4 == null) {
            role4 = new AuthRole();
            role4.setId("4");
        }
        role4.setTenantId("01");
        role4.setDescription("LSLB_ADMIN");
        role4.setName("LSLB_ADMIN");
        role4.setSsoRoleMapping("clientadmin");
        role4.getAuthPermissionIds().addAll(Arrays.asList("13", "14", "15", "16", "17"));

        AuthRole role5 = (AuthRole) mongoRepositoryReactive.findById("5", AuthRole.class).block();
        if (role5 == null) {
            role5 = new AuthRole();
            role5.setId("5");
        }
        role5.setTenantId("01");
        role5.setDescription("LSLB_USER");
        role5.setName("LSLB_USER");
        role5.setSsoRoleMapping("clientuser");
        role5.getAuthPermissionIds().addAll(Arrays.asList("13", "14"));



        AuthRole role6 = (AuthRole) mongoRepositoryReactive.findById("6", AuthRole.class).block();
        if (role6 == null) {
            role6 = new AuthRole();
            role6.setId("6");
        }
        role6.setDescription("GAMING_OPERATOR");
        role6.setName("GAMING_OPERATOR");
        role6.setSsoRoleMapping("clientuser");
        role6.getAuthPermissionIds().addAll(Arrays.asList("1", "2"));

        AuthRole role7 = (AuthRole) mongoRepositoryReactive.findById("7", AuthRole.class).block();
        if (role7 == null) {
            role7 = new AuthRole();
            role7.setId("7");
        }
        role7.setDescription("AGENT");
        role7.setName("AGENT");
        role7.setSsoRoleMapping("clientuser");
        role7.getAuthPermissionIds().addAll(Arrays.asList("1", "2"));


        mongoRepositoryReactive.saveOrUpdate(role4);
        mongoRepositoryReactive.saveOrUpdate(role5);
        mongoRepositoryReactive.saveOrUpdate(role6);
        mongoRepositoryReactive.saveOrUpdate(role7);

    }
}
