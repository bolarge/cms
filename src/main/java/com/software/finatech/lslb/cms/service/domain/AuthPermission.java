package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * CREATE, UPDATE, DELETE,READ,ROOT
 */
@SuppressWarnings("serial")
@Document(collection = "AuthPermissions")
public class AuthPermission extends EnumeratedFact {

    private String authRoleId;
    private boolean usedBySystem;

    public String getAuthRoleId() {
        return authRoleId;
    }

    public void setAuthRoleId(String authRoleId) {
        this.authRoleId = authRoleId;
    }

    public boolean isUsedBySystem() {
        return usedBySystem;
    }

    public void setUsedBySystem(boolean usedBySystem) {
        this.usedBySystem = usedBySystem;
    }

    @Override
    public String getFactName() {
        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public AuthPermissionDto convertToDto() {
        AuthPermissionDto authPermissionDto = new AuthPermissionDto();
        authPermissionDto.setCode(getCode());
        authPermissionDto.setDescription(getDescription());
        authPermissionDto.setName(getName());
        authPermissionDto.setId(getId());
        authPermissionDto.setUsedBySystem(isUsedBySystem());
        AuthRole role = getAuthRole();
        if (role != null) {
            authPermissionDto.setAuthRoleId(this.authRoleId);
            authPermissionDto.setAuthRoleName(role.getName());
        }
        return authPermissionDto;
    }

    public AuthRole getAuthRole() {
        if (StringUtils.isEmpty(this.authRoleId)) {
            return null;
        }
        Map authRoleMap = Mapstore.STORE.get("AuthRole");
        AuthRole authRole = null;
        if (authRoleMap != null) {
            authRole = (AuthRole) authRoleMap.get(this.authRoleId);
        }
        if (authRole == null) {
            authRole = (AuthRole) mongoRepositoryReactive.findById(this.authRoleId, AuthRole.class).block();
            if (authRole != null && authRoleMap != null) {
                authRoleMap.put(authRole.getId(), authRole);
            }
        }
        return authRole;
    }
}

