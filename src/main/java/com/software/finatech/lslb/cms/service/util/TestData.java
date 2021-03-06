package com.software.finatech.lslb.cms.service.util;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestData {

    private static Logger logger = LoggerFactory.getLogger(TestData.class);

    /**
     * Generates test users for Dev and test
     *
     * @param mongoRepositoryReactive
     */
    public static void generateAuthTestData(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById("10", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("10");
        }
        authInfo.setSsoUserId("2c36e97a-726c-499a-bc7e-28db16155417");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("1");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("vgg.superadmin@venturegardengroup.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS VGG");
        authInfo.setLastName("Super Admin");
        authInfo.setFullName("RACS VGG" + " " + "Super Admin");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);


        authInfo = (AuthInfo) mongoRepositoryReactive.findById("3", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("3");
        }
        authInfo.setSsoUserId("0f00d700-defb-47e7-89aa-60d6cfd20f62");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("2");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("vgg.admin@venturegardengroup.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS VGG");
        authInfo.setLastName("Admin");
        authInfo.setFullName("RACS VGG" + " " + "Admin");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);


        authInfo = (AuthInfo) mongoRepositoryReactive.findById("4", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("4");
        }
        authInfo.setSsoUserId("e00e33de-dee6-4fc0-8d16-4e572751ae6c");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("3");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("vgg.user@venturegardengroup.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS VGG");
        authInfo.setLastName("User");
        authInfo.setFullName("RACS VGG" + " " + "User");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);


        authInfo = (AuthInfo) mongoRepositoryReactive.findById("5", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("5");
        }
        authInfo.setSsoUserId("a279a210-4fc3-48e0-b1fb-efb54b886f3c");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("4");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("revenue.unit@RACSng.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS Revenue");
        authInfo.setLastName("Unit");
        authInfo.setFullName("RACS Revenue" + " " + "Unit");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);


        authInfo = (AuthInfo) mongoRepositoryReactive.findById("6", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("6");
        }
        authInfo.setSsoUserId("795437c1-0887-4e2d-b904-44f1988c70c5");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("5");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("moh@RACSng.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS MOH");
        authInfo.setLastName("MOH");
        authInfo.setFullName("RACS MOH" + " " + "MOH");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);


        authInfo = (AuthInfo) mongoRepositoryReactive.findById("7", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("7");
        }
        authInfo.setSsoUserId("3a04bc86-dcad-442d-8f4f-fe1e54f7100d");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("6");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("mof@RACSng.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS MOF");
        authInfo.setLastName("MOF");
        authInfo.setFullName("RACS MOF" + " " + "MOF");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);

        authInfo = (AuthInfo) mongoRepositoryReactive.findById("8", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("8");
        }
        authInfo.setSsoUserId("b06c8ade-ab15-4ee0-8167-1214d09d46db");
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("7");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("system.admin@RACSng.com");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("RACS System");
        authInfo.setLastName("Admin");
        authInfo.setFullName("RACS System" + " " + "Admin");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);
    }
}
