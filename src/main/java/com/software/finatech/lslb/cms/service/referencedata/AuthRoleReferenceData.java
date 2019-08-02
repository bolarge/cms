package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AuthRoleReferenceData {
    public static final String VGG_ADMIN_ID = "2";
    public static final String SUPER_ADMIN_ID = "1";
    public static final String VGG_USER_ID = "3";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AuthRole role1 = (AuthRole) mongoRepositoryReactive.findById(SUPER_ADMIN_ID, AuthRole.class).block();
        if (role1 == null) {
            role1 = new AuthRole();
            role1.setId(SUPER_ADMIN_ID);
        }
        role1.setDescription("VGG SUPER ADMIN");
        role1.setName("VGG SUPER ADMIN");
        role1.setSsoRoleMapping("vgg_superadmin");
        Set<String> permissionIds = LSLBAuthPermissionReferenceData.getAllVGGSuperAdminPermissions();
        role1.getAuthPermissionIds().addAll(permissionIds);

        AuthRole role2 = (AuthRole) mongoRepositoryReactive.findById(VGG_ADMIN_ID, AuthRole.class).block();
        if (role2 == null) {
            role2 = new AuthRole();
            role2.setId(VGG_ADMIN_ID);
        }
        role2.setDescription("VGG ADMIN");
        role2.setName("VGG ADMIN");
        role2.setSsoRoleMapping("vgg_admin");
        role2.getAuthPermissionIds().addAll(permissionIds);

        AuthRole role3 = (AuthRole) mongoRepositoryReactive.findById(VGG_USER_ID, AuthRole.class).block();
        if (role3 == null) {
            role3 = new AuthRole();
            role3.setId(VGG_USER_ID);
        }
        role3.setDescription("VGG USER");
        role3.setName("VGG USER");
        role3.setSsoRoleMapping("vgg_user");

        mongoRepositoryReactive.saveOrUpdate(role1);
        mongoRepositoryReactive.saveOrUpdate(role2);
        mongoRepositoryReactive.saveOrUpdate(role3);
    }


    /**
     * Not allowed role ids for LSLB ROLES
     * (LSLB is not meant to see VGG ROLES AND VGG USERS)
     * @return
     */
    public static List<String> getNotAllowedRoleIds() {
        return Arrays.asList(AuthRoleReferenceData.SUPER_ADMIN_ID,
                AuthRoleReferenceData.VGG_ADMIN_ID,
                AuthRoleReferenceData.VGG_USER_ID);
    }
}
