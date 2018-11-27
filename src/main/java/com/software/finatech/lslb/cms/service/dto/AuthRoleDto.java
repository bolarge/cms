package com.software.finatech.lslb.cms.service.dto;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuthRoleDto extends EnumeratedFactDto{
    protected String ssoRoleMapping;
    protected List<AuthPermissionDto> authPermissions = new ArrayList<>();

    public String getSsoRoleMapping() {
        return ssoRoleMapping;
    }

    public void setSsoRoleMapping(String ssoRoleMapping) {
        this.ssoRoleMapping = ssoRoleMapping;
    }

    public List<AuthPermissionDto> getAuthPermissions() {
        return authPermissions;
    }

    public void setAuthPermissions(List<AuthPermissionDto> authPermissions) {
        this.authPermissions = authPermissions;
    }
}
