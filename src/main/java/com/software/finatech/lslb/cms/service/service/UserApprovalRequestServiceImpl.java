package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.domain.PendingAuthInfo;
import com.software.finatech.lslb.cms.service.domain.UserApprovalRequest;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.dto.UserApprovalRequestDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.UserApprovalRequestService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class UserApprovalRequestServiceImpl implements UserApprovalRequestService {

    private static final Logger logger = LoggerFactory.getLogger(UserApprovalRequestServiceImpl.class);
    private static final String userAuditActionId = AuditActionReferenceData.USER_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private FrontEndPropertyHelper frontEndPropertyHelper;
    private AuthInfoService authInfoService;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;

    @Autowired
    public UserApprovalRequestServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                          FrontEndPropertyHelper frontEndPropertyHelper,
                                          AuthInfoService authInfoService,
                                          AuditLogHelper auditLogHelper,
                                          SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.frontEndPropertyHelper = frontEndPropertyHelper;
        this.authInfoService = authInfoService;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }


    @Override
    public Mono<ResponseEntity> findAllUserApprovalRequests(int page,
                                                            int pageSize,
                                                            String sortDirection,
                                                            String sortProperty,
                                                            String approvalRequestStatusId,
                                                            String userApprovalRequestTypeId,
                                                            String initiatorId,
                                                            String approverId,
                                                            String rejectorId,
                                                            String userId,
                                                            String startDate,
                                                            String endDate,
                                                            HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(approvalRequestStatusId)) {
                query.addCriteria(Criteria.where("approvalRequestStatusId").is(approvalRequestStatusId));
            }
            if (!StringUtils.isEmpty(userApprovalRequestTypeId)) {
                query.addCriteria(Criteria.where("userApprovalRequestTypeId").is(userApprovalRequestTypeId));
            }
            if (!StringUtils.isEmpty(initiatorId)) {
                query.addCriteria(Criteria.where("initiatorId").is(initiatorId));
            }
            if (!StringUtils.isEmpty(approverId)) {
                query.addCriteria(Criteria.where("approverId").is(approverId));
            }
            if (!StringUtils.isEmpty(rejectorId)) {
                query.addCriteria(Criteria.where("rejectorId").is(rejectorId));
            }
            if (!StringUtils.isEmpty(userId)) {
                query.addCriteria(new Criteria().orOperator(Criteria.where("authInfoId").is(userId), Criteria.where("pendingAuthInfoId").is(userId)));
            }
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                query.addCriteria(Criteria.where("dateCreated").gte(new LocalDate(startDate)).lte(new LocalDate(endDate)));
            }

            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser != null) {
                query.addCriteria(Criteria.where("initiatorId").ne(loggedInUser.getId()));
                if (!loggedInUser.isSuperAdmin()) {
                    query.addCriteria(Criteria.where("initiatorAuthRoleId").is(loggedInUser.getAuthRoleId()));
                }
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, UserApprovalRequest.class).block();
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

            ArrayList<UserApprovalRequest> userApprovalRequests = (ArrayList<UserApprovalRequest>) mongoRepositoryReactive.findAll(query, UserApprovalRequest.class).toStream().collect(Collectors.toList());
            if (userApprovalRequests == null || userApprovalRequests.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<UserApprovalRequestDto> userApprovalRequestDtos = new ArrayList<>();

            userApprovalRequests.forEach(userApprovalRequest -> {
                userApprovalRequestDtos.add(userApprovalRequest.convertToHalfDto());
            });
            return Mono.just(new ResponseEntity<>(userApprovalRequestDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get user approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> getAllUserApprovalRequestType() {
        return getAllEnumeratedEntity("UserApprovalRequestType");
    }

    @Override
    public Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        try {
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String approvalRequestId = requestOperationtDto.getApprovalRequestId();
            UserApprovalRequest userApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (userApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("User approval request with id %s not found", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (userApprovalRequest.isApprovedRequest() ||
                    userApprovalRequest.isRejectedRequest() ||
                    !userApprovalRequest.canBeApprovedByUser(user.getId())
                    ) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }

            if (userApprovalRequest.isCreateUser()) {
                approveCreateUser(userApprovalRequest);
            } else if (userApprovalRequest.isUpdateUserRole()) {
                approveChangeUserRole(userApprovalRequest);
            } else if (userApprovalRequest.isRemovePermissionFromUser()) {
                approveRemovePermissionFromUser(userApprovalRequest);
            } else if (userApprovalRequest.isAddPermissionToUser()) {
                approveAddPermissionsToUser(userApprovalRequest);
            } else if (userApprovalRequest.isEnableUser()) {
                approveEnableUser(userApprovalRequest);
            } else if (userApprovalRequest.isDisableUser()) {
                approveDisableUser(userApprovalRequest);
            } else {
                return Mono.just(new ResponseEntity<>("Invalid Request supplied", HttpStatus.BAD_REQUEST));
            }

            userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            userApprovalRequest.setApproverId(user.getId());
            mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
            String verbiage = String.format("Approved User approval request ->  Type -> %s,Id -> %s ", userApprovalRequest.getUserApprovalRequestType(), userApprovalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), userApprovalRequest.getSubjectUserName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving user approval request ", e);
        }
    }

    @Override
    public UserApprovalRequest findApprovalRequestById(String approvalRequestId) {
        if (StringUtils.isEmpty(approvalRequestId)) {
            return null;
        }
        return (UserApprovalRequest) mongoRepositoryReactive.findById(approvalRequestId, UserApprovalRequest.class).block();
    }

    @Override
    public Mono<ResponseEntity> rejectRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        try {
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String approvalRequestId = requestOperationtDto.getApprovalRequestId();
            UserApprovalRequest userApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (userApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("User approval request with id %s not found", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            if (userApprovalRequest.isApprovedRequest() ||
                    userApprovalRequest.isRejectedRequest()||
                   !userApprovalRequest.canBeApprovedByUser(user.getId())) {
                return Mono.just(new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST));
            }
            if (userApprovalRequest.isCreateUser()) {
                rejectCreateUserRequest(userApprovalRequest);
            }

            userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            userApprovalRequest.setRejectorId(user.getId());
            userApprovalRequest.setRejectionReason(requestOperationtDto.getReason());
            mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
            String verbiage = String.format("Rejected User approval request ->  Type -> %s, Id -> %s", userApprovalRequest.getUserApprovalRequestType(), userApprovalRequest.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), userApprovalRequest.getSubjectUserName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while rejecting approval request ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getUserApprovalRequestFullDetail(String userApprovalRequestId) {
        try {
            UserApprovalRequest userApprovalRequest = findApprovalRequestById(userApprovalRequestId);
            if (userApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("User approval request with id %s does not exist", userApprovalRequestId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting user approval request full detail", e);
        }
    }

    private void approveCreateUser(UserApprovalRequest userApprovalRequest) {
        PendingAuthInfo pendingAuthInfo = getPendingAuthInfoById(userApprovalRequest.getPendingAuthInfoId());
        if (pendingAuthInfo != null) {
            AuthInfoCreateDto authInfoCreateDto = pendingAuthInfoToCreateAuthInfo(pendingAuthInfo);
            authInfoService.createAuthInfo(authInfoCreateDto, frontEndPropertyHelper.getFrontEndUrl(), null).block();
            pendingAuthInfo.setUserApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            mongoRepositoryReactive.saveOrUpdate(pendingAuthInfo);
        }
    }

    private void approveChangeUserRole(UserApprovalRequest userApprovalRequest) {
        AuthInfo user = userApprovalRequest.getAuthInfo(userApprovalRequest.getAuthInfoId());
        if (user != null) {
            user.setAuthRoleId(userApprovalRequest.getNewAuthRoleId());
            mongoRepositoryReactive.saveOrUpdate(user);
        }
    }

    private void approveEnableUser(UserApprovalRequest userApprovalRequest) {
        AuthInfo user = userApprovalRequest.getAuthInfo(userApprovalRequest.getAuthInfoId());
        if (user != null) {
            user.setEnabled(true);
            mongoRepositoryReactive.saveOrUpdate(user);
        }
    }

    private void approveDisableUser(UserApprovalRequest userApprovalRequest) {
        AuthInfo user = userApprovalRequest.getAuthInfo(userApprovalRequest.getAuthInfoId());
        if (user != null) {
            user.setEnabled(false);
            mongoRepositoryReactive.saveOrUpdate(user);
        }
    }

    private void approveAddPermissionsToUser(UserApprovalRequest userApprovalRequest) {
        Set<String> newPermissionIds = userApprovalRequest.getNewPermissionIds();
        AuthInfo user = userApprovalRequest.getAuthInfo(userApprovalRequest.getAuthInfoId());
        if (user != null) {
            Set<String> allUserPermissionIdsForUser = user.getAllUserPermissionIdsForUser();
            for (String newPermissionId : newPermissionIds) {
                AuthPermission authPermission = userApprovalRequest.getAuthPermission(newPermissionId);
                if (authPermission != null && !allUserPermissionIdsForUser.contains(newPermissionId)) {
                    user.getAuthPermissionIds().add(newPermissionId);
                }
            }
            mongoRepositoryReactive.saveOrUpdate(user);
        }
    }

    private void approveRemovePermissionFromUser(UserApprovalRequest userApprovalRequest) {
        String userId = userApprovalRequest.getAuthInfoId();
        Set<String> removedPermissionIds = userApprovalRequest.getRemovedPermissionIds();
        AuthInfo user = userApprovalRequest.getAuthInfo(userId);
        if (user != null) {
            for (String removedPermissionId : removedPermissionIds) {
                AuthPermission authPermission = userApprovalRequest.getAuthPermission(removedPermissionId);
                if (authPermission != null) {
                    user.getAuthPermissionIds().remove(removedPermissionId);
                }
            }
            mongoRepositoryReactive.saveOrUpdate(user);
        }
    }

    private void rejectCreateUserRequest(UserApprovalRequest userApprovalRequest) {
        PendingAuthInfo pendingAuthInfo = userApprovalRequest.getPendingAuthInfo(userApprovalRequest.getPendingAuthInfoId());
        if (pendingAuthInfo != null) {
            pendingAuthInfo.setUserApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            mongoRepositoryReactive.saveOrUpdate(pendingAuthInfo);
        }
    }

    private AuthInfoCreateDto pendingAuthInfoToCreateAuthInfo(PendingAuthInfo pendingAuthInfo) {
        AuthInfoCreateDto authInfoCreateDto = new AuthInfoCreateDto();
        authInfoCreateDto.setLastName(pendingAuthInfo.getLastName());
        authInfoCreateDto.setFirstName(pendingAuthInfo.getFirstName());
        authInfoCreateDto.setPhoneNumber(pendingAuthInfo.getPhoneNumber());
        authInfoCreateDto.setAuthRoleId(pendingAuthInfo.getAuthRoleId());
        authInfoCreateDto.setEmailAddress(pendingAuthInfo.getEmailAddress());
        authInfoCreateDto.setInstitutionId(pendingAuthInfo.getInstitutionId());
        authInfoCreateDto.setAgentId(pendingAuthInfo.getAgentId());
        authInfoCreateDto.setTitle(pendingAuthInfo.getTitle());
        return authInfoCreateDto;
    }

    private PendingAuthInfo getPendingAuthInfoById(String pendingAuthInfoId) {
        if (StringUtils.isEmpty(pendingAuthInfoId)) {
            return null;
        }
        return (PendingAuthInfo) mongoRepositoryReactive.findById(pendingAuthInfoId, PendingAuthInfo.class).block();
    }
}
