package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class AuthRoleCreateDto {

    @NotEmpty(message = "Please provide Role Name")
    protected String name;
    @NotEmpty(message = "Please provide Role Description")
    protected String description;
    protected String code;
    @NotEmpty(message = "Please provide SSO Mapping")
    protected String ssoRoleMapping;
    protected Set<String> authPermissionIds = new java.util.HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSsoRoleMapping() {
        return ssoRoleMapping;
    }

    public void setSsoRoleMapping(String ssoRoleMapping) {
        this.ssoRoleMapping = ssoRoleMapping;
    }

    public Set<String> getAuthPermissionIds() {
        return authPermissionIds;
    }

    public void setAuthPermissionIds(Set<String> authPermissionIds) {
        this.authPermissionIds = authPermissionIds;
    }
}
