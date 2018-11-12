package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTransferStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ScheduledMeetingPurposeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.LicenseTransferMailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class LicenseTransferServiceImpl implements LicenseTransferService {
    private static final Logger logger = LoggerFactory.getLogger(LicenseTransferServiceImpl.class);
    private static final String licenseAuditActionId = AuditActionReferenceData.LICENCE_ID;


    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseService licenseService;
    private InstitutionService institutionService;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private LicenseTransferMailSenderAsync licenseTransferMailSenderAsync;
    private PaymentRecordService paymentRecordService;
    private ScheduledMeetingService scheduledMeetingService;

    @Autowired
    public LicenseTransferServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                      LicenseService licenseService,
                                      InstitutionService institutionService,
                                      AuditLogHelper auditLogHelper,
                                      SpringSecurityAuditorAware springSecurityAuditorAware,
                                      LicenseTransferMailSenderAsync licenseTransferMailSenderAsync,
                                      PaymentRecordService paymentRecordService,
                                      ScheduledMeetingService scheduledMeetingService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseService = licenseService;
        this.institutionService = institutionService;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.licenseTransferMailSenderAsync = licenseTransferMailSenderAsync;
        this.paymentRecordService = paymentRecordService;
        this.scheduledMeetingService = scheduledMeetingService;
    }

    @Override
    public Mono<ResponseEntity> findAllLicenseTransfers(int page,
                                                        int pageSize,
                                                        String sortType,
                                                        String sortParam,
                                                        String fromInstitutionId,
                                                        String toInstitutionId,
                                                        String statusId,
                                                        String gameTypeId,
                                                        HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(statusId)) {
                query.addCriteria(Criteria.where("licenseTransferStatusId").is(statusId));
            }
            if (!StringUtils.isEmpty(fromInstitutionId)) {
                query.addCriteria(Criteria.where("fromInstitutionId").is(fromInstitutionId));
            }
            if (!StringUtils.isEmpty(toInstitutionId)) {
                query.addCriteria(Criteria.where("otInstitutionId").is(toInstitutionId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, LicenseTransfer.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort;
            if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
                sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortParam);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<LicenseTransfer> licenseTransfers = (ArrayList<LicenseTransfer>) mongoRepositoryReactive.findAll(query, LicenseTransfer.class).toStream().collect(Collectors.toList());
            if (licenseTransfers == null || licenseTransfers.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<LicenseTransferDto> dtos = new ArrayList<>();
            licenseTransfers.forEach(licenseTransfer -> {
                dtos.add(licenseTransfer.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding all license transfers", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createLicenseTransfer(LicenseTrannsferCreateRequest licenseTransferCreateRequest,
                                                      HttpServletRequest request) {
        try {
            String institutionId = licenseTransferCreateRequest.getFromInstitutionId();
            String licenseId = licenseTransferCreateRequest.getLicenseId();

            Institution institution = institutionService.findByInstitutionId(institutionId);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s not found", institutionId), HttpStatus.BAD_REQUEST));
            }
            License license = licenseService.findLicenseById(licenseId);
            if (license == null) {
                return Mono.just(new ResponseEntity<>(String.format("License with id %s not found", licenseId), HttpStatus.BAD_REQUEST));
            }
            String gameTypeId = license.getGameTypeId();
            Mono<ResponseEntity> validateResponse = validateLicenseTransfer(institutionId, license);
            if (validateResponse != null) {
                //   return validateResponse;
            }

            LicenseTransfer licenseTransfer = new LicenseTransfer();
            licenseTransfer.setId(UUID.randomUUID().toString());
            licenseTransfer.setFromInstitutionId(institutionId);
            licenseTransfer.setLicenseId(licenseId);
            licenseTransfer.setGameTypeId(gameTypeId);
            licenseTransfer.setLicenseTransferStatusId(LicenseTransferStatusReferenceData.PENDING_INITIAL_APPROVAL_ID);
            mongoRepositoryReactive.saveOrUpdate(licenseTransfer);

            String verbiage = String.format("Created License Transfer Request, License Category  -> %s, License Number Number -> %s , Id -> %s",
                    license.getGameType(), license.getLicenseNumber(), licenseTransfer.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(licenseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), institution.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            licenseTransferMailSenderAsync.sendLicenseTransferInitialMailNotificationsToLslbAdmins(licenseTransfer);
            return Mono.just(new ResponseEntity<>(licenseTransfer.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating license transfer ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> setToFromInstitution(LicenseTransferSetToInstitutionRequest setToInstitutionRequest, HttpServletRequest request) {
        try {
            String licenseTransferId = setToInstitutionRequest.getLicenseTransferId();
            String toInstitutionId = setToInstitutionRequest.getToInstitutionId();
            LicenseTransfer licenseTransfer = findLicenseTransferById(licenseTransferId);
            if (licenseTransfer == null) {
                return Mono.just(new ResponseEntity<>(String.format("License transfer with id %s not found", licenseTransferId), HttpStatus.BAD_REQUEST));
            }
            Institution institution = institutionService.findByInstitutionId(toInstitutionId);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Operator with id %s not found", toInstitutionId), HttpStatus.BAD_REQUEST));
            }
            if (!licenseTransfer.isPendingNewInstitutionAddition()) {
                return Mono.just(new ResponseEntity<>("Licence Transfer is not pending new operator addition", HttpStatus.BAD_REQUEST));
            }
            PaymentRecord existingConfirmedPaymentRecord = paymentRecordService.findExistingConfirmedApplicationFeeForInstitutionAndGameType(toInstitutionId, licenseTransfer.getGameTypeId());
            if (existingConfirmedPaymentRecord == null) {
                return Mono.just(new ResponseEntity<>("Kindly make application fee payment for category to proceed", HttpStatus.BAD_REQUEST));
            }

            licenseTransfer.setToInstitutionId(toInstitutionId);
            licenseTransfer.setLicenseTransferStatusId(LicenseTransferStatusReferenceData.PENDING_ADD_INSTITUTION_APPROVAL_ID);
            mongoRepositoryReactive.saveOrUpdate(licenseTransfer);
            licenseTransferMailSenderAsync.sendNewAddOperatorNotificationToLSlBAdmins(licenseTransfer);

            String verbiage = String.format("Created Added Self as Transferee to License Transfer , Category -> %s, License Number Number -> %s, License Transferor -> %s,Id -> %s",
                    licenseTransfer.getGameType(), licenseTransfer.getLicenseNumber(), licenseTransfer.getFromInstitution(), licenseTransfer.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(licenseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), institution.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(licenseTransfer.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding transferee to license transfer", e);
        }
    }

    @Override
    public Mono<ResponseEntity> approveLicenseTransfer(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        try {
            String licenseTransferId = approvalRequestOperationtDto.getApprovalRequestId();
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            LicenseTransfer licenseTransfer = findLicenseTransferById(licenseTransferId);
            if (licenseTransfer == null) {
                return Mono.just(new ResponseEntity<>(String.format("License Transfer with id %s not found", licenseTransferId), HttpStatus.BAD_REQUEST));
            }
            LicenseTransferStatus oldStatus = licenseTransfer.getLicenseTransferStatus();
            if (licenseTransfer.isPendingInitialApproval()) {
                ScheduledMeeting scheduledMeeting = scheduledMeetingService.findCompletedMeetingForEntity(licenseTransferId, ScheduledMeetingPurposeReferenceData.TRANSFEROR_ID);
                if (scheduledMeeting == null) {
                    return Mono.just(new ResponseEntity<>("There is no completed exit meeting scheduled for Transferor", HttpStatus.BAD_REQUEST));
                }
                licenseTransferMailSenderAsync.sendInitialLicenseTransferApprovalMailToTransferorAdmins(licenseTransfer);
                licenseTransfer.setLicenseTransferStatusId(LicenseTransferStatusReferenceData.PENDING_NEW_INSTITUTION_ADDITION_ID);
            } else if (licenseTransfer.isPendingAddInstitutionApproval()) {
                licenseTransfer.setLicenseTransferStatusId(LicenseTransferStatusReferenceData.PENDING_FINAL_APPROVAL_ID);
            } else if (licenseTransfer.isPendingFinalApproval()) {
                ScheduledMeeting scheduledMeeting = scheduledMeetingService.findCompletedMeetingForEntity(licenseTransferId, ScheduledMeetingPurposeReferenceData.TRANSFEREE_ID);
                if (scheduledMeeting == null) {
                    return Mono.just(new ResponseEntity<>("There is no completed presentation scheduled for Transferee", HttpStatus.BAD_REQUEST));
                }
                licenseTransfer.setLicenseTransferStatusId(LicenseTransferStatusReferenceData.APPROVED_ID);
                licenseTransferMailSenderAsync.sendLicenseTransferApprovalMailToTransferorAndTransferee(licenseTransfer);
            } else {
                return Mono.just(new ResponseEntity<>("Licence Transfer is not in a  valid state to be approved", HttpStatus.BAD_REQUEST));
            }

            licenseTransfer.getTransferDecisions().add(LicenseTransferDecision.fromNameNewAndOldStatus(loggedInUser.getFullName(), String.valueOf(oldStatus),
                    String.valueOf(licenseTransfer.getLicenseTransferStatus())));
            String verbiage = String.format("Approved License Transfer , Transferor -> %s, Transferee  -> %s, Category -> %s, License Number Number -> %s, Old Status -> %s, New Status -> %s, Id -> %s",
                    licenseTransfer.getFromInstitution(), licenseTransfer.getToInstitution(), licenseTransfer.getGameType(), licenseTransfer.getLicenseNumber(),
                    oldStatus, licenseTransfer.getLicenseTransferStatus(), licenseTransfer.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(licenseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            mongoRepositoryReactive.saveOrUpdate(licenseTransfer);
            return Mono.just(new ResponseEntity<>(licenseTransfer.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving license transfer", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectLicenseTransfer(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        try {
            String licenseTransferId = approvalRequestOperationtDto.getApprovalRequestId();
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            LicenseTransfer licenseTransfer = findLicenseTransferById(licenseTransferId);
            if (licenseTransfer == null) {
                return Mono.just(new ResponseEntity<>(String.format("License Transfer with id %s not found", licenseTransferId), HttpStatus.BAD_REQUEST));
            }
            LicenseTransferStatus oldStatus = licenseTransfer.getLicenseTransferStatus();
            licenseTransfer.setLicenseTransferStatusId(LicenseTransferStatusReferenceData.REJECTED_ID);
            licenseTransfer.getTransferDecisions().add(LicenseTransferDecision.fromNameNewAndOldStatus(loggedInUser.getFullName(), String.valueOf(oldStatus),
                    String.valueOf(licenseTransfer.getLicenseTransferStatus())));
            mongoRepositoryReactive.saveOrUpdate(licenseTransfer);

            if (oldStatus.isPendingFinalApproval()) {
                licenseTransferMailSenderAsync.sendLicenseTransferRejectionMailToTransferorAndTransferee(licenseTransfer);
            }
            if (oldStatus.isPendingInitialApproval()) {
                licenseTransferMailSenderAsync.sendInitialLicenseTransferRejectionMailToTransferor(licenseTransfer);
            }
            if (oldStatus.isPendingAddInstitutionApproval()) {
                licenseTransferMailSenderAsync.sendRejectionNotificationToTransferee(licenseTransfer);
            }
            String verbiage = String.format("Rejected License Transfer , Transferror -> %s, Transferee  -> %s, Category -> %s, License Number Number -> %s, Id -> %s",
                    licenseTransfer.getFromInstitution(), licenseTransfer.getToInstitution(), licenseTransfer.getGameType(), licenseTransfer.getLicenseNumber(), licenseTransfer.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(licenseAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(licenseTransfer.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting license transfer", e);
        }
    }

    @Override
    public LicenseTransfer findLicenseTransferById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return (LicenseTransfer) mongoRepositoryReactive.findById(id, LicenseTransfer.class).block();
    }

    @Override
    public Mono<ResponseEntity> getLicenseTransferFullDetail(String id) {
        try {
            LicenseTransfer licenseTransfer = findLicenseTransferById(id);
            if (licenseTransfer == null) {
                return Mono.just(new ResponseEntity<>(String.format("License Transfer woth id %s not found", id), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(licenseTransfer.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting license transfer full detail", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllLicenseTransferStatus() {
        return getAllEnumeratedEntity("LicenseTransferStatus");
    }


    /**
     * Get License
     * transfer for payment
     *
     * @param institutionId
     * @return
     */
    @Override
    public Mono<ResponseEntity> getAllLicenseTransferForPayment(String institutionId, String gameTypeId) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("toInstitutionId").is(institutionId));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            query.addCriteria(Criteria.where("licenseTransferStatusId").is(LicenseTransferStatusReferenceData.APPROVED_ID));
            ArrayList<LicenseTransfer> licenseTransfers = (ArrayList<LicenseTransfer>) mongoRepositoryReactive.findAll(query, LicenseTransfer.class).toStream().collect(Collectors.toList());
            if (licenseTransfers.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<LicenseTransferDto> dtos = new ArrayList<>();
            for (LicenseTransfer licenseTransfer : licenseTransfers) {
                if (StringUtils.isEmpty(licenseTransfer.getPaymentRecordId())) {
                    LicenseTransferDto dto = new LicenseTransferDto();
                    dto.setId(licenseTransfer.getId());
                    dto.setFromInstitutionName(String.valueOf(licenseTransfer.getFromInstitution()));
                    dtos.add(dto);
                }
            }
            if (dtos.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting license transfer for institution", e);
        }
    }

    private Mono<ResponseEntity> validateLicenseTransfer(String institutionId, License license) {
        Query query = new Query();
        query.addCriteria(Criteria.where("licenseId").is(license.getId()));
        LicenseTransfer transfer = (LicenseTransfer) mongoRepositoryReactive.find(query, LicenseTransfer.class).block();
        if (transfer != null) {
            return Mono.just(new ResponseEntity<>("A license Transfer already exists for the Licence", HttpStatus.BAD_REQUEST));
        }

        query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
        ArrayList<LicenseTransfer> transfers = (ArrayList<LicenseTransfer>) mongoRepositoryReactive.findAll(query, LicenseTransfer.class).toStream().collect(Collectors.toList());
        if (!transfers.isEmpty()) {
            for (LicenseTransfer licenseTransfer : transfers) {
                if (!licenseTransfer.isFinallyApproved()) {
                    return Mono.just(new ResponseEntity<>("You Currently have a licence Transfer in the category pending", HttpStatus.BAD_REQUEST));
                }
            }
        }
        return null;
    }
}