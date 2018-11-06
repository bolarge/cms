package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.LicenseTransfer;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class LicenseTransferMailSenderAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(LicenseTransferMailSenderAsync.class);

    @Async
    public void sendLicenseTransferInitialMailNotificationsToLslbAdmins(LicenseTransfer licenseTransfer) {
        List<AuthInfo> lslbMembers = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_LICENSE_TRANSFER_NOTIFICATION_ID);
        if (lslbMembers.isEmpty()) {
            return;
        }
        String mailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferInitialNotificationLSLBAdmin");
        for (AuthInfo lslbMember : lslbMembers) {
            sendLicenseTransferMail(lslbMember.getEmailAddress(), mailContent, "New Licence Transfer Application On LSLB CMS");
        }
    }

    @Async
    public void sendInitialLicenseTransferRejectionMailToTransferor(LicenseTransfer licenseTransfer) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getFromInstitutionId());
        String mailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferRejectionNotificationTransferor");
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendLicenseTransferMail(institutionAdmin.getEmailAddress(), mailContent, "Update on your License Transfer from LSLB CMS");
        }
    }

    @Async
    public void sendInitialLicenseTransferApprovalMailToTransferorAdmins(LicenseTransfer licenseTransfer) {
        List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getFromInstitutionId());
        String mailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferInitialApprovalNotificationTransferor");
        for (AuthInfo institutionAdmin : institutionAdmins) {
            sendLicenseTransferMail(institutionAdmin.getEmailAddress(), mailContent, "Update on your License Transfer from LSLB CMS");
        }
    }

    @Async
    public void sendLicenseTransferRejectionMailToTransferorAndTransferee(LicenseTransfer licenseTransfer) {
        //send mail to transferorr
        List<AuthInfo> transferorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getFromInstitutionId());
        String transferorMailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferRejectionNotificationTransferorPostInitial");
        for (AuthInfo transferorAdmin : transferorAdmins) {
            sendLicenseTransferMail(transferorAdmin.getEmailAddress(), transferorMailContent, "Update On your Licence Transfer From LSLB");
        }

        //send maiil to transferee
        List<AuthInfo> transfereeAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getToInstitutionId());
        String transfereeMailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferRejectionNotificationTransferee");
        for (AuthInfo transfereeAdmin : transfereeAdmins) {
            sendLicenseTransferMail(transfereeAdmin.getEmailAddress(), transfereeMailContent, "Update On your Licence Transfer From LSLB");
        }
    }

    @Async
    public void sendLicenseTransferApprovalMailToTransferorAndTransferee(LicenseTransfer licenseTransfer) {
        List<AuthInfo> transferorAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getFromInstitutionId());
        String transferorMailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferFinalApprovalNotificationTransferor");
        for (AuthInfo transferorAdmin : transferorAdmins) {
            sendLicenseTransferMail(transferorAdmin.getEmailAddress(), transferorMailContent, "Update On your Licence Transfer From LSLB");
        }

        //send maiil to transferee
        List<AuthInfo> transfereeAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getToInstitutionId());
        String transfereeMailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferFinalApprovalNotificationTransferee");
        for (AuthInfo transfereeAdmin : transfereeAdmins) {
            sendLicenseTransferMail(transfereeAdmin.getEmailAddress(), transfereeMailContent, "Update On your Licence Transfer From LSLB");
        }
    }

    @Async
    public void sendNewAddOperatorNotificationToLSlBAdmins(LicenseTransfer licenseTransfer) {
        List<AuthInfo> lslbMembers = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_LICENSE_TRANSFER_NOTIFICATION_ID);
        if (lslbMembers.isEmpty()) {
            return;
        }
        String mailContent = buildLicenceTransferEmailContent(licenseTransfer, "licenese-transfer/LicenseTransferAddOperatorNotificationLSLBAdmin");
        for (AuthInfo lslbMember : lslbMembers) {
            String subject = String.format("%s has applied to acquire a licence Transfer", licenseTransfer.getToInstitution());
            sendLicenseTransferMail(lslbMember.getEmailAddress(), mailContent, subject);
        }
    }

    @Async
    public void sendRejectionNotificationToTransferee(LicenseTransfer licenseTransfer){
        List<AuthInfo> transfereeAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(licenseTransfer.getToInstitutionId());
        String transfereeMailContent = buildLicenceTransferEmailContent(licenseTransfer, "license-transfer/LicenseTransferRejectionNotificationTransferee");
        for (AuthInfo transfereeAdmin : transfereeAdmins) {
            sendLicenseTransferMail(transfereeAdmin.getEmailAddress(), transfereeMailContent, "Update On your Licence Transfer From LSLB");
        }
    }

    private String buildLicenceTransferEmailContent(LicenseTransfer licenseTransfer, String templateName) {
        String url = String.format("%s/licence-transfer-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), licenseTransfer.getId());
        String paymentPageUrl = String.format("%s/payment-page", frontEndPropertyHelper.getFrontEndUrl());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        HashMap<String, Object> model = new HashMap<>();
        Institution transferee = null;
        if (!StringUtils.isEmpty(licenseTransfer.getToInstitutionId())) {
            transferee = licenseTransfer.getToInstitution();
        }
        model.put("date", presentDateString);
        model.put("gameType", String.valueOf(licenseTransfer.getGameType()));
        model.put("transferor", String.valueOf(licenseTransfer.getFromInstitution()));
        model.put("rejectReason", licenseTransfer.getRejectionReason());
        model.put("frontEndUrl", url);
        model.put("frontEndUrlPayment", paymentPageUrl);
        if (transferee != null) {
            model.put("transferee", String.valueOf(transferee));
        }
        return mailContentBuilderService.build(model, templateName);
    }

    private void sendLicenseTransferMail(String emailAddress, String mailContent, String mailSubject) {
        try {
            logger.info("Sending licence transfer mail to {}", emailAddress);
            emailService.sendEmail(mailContent, mailSubject, emailAddress);
        } catch (Exception e) {
            logger.info("An error occurred while sending licence transfer mail to  {}", emailAddress, e);
        }
    }
}
