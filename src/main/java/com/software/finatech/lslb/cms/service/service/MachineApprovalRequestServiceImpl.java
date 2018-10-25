package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.MachineApprovalRequestDto;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.MachineApprovalRequestService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class MachineApprovalRequestServiceImpl implements MachineApprovalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(MachineApprovalRequestServiceImpl.class);

    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public MachineApprovalRequestServiceImpl(SpringSecurityAuditorAware springSecurityAuditorAware,
                                             AuditLogHelper auditLogHelper,
                                             MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> findAllMachineApprovalRequests(int page,
                                                               int pageSize,
                                                               String sortDirection,
                                                               String sortProperty,
                                                               String approvalRequestStatusId,
                                                               String requestTypeId,
                                                               String initiatorId,
                                                               String approverId,
                                                               String rejectorId,
                                                               String institutionId,
                                                               String gamingMachineId,
                                                               String gamingTerminalId,
                                                               String startDate,
                                                               String endDate,
                                                               HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(approverId)) {
                query.addCriteria(Criteria.where("approverId").is(approverId));
            }
            if (!StringUtils.isEmpty(gamingMachineId)) {
                query.addCriteria(Criteria.where("gamingMachineId").is(gamingMachineId));
            }
            if (!StringUtils.isEmpty(rejectorId)) {
                query.addCriteria(Criteria.where("gamingTerminalId").is(gamingTerminalId));
            }
            if (!StringUtils.isEmpty(approvalRequestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(approvalRequestStatusId));
            }
            if (!StringUtils.isEmpty(requestTypeId)) {
                query.addCriteria(Criteria.where("machineApprovalRequestTypeId").is(requestTypeId));
            }
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                query.addCriteria(Criteria.where("dateCreated").gte(new LocalDate(startDate)).lte(new LocalDate(endDate)));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, MachineApprovalRequest.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
                if (count == 0) {
                    return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
                }
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
            ArrayList<MachineApprovalRequest> machineApprovalRequests = (ArrayList<MachineApprovalRequest>) mongoRepositoryReactive.findAll(query, MachineApprovalRequest.class).toStream().collect(Collectors.toList());
            if (machineApprovalRequests == null || machineApprovalRequests.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<MachineApprovalRequestDto> machineApprovalRequestDtos = new ArrayList<>();
            machineApprovalRequests.forEach(machineApprovalRequest -> {
                machineApprovalRequestDtos.add(machineApprovalRequest.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(machineApprovalRequestDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding machine approval requests";
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> getAllMachineApprovalRequestType() {
        return getAllEnumeratedEntity("MachineApprovalRequestType");

    }

    @Override
    public Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        String approvalRequestId = approvalRequestOperationtDto.getApprovalRequestId();
        try {
            MachineApprovalRequest approvalRequest = findApprovalRequestById(approvalRequestId);
            if (approvalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Approval request with id %s does not exist", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo approvingUser = springSecurityAuditorAware.getLoggedInUser();
            if (approvingUser == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }

            boolean isGamingTerminal = false;
            if (approvalRequest.isCreateGamingMachine()) {
                approveCreateGamingMachine(approvalRequest);
            }
            if (approvalRequest.isAddGamesToGamingMachine()) {
                approveAddGamesToGamingMachine(approvalRequest);
            }

            if (approvalRequest.isCreateGamingTerminal()) {
                isGamingTerminal = true;
                //Approve gaming terminal here
            }
            String auditActionId;
            if (isGamingTerminal) {
                auditActionId = AuditActionReferenceData.GAMING_TERMINAL_ID;
            } else {
                auditActionId = AuditActionReferenceData.GAMING_MACHINE_ID;
            }
            approvalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            approvalRequest.setApproverId(approvingUser.getId());
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            String verbiage = String.format("Approved Machine approval request -> Type: %s ,  Id -> %s", approvalRequest.getMachineApprovalRequestType(), approvalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(auditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), approvalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            //    agentCreationNotifierAsync.sendEmailNotificationToInstitutionAdminsAndLslbOnAgentRequestCreation(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>("Request successfully approved", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    private void approveAddGamesToGamingMachine(MachineApprovalRequest approvalRequest) {
        GamingMachine gamingMachine = approvalRequest.getGamingMachine();
        if (gamingMachine != null) {
            addGamesToMachine(approvalRequest.getNewMachineGames(), gamingMachine);
        }
    }

    private void approveCreateGamingMachine(MachineApprovalRequest approvalRequest) {
        PendingGamingMachine pendingGamingMachine = approvalRequest.getPendingGamingMachine();
        if (pendingGamingMachine != null) {
            GamingMachine gamingMachine = new GamingMachine();
            BeanUtils.copyProperties(pendingGamingMachine, gamingMachine);
            gamingMachine.setCreated(null);
            gamingMachine.setCreatedAt(null);
            gamingMachine.setCreatedBy(null);
            gamingMachine.setLastModified(null);
            gamingMachine.setLastModifiedBy(null);
            gamingMachine.setId(UUID.randomUUID().toString());
            mongoRepositoryReactive.saveOrUpdate(gamingMachine);
            Set<MachineGameDetails> machineGameDetails = pendingGamingMachine.getGameDetailsList();
            if (!machineGameDetails.isEmpty()) {
                addGamesToMachine(machineGameDetails, gamingMachine);
            }
            pendingGamingMachine.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            mongoRepositoryReactive.saveOrUpdate(pendingGamingMachine);
        }
    }

    private void addGamesToMachine(Collection<MachineGameDetails> machineGameDetails, GamingMachine gamingMachine) {
        for (MachineGameDetails gameDetails : machineGameDetails) {
            MachineGame machineGame = new MachineGame();
            machineGame.setGameName(gameDetails.getGameName());
            machineGame.setGameVersion(gameDetails.getGameVersion());
            machineGame.setGamingMachineId(gamingMachine.getId());
            machineGame.setActive(true);
            machineGame.setId(UUID.randomUUID().toString());
            mongoRepositoryReactive.saveOrUpdate(machineGame);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto approvalRequestOperationtDto, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(approvalRequestOperationtDto.getReason())) {
                return Mono.just(new ResponseEntity<>("Rejection reason must not be empty", HttpStatus.BAD_REQUEST));
            }

            String approvalRequestId = approvalRequestOperationtDto.getApprovalRequestId();
            String rejectReason = approvalRequestOperationtDto.getReason();
            MachineApprovalRequest approvalRequest = findApprovalRequestById(approvalRequestId);
            if (approvalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Approval request with id %s does not exist", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo rejectingUser = springSecurityAuditorAware.getLoggedInUser();
            if (rejectingUser == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }

            if (approvalRequest.isCreateGamingMachine()) {
                rejectCreateGamingMachine(approvalRequest);
            }

            boolean isGamingTerminal = false;
            if (approvalRequest.isCreateGamingTerminal()) {
                isGamingTerminal = true;
                //Approve gaming terminal here
            }
            String auditActionId;
            if (isGamingTerminal) {
                auditActionId = AuditActionReferenceData.GAMING_TERMINAL_ID;
            } else {
                auditActionId = AuditActionReferenceData.GAMING_MACHINE_ID;
            }
            approvalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            approvalRequest.setRejectorId(rejectingUser.getId());
            approvalRequest.setRejectionReason(rejectReason);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            String verbiage = String.format("Rejected Machine approval request -> Type: %s ,  Id -> %s", approvalRequest.getMachineApprovalRequestType(), approvalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(auditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), approvalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>("Request successfully rejected", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    private void rejectCreateGamingMachine(MachineApprovalRequest approvalRequest) {
        PendingGamingMachine pendingGamingMachine = approvalRequest.getPendingGamingMachine();
        if (pendingGamingMachine != null) {
            pendingGamingMachine.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
        }
    }

    @Override
    public MachineApprovalRequest findApprovalRequestById(String approvalRequestId) {
        if (StringUtils.isEmpty(approvalRequestId)) {
            return null;
        }
        return (MachineApprovalRequest) mongoRepositoryReactive.findById(approvalRequestId, MachineApprovalRequest.class).block();
    }

    @Override
    public Mono<ResponseEntity> getMachineApprovalRequestFullDetail(String approvalRequestId) {
        try {
            MachineApprovalRequest approvalRequest = findApprovalRequestById(approvalRequestId);
            if (approvalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Approval request with id %s does not exist", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(approvalRequest.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger,
                    String.format("An error occurred while getting full detail of machine approval request with id %s", approvalRequestId), e);
        }
    }
}
