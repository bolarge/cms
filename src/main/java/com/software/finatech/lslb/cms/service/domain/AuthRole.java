package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.dto.AuthRoleDto;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @David Jaiyeola
 */
@SuppressWarnings("serial")
@Document(collection = "AuthRoles")
public class AuthRole extends EnumeratedFact {
    protected String ssoRoleMapping;
    protected Set<String> authPermissionIds = new java.util.HashSet<>();

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

    @Override
    public String getFactName() {
        return "AuthRole";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private AuthPermission getAuthPermission(String authPermissionId) {
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
            Mapstore.STORE.get("AuthPermission").put(authPermission.getId(), authPermission);
        }
        return authPermission;
    }

    private Set<AuthPermissionDto> getAuthPermissionDtos() {
        Set<AuthPermissionDto> authPermissionDtos = new HashSet<>();
        for (String permissionId : authPermissionIds) {
            AuthPermission authPermission = getAuthPermission(permissionId);
            if (authPermission != null) {
                authPermissionDtos.add(authPermission.convertToDto());
            }
        }
        return authPermissionDtos;
    }

    public AuthRoleDto convertToDto() {
        AuthRoleDto authRoleDto = convertToHalfDto();
        authRoleDto.setAuthPermissions(getAuthPermissionDtos());
        return authRoleDto;
    }

    public AuthRoleDto convertToHalfDto() {
        AuthRoleDto authRoleDto = new AuthRoleDto();
        authRoleDto.setCode(getCode());
        authRoleDto.setDescription(getDescription());
        authRoleDto.setName(getName());
        authRoleDto.setSsoRoleMapping(getSsoRoleMapping());
        authRoleDto.setId(getId());
        return authRoleDto;
    }

    public boolean isSSOClientAdmin() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.SSO_CLIENT_ADMIN, this.ssoRoleMapping);
    }
}
