package com.software.finatech.lslb.cms.service.dto;

import java.util.HashSet;
import java.util.Set;

public class AuthInfoDto {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String phoneNumber;
    protected String fullName;
    protected Boolean enabled;
    protected Boolean accountLocked;
    protected String emailAddress;
    protected String authRoleId;
    protected String attachmentId;
    protected String institutionId;
    protected String ssoUserId;
    protected AuthRoleDto authRole;
    protected  String gameTypeId;
    protected String gameTypeName;
    protected String institutionName;
    protected String agentId;
    private Set<AuthPermissionDto> rolePermissions = new HashSet<>();
    private Set<AuthPermissionDto>  userPermissions = new HashSet<>();

    public Set<AuthPermissionDto> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<AuthPermissionDto> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public Set<AuthPermissionDto> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(Set<AuthPermissionDto> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public AuthRoleDto getAuthRole() {
        return authRole;
    }

    public void setAuthRole(AuthRoleDto authRole) {
        this.authRole = authRole;
    }

    public String getSsoUserId() {
        return ssoUserId;
    }

    public void setSsoUserId(String ssoUserId) {
        this.ssoUserId = ssoUserId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAuthRoleId() {
        return authRoleId;
    }

    public void setAuthRoleId(String authRoleId) {
        this.authRoleId = authRoleId;
    }
}
