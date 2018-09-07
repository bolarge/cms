package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.FeeDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailUpdateDto;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ModeOfPaymentReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class PaymentRecordDetailServiceImpl implements PaymentRecordDetailService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordDetailServiceImpl.class);

    private FeeService feeService;
    private PaymentRecordService paymentRecordService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private VigipayService vigipayService;
    private InstitutionService institutionService;
    private AgentService agentService;
    private GamingMachineService gamingMachineService;
    private AuthInfoService authInfoService;

    @Autowired
    public PaymentRecordDetailServiceImpl(FeeService feeService,
                                          PaymentRecordService paymentRecordService,
                                          MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                          VigipayService vigipayService,
                                          InstitutionService institutionService,
                                          AgentService agentService,
                                          GamingMachineService gamingMachineService,
                                          AuthInfoService authInfoService) {
        this.feeService = feeService;
        this.paymentRecordService = paymentRecordService;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.vigipayService = vigipayService;
        this.institutionService = institutionService;
        this.agentService = agentService;
        this.gamingMachineService = gamingMachineService;
        this.authInfoService = authInfoService;
    }

    @Override
    public Mono<ResponseEntity> updatePaymentRecordDetail(PaymentRecordDetailUpdateDto paymentRecordDetailUpdateDto) {
        try {
            String paymentRecordDetailId = paymentRecordDetailUpdateDto.getId();
            PaymentRecordDetail existingPaymentRecordDetail = findById(paymentRecordDetailId);
            if (existingPaymentRecordDetail == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment with is %s does not exist", paymentRecordDetailId), HttpStatus.BAD_REQUEST));
            }

            PaymentRecord paymentRecord = paymentRecordService.findById(existingPaymentRecordDetail.getPaymentRecordId());

            if (StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, paymentRecordDetailUpdateDto.getPaymentStatusId())
                    && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingPaymentRecordDetail.getPaymentStatusId())) {
                double amountPaid = paymentRecord.getAmountPaid();
                amountPaid = amountPaid + existingPaymentRecordDetail.getAmount();
                paymentRecord.setAmountPaid(amountPaid);

                double amountOutstanding = paymentRecord.getAmountOutstanding();
                amountOutstanding = amountOutstanding - existingPaymentRecordDetail.getAmount();
                paymentRecord.setAmountOutstanding(amountOutstanding);

                if (paymentRecord.getAmountOutstanding() <= 0) {
                    paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID);
                } else {
                    paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID);
                }
                paymentRecordService.savePaymentRecord(paymentRecord);
            }
            existingPaymentRecordDetail.setInvoiceNumber(paymentRecordDetailUpdateDto.getInvoiceNumber());
            existingPaymentRecordDetail.setPaymentDate(LocalDate.now());
            existingPaymentRecordDetail.setPaymentStatusId(paymentRecordDetailUpdateDto.getPaymentStatusId());
            savePaymentRecordDetail(existingPaymentRecordDetail);
            return Mono.just(new ResponseEntity<>(existingPaymentRecordDetail.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating payment record detail", e);
        }
    }

    @Override
    public void savePaymentRecordDetail(PaymentRecordDetail paymentRecordDetail) {
        mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
    }

    @Override
    public PaymentRecordDetail findById(String paymentRecordDetailId) {
        return (PaymentRecordDetail) mongoRepositoryReactive.findById(paymentRecordDetailId, PaymentRecordDetail.class).block();
    }


    @Override
    public Mono<ResponseEntity> createInBranchPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        String invoiceNumber = null;
        Agent agent = null;
        Institution institution = null;
        String feeId = paymentRecordDetailCreateDto.getFeeId();
        Fee fee = feeService.findFeeById(feeId);
        if (fee == null) {
            return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
        }

        FeeDto feeDto = fee.convertToDto();
        String feeName = feeDto.getFeePaymentType() != null ? feeDto.getFeePaymentType().getName() : "";
        String gameTypeName = feeDto.getGameType() != null ? feeDto.getGameType().getDescription() : "";
        String revenueName = feeDto.getRevenueName() != null ? feeDto.getRevenueName().getName() : "";
        String feeDescription = String.format("%s Fee for %ss for category %s ", feeName, revenueName, gameTypeName);


        PaymentRecord paymentRecord = new PaymentRecord();
        String paymentRecordId = paymentRecordDetailCreateDto.getPaymentRecordId();
        if (!StringUtils.isEmpty(paymentRecordId)) {
            paymentRecord = paymentRecordService.findById(paymentRecordId);
            if (paymentRecord == null) {
                return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s does not exist", paymentRecordDetailCreateDto.getPaymentRecordId()), HttpStatus.BAD_REQUEST));
            }
        }

        ArrayList<AuthInfo> institutionAdmins;
        if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
            String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
            institutionAdmins = authInfoService.getAllGamingOperatorAdminsForInstitution(institutionId);
            if (institutionAdmins.isEmpty()) {
                return Mono.just(new ResponseEntity<>("There are no gaming operator admins for institution", HttpStatus.BAD_REQUEST));
            }
            institution = institutionService.findById(institutionId);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
            }
            invoiceNumber = createInBranchRecordDetailForInstitution(institution, feeDescription, paymentRecordDetailCreateDto, institutionAdmins);
        }

        if (paymentRecordDetailCreateDto.isGamingMachinePayment()) {
            GamingMachine gamingMachine = gamingMachineService.findById(paymentRecordDetailCreateDto.getGamingMachineId());
            if (gamingMachine != null) {
                institution = institutionService.findById(gamingMachine.getInstitutionId());
                institutionAdmins = authInfoService.getAllGamingOperatorAdminsForInstitution(institution.getId());
                if (institutionAdmins.isEmpty()) {
                    return Mono.just(new ResponseEntity<>("There are no gaming operator admins for institution owning gaming machine", HttpStatus.BAD_REQUEST));
                }
                invoiceNumber = createInBranchRecordDetailForInstitution(institution, feeDescription, paymentRecordDetailCreateDto, institutionAdmins);
            }
        }
        if (paymentRecordDetailCreateDto.isAgentPayment()) {
            agent = agentService.findById(paymentRecordDetailCreateDto.getAgentId());
            if (agent != null) {
                invoiceNumber = createInBranchRecordDetailForAgent(agent, feeDescription, paymentRecordDetailCreateDto);
            }
        }

        if (StringUtils.isEmpty(invoiceNumber)) {
            return Mono.just(new ResponseEntity<>("Invoice was not created successfully", HttpStatus.INTERNAL_SERVER_ERROR));
        }
        if (StringUtils.isEmpty(paymentRecordId)) {
            paymentRecord = new PaymentRecord();
            paymentRecord.setId(UUID.randomUUID().toString());
            paymentRecord.setAmount(fee.getAmount());
            paymentRecord.setAmountOutstanding(fee.getAmount());
            paymentRecord.setAmountPaid(0);
            paymentRecord.setGameTypeId(fee.getGameTypeId());
            if (institution != null) {
                paymentRecord.setInstitutionId(institution.getId());
            }
            if (agent != null) {
                paymentRecord.setAgentId(agent.getId());
            }
            paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
            paymentRecordService.savePaymentRecord(paymentRecord);

        }


        PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
        paymentRecordDetail.setId(UUID.randomUUID().toString());
        paymentRecordDetail.setAmount(paymentRecordDetailCreateDto.getAmount());
        paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.IN_BRANCH_ID);
        paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
        paymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
        paymentRecordDetail.setInvoiceNumber(invoiceNumber);
        savePaymentRecordDetail(paymentRecordDetail);

        List<String> paymentRecordDetailIdList = paymentRecord.getPaymentRecordDetailIds();
        paymentRecordDetailIdList.add(paymentRecordDetail.getId());
        paymentRecord.setPaymentRecordDetailIds(paymentRecordDetailIdList);
        paymentRecordService.savePaymentRecord(paymentRecord);
        return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity> createWebPaymentPaymentRecordDetail(PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        try {
            String feeId = paymentRecordDetailCreateDto.getFeeId();
            Fee fee = feeService.findFeeById(feeId);
            if (fee == null) {
                return Mono.just(new ResponseEntity<>(String.format("Fee with id %s not found", feeId), HttpStatus.BAD_REQUEST));
            }
            PaymentRecord paymentRecord = new PaymentRecord();
            String paymentRecordId = paymentRecordDetailCreateDto.getPaymentRecordId();
            Agent agent = null;
            if (paymentRecordDetailCreateDto.isAgentPayment()) {
                String agentId = paymentRecordDetailCreateDto.getAgentId();
                agent = agentService.findById(agentId);
                if (agent == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Agent with id %s does not exist", agentId), HttpStatus.BAD_REQUEST));
                }
            }
            Institution institution = null;
            if (paymentRecordDetailCreateDto.isGamingMachinePayment()) {
                String gamingMachineId = paymentRecordDetailCreateDto.getGamingMachineId();
                GamingMachine gamingMachine = gamingMachineService.findById(gamingMachineId);
                if (gamingMachine == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
                }
                institution = institutionService.findById(gamingMachine.getInstitutionId());
            }

            if (paymentRecordDetailCreateDto.isInstitutionPayment()) {
                String institutionId = paymentRecordDetailCreateDto.getInstitutionId();
                institution = institutionService.findById(institutionId);
                if (institution == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
                }
            }
            if (!StringUtils.isEmpty(paymentRecordId)) {
                paymentRecord = paymentRecordService.findById(paymentRecordId);
                if (paymentRecord == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Payment record with id %s does not exist", paymentRecordDetailCreateDto.getPaymentRecordId()), HttpStatus.BAD_REQUEST));
                }
            }
            if (StringUtils.isEmpty(paymentRecordId)) {
                paymentRecord = new PaymentRecord();
                paymentRecord.setId(UUID.randomUUID().toString());
                paymentRecord.setAmountOutstanding(fee.getAmount());
                paymentRecord.setAmount(fee.getAmount());
                paymentRecord.setAmountPaid(0);
                paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
                paymentRecord.setFeeId(feeId);
                paymentRecord.setGameTypeId(fee.getGameTypeId());
                if (agent != null) {
                    paymentRecord.setAgentId(agent.getId());
                }
                if (institution != null) {
                    paymentRecord.setInstitutionId(institution.getId());
                }
            }

            PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
            paymentRecordDetail.setId(UUID.randomUUID().toString());
            paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.WEB_PAYMENT_ID);
            paymentRecordDetail.setAmount(paymentRecordDetailCreateDto.getAmount());
            paymentRecordDetail.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
            paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
            savePaymentRecordDetail(paymentRecordDetail);

            List<String> paymentRecordDetailIds = paymentRecord.getPaymentRecordDetailIds();
            paymentRecordDetailIds.add(paymentRecordDetail.getId());
            paymentRecord.setPaymentRecordDetailIds(paymentRecordDetailIds);
            paymentRecordService.savePaymentRecord(paymentRecord);

            return Mono.just(new ResponseEntity<>(paymentRecordDetail.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating payment record detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findAllPaymentRecordDetailForPaymentRecord(String paymentRecord) {
       return   null;
    }

    private String createInBranchRecordDetailForInstitution(Institution institution, String feeDescription, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, List<AuthInfo> admins) {
        VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
        vigipayInvoiceItem.setAmount(paymentRecordDetailCreateDto.getAmount());
        vigipayInvoiceItem.setDetail(feeDescription);
        vigipayInvoiceItem.setQuantity(1);
        vigipayInvoiceItem.setProductCode("");
        return vigipayService.createInBranchInvoiceForInstitution(institution, admins, vigipayInvoiceItem);
    }

    private String createInBranchRecordDetailForAgent(Agent agent, String feeDescription, PaymentRecordDetailCreateDto paymentRecordDetailCreateDto) {
        VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
        vigipayInvoiceItem.setAmount(paymentRecordDetailCreateDto.getAmount());
        vigipayInvoiceItem.setDetail(feeDescription);
        vigipayInvoiceItem.setQuantity(1);
        vigipayInvoiceItem.setProductCode("");
        return vigipayService.createInBranchInvoiceForAgent(agent, vigipayInvoiceItem);
    }
}
