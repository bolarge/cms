package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;

import java.util.HashSet;
import java.util.Set;

public class UserApprovalRequestDto {
    private String id;
    private AuthInfo pendingUser;
    private Set<AuthPermissionDto> newPermissions = new HashSet<>();
    private Set<AuthPermissionDto> removedPermissions = new HashSet<>();
    private String userApprovalRequestTypeId;
    private String userApprovalRequestTypeName;
    private String approvalRequestStatusId;
    private String approvalRequestStatusName;
    private String approverName;
    private String approverId;
    private String rejectorId;
    private String rejectorName;
    private String initiatorId;
    private String initiatorName;
    private String subjectUserName;
    private String newRoleId;
    private String newRoleName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewRoleId() {
        return newRoleId;
    }

    public void setNewRoleId(String newRoleId) {
        this.newRoleId = newRoleId;
    }

    public String getNewRoleName() {
        return newRoleName;
    }

    public void setNewRoleName(String newRoleName) {
        this.newRoleName = newRoleName;
    }

    public AuthInfo getPendingUser() {
        return pendingUser;
    }

    public void setPendingUser(AuthInfo pendingUser) {
        this.pendingUser = pendingUser;
    }

    public Set<AuthPermissionDto> getNewPermissions() {
        return newPermissions;
    }

    public void setNewPermissions(Set<AuthPermissionDto> newPermissions) {
        this.newPermissions = newPermissions;
    }

    public Set<AuthPermissionDto> getRemovedPermissions() {
        return removedPermissions;
    }

    public void setRemovedPermissions(Set<AuthPermissionDto> removedPermissions) {
        this.removedPermissions = removedPermissions;
    }

    public String getUserApprovalRequestTypeId() {
        return userApprovalRequestTypeId;
    }

    public void setUserApprovalRequestTypeId(String userApprovalRequestTypeId) {
        this.userApprovalRequestTypeId = userApprovalRequestTypeId;
    }

    public String getUserApprovalRequestTypeName() {
        return userApprovalRequestTypeName;
    }

    public void setUserApprovalRequestTypeName(String userApprovalRequestTypeName) {
        this.userApprovalRequestTypeName = userApprovalRequestTypeName;
    }

    public String getApprovalRequestStatusId() {
        return approvalRequestStatusId;
    }

    public void setApprovalRequestStatusId(String approvalRequestStatusId) {
        this.approvalRequestStatusId = approvalRequestStatusId;
    }

    public String getApprovalRequestStatusName() {
        return approvalRequestStatusName;
    }

    public void setApprovalRequestStatusName(String approvalRequestStatusName) {
        this.approvalRequestStatusName = approvalRequestStatusName;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }

    public String getRejectorName() {
        return rejectorName;
    }

    public void setRejectorName(String rejectorName) {
        this.rejectorName = rejectorName;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getSubjectUserName() {
        return subjectUserName;
    }

    public void setSubjectUserName(String subjectUserName) {
        this.subjectUserName = subjectUserName;
    }
}
