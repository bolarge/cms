package com.software.finatech.lslb.cms.service.util;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.GameTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.RevenueNameReferenceData;
import org.joda.time.LocalDateTime;
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

    public static void generateTestData(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        Fee fee = (Fee) mongoRepositoryReactive.findById("1", Fee.class).block();
        if (fee == null) {
            fee = new Fee();
            fee.setId("1");
        }
        fee.setAmount(200000);
        fee.setFeePaymentTypeId("02");
        fee.setGameTypeId("01");
        fee.setActive(true);
        fee.setRevenueNameId(RevenueNameReferenceData.INSTITUTION_REVENUE_CODE);
        mongoRepositoryReactive.saveOrUpdate(fee);
        Fee fee2 = (Fee) mongoRepositoryReactive.findById("2", Fee.class).block();
        if (fee2 == null) {
            fee2 = new Fee();
            fee2.setId("2");
        }
        fee2.setAmount(100000);
        fee2.setFeePaymentTypeId("02");
        fee2.setGameTypeId("02");
        fee2.setActive(true);
        fee2.setRevenueNameId(RevenueNameReferenceData.INSTITUTION_REVENUE_CODE);
        mongoRepositoryReactive.saveOrUpdate(fee2);
        Fee fee3 = (Fee) mongoRepositoryReactive.findById("3", Fee.class).block();
        if (fee3 == null) {
            fee3 = new Fee();
            fee3.setId("3");
        }
        fee3.setAmount(100000);
        fee3.setFeePaymentTypeId("02");
        fee3.setGameTypeId("01");
        fee3.setActive(true);
        fee3.setRevenueNameId(RevenueNameReferenceData.AGENT_REVENUE_CODE);
        mongoRepositoryReactive.saveOrUpdate(fee3);
        Fee fee4 = (Fee) mongoRepositoryReactive.findById("4", Fee.class).block();
        if (fee4 == null) {
            fee4 = new Fee();
            fee4.setId("4");
        }
        fee4.setAmount(200000);
        fee4.setFeePaymentTypeId("02");
        fee4.setGameTypeId("02");
        fee4.setActive(true);
        fee4.setRevenueNameId(RevenueNameReferenceData.AGENT_REVENUE_CODE);
        mongoRepositoryReactive.saveOrUpdate(fee4);
        for (int i = 1; i < 6; i++) {

            Institution institution = (Institution) mongoRepositoryReactive.findById(String.valueOf(i), Institution.class).block();
            if (institution == null) {
                institution = new Institution();
                institution.setId(String.valueOf(i));
            }
            institution.setEmailAddress("ldapcmstest_" + i + "@gmail.com");

            if (i == 1) {
                institution.setEmailAddress("samelikzra@gmail.com");

            }
            if (i == 5) {
                institution.setEmailAddress("azy@qa.team");

            }
            institution.setActive(true);
            institution.getGameTypeIds().addAll(Arrays.asList("01"));
            institution.setInstitutionName("Test Institution " + i);
            institution.setPhoneNumber("12345" + i);
            institution.setStatus(true);

            mongoRepositoryReactive.saveOrUpdate(institution);

            Agent agent = (Agent) mongoRepositoryReactive.findById(String.valueOf(i), Agent.class).block();
            if (agent == null) {
                agent = new Agent();
                agent.setId(String.valueOf(i));
            }
            agent.setFirstName("Agent");
            agent.setLastName(String.valueOf(i));
            agent.setFullName(agent.getFirstName() + " " + agent.getLastName());
            agent.setEmailAddress("testcms " + i + "@gmail.com");
            Set<String> institutionIds = new HashSet<>();
            institutionIds.add(String.valueOf(i));
            Set<String> gameTypes = new HashSet<>();
            gameTypes.add(GameTypeReferenceData.POL_GAME_TYPE_ID);
            agent.setInstitutionIds(institutionIds);
            agent.setGameTypeIds(gameTypes);

            GamingMachine gamingMachine = (GamingMachine) mongoRepositoryReactive.findById(String.valueOf(i), GamingMachine.class).block();
            if (gamingMachine == null) {
                gamingMachine = new GamingMachine();
                gamingMachine.setId(String.valueOf(i));
            }

            gamingMachine.setMachineNumber(String.valueOf(i));
            gamingMachine.setInstitutionId(String.valueOf(i));

            PaymentRecord paymentRecord = (PaymentRecord) mongoRepositoryReactive.findById(String.valueOf(i), PaymentRecord.class).block();
            if (paymentRecord == null) {
                paymentRecord = new PaymentRecord();
                paymentRecord.setId(String.valueOf(i));
            }
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
            paymentRecord.setInstitutionId("" + i);
            paymentRecord.setFeeId(fee.getId());
            paymentRecord.setApproverId("1");
            paymentRecord.setStartYear("2018");
            paymentRecord.setEndYear("2019");
            License license = (License) mongoRepositoryReactive.findById(paymentRecord.getId(), License.class).block();
            if (license == null) {
                license = new License();
                license.setId("" + i);
            }
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
            license.setInstitutionId(paymentRecord.getInstitutionId());
            license.setGameTypeId("01");
            license.setPaymentRecordId(paymentRecord.getId());
            license.setStartDate(LocalDateTime.now());
            LocalDateTime startDate = new LocalDateTime();
            license.setEndDate(startDate.plusMonths(Integer.parseInt(paymentRecord.convertToDto().getFee().getGameType().getLicenseDuration())));
            license.setLicenseType("institution");
            license.setFirstPayment(false);

            if(i==1){
                license.setStartDate(LocalDateTime.now());
                license.setLicenseStatusId(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID);
                license.setEndDate(startDate.plusMonths(Integer.parseInt(paymentRecord.convertToDto().getFee().getGameType().getAipDuration())));

            }
            if (i == 3) {
                paymentRecord.setGamingMachineId(gamingMachine.getId());
                license.setGamingMachineId(paymentRecord.getGamingMachineId());
                license.setEndDate(startDate.plusMonths(Integer.parseInt(paymentRecord.convertToDto().getFee().getGameType().getGamingMachineLicenseDuration())));
                license.setLicenseType("gamingMachine");

            }
            if (i == 4) {
                paymentRecord.setAgentId(agent.getId());
                license.setAgentId(paymentRecord.getAgentId());
                license.setEndDate(startDate.plusMonths(Integer.parseInt(paymentRecord.convertToDto().getFee().getGameType().getAgentLicenseDuration())));
                license.setLicenseType("agent");
                agent.setEmailAddress("samelikzra@gmail.com");

            }
            if(i==5){
                license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_IN_PROGRESS_LICENSE_STATUS_ID);
                
            }
            license.setRenewalStatus("false");
            mongoRepositoryReactive.saveOrUpdate(gamingMachine);
            mongoRepositoryReactive.saveOrUpdate(agent);
            mongoRepositoryReactive.saveOrUpdate(paymentRecord);
            mongoRepositoryReactive.saveOrUpdate(license);

        }
    }

}
