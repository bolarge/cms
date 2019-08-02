package com.software.finatech.lslb.cms.service.dto;

import java.util.HashSet;
import java.util.Set;

public class AuthPermissionDto extends EnumeratedFactDto {
    private Boolean usedBySystem;
    private String authRoleName;
    private String authRoleId;
    private Set<AuthRoleDto> authRoles = new HashSet<>();

    public Set<AuthRoleDto> getAuthRoles() {
        return authRoles;
    }

    public void setAuthRoles(Set<AuthRoleDto> authRoles) {
        this.authRoles = authRoles;
    }

    public String getAuthRoleName() {
        return authRoleName;
    }

    public void setAuthRoleName(String authRoleName) {
        this.authRoleName = authRoleName;
    }

    public String getAuthRoleId() {
        return authRoleId;
    }

    public void setAuthRoleId(String authRoleId) {
        this.authRoleId = authRoleId;
    }


    public Boolean getUsedBySystem() {
        return usedBySystem;
    }

    public void setUsedBySystem(Boolean usedBySystem) {
        this.usedBySystem = usedBySystem;
    }
}
