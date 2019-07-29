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

        Mapstore.FACT_ENUM.put("AuthRole", AuthRole.class);
        Mapstore.FACT_ENUM.put("AuthPermission", AuthPermission.class);
        Mapstore.FACT_ENUM.put("GameType", GameType.class);
        Mapstore.FACT_ENUM.put("ApplicationFormStatus", ApplicationFormStatus.class);
        Mapstore.FACT_ENUM.put("LicenseStatus", LicenseStatus.class);
        Mapstore.FACT_ENUM.put("PaymentStatus", PaymentStatus.class);
        Mapstore.FACT_ENUM.put("FeePaymentType", FeePaymentType.class);
        Mapstore.FACT_ENUM.put("ScheduledMeetingStatus", ScheduledMeetingStatus.class);
        Mapstore.FACT_ENUM.put("DocumentPurpose", DocumentPurpose.class);
        Mapstore.FACT_ENUM.put("DocumentType", DocumentType.class);
        Mapstore.FACT_ENUM.put("ModeOfPayment", ModeOfPayment.class);
        Mapstore.FACT_ENUM.put("LicenseType", LicenseType.class);
        Mapstore.FACT_ENUM.put("ApprovalRequestStatus", ApprovalRequestStatus.class);
        Mapstore.FACT_ENUM.put("AgentApprovalRequestType", AgentApprovalRequestType.class);
        Mapstore.FACT_ENUM.put("RenewalFormStatus", RenewalFormStatus.class);
        Mapstore.FACT_ENUM.put("CustomerComplainStatus", CustomerComplainStatus.class);
        Mapstore.FACT_ENUM.put("AuditAction", AuditAction.class);
        Mapstore.FACT_ENUM.put("LoggedCaseStatus", LoggedCaseStatus.class);
        Mapstore.FACT_ENUM.put("UserApprovalRequestType", UserApprovalRequestType.class);
        Mapstore.FACT_ENUM.put("FeeApprovalRequestType", FeeApprovalRequestType.class);
        Mapstore.FACT_ENUM.put("DocumentApprovalRequestType", DocumentApprovalRequestType.class);
        Mapstore.FACT_ENUM.put("MachineApprovalRequestType", MachineApprovalRequestType.class);
        Mapstore.FACT_ENUM.put("MachineType", MachineType.class);
        Mapstore.FACT_ENUM.put("MachineStatus", MachineStatus.class);
        Mapstore.FACT_ENUM.put("LicenseTransferStatus", LicenseTransferStatus.class);
        Mapstore.FACT_ENUM.put("MeetingPurpose", ScheduledMeetingPurpose.class);
        Mapstore.FACT_ENUM.put("AgentStatus", AgentStatus.class);
        Mapstore.FACT_ENUM.put("CaseAndComplainType", CaseAndComplainType.class);
        Mapstore.FACT_ENUM.put("CaseAndComplainCategory", CaseAndComplainCategory.class);
        Mapstore.FACT_ENUM.put("LoggedCaseOutcome", LoggedCaseOutcome.class);
        Mapstore.FACT_ENUM.put("InspectionStatus", InspectionStatus.class);
        Mapstore.FACT_ENUM.put("Gender", Gender.class);
        Mapstore.FACT_ENUM.put("PaymentConfirmationApprovalRequestType", PaymentConfirmationApprovalRequestType.class);


            for (Map.Entry<String, Class> entry : Mapstore.FACT_ENUM.entrySet()) {

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
