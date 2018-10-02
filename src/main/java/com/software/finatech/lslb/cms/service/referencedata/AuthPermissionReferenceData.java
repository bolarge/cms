package com.software.finatech.lslb.cms.service.referencedata;


import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class AuthPermissionReferenceData {

    public static final String RECEIVE_APPLICATION_ID = "1";
    public static final String RECEIVE_PAYMENT_NOTIFICATION_ID = "1";
    public static final String RECEIVE_CUSTOMER_COMPLAIN_ID = "3";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AuthPermission permission1 = (AuthPermission) mongoRepositoryReactive.findById("1", AuthPermission.class).block();
        if (permission1 == null) {
            permission1 = new AuthPermission();
            permission1.setId("1");

        }
        permission1.setDescription("Can receive application form submissions notifications");
        permission1.setName("CAN RECEIVE APPLICATION SUBMISSION NOTIFICATION");

        AuthPermission permission2 = (AuthPermission) mongoRepositoryReactive.findById("2", AuthPermission.class).block();
        if (permission2 == null) {
            permission2 = new AuthPermission();
            permission2.setId("2");
        }

        permission2.setDescription("Can receive payment notification");
        permission2.setName("RECEIVE AGENT NOTIFICATION");

        AuthPermission permission3 = (AuthPermission) mongoRepositoryReactive.findById("3", AuthPermission.class).block();
        if (permission3 == null) {
            permission3 = new AuthPermission();
            permission3.setId("3");
        }
        permission3.setDescription("Can receive customer complains  notifications (new and reminder) , also can resolve customer complains");
        permission3.setName("RECEIVE CUSTOMER COMPLAIN NOTIFICATION");

        mongoRepositoryReactive.saveOrUpdate(permission1);
        mongoRepositoryReactive.saveOrUpdate(permission2);
        mongoRepositoryReactive.saveOrUpdate(permission3);
    }
}
