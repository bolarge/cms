package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by djaiyeola on 9/21/17.
 */
@Component("databaseLoaderUtils")
public class DatabaseLoaderUtils {
    private static Logger logger = LoggerFactory.getLogger(DatabaseLoaderUtils.class);
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
    protected io.advantageous.boon.json.ObjectMapper mapper;

    public void runSeedData(Environment env) {
        //Seed AuthInfo
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById("1", AuthInfo.class).block();
        if (authInfo == null) {
            authInfo = new AuthInfo();
            authInfo.setId("1");
        }
        if (Arrays.asList(env.getActiveProfiles()).contains("development") || Arrays.asList(env.getActiveProfiles()).contains("test")) {
            authInfo.setEmailAddress("David_J");
        } else if (Arrays.asList(env.getActiveProfiles()).contains("staging")) {
            authInfo.setEmailAddress("david.jaiyeola@venturegardengroup.com");
            //authInfo.setSsoUserId("44016f38-7897-4a5b-b9af-46ee4589b9a1");
        } else if (Arrays.asList(env.getActiveProfiles()).contains("production")) {
            authInfo.setEmailAddress("david.jaiyeola@venturegardengroup.com");
            authInfo.setSsoUserId("44016f38-7897-4a5b-b9af-46ee4589b9a1");
        }

        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("2");
        authInfo.setAccountLocked(false);
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("David");
        authInfo.setLastName("Jaiyeola");
        authInfo.setFullName("David" + " " + "Jaiyeola");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);

        AuthRoleReferenceData.load(mongoRepositoryReactive);
        LSLBAuthPermissionReferenceData.load(mongoRepositoryReactive);
        LSLBAuthRoleReferenceData.load(mongoRepositoryReactive);
        GameTypeReferenceData.load(mongoRepositoryReactive);
        ApplicationFormStatusReferenceData.load(mongoRepositoryReactive);
        PaymentStatusReferenceData.load(mongoRepositoryReactive);
        LicenseStatusReferenceData.load(mongoRepositoryReactive);
        FeePaymentTypeReferenceData.load(mongoRepositoryReactive);
        ScheduledMeetingStatusReferenceData.load(mongoRepositoryReactive);
        DocumentPurposeReferenceData.load(mongoRepositoryReactive);
        DocumentTypeReferenceData.load(mongoRepositoryReactive);
        ModeOfPaymentReferenceData.load(mongoRepositoryReactive);
        LicenseTypeReferenceData.load(mongoRepositoryReactive);
        ApprovalRequestStatusReferenceData.load(mongoRepositoryReactive);
        AgentApprovalRequestTypeReferenceData.load(mongoRepositoryReactive);
        RenewalFormStatusReferenceData.load(mongoRepositoryReactive);
        AuditActionReferenceData.load(mongoRepositoryReactive);
        CustomerComplainStatusReferenceData.load(mongoRepositoryReactive);
        LoggedCaseStatusReferenceData.load(mongoRepositoryReactive);
        UserApprovalRequestTypeReferenceData.load(mongoRepositoryReactive);
        FeeApprovalRequestTypeReferenceData.load(mongoRepositoryReactive);
        DocumentApprovalRequestTypeReferenceData.load(mongoRepositoryReactive);
        MachineApprovalRequestTypeReferenceData.load(mongoRepositoryReactive);
        MachineTypeReferenceData.load(mongoRepositoryReactive);
        MachineStatusReferenceData.load(mongoRepositoryReactive);
        LicenseTransferStatusReferenceData.load(mongoRepositoryReactive);
        ScheduledMeetingPurposeReferenceData.load(mongoRepositoryReactive);
        AgentStatusReferenceData.load(mongoRepositoryReactive);
        CaseAndComplainCategoryReferenceData.load(mongoRepositoryReactive);
        CaseAndComplainTypeReferenceData.load(mongoRepositoryReactive);
        LoggedCaseOutcomeReferenceData.load(mongoRepositoryReactive);
        GenderReferenceData.load(mongoRepositoryReactive);
    }

    // @Profile("test")

    public void generateAuthTestData() {
        TestData.generateAuthTestData(mongoRepositoryReactive);
    }

    // @PostConstruct
    public void runLoadData() {

        ConcurrentHashMap<String, Class> factEnums = new ConcurrentHashMap<>();
        factEnums.put("AuthRole", AuthRole.class);
        factEnums.put("AuthPermission", AuthPermission.class);
        factEnums.put("GameType", GameType.class);
        factEnums.put("ApplicationFormStatus", ApplicationFormStatus.class);
        factEnums.put("LicenseStatus", LicenseStatus.class);
        factEnums.put("PaymentStatus", PaymentStatus.class);
        factEnums.put("FeePaymentType", FeePaymentType.class);
        factEnums.put("ScheduledMeetingStatus", ScheduledMeetingStatus.class);
        factEnums.put("DocumentPurpose", DocumentPurpose.class);
        factEnums.put("DocumentType", DocumentType.class);
        factEnums.put("ModeOfPayment", ModeOfPayment.class);
        factEnums.put("LicenseType", LicenseType.class);
        factEnums.put("ApprovalRequestStatus", ApprovalRequestStatus.class);
        factEnums.put("AgentApprovalRequestType", AgentApprovalRequestType.class);
        factEnums.put("RenewalFormStatus", RenewalFormStatus.class);
        factEnums.put("CustomerComplainStatus", CustomerComplainStatus.class);
        factEnums.put("AuditAction", AuditAction.class);
        factEnums.put("LoggedCaseStatus", LoggedCaseStatus.class);
        factEnums.put("UserApprovalRequestType", UserApprovalRequestType.class);
        factEnums.put("FeeApprovalRequestType", FeeApprovalRequestType.class);
        factEnums.put("DocumentApprovalRequestType", DocumentApprovalRequestType.class);
        factEnums.put("MachineApprovalRequestType", MachineApprovalRequestType.class);
        factEnums.put("MachineType", MachineType.class);
        factEnums.put("MachineStatus", MachineStatus.class);
        factEnums.put("LicenseTransferStatus", LicenseTransferStatus.class);
        factEnums.put("MeetingPurpose", ScheduledMeetingPurpose.class);
        factEnums.put("AgentStatus", AgentStatus.class);
        factEnums.put("CaseAndComplainType", CaseAndComplainType.class);
        factEnums.put("CaseAndComplainCategory", CaseAndComplainCategory.class);
        factEnums.put("LoggedCaseOutcome", LoggedCaseOutcome.class);
        factEnums.put("Gender", Gender.class);

        for (Map.Entry<String, Class> entry : factEnums.entrySet()) {
            logger.info("Importing ReferenceMasterData for > " + entry.getKey());
            Long startTime = System.nanoTime();
            HashSet<FactObject> factObjects =
                    (HashSet<FactObject>) mongoRepositoryReactive.findAll(entry.getValue()).toStream().collect(Collectors.toSet());

            ConcurrentHashMap<String, FactObject> facts = new ConcurrentHashMap<>();
            factObjects.forEach(fact -> {
                facts.put(fact.getId(), fact);
            });

            Mapstore.STORE.put(entry.getKey(), facts);
            Long endTime = System.nanoTime() - startTime;
            Double timeMills = Double.valueOf(endTime) / Double.valueOf(1000000);
            logger.info("Importing took " + " -> " + endTime + "ns" + " >>> " + timeMills + "ms");
        }
    }

    public MongoRepositoryReactiveImpl getMongoRepositoryReactive() {
        return mongoRepositoryReactive;
    }
}
