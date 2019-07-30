package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.sso.SSOUserDetailInfo;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

    @Value("${seed_user_1.email}")
    private String seedUserEmail1;

    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    protected AuthInfoService authInfoService;

    protected io.advantageous.boon.json.ObjectMapper mapper;

    public void runSeedData(Environment env) {

        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById("1", AuthInfo.class).block();

        if (authInfo == null) {

            authInfo = new AuthInfo();
            authInfo.setId("1");

        }

        SSOUserDetailInfo user = authInfoService.getSSOUserDetailInfoByEmail(seedUserEmail1);
        authInfo.setEmailAddress(seedUserEmail1);
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId(AuthRoleReferenceData.SUPER_ADMIN_ID);
        authInfo.setAccountLocked(user.isLocked());
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber(user.getPhoneNumber());
        authInfo.setFirstName(user.getFirstName());
        authInfo.setLastName(user.getLastName());
        authInfo.setFullName(user.getFirstName() + " " + user.getLastName());
        authInfo.setInstitutionId(null);
        if (user != null) {
            authInfo.setSsoUserId(user.getId());
        }

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
        InspectionStatusReferenceData.load(mongoRepositoryReactive);
        PaymentConfirmationApprovalRequestTypeReferenceData.load(mongoRepositoryReactive);
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
        factEnums.put("InspectionStatus", InspectionStatus.class);
        factEnums.put("Gender", Gender.class);
        factEnums.put("PaymentConfirmationApprovalRequestType", PaymentConfirmationApprovalRequestType.class);

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
