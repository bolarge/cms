package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AgentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AgentStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AgentApprovalRequestService;
import com.software.finatech.lslb.cms.service.util.AgentUserCreator;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AgentCreationNotifierAsync;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class AgentApprovalRequestServiceImpl implements AgentApprovalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(AgentApprovalRequestServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AgentUserCreator agentUserCreatorAsync;
    private AgentCreationNotifierAsync agentCreationNotifierAsync;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;

    private static final String agentAuditActionId = AuditActionReferenceData.AGENT_ID;

    @Autowired
    public AgentApprovalRequestServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                           AgentUserCreator agentUserCreatorAsync,
                                           AgentCreationNotifierAsync agentCreationNotifierAsync,
                                           SpringSecurityAuditorAware springSecurityAuditorAware,
                                           AuditLogHelper auditLogHelper) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.agentUserCreatorAsync = agentUserCreatorAsync;
        this.agentCreationNotifierAsync = agentCreationNotifierAsync;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
    }

    @Override
    public Mono<ResponseEntity> findAllAgentApprovalRequests(int page,
                                                             int pageSize,
                                                             String sortDirection,
                                                             String sortProperty,
                                                             String institutionId,
                                                             String agentId,
                                                             String approverId,
                                                             String gameTypeId,
                                                             String rejectorId,
                                                             String requestTypeId,
                                                             String requestStatusId,
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
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(rejectorId)) {
                query.addCriteria(Criteria.where("rejectorId").is(rejectorId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(requestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(requestStatusId));
            }
            if (!StringUtils.isEmpty(requestTypeId)) {
                query.addCriteria(Criteria.where("agentApprovalRequestTypeId").is(requestTypeId));
            }
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                query.addCriteria(Criteria.where("dateCreated").gte(new LocalDate(startDate)).lte(new LocalDate(endDate)));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser != null && (loggedInUser.isLSLBAdmin() || loggedInUser.isLSLBUser())) {
                query.addCriteria(Criteria.where("initiatorId").ne(loggedInUser.getId()));
            }

            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, AgentApprovalRequest.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
                if (count == null || count == 0) {
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
            ArrayList<AgentApprovalRequest> agentApprovalRequests = (ArrayList<AgentApprovalRequest>) mongoRepositoryReactive.findAll(query, AgentApprovalRequest.class).toStream().collect(Collectors.toList());
            if (agentApprovalRequests.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AgentApprovalRequestDto> agentApprovalRequestDtos = new ArrayList<>();
            agentApprovalRequests.forEach(agentApprovalRequest -> {
                agentApprovalRequestDtos.add(agentApprovalRequest.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(agentApprovalRequestDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding agent approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> getAllAgentApprovalRequestType() {
        return getAllEnumeratedEntity("AgentApprovalRequestType", AgentApprovalRequestType.class);
    }

    @Override
    public Mono<ResponseEntity> getAllApprovalRequestStatus() {
        return
                getAllEnumeratedEntity("ApprovalRequestStatus", ApprovalRequestStatus.class);
    }

    @Override
    public Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto agentApprovalRequestOperationtDto, HttpServletRequest request) {
        String agentApprovalRequestId = agentApprovalRequestOperationtDto.getApprovalRequestId();
        try {
            AgentApprovalRequest agentApprovalRequest = findAgentApprovalRequestById(agentApprovalRequestId);
            if (agentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent approval request with id %s does not exist", agentApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo approvingUser = springSecurityAuditorAware.getLoggedInUser();
            if (approvingUser == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }

            if (agentApprovalRequest.isApprovedRequest() || agentApprovalRequest.isRejectedRequest() || StringUtils.equals(agentApprovalRequest.getInitiatorId(), approvingUser.getId())) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                approveAgentCreationRequest(agentApprovalRequest, approvingUser.getId());
            }
            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.ADD_INSTITUTION_TO_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                approveAddInstitutionToAgentRequest(agentApprovalRequest, approvingUser.getId());
            }

            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.BLACK_LIST_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                approveBlackListAgentRequest(agentApprovalRequest);
            }

            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.WHITE_LIST_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                approveWhiteListAgentRequest(agentApprovalRequest);
            }

            String verbiage = String.format("Approved agent approval request -> Type: %s ", agentApprovalRequest.getAgentApprovalRequestTypeName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), agentApprovalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            agentCreationNotifierAsync.sendEmailNotificationToInstitutionAdminsAndLslbOnAgentRequestCreation(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>("Request successfully approved", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    private void approveWhiteListAgentRequest(AgentApprovalRequest agentApprovalRequest) {
        Agent agent = agentApprovalRequest.getAgent();
        if (agent != null) {
            agent.setAgentStatusId(AgentStatusReferenceData.ACTIVE_ID);
            mongoRepositoryReactive.saveOrUpdate(agent);
            agentApprovalRequest.setAsApproved();
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
        }
    }

    private void approveBlackListAgentRequest(AgentApprovalRequest agentApprovalRequest) {
        Agent agent = agentApprovalRequest.getAgent();
        if (agent != null) {
            agent.setAgentStatusId(AgentStatusReferenceData.BLACK_LISTED_ID);
            agentApprovalRequest.setAsApproved();
            mongoRepositoryReactive.saveOrUpdate(agent);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto agentApprovalRequestRejectDto, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(agentApprovalRequestRejectDto.getReason())) {
                return Mono.just(new ResponseEntity<>("Rejection reason must not be empty", HttpStatus.BAD_REQUEST));
            }

            String agentApprovalRequestId = agentApprovalRequestRejectDto.getApprovalRequestId();
            String rejectReason = agentApprovalRequestRejectDto.getReason();
            AgentApprovalRequest agentApprovalRequest = findAgentApprovalRequestById(agentApprovalRequestId);
            if (agentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent approval request with id %s does not exist", agentApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (agentApprovalRequest.isApprovedRequest() || agentApprovalRequest.isRejectedRequest()) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }

            AuthInfo rejectingUser = springSecurityAuditorAware.getLoggedInUser();
            if (rejectingUser == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }

            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                rejectAgentCreationRequest(agentApprovalRequest, rejectingUser.getId(), rejectReason);
            }
            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.ADD_INSTITUTION_TO_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                rejectAddInstitutionToAgentRequest(agentApprovalRequest, rejectingUser.getId(), rejectReason);
            }
            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.BLACK_LIST_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())
                    || StringUtils.equals(AgentApprovalRequestTypeReferenceData.WHITE_LIST_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
                mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
            }

            String verbiage = String.format("Rejected agent approval request -> Type: %s ", agentApprovalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(agentAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), agentApprovalRequest.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            agentCreationNotifierAsync.sendEmailNotificationToInstitutionAdminsAndLslbOnAgentRequestCreation(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>("Request successfully rejected", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAgentApprovalRequestFullDetail(String agentApprovalRequestId) {
        try {
            AgentApprovalRequest agentApprovalRequest = findAgentApprovalRequestById(agentApprovalRequestId);
            if (agentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent approval request with id %s does not exist", agentApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(agentApprovalRequest.convertToDtoFullDetail(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger,
                    String.format("An error occurred while getting full detail of agent approval request with id %s", agentApprovalRequestId), e);
        }
    }

    private void rejectAddInstitutionToAgentRequest(AgentApprovalRequest agentApprovalRequest, String userId, String rejectReason) {
        agentApprovalRequest.setRejectionReason(rejectReason);
        agentApprovalRequest.setRejectorId(userId);
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
    }

    private void rejectAgentCreationRequest(AgentApprovalRequest agentApprovalRequest, String userId, String rejectReason) {
        PendingAgent pendingAgent = findPendingAgentById(agentApprovalRequest.getPendingAgentId());
        if (pendingAgent != null) {
            agentApprovalRequest.setRejectorId(userId);
            agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            agentApprovalRequest.setRejectionReason(rejectReason);
            Query queryForAgentDocument = new Query();
            queryForAgentDocument.addCriteria(Criteria.where("entityId").is(pendingAgent.getId()));
            ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(queryForAgentDocument, Document.class).toStream().collect(Collectors.toList());
            for (Document document : documents) {
                try {
                    mongoRepositoryReactive.delete(document);
                } catch (Exception e) {
                    logger.error("An error occurred while deleting document {}", document.getId(), e);
                }
            }
            pendingAgent.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            pendingAgent.setEnabled(false);
            mongoRepositoryReactive.saveOrUpdate(pendingAgent);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
        }
    }


    private void approveAddInstitutionToAgentRequest(AgentApprovalRequest agentApprovalRequest, String userId) {
        Agent agent = findById(agentApprovalRequest.getAgentId());
        Set<String> gameTypeIds = agent.getGameTypeIds();
        Set<String> institutionIds = agent.getInstitutionIds();
        List<AgentInstitution> agentInstitutions = agent.getAgentInstitutions();
        Set<String> agentBusinessAddresses = agent.getBusinessAddresses();

        String gameTypeId = agentApprovalRequest.getGameTypeId();
        String institutionId = agentApprovalRequest.getInstitutionId();
        AgentInstitution agentInstitution = new AgentInstitution();
        agentInstitution.getGameTypeIds().add(gameTypeId);
        agentInstitution.setInstitutionId(institutionId);
        agentInstitution.setBusinessAddressList(agentApprovalRequest.getBusinessAddressList());
        agentInstitutions.add(agentInstitution);
        agent.setAgentInstitutions(agentInstitutions);
        gameTypeIds.add(gameTypeId);
        institutionIds.add(institutionId);
        agentBusinessAddresses.addAll(agentApprovalRequest.getBusinessAddressList());
        agent.setBusinessAddresses(agentBusinessAddresses);
        agent.setInstitutionIds(institutionIds);
        agent.setGameTypeIds(gameTypeIds);
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
        agentApprovalRequest.setApproverId(userId);
        mongoRepositoryReactive.saveOrUpdate(agent);
        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
    }

    private void approveAgentCreationRequest(AgentApprovalRequest agentApprovalRequest, String userId) {
        PendingAgent pendingAgent = findPendingAgentById(agentApprovalRequest.getPendingAgentId());
        if (pendingAgent != null) {
            pendingAgent.setEnabled(true);
            pendingAgent.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            mongoRepositoryReactive.saveOrUpdate(pendingAgent);
            Agent agent = new Agent();
            agent.setId(pendingAgent.getId());
            agent.setEnabled(true);
            agent.setDateOfBirth(pendingAgent.getDateOfBirth());
            agent.setDob(pendingAgent.getDob());
            agent.setAgentId(pendingAgent.getAgentId());
            agent.setBusinessAddresses(pendingAgent.getBusinessAddresses());
            agent.setGameTypeIds(pendingAgent.getGameTypeIds());
            agent.setInstitutionIds(pendingAgent.getInstitutionIds());
            agent.setResidentialAddress(pendingAgent.getResidentialAddress());
            agent.setEmailAddress(pendingAgent.getEmailAddress());
            agent.setTitle(pendingAgent.getTitle());
            agent.setBvn(pendingAgent.getBvn());
            agent.setFullName(pendingAgent.getFullName());
            agent.setFirstName(pendingAgent.getFirstName());
            agent.setLastName(pendingAgent.getLastName());
            agent.setMeansOfId(pendingAgent.getMeansOfId());
            agent.setPhoneNumber(pendingAgent.getPhoneNumber());
            agent.setIdNumber(pendingAgent.getIdNumber());
            agent.setMiddleName(pendingAgent.getMiddleName());
            agent.setAgentStatusId(AgentStatusReferenceData.ACTIVE_ID);
            List<AgentInstitution> agentInstitutions = new ArrayList<>();
            for (AgentInstitution agentInstitution : pendingAgent.getAgentInstitutions()) {
                agentInstitutions.add(agentInstitution);
            }
            agent.setAgentInstitutions(agentInstitutions);
            agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            agentApprovalRequest.setApproverId(userId);
            mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
            mongoRepositoryReactive.saveOrUpdate(agent);
            agentUserCreatorAsync.createUserAndCustomerCodeForAgent(agent);
        }
    }


    private PendingAgent findPendingAgentById(String pendingAgentId) {
        if (StringUtils.isEmpty(pendingAgentId)) {
            return null;
        }
        return (PendingAgent) mongoRepositoryReactive.findById(pendingAgentId, PendingAgent.class).block();
    }

    private AgentApprovalRequest findAgentApprovalRequestById(String agentApprovalRequestId) {
        if (StringUtils.isEmpty(agentApprovalRequestId)) {
            return null;
        }
        return (AgentApprovalRequest) mongoRepositoryReactive.findById(agentApprovalRequestId, AgentApprovalRequest.class).block();
    }


    private Agent findById(String agentId) {
        return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
    }
}
