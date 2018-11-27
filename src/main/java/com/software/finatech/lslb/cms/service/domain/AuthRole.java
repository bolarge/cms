package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.dto.AuthRoleDto;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

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

    private List<AuthPermissionDto> getAuthPermissionDtos() {
        List<AuthPermissionDto> authPermissionDtos = new ArrayList<>();

        for (String permissionId : authPermissionIds) {
            AuthPermission authPermission = getAuthPermission(permissionId);
            if (authPermission != null) {
                authPermissionDtos.add(authPermission.convertToDto());
            }
        }
        authPermissionDtos.sort(permissionDtoComparator);
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

    @Transient
    private Comparator<AuthPermissionDto> permissionDtoComparator = new Comparator<AuthPermissionDto>() {
        @Override
        public int compare(AuthPermissionDto o1, AuthPermissionDto o2) {
            return StringUtils.compare(o1.getName(), o2.getName());
        }
    };

    public boolean isSSOClientAdmin() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.SSO_CLIENT_ADMIN, this.ssoRoleMapping);
    }
}
