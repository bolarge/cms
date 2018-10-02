package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestDto;
import com.software.finatech.lslb.cms.service.dto.AgentApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AgentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AgentApprovalRequestService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.async_helpers.AgentCreationNotifierAsync;
import com.software.finatech.lslb.cms.service.util.AgentUserCreator;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class AgentApprovalRequestServiceImpl implements AgentApprovalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(AgentApprovalRequestServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private AgentUserCreator agentUserCreatorAsync;
    private AgentCreationNotifierAsync agentCreationNotifierAsync;

    @Autowired
    public AgentApprovalRequestServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                           AuthInfoService authInfoService,
                                           AgentUserCreator agentUserCreatorAsync,
                                           AgentCreationNotifierAsync agentCreationNotifierAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
        this.agentUserCreatorAsync = agentUserCreatorAsync;
        this.agentCreationNotifierAsync = agentCreationNotifierAsync;
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
                query.addCriteria(Criteria.where("rejectorId").is(agentId));
            }
            if (!StringUtils.isEmpty(gameTypeId)) {
                query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(requestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(gameTypeId));
            }
            if (!StringUtils.isEmpty(requestTypeId)) {
                query.addCriteria(Criteria.where("agentApprovalRequestTypeId").is(gameTypeId));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, ApplicationForm.class).block();
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
            ArrayList<AgentApprovalRequest> agentApprovalRequests = (ArrayList<AgentApprovalRequest>) mongoRepositoryReactive.findAll(query, AgentApprovalRequest.class).toStream().collect(Collectors.toList());
            if (agentApprovalRequests == null || agentApprovalRequests.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AgentApprovalRequestDto> agentApprovalRequestDtos = new ArrayList<>();
            agentApprovalRequests.forEach(agentApprovalRequest -> {
                agentApprovalRequestDtos.add(agentApprovalRequest.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(agentApprovalRequestDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding agent approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> getAllAgentApprovalRequestType() {
        try {
            ArrayList<AgentApprovalRequestType> agentApprovalRequestTypes = (ArrayList<AgentApprovalRequestType>) mongoRepositoryReactive
                    .findAll(new Query(), AgentApprovalRequestType.class).toStream().collect(Collectors.toList());

            if (agentApprovalRequestTypes == null || agentApprovalRequestTypes.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> agentApprovalRequestTypeDtos = new ArrayList<>();
            agentApprovalRequestTypes.forEach(agentApprovalRequestType -> {
                agentApprovalRequestTypeDtos.add(agentApprovalRequestType.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(agentApprovalRequestTypeDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all agent approval request types";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllApprovalRequestStatus() {
        try {
            ArrayList<ApprovalRequestStatus> approvalRequestStatuses = (ArrayList<ApprovalRequestStatus>) mongoRepositoryReactive
                    .findAll(new Query(), ApprovalRequestStatus.class).toStream().collect(Collectors.toList());

            if (approvalRequestStatuses == null || approvalRequestStatuses.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> approvalRequestStatusDtos = new ArrayList<>();
            approvalRequestStatuses.forEach(approvalRequestStatus -> {
                approvalRequestStatusDtos.add(approvalRequestStatus.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(approvalRequestStatusDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all approval request statuses";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> approveRequest(AgentApprovalRequestOperationtDto agentApprovalRequestOperationtDto) {
        String agentApprovalRequestId = agentApprovalRequestOperationtDto.getAgentApprovalRequestId();
        String userId = agentApprovalRequestOperationtDto.getUserId();
        try {
            AgentApprovalRequest agentApprovalRequest = findAgentApprovalRequestById(agentApprovalRequestId);
            if (agentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent approval request with id %s does not exist", agentApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo approvingUser = authInfoService.getUserById(userId);
            if (approvingUser == null) {
                return Mono.just(new ResponseEntity<>(String.format("USer with id %s not found", userId), HttpStatus.BAD_REQUEST));
            }

            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                approveAgentCreationRequest(agentApprovalRequest, userId);
            }
            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.ADD_INSTITUTION_TO_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                approveAddInstitutionToAgentRequest(agentApprovalRequest, userId);
            }
            agentCreationNotifierAsync.sendEmailNotificationToInstitutionAdminsAndLslbOnAgentRequestCreation(agentApprovalRequest);
            return Mono.just(new ResponseEntity<>("Request successfully approved", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving request", e);
        }
    }

    @Override
    public Mono<ResponseEntity> rejectRequest(AgentApprovalRequestOperationtDto agentApprovalRequestRejectDto) {
        try {
            if (StringUtils.isEmpty(agentApprovalRequestRejectDto.getReason())) {
                return Mono.just(new ResponseEntity<>("Rejection reason must not be empty", HttpStatus.BAD_REQUEST));
            }

            String agentApprovalRequestId = agentApprovalRequestRejectDto.getAgentApprovalRequestId();
            String userId = agentApprovalRequestRejectDto.getUserId();
            String rejectReason = agentApprovalRequestRejectDto.getReason();
            AgentApprovalRequest agentApprovalRequest = findAgentApprovalRequestById(agentApprovalRequestId);
            if (agentApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent approval request with id %s does not exist", agentApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo approvingUser = authInfoService.getUserById(userId);
            if (approvingUser == null) {
                return Mono.just(new ResponseEntity<>(String.format("USer with id %s not found", userId), HttpStatus.BAD_REQUEST));
            }

            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.CREATE_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                rejectAgentCreationRequest(agentApprovalRequest, userId, rejectReason);
            }
            if (StringUtils.equals(AgentApprovalRequestTypeReferenceData.ADD_INSTITUTION_TO_AGENT_ID, agentApprovalRequest.getAgentApprovalRequestTypeId())) {
                rejectAddInstitutionToAgentRequest(agentApprovalRequest, userId, rejectReason);
            }

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
        Agent agent = findById(agentApprovalRequest.getAgentId());
        agentApprovalRequest.setRejectorId(userId);
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
        agentApprovalRequest.setRejectionReason(rejectReason);
        Query queryForAgentDocument = new Query();
        queryForAgentDocument.addCriteria(Criteria.where("entityId").is(agent.getId()));
        ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(queryForAgentDocument, Document.class).toStream().collect(Collectors.toList());
        if (documents != null || !documents.isEmpty()) {
            for (Document document : documents) {
                try {
                    mongoRepositoryReactive.delete(document);
                } catch (Exception e) {
                    logger.error("An error occurred while deleting document {}", document.getId(),e);
                }
            }
        }
        mongoRepositoryReactive.delete(agent);
        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
    }


    private void approveAddInstitutionToAgentRequest(AgentApprovalRequest agentApprovalRequest, String userId) {
        Agent agent = findById(agentApprovalRequest.getAgentId());
        Set<String> gameTypeIds = agent.getGameTypeIds();
        Set<String> institutionIds = agent.getInstitutionIds();
        List<AgentInstitution> agentInstitutions = agent.getAgentInstitutions();
        List<String> agentBusinessAddresses = agent.getBusinessAddresses();

        String gameTypeId = agentApprovalRequest.getGameTypeId();
        String institutionId = agentApprovalRequest.getInstitutionId();
        AgentInstitution agentInstitution = new AgentInstitution();
        agentInstitution.setGameTypeId(gameTypeId);
        agentInstitution.setInstitutionId(institutionId);
        agentInstitution.setBusinessAddressList(agentApprovalRequest.getBusinessAddressList());
        agentInstitutions.add(agentInstitution);
        agent.setAgentInstitutions(agentInstitutions);
        if (!gameTypeIds.contains(gameTypeId)) {
            gameTypeIds.add(gameTypeId);
        }
        if (!institutionIds.contains(institutionId)) {
            institutionIds.add(institutionId);
        }
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
        Agent agent = findById(agentApprovalRequest.getAgentId());
        agent.setEnabled(true);
        mongoRepositoryReactive.saveOrUpdate(agent);
        agentApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
        agentApprovalRequest.setApproverId(userId);
        mongoRepositoryReactive.saveOrUpdate(agentApprovalRequest);
        agentUserCreatorAsync.createUserAndCustomerCodeForAgent(agent);
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
