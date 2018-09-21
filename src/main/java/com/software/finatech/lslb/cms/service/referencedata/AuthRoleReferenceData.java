package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.Arrays;

public class AuthRoleReferenceData {
    public static  final String VGG_ADMIN_ID = "2";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        AuthRole role1 = (AuthRole) mongoRepositoryReactive.findById("1",AuthRole.class).block();
        if(role1==null){
            role1 = new AuthRole();
            role1.setId("1");

        }
        role1.setDescription("VGG SUPER ADMIN");
        role1.setName("VGG SUPER ADMIN");
        role1.setSsoRoleMapping("vgg_superadmin");
        role1.getAuthPermissionIds().addAll(Arrays.asList("1","12","18"));

        AuthRole role2 = (AuthRole) mongoRepositoryReactive.findById(VGG_ADMIN_ID,AuthRole.class).block();
        if(role2==null){
            role2 = new AuthRole();
            role2.setId(VGG_ADMIN_ID);
        }
        role2.setDescription("VGG ADMIN");
        role2.setName("VGG ADMIN");
        role2.setSsoRoleMapping("vgg_admin");
        //role2.getAuthPermissionIds().addAll(Arrays.asList("",""));
        for(int count=1; count<25; count++)
            role2.getAuthPermissionIds().add(count+"");



        AuthRole role3 = (AuthRole) mongoRepositoryReactive.findById("3",AuthRole.class).block();
        if(role3==null){
            role3 = new AuthRole();
            role3.setId("3");
        }
        role3.setDescription("VGG USER");
        role3.setName("VGG USER");
        role3.setSsoRoleMapping("vgg_user");
        role3.getAuthPermissionIds().addAll(Arrays.asList("1","2"));


        mongoRepositoryReactive.saveOrUpdate(role1);
        mongoRepositoryReactive.saveOrUpdate(role2);
        mongoRepositoryReactive.saveOrUpdate(role3);
    }
}
