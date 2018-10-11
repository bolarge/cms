package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.UserApprovalRequestType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

public class UserApprovalRequestTypeReferenceData {

    public static final String CREATE_USER_ID = "1";
    public static final String CHANGE_USER_ROLE_ID = "2";
    public static final String ADD_PERMISSION_TO_USER_ID = "3";
    public static final String REMOVE_PERMISSION_FROM_USER_ID = "4";
    public static final String ACTIVATE_USER_ID = "5";
    public static final String DEACTIVATE_USER_ID = "6";

    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        UserApprovalRequestType userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(CREATE_USER_ID, UserApprovalRequestType.class).block();
        if (userApprovalRequestType == null) {
            userApprovalRequestType = new UserApprovalRequestType();
            userApprovalRequestType.setId(CREATE_USER_ID);
        }
        userApprovalRequestType.setName("CREATE USER");
        userApprovalRequestType.setDescription("Create a new user");
        mongoRepositoryReactive.saveOrUpdate(userApprovalRequestType);

        userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(CHANGE_USER_ROLE_ID, UserApprovalRequestType.class).block();
        if (userApprovalRequestType == null) {
            userApprovalRequestType = new UserApprovalRequestType();
            userApprovalRequestType.setId(CHANGE_USER_ROLE_ID);
        }
        userApprovalRequestType.setName("CHANGE USER ROLE");
        userApprovalRequestType.setDescription("Update role of a user");
        mongoRepositoryReactive.saveOrUpdate(userApprovalRequestType);


        userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(ADD_PERMISSION_TO_USER_ID, UserApprovalRequestType.class).block();
        if (userApprovalRequestType == null) {
            userApprovalRequestType = new UserApprovalRequestType();
            userApprovalRequestType.setId(ADD_PERMISSION_TO_USER_ID);
        }
        userApprovalRequestType.setName("ADD PERMISSION TO USER");
        userApprovalRequestType.setDescription("Add permission to user");
        mongoRepositoryReactive.saveOrUpdate(userApprovalRequestType);

        userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(REMOVE_PERMISSION_FROM_USER_ID, UserApprovalRequestType.class).block();
        if (userApprovalRequestType == null) {
            userApprovalRequestType = new UserApprovalRequestType();
            userApprovalRequestType.setId(REMOVE_PERMISSION_FROM_USER_ID);
        }
        userApprovalRequestType.setName("REMOVE PERMISSION FROM USER");
        userApprovalRequestType.setDescription("Add permission to user");
        mongoRepositoryReactive.saveOrUpdate(userApprovalRequestType);

        userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(ACTIVATE_USER_ID, UserApprovalRequestType.class).block();
        if (userApprovalRequestType == null) {
            userApprovalRequestType = new UserApprovalRequestType();
            userApprovalRequestType.setId(ACTIVATE_USER_ID);
        }
        userApprovalRequestType.setName("ACTIVATE USER");
        userApprovalRequestType.setDescription("Enable user");
        mongoRepositoryReactive.saveOrUpdate(userApprovalRequestType);

        userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(DEACTIVATE_USER_ID, UserApprovalRequestType.class).block();
        if (userApprovalRequestType == null) {
            userApprovalRequestType = new UserApprovalRequestType();
            userApprovalRequestType.setId(DEACTIVATE_USER_ID);
        }
        userApprovalRequestType.setName("DEACTIVATE USER");
        userApprovalRequestType.setDescription("Disable user");
        mongoRepositoryReactive.saveOrUpdate(userApprovalRequestType);
    }
}
