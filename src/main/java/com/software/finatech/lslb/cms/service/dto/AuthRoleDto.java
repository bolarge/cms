package com.software.finatech.lslb.cms.service.dto;


import java.util.Set;

public class AuthRoleDto extends EnumeratedFactDto{
    protected String ssoRoleMapping;
    protected Set<AuthPermissionDto> authPermissions = new java.util.HashSet<>();

    public String getSsoRoleMapping() {
        return ssoRoleMapping;
    }

    public void setSsoRoleMapping(String ssoRoleMapping) {
        this.ssoRoleMapping = ssoRoleMapping;
    }

    public Set<AuthPermissionDto> getAuthPermissions() {
        return authPermissions;
    }

    public void setAuthPermissions(Set<AuthPermissionDto> authPermissions) {
        this.authPermissions = authPermissions;
    }
}
