package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.controller.AuditTrailController;
import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.dto.UserApprovalRequestDto;
import com.software.finatech.lslb.cms.service.referencedata.UserApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("serial")
@Document(collection = "UserApprovalRequests")
public class UserApprovalRequest extends AbstractApprovalRequest {

    private static Logger logger = LoggerFactory.getLogger(UserApprovalRequest.class);

    private String newAuthRoleId;
    private String pendingAuthInfoId;
    private String authInfoId;
    private String userApprovalRequestTypeId;
    private String approvalReason;
    private Set<String> newPermissionIds = new HashSet<>();
    private Set<String> removedPermissionIds = new HashSet<>();

    public String getAuthInfoId() {
        return authInfoId;
    }

    public void setAuthInfoId(String authInfoId) {
        this.authInfoId = authInfoId;
    }

    public String getNewAuthRoleId() {
        return newAuthRoleId;
    }

    public void setNewAuthRoleId(String newAuthRoleId) {
        this.newAuthRoleId = newAuthRoleId;
    }

    public String getPendingAuthInfoId() {
        return pendingAuthInfoId;
    }

    public void setPendingAuthInfoId(String pendingAuthInfoId) {
        this.pendingAuthInfoId = pendingAuthInfoId;
    }

    public String getUserApprovalRequestTypeId() {
        return userApprovalRequestTypeId;
    }

    public void setUserApprovalRequestTypeId(String userApprovalRequestTypeId) {
        this.userApprovalRequestTypeId = userApprovalRequestTypeId;
    }

    public Set<String> getNewPermissionIds() {
        return newPermissionIds;
    }

    public void setNewPermissionIds(Set<String> newPermissionIds) {
        this.newPermissionIds = newPermissionIds;
    }

    public Set<String> getRemovedPermissionIds() {
        return removedPermissionIds;
    }

    public void setRemovedPermissionIds(Set<String> removedPermissionIds) {
        this.removedPermissionIds = removedPermissionIds;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public UserApprovalRequestType getUserApprovalRequestType() {
        if (StringUtils.isEmpty(this.userApprovalRequestTypeId)) {
            return null;
        }
        UserApprovalRequestType userApprovalRequestType = null;
        Map userApprovalRequestTypeMap = Mapstore.STORE.get("UserApprovalRequestType");
        if (userApprovalRequestTypeMap != null) {
            userApprovalRequestType = (UserApprovalRequestType) userApprovalRequestTypeMap.get(this.userApprovalRequestTypeId);
        }
        if (userApprovalRequestType == null) {
            userApprovalRequestType = (UserApprovalRequestType) mongoRepositoryReactive.findById(this.userApprovalRequestTypeId, UserApprovalRequestType.class).block();
            if (userApprovalRequestType != null && userApprovalRequestTypeMap != null) {
                userApprovalRequestTypeMap.put(this.userApprovalRequestTypeId, userApprovalRequestType);
            }
        }
        return userApprovalRequestType;
    }

    public AuthInfo getAuthInfo(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    public PendingAuthInfo getPendingAuthInfo(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (PendingAuthInfo) mongoRepositoryReactive.findById(userId, PendingAuthInfo.class).block();
    }

    public ApprovalRequestStatus getApprovalRequestStatus() {
        if (StringUtils.isEmpty(this.approvalRequestStatusId)) {
            return null;
        }
        Map approvalRequestStatusMap = Mapstore.STORE.get("ApprovalRequestStatus");
        ApprovalRequestStatus approvalRequestStatus = null;
        if (approvalRequestStatusMap != null) {
            approvalRequestStatus = (ApprovalRequestStatus) approvalRequestStatusMap.get(this.approvalRequestStatusId);
        }
        if (approvalRequestStatus == null) {
            approvalRequestStatus = (ApprovalRequestStatus) mongoRepositoryReactive.findById(this.approvalRequestStatusId, ApprovalRequestStatus.class).block();
            if (approvalRequestStatus != null && approvalRequestStatusMap != null) {
                approvalRequestStatusMap.put(this.approvalRequestStatusId, approvalRequestStatus);
            }
        }
        return approvalRequestStatus;
    }

    public AuthPermission getAuthPermission(String authPermissionId) {
        if (StringUtils.isEmpty(authPermissionId)) {
            return null;
        }
        AuthPermission authPermission = null;
        Map authPermissionMap = Mapstore.STORE.get("AuthPermission");
        if (authPermissionMap != null) {
            authPermission = (AuthPermission) authPermissionMap.get(authPermissionId);
        }
        if (authPermission == null)
            authPermission = (AuthPermission) mongoRepositoryReactive.findById(authPermissionId, AuthPermission.class).block();
        if (authPermission != null && authPermissionMap != null) {
            authPermissionMap.put(authPermission.getId(), authPermission);
        }
        return authPermission;
    }

    private Set<AuthPermissionDto> getAuthPermissionDtos(Set<String> authPermissionIds) {
        Set<AuthPermissionDto> permissionDtos = new HashSet<>();
        for (String permissionId : authPermissionIds) {
            AuthPermission permission = getAuthPermission(permissionId);
            if (permission != null) {
                permissionDtos.add(permission.convertToDto());
            }
        }
        return permissionDtos;
    }

    public UserApprovalRequestDto convertToHalfDto() {
        UserApprovalRequestDto dto = new UserApprovalRequestDto();
        dto.setId(getId());
        ApprovalRequestStatus approvalRequestStatus = getApprovalRequestStatus();
        if (approvalRequestStatus != null) {
            dto.setRequestStatusId(approvalRequestStatus.getId());
            dto.setRequestStatusName(approvalRequestStatus.getName());
        }

        UserApprovalRequestType approvalRequestType = getUserApprovalRequestType();
        if (approvalRequestType != null) {
            dto.setRequestTypeId(approvalRequestType.getId());
            dto.setRequestTypeName(approvalRequestType.getName());
        }

        AuthInfo initiator = getAuthInfo(this.initiatorId);
        if (initiator != null) {
            dto.setInitiatorId(initiator.getId());
            dto.setInitiatorName(initiator.getFullName());
        }

        AuthInfo authInfo = getAuthInfo(this.authInfoId);
        if (authInfo != null) {
            dto.setSubjectUserName(authInfo.getFullName());
        }

        PendingAuthInfo pendingAuthInfo = getPendingAuthInfo(this.pendingAuthInfoId);
        if (pendingAuthInfo != null) {
            if (!StringUtils.isEmpty(pendingAuthInfo.getFullName())) {
                dto.setSubjectUserName(pendingAuthInfo.getFullName());
            } else {
                dto.setSubjectUserName(pendingAuthInfo.getFirstName() + " " + pendingAuthInfo.getLastName());
            }
        }
        LocalDateTime dateCreated = getDateCreated();
        if (dateCreated != null) {
            dto.setDateCreated(dateCreated.toString("dd-MM-yyyy hh:mm a"));
        }
        return dto;
    }


    public UserApprovalRequestDto convertToFullDto() {
        UserApprovalRequestDto dto = convertToHalfDto();
        dto.setNewPermissions(getAuthPermissionDtos(this.getNewPermissionIds()));
        dto.setRemovedPermissions(getAuthPermissionDtos(this.removedPermissionIds));
        PendingAuthInfo pendingAuthInfo = getPendingAuthInfo(this.pendingAuthInfoId);
        if (pendingAuthInfo != null) {
            dto.setPendingUser(pendingAuthInfo.convertToDto());
        }
        AuthInfo authInfo = getAuthInfo(this.authInfoId);
        if (authInfo != null) {
            dto.setPendingUser(authInfo.convertToDto());
        }
        AuthRole newRole = getAuthRole(this.newAuthRoleId);
        if (newRole != null) {
            dto.setNewRoleName(newRole.getName());
            dto.setNewRoleId(newRole.getId());
        }

        AuthInfo approver = getAuthInfo(this.approverId);
        if (approver != null) {
            dto.setApproverId(this.approverId);
            dto.setApproverName(approver.getFullName());
        }

        AuthInfo rejector = getAuthInfo(this.rejectorId);
        if (rejector != null) {
            dto.setRejectorId(this.rejectorId);
            dto.setRejectorName(rejector.getFullName());
        }
        return dto;
    }


    public AuthRole getAuthRole(String authRoleId) {
        if (StringUtils.isEmpty(authRoleId)) {
            return null;
        }
        Map authRoleMap = Mapstore.STORE.get("AuthRole");
        AuthRole authRole = null;
        if (authRoleMap != null) {
            authRole = (AuthRole) authRoleMap.get(authRoleId);
        }
        if (authRole == null) {
            authRole = (AuthRole) mongoRepositoryReactive.findById(authRoleId, AuthRole.class).block();
            if (authRole != null && authRoleMap != null) {
                authRoleMap.put(authRole.getId(), authRole);
            }
        }
        return authRole;
    }


    public boolean isCreateUser() {
        return StringUtils.equals(UserApprovalRequestTypeReferenceData.CREATE_USER_ID, this.userApprovalRequestTypeId);
    }

    public boolean isAddPermissionToUser() {
        return StringUtils.equals(UserApprovalRequestTypeReferenceData.ADD_PERMISSION_TO_USER_ID, this.userApprovalRequestTypeId);
    }

    public boolean isRemovePermissionFromUser() {
        return StringUtils.equals(UserApprovalRequestTypeReferenceData.REMOVE_PERMISSION_FROM_USER_ID, this.userApprovalRequestTypeId);
    }

    public boolean isUpdateUserRole() {
        return StringUtils.equals(UserApprovalRequestTypeReferenceData.CHANGE_USER_ROLE_ID, this.userApprovalRequestTypeId);
    }

    public boolean isEnableUser() {
        return StringUtils.equals(UserApprovalRequestTypeReferenceData.ACTIVATE_USER_ID, this.userApprovalRequestTypeId);
    }

    public boolean isDisableUser() {
        return StringUtils.equals(UserApprovalRequestTypeReferenceData.DEACTIVATE_USER_ID, this.userApprovalRequestTypeId);
    }

    public String getSubjectUserName() {
        if (isCreateUser()) {
            PendingAuthInfo pendingAuthInfo = getPendingAuthInfo(this.pendingAuthInfoId);
            if (pendingAuthInfo != null) {
                return pendingAuthInfo.getFullName();
            }
        }
        if (isAddPermissionToUser() || isRemovePermissionFromUser() || isUpdateUserRole()
                || isDisableUser() || isEnableUser()) {
            AuthInfo authInfo = getAuthInfo(this.authInfoId);
            if (authInfo != null) {
                return authInfo.getFullName();
            }
        }
        return null;
    }

    @Override
    public String getFactName() {
        return "UserApprovalRequests";
    }
}
