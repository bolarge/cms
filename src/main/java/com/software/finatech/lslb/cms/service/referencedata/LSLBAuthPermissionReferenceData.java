package com.software.finatech.lslb.cms.userservice.referencedata;

import com.software.finatech.lslb.cms.userservice.domain.AuthPermission;
import com.software.finatech.lslb.cms.userservice.persistence.MongoRepositoryReactiveImpl;

public class LSLBAuthPermissionReferenceData {
    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive){
        AuthPermission permission1 = (AuthPermission) mongoRepositoryReactive.findById("1",AuthPermission.class).block();
        if(permission1==null){
            permission1 = new AuthPermission();
            permission1.setId("1");

        }
        permission1.setTenantId("01");
        permission1.setDescription("Can View Dashboard");
        permission1.setName("Can View Dashboard");

        AuthPermission permission2 = (AuthPermission) mongoRepositoryReactive.findById("2",AuthPermission.class).block();
        if(permission2==null){
            permission2 = new AuthPermission();
            permission2.setId("2");
        }
        permission2.setTenantId("01");
        permission2.setDescription("Can View Data Grid");
        permission2.setName("Can View Data Grid");

        AuthPermission permission3 = (AuthPermission) mongoRepositoryReactive.findById("3",AuthPermission.class).block();
        if(permission3==null){
            permission3 = new AuthPermission();
            permission3.setId("3");
        }
        permission3.setTenantId("01");
        permission3.setDescription("Can Give Sub Admin Access To Certain Modules");
        permission3.setName("Can Give Sub Admin Access To Certain Modules");

        AuthPermission permission4 = (AuthPermission) mongoRepositoryReactive.findById("4",AuthPermission.class).block();
        if(permission4==null){
            permission4 = new AuthPermission();
            permission4.setId("4");
        }
        permission4.setTenantId("01");
        permission4.setDescription("Can Deactivate User");
        permission4.setName("Can Deactivate User");

        AuthPermission permission5 = (AuthPermission) mongoRepositoryReactive.findById("5",AuthPermission.class).block();
        if(permission5==null){
            permission5 = new AuthPermission();
            permission5.setId("5");
         }
        permission5.setTenantId("01");
        permission5.setDescription("Can Activate User");
        permission5.setName("Can Activate User");

        AuthPermission permission6 = (AuthPermission) mongoRepositoryReactive.findById("6",AuthPermission.class).block();
        if(permission6==null){
            permission6 = new AuthPermission();
            permission6.setId("6");
        }
        permission6.setTenantId("01");
        permission6.setDescription("Can Create Institution Banks");
        permission6.setName("Can Create Institution Banks");

        AuthPermission permission7 = (AuthPermission) mongoRepositoryReactive.findById("7",AuthPermission.class).block();
        if(permission7==null){
            permission7 = new AuthPermission();
            permission7.setId("7");
        }
        permission7.setTenantId("01");
        permission7.setDescription("Can Create Institution Transaction Data Source");
        permission7.setName("Can Create Institution Transaction Data Source");

        AuthPermission permission8 = (AuthPermission) mongoRepositoryReactive.findById("8",AuthPermission.class).block();
        if(permission8==null){
            permission8 = new AuthPermission();
            permission8.setId("8");
        }
        permission8.setTenantId("01");
        permission8.setDescription("Can Create Institutions");
        permission8.setName("Can Create Institutions");

        AuthPermission permission9 = (AuthPermission) mongoRepositoryReactive.findById("9",AuthPermission.class).block();
        if(permission9==null){
            permission9 = new AuthPermission();
            permission9.setId("9");
        }
        permission9.setTenantId("01");
        permission9.setDescription("Can Map Permissions To Roles");
        permission9.setName("Can Map Permissions To Roles");

        AuthPermission permission10 = (AuthPermission) mongoRepositoryReactive.findById("10",AuthPermission.class).block();
        if(permission10==null){
            permission10 = new AuthPermission();
            permission10.setId("10");
        }
        permission10.setTenantId("01");
        permission10.setDescription("Can Create Permissions");
        permission10.setName("Can Create Permissions");

        AuthPermission permission11 = (AuthPermission) mongoRepositoryReactive.findById("11",AuthPermission.class).block();
        if(permission11==null){
            permission11 = new AuthPermission();
            permission11.setId("11");
        }
        permission11.setTenantId("01");
        permission11.setDescription("Can Create Roles");
        permission11.setName("Can Create Roles");


        AuthPermission permission12 = (AuthPermission) mongoRepositoryReactive.findById("12",AuthPermission.class).block();
        if(permission12==null){
            permission12 = new AuthPermission();
            permission12.setId("12");
        }
        permission12.setTenantId("01");
        permission12.setDescription("Can Create a VGG Admin");
        permission12.setName("Can Create a VGG Admin");


        AuthPermission permission13 = (AuthPermission) mongoRepositoryReactive.findById("13",AuthPermission.class).block();
        if(permission13==null){
            permission13 = new AuthPermission();
            permission13.setId("13");
        }
        permission13.setTenantId("01");
        permission13.setDescription("Can View Summary Dashboard");
        permission13.setName("Can View Summary Dashboard");

        AuthPermission permission14 = (AuthPermission) mongoRepositoryReactive.findById("14",AuthPermission.class).block();
        if(permission14==null){
            permission14 = new AuthPermission();
            permission14.setId("14");
        }
        permission14.setTenantId("01");
        permission14.setDescription("Can View Transaction Report");
        permission14.setName("Can View Transaction Report");


        AuthPermission permission15 = (AuthPermission) mongoRepositoryReactive.findById("15",AuthPermission.class).block();
        if(permission15==null){
            permission15 = new AuthPermission();
            permission15.setId("15");
        }
        permission15.setTenantId("01");
        permission15.setDescription("Can Manage Account and Create More Users with LSLB and GO roles");
        permission15.setName("Can Manage Account and Create More Users with LSLB and GO roles");


        AuthPermission permission16= (AuthPermission) mongoRepositoryReactive.findById("16",AuthPermission.class).block();
        if(permission16==null){
            permission16 = new AuthPermission();
            permission16.setId("16");
        }
        permission16.setTenantId("01");
        permission16.setDescription("Can View Audit Trail of GO Users");
        permission16.setName("Can View Audit Trail of GO Users");

        AuthPermission permission17= (AuthPermission) mongoRepositoryReactive.findById("17",AuthPermission.class).block();
        if(permission17==null){
            permission17 = new AuthPermission();
            permission17.setId("17");
        }
        permission17.setTenantId("01");
        permission17.setDescription("Can View Audit Trail of LSLB Users");
        permission17.setName("Can View View Audit Trail of LSLB Users");

        AuthPermission permission18= (AuthPermission) mongoRepositoryReactive.findById("18",AuthPermission.class).block();
        if(permission18==null){
            permission18 = new AuthPermission();
            permission18.setId("18");
        }
        permission18.setTenantId("01");
        permission18.setDescription("Can Deactivate VGG Admin");
        permission18.setName("Can Deactivate VGG Admin");


        AuthPermission permission19= (AuthPermission) mongoRepositoryReactive.findById("19",AuthPermission.class).block();
        if(permission19==null){
            permission19 = new AuthPermission();
            permission19.setId("19");
        }
        permission19.setTenantId("01");
        permission19.setDescription("Can Edit Notifications Settings");
        permission19.setName("Can Edit Notifications Settings");

        AuthPermission permission20= (AuthPermission) mongoRepositoryReactive.findById("20",AuthPermission.class).block();
        if(permission20==null){
            permission20 = new AuthPermission();
            permission20.setId("20");
        }
        permission20.setTenantId("01");
        permission20.setDescription("Can View transaction data");
        permission20.setName("Can View transaction data");

        AuthPermission permission21= (AuthPermission) mongoRepositoryReactive.findById("21",AuthPermission.class).block();
        if(permission21==null){
            permission21 = new AuthPermission();
            permission21.setId("21");
        }
        permission21.setTenantId("01");
        permission21.setDescription("Can View Monthly Revenue");
        permission21.setName("Can View Monthly Revenue");

        AuthPermission permission22= (AuthPermission) mongoRepositoryReactive.findById("22",AuthPermission.class).block();
        if(permission22==null){
            permission22 = new AuthPermission();
            permission22.setId("22");
        }
        permission22.setTenantId("01");
        permission22.setDescription("Can View self-diagnostic notification");
        permission22.setName("Can View self-diagnostic notification");

        AuthPermission permission23= (AuthPermission) mongoRepositoryReactive.findById("23",AuthPermission.class).block();
        if(permission23==null){
            permission23 = new AuthPermission();
            permission23.setId("23");
        }
        permission23.setTenantId("01");
        permission23.setDescription("Can access all modules");
        permission23.setName("Can access all modules");

        AuthPermission permission24= (AuthPermission) mongoRepositoryReactive.findById("24",AuthPermission.class).block();
        if(permission24==null){
            permission24 = new AuthPermission();
            permission24.setId("24");
        }
        permission24.setTenantId("01");
        permission24.setDescription("Can configure rights to other users");
        permission24.setName("Can configure rights to other users");

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



    }
}
