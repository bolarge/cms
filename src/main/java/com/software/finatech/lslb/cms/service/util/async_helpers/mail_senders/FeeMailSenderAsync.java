package com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Fee;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class FeeMailSenderAsync extends AbstractMailSender {
    private static final Logger logger = LoggerFactory.getLogger(FeeMailSenderAsync.class);


    public void sendFeeExpiryNotificationForFeeSync(Fee fee) {
        String mailContent = buildExpiryMailContent(fee);
        ArrayList<AuthInfo> lslbAdmins = authInfoService.findAllLSLBMembersThatHasPermission(LSLBAuthPermissionReferenceData.RECEIVE_FEE_EXPIRIY_NOTIFICATION_ID);
        for (AuthInfo lslbAdmin : lslbAdmins) {
            String adminEmail = lslbAdmin.getEmailAddress();
            try {
                logger.info("Sending fee expiry email to {}", adminEmail);
                emailService.sendEmail(mailContent, "Fee Configuration Expiry On LSLB CMS", adminEmail);
                fee.setNextNotificationDate(fee.getEndDate().plusDays(7));
                mongoRepositoryReactive.saveOrUpdate(fee);
            } catch (Exception e) {
                logger.error("An error occurred while sending fee expiry mail to {}", adminEmail, e);
            }
        }
    }

    private String buildExpiryMailContent(Fee fee) {
        String url = String.format("%s/fee-configuration-view/%s", frontEndPropertyHelper.getFrontEndUrl(), fee.getId());
        String presentDateString = LocalDate.now().toString("dd-MM-yyyy");
        String endDateString = "";
        if (fee.getEndDate() != null) {
            endDateString = fee.getEndDate().toString("dd-MM-yyyy");
        }
        HashMap<String, Object> model = new HashMap<>();
        model.put("date", presentDateString);
        model.put("feePaymentType", String.valueOf(fee.getFeePaymentType()));
        model.put("gameType", String.valueOf(fee.getGameType()));
        model.put("licenseType", String.valueOf(fee.getLicenseType()));
        model.put("endDate", endDateString);
        model.put("frontEndUrl", url);
        return mailContentBuilderService.build(model, "pending-fee/FeeExpiry");
    }
}
