package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.PaymentRecord;
import com.software.finatech.lslb.cms.service.domain.PaymentRecordDetail;
import com.software.finatech.lslb.cms.service.domain.PaymentStatus;
import com.software.finatech.lslb.cms.service.dto.PaymentReceiptResponse;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordService;
import com.software.finatech.lslb.cms.service.util.QueryUtils;
import com.software.finatech.lslb.cms.service.util.SendEmail;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;
import static com.software.finatech.lslb.cms.service.util.StringCapitalizer.convertToTitleCaseIteratingChars;


@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    @Autowired
    SendEmail sendEmail;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> findAllPaymentRecords(int page,
                                                      int pageSize,
                                                      String sortDirection,
                                                      String sortProperty,
                                                      String institutionId,
                                                      String agentId,
                                                      String gamingMachineId,
                                                      String gameTypeId,
                                                      String feePaymentTypeId,
                                                      String revenueNameId,
                                                      String paymentStatusId,
                                                      String startDate,
                                                      String endDate,
                                                      String dateProperty,
                                                      String forOutsideSystemPayment,
                                                      HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(gamingMachineId)) {
                query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(forOutsideSystemPayment)) {
                Boolean forOutside = Boolean.valueOf(forOutsideSystemPayment);
                query.addCriteria(Criteria.where("forOutsideSystemPayment").is(forOutside));
            }
            if (!StringUtils.isEmpty(feePaymentTypeId)) {
                query.addCriteria(Criteria.where("feePaymentTypeId").is(feePaymentTypeId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(revenueNameId)) {
                query.addCriteria(Criteria.where("licenseTypeId").is(revenueNameId));
            }
            if (!StringUtils.isEmpty(paymentStatusId)) {
                query.addCriteria(Criteria.where("paymentStatusId").is(paymentStatusId));
            }
            QueryUtils.addDateToQuery(query, startDate, endDate, dateProperty);
            if (page == 0 && httpServletResponse != null) {
                long count = mongoRepositoryReactive.count(query, PaymentRecord.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<PaymentRecord> paymentRecords = (ArrayList<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());
            if (paymentRecords.size() == 0 || paymentRecords.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<PaymentRecordDto> paymentRecordDtos = new ArrayList<>();

            paymentRecords.forEach(paymentRecord -> {
                paymentRecordDtos.add(paymentRecord.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(paymentRecordDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all payment records";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllPaymentStatus() {
        return ReferenceDataUtil.getAllEnumeratedEntity("PaymentStatus", PaymentStatus.class);
    }

    @Override
    public List<PaymentRecord> findPayments(String institutionId, String agentId, String gamingMachineId, String feeId, String startYear) {
        List<PaymentRecord> findPaymentRecords = findPaymentRecords(institutionId, agentId, gamingMachineId, feeId, startYear);
        return findPaymentRecords;

    }

    public List<PaymentRecord> findPaymentRecords(String institutionId, String agentId, String gamingMachineId, String feeId, String startYear) {
        try {

            Query query = new Query();

            if (StringUtils.isEmpty(agentId) && StringUtils.isEmpty(gamingMachineId) && !StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));

                if (!StringUtils.isEmpty(agentId)) {
                    query.addCriteria(Criteria.where("agentId").is(""));
                }
                if (!StringUtils.isEmpty(gamingMachineId)) {
                    query.addCriteria(Criteria.where("gamingMachineId").is(""));
                }
            } else {
                if (!StringUtils.isEmpty(agentId)) {
                    query.addCriteria(Criteria.where("agentId").is(agentId));
                }
                if (!StringUtils.isEmpty(gamingMachineId)) {
                    query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
                }

            }
            if (!StringUtils.isEmpty(feeId)) {
                query.addCriteria(Criteria.where("feeId").is(feeId));
            }
            if (!StringUtils.isEmpty(startYear)) {
                query.addCriteria(Criteria.where("startYear").is(startYear));
            }
            List<PaymentRecord> paymentRecords = (List<PaymentRecord>) mongoRepositoryReactive.findAll(query, PaymentRecord.class).toStream().collect(Collectors.toList());


            return paymentRecords;

        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching license with id";
            return null;
        }
    }

    @Override
    public PaymentRecord findById(String paymentRecordId) {
        return (PaymentRecord) mongoRepositoryReactive.findById(paymentRecordId, PaymentRecord.class).block();
    }


    @Override
    public void savePaymentRecord(PaymentRecord paymentRecord) {
        mongoRepositoryReactive.saveOrUpdate(paymentRecord);
    }

    @Override
    public PaymentRecord findExistingConfirmedApplicationFeeForInstitutionAndGameType(String institutionId, String gameTypeId) {
        String applicationFeeTypeId = FeePaymentTypeReferenceData.APPLICATION_FEE_TYPE_ID;
        Query queryForExistingConfirmedPaymentRecord = new Query();
        String confirmedPaymentStatusId = PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("institutionId").is(institutionId));
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("paymentStatusId").is(confirmedPaymentStatusId));
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryForExistingConfirmedPaymentRecord.addCriteria(Criteria.where("feePaymentTypeId").is(applicationFeeTypeId));
        return (PaymentRecord) mongoRepositoryReactive.find(queryForExistingConfirmedPaymentRecord, PaymentRecord.class).block();
    }


    @Override
    public PaymentRecord findPaymentRecordForGamingMachine(String gamingMachineId,
                                                           String gameTypeId,
                                                           String institutionId,
                                                           String feePaymentTypeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("feePaymentTypeId").is(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID));
        query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
        query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.GAMING_MACHINE_ID));
        return (PaymentRecord) mongoRepositoryReactive.find(query, PaymentRecord.class).block();
    }

    @Override
    public Mono<ResponseEntity> findPaymentRecordById(String paymentRecordId) {
        try {
            PaymentRecord paymentRecord = findById(paymentRecordId);
            if (paymentRecord == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s not found", paymentRecordId), HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(paymentRecord.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting payment record by id", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getPaymentReceiptDetails(String paymentRecordId) {
        try {
            PaymentRecord paymentRecord = findById(paymentRecordId);
            if (paymentRecord == null) {
                return Mono.just(new ResponseEntity<>(String.format("There is no payment record with id %s", paymentRecordId), HttpStatus.BAD_REQUEST));
            }
            if (!paymentRecord.isCompletedPayment()) {
                return Mono.just(new ResponseEntity<>("The payment is not yet completed", HttpStatus.OK));
            }

            PaymentReceiptResponse paymentReceiptResponse = new PaymentReceiptResponse();
            paymentReceiptResponse.setGameTypeName(convertToTitleCaseIteratingChars(paymentRecord.getGameTypeName()));
            paymentReceiptResponse.setFeePaymentTypeName(convertToTitleCaseIteratingChars(paymentRecord.getFeePaymentTypeName()));
            paymentReceiptResponse.setOwnerName(convertToTitleCaseIteratingChars(paymentRecord.getOwnerName()));
            paymentReceiptResponse.setPaymentReference(paymentRecord.getPaymentReference());
            paymentReceiptResponse.setAmount(paymentRecord.getAmount());
            paymentReceiptResponse.setRevenueName(String.valueOf(paymentRecord.getLicenseType()));
            License paymentLicense = paymentRecord.getLicense();
            if (paymentLicense != null && paymentLicense.getExpiryDate() != null && paymentLicense.getEffectiveDate() != null) {
                paymentReceiptResponse.setStartDate(paymentLicense.getStartDateString());
                paymentReceiptResponse.setEndDate(paymentLicense.getEndDateString());
                paymentReceiptResponse.setLicenseStatus(paymentLicense.getLicenseStatusName());
                paymentReceiptResponse.setLicenseNumber(paymentLicense.getLicenseNumber());
            }
            PaymentRecordDetail latestPaymentDetail = getMostRecentPaymentDetailForPaymentRecord(paymentRecordId);
            if (latestPaymentDetail != null) {
                paymentReceiptResponse.setPaymentDate(latestPaymentDetail.getPaymentDateString());
                paymentReceiptResponse.setPaymentTime(latestPaymentDetail.getPaymentTimeString());
                paymentReceiptResponse.setLastModeOfPayment(convertToTitleCaseIteratingChars(latestPaymentDetail.getModeOfPaymentName()));
            }
            if (paymentRecord.isGamingTerminalPayment()) {
                paymentReceiptResponse.setGamingTerminals(paymentRecord.getGamingTerminalDtos());
            }
            if (paymentRecord.isGamingMachinePayment()) {
                paymentReceiptResponse.setGamingMachines(paymentRecord.getGamingMachineDtos());
            }
            return Mono.just(new ResponseEntity<>(paymentReceiptResponse, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while fetching payment receipt details", e);
        }
    }

    private PaymentRecordDetail getMostRecentPaymentDetailForPaymentRecord(String paymentRecordId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
        Sort sort = new Sort(Sort.Direction.DESC, "paymentDate");
        query.with(sort);
        return (PaymentRecordDetail) mongoRepositoryReactive.find(query, PaymentRecordDetail.class).block();
    }
}

