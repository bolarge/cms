package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.exception.VigiPayServiceException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.PaymentEmailNotifierAsync;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ModeOfPaymentReferenceData.IN_BRANCH_ID;
import static com.software.finatech.lslb.cms.service.referencedata.ModeOfPaymentReferenceData.WEB_PAYMENT_ID;
import static com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData.*;


@Component
public class VigipayRequeryTask {
    private static final Logger logger = LoggerFactory.getLogger(VigipayRequeryTask.class);

    @Autowired
    private VigipayService vigipayService;
    @Autowired
    private PaymentEmailNotifierAsync paymentEmailNotifierAsync;
    @Autowired
    private PaymentRecordDetailService paymentRecordDetailService;
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    private static final int SIX_HOURS = 60 * 60 * 1000;

   // @Scheduled(fixedRate = 60 * 50 * 1000, initialDelay = 600)
   // @SchedulerLock(name = "ReQuery Vigipay Invoices(UnPaid And Pending)", lockAtMostFor = SIX_HOURS, lockAtLeastFor = SIX_HOURS)
   @Scheduled(fixedRate = 300000000)
    public void doReQueryForInvoices() {
        try {
            List<PaymentRecordDetail> paymentsPendingRequery = getListOfInvoicesPendingReQuery();
            for (PaymentRecordDetail paymentPendingRequery : paymentsPendingRequery) {
                doInvoiceLookUpAndUpdate(paymentPendingRequery);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private List<PaymentRecordDetail> getListOfInvoicesPendingReQuery() {
        Query query = new Query();
        List<String> requeryPaymentStatuses = Arrays.asList(UNPAID_STATUS_ID, PENDING_VIGIPAY_CONFIRMATION_STATUS_ID);
        Criteria inBranchCriteria = Criteria.where("paymentStatusId").in(requeryPaymentStatuses).
                andOperator(Criteria.where("modeOfPaymentId").is(IN_BRANCH_ID));

        Criteria webCriteria = Criteria.where("invoiceNumber").ne(null).
                andOperator(Criteria.where("paymentStatus").in(requeryPaymentStatuses),
                        Criteria.where("modeOfPaymentId").is(WEB_PAYMENT_ID));

        query.addCriteria(new Criteria().orOperator(inBranchCriteria, webCriteria));
        return (ArrayList<PaymentRecordDetail>) mongoRepositoryReactive.findAll(query, PaymentRecordDetail.class).toStream().collect(Collectors.toList());
    }

    private void doInvoiceLookUpAndUpdate(PaymentRecordDetail paymentRecordDetail) {
        try {
            String invoiceNumber = paymentRecordDetail.getInvoiceNumber();
            if (StringUtils.isEmpty(invoiceNumber)) {
                return;
            }
            String vigiPayPaymentStatus = vigipayService.getVigipayPaymentStatusForInvoice(invoiceNumber);
            //VigiPay Status 0 = UnPaid, 1= Paid, Other statuses are Invalid
            if (StringUtils.equals("1", vigiPayPaymentStatus)) {
                PaymentRecordDetailUpdateDto updateDto = PaymentRecordDetailUpdateDto.fromIdAndPaymentStatus(paymentRecordDetail.getId(), COMPLETED_PAYMENT_STATUS_ID);
                updateDto.setInvoiceNumber(paymentRecordDetail.getInvoiceNumber());
                paymentRecordDetailService.updatePaymentRecordDetail(updateDto, null).block();
                return;
            }
            if (StringUtils.equals("0", vigiPayPaymentStatus)) {
                return;
            }
            if (!StringUtils.equalsAny("0", "1")) {
                paymentEmailNotifierAsync.sendIrregularPaymentStatusToVGGAdminAndUsers(paymentRecordDetail, paymentRecordDetail.getPaymentRecord(), vigiPayPaymentStatus);
            }
        } catch (VigiPayServiceException e) {
            logger.error(e.getMessage(), e);
        }
    }
}