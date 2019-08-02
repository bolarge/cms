package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.InspectionForm;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class InspectionFormMailSenderAsync extends AbstractMailSender {

    private static Logger logger = LoggerFactory.getLogger(InspectionFormMailSenderAsync.class);

    @Async
    public void sendNewInspectionFormNotificationToLSLBAdmins(InspectionForm inspectionForm) {
        ArrayList<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_INSPECTION_FORM_NOTIFICATION_ID);
        if (lslbAdmins.isEmpty()) {
            logger.info("There are no lslb admins with new inspection form permission");
            return;
        }

        String mailContent = buildNewInspectionFormLslbAdminMaiLContent(inspectionForm);
        for (AuthInfo lslbAdmin : lslbAdmins) {
            String emailAddress = lslbAdmin.getEmailAddress();
            try {
                logger.info("Sending new Inspection form notification to {} ", emailAddress);
                emailService.sendEmail(mailContent, "New Inspection form submitted on LSLB Customer Management System", emailAddress);
            } catch (Exception e) {
                logger.error("An error occurred while sending new inspection form notification to {}", emailAddress, e);
            }
        }
    }


    private String buildNewInspectionFormLslbAdminMaiLContent(InspectionForm inspectionForm) {
        String presentDateString = DateTime.now().toString("dd-MM-yyyy");
        String frontEndUrl = String.format("%s/logged-reports-detail/%s", frontEndPropertyHelper.getFrontEndUrl(), inspectionForm.getId());
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("frontEndUrl", frontEndUrl);
        return mailContentBuilderService.build(model, "inspection-form/NewInspectionFormLSLBAdmin");
    }
}
