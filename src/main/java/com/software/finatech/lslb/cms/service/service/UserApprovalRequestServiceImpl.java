package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.ApprovalRequestOperationtDto;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.UserApprovalRequestDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                                                            String loggedInUserId,
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

            if (!StringUtils.isEmpty(loggedInUserId)) {
                /**
                 *    if logged in user id is sent,
                 *    find the approval requests that are not logged by him
                 *    and are logged by people of his role
                 */
                AuthInfo loggedInUser = getAuthInfoById(loggedInUserId);
                if (loggedInUser != null) {
                    query.addCriteria(Criteria.where("initiatorId").ne(initiatorId));
                    if (!loggedInUser.isSuperAdmin()) {
                        query.addCriteria(Criteria.where("initiatorAuthRoleId").is(loggedInUser.getAuthRoleId()));
                    }
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
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get user approval requests";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> getAllUserApprovalRequestType() {
        try {
            ArrayList<UserApprovalRequestType> approvalRequestTypes = (ArrayList<UserApprovalRequestType>) mongoRepositoryReactive
                    .findAll(new Query(), UserApprovalRequestType.class).toStream().collect(Collectors.toList());

            if (approvalRequestTypes == null || approvalRequestTypes.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.OK));
            }
            List<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
            approvalRequestTypes.forEach(approvalRequestType -> {
                enumeratedFactDtos.add(approvalRequestType.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all approval request statuses";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> approveRequest(ApprovalRequestOperationtDto requestOperationtDto, HttpServletRequest request) {
        try {
            String approvalRequestId = requestOperationtDto.getApprovalRequestId();

            UserApprovalRequest userApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (userApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("User approval request with id %s not found", approvalRequestId), HttpStatus.BAD_REQUEST));
            }

            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }

            if (!canApproveRequest(user)) {
                return Mono.just(new ResponseEntity<>("User does not have permission to approve request", HttpStatus.BAD_REQUEST));
            }

            if (userApprovalRequest.isCreateUser()) {
                approveCreateUser(userApprovalRequest);
            }
            if (userApprovalRequest.isUpdateUserRole()) {
                approveChangeUserRole(userApprovalRequest);
            }
            if (userApprovalRequest.isRemovePermissionFromUser()) {
                approveRemovePermissionFromUser(userApprovalRequest);
            }
            if (userApprovalRequest.isAddPermissionToUser()) {
                approveAddPermissionsToUser(userApprovalRequest);
            }
            if (userApprovalRequest.isEnableUser()) {
                approveEnableUser(userApprovalRequest);
            }
            if (userApprovalRequest.isDisableUser()) {
                approveDisableUser(userApprovalRequest);
            }

            userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            userApprovalRequest.setApproverId(user.getId());
            mongoRepositoryReactive.save(userApprovalRequest);
            String verbiage = String.format("Approved User approval request ->  Type : %s", userApprovalRequest.getUserApprovalRequestType());
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
            String approvalRequestId = requestOperationtDto.getApprovalRequestId();
            UserApprovalRequest userApprovalRequest = findApprovalRequestById(approvalRequestId);
            if (userApprovalRequest == null) {
                return Mono.just(new ResponseEntity<>(String.format("User approval request with id %s not found", approvalRequestId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
        if (!canApproveRequest(user)) {
            return Mono.just(new ResponseEntity<>("User does not have permission to reject request", HttpStatus.BAD_REQUEST));
        }

        userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
        userApprovalRequest.setRejectorId(user.getId());
        userApprovalRequest.setRejectionReason(requestOperationtDto.getReason());
        mongoRepositoryReactive.save(userApprovalRequest);
        String verbiage = String.format("Rejected User approval request ->  Type : %s", userApprovalRequest.getUserApprovalRequestType());
        auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                springSecurityAuditorAware.getCurrentAuditorNotNull(), userApprovalRequest.getSubjectUserName(),
                LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
        return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
    } catch(
    Exception e)

    {
        return logAndReturnError(logger, "An error occurred while approving user approval request ", e);
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
            Set<String> userSpecificPermissionIdsForUser = user.getAuthPermissionIds();
            for (String newPermissionId : newPermissionIds) {
                AuthPermission authPermission = userApprovalRequest.getAuthPermission(newPermissionId);
                if (authPermission != null && !allUserPermissionIdsForUser.contains(newPermissionId)) {
                    userSpecificPermissionIdsForUser.add(newPermissionId);
                }
            }
            user.setAuthPermissionIds(userSpecificPermissionIdsForUser);
            mongoRepositoryReactive.saveOrUpdate(user);
        }
    }

    private void approveRemovePermissionFromUser(UserApprovalRequest userApprovalRequest) {
        String userId = userApprovalRequest.getAuthInfoId();
        Set<String> removedPermissionIds = userApprovalRequest.getRemovedPermissionIds();
        AuthInfo user = userApprovalRequest.getAuthInfo(userId);
        if (user != null) {
            Set<String> userMappedPermissions = user.getAuthPermissionIds();
            for (String removedPermissionId : removedPermissionIds) {
                AuthPermission authPermission = userApprovalRequest.getAuthPermission(removedPermissionId);
                if (authPermission != null) {
                    userMappedPermissions.remove(removedPermissionId);
                }
            }
            user.setAuthPermissionIds(userMappedPermissions);
            mongoRepositoryReactive.saveOrUpdate(user);
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

    private boolean canApproveRequest(AuthInfo authInfo) {
        return authInfo.getEnabled() && getAllowedRoleIdsForApprovals().contains(authInfo.getAuthRoleId());
    }

    private List<String> getAllowedRoleIdsForApprovals() {
        List<String> allowedRoleIds = new ArrayList<>();
        allowedRoleIds.add(AuthRoleReferenceData.SUPER_ADMIN_ID);
        allowedRoleIds.add(AuthRoleReferenceData.VGG_ADMIN_ID);
        allowedRoleIds.add(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID);
        return allowedRoleIds;
    }

    private AuthInfo getAuthInfoById(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }
}