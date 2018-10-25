package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;

import java.util.HashSet;
import java.util.Set;

public class UserApprovalRequestDto extends AbstractApprovalRequestDto {
    private AuthInfo pendingUser;
    private Set<AuthPermissionDto> newPermissions = new HashSet<>();
    private Set<AuthPermissionDto> removedPermissions = new HashSet<>();
    private String subjectUserName;
    private String newRoleId;
    private String newRoleName;

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

    public String getSubjectUserName() {
        return subjectUserName;
    }

    public void setSubjectUserName(String subjectUserName) {
        this.subjectUserName = subjectUserName;
    }
}
