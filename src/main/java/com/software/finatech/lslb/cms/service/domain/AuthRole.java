package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AuthRoleDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * @David Jaiyeola
 */
@SuppressWarnings("serial")
@Document(collection = "AuthRoles")
public class AuthRole  extends EnumeratedFact  {
	protected String ssoRoleMapping;
	protected Set<String> authPermissionIds = new java.util.HashSet<>();

	@Transient
	protected Set<AuthPermission> authPermissions = new java.util.HashSet<>();

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

	public Set<AuthPermission> getAuthPermissions() {
		return authPermissions;
	}

	public void setAuthPermissions(Set<AuthPermission> authPermissions) {
		this.authPermissions = authPermissions;
	}

	@Override
	public String getFactName() {
		return "AuthRole";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void setAssociatedProperties() throws FactNotFoundException {
		if (authPermissionIds.size()>0) {
			authPermissionIds.stream().forEach(authPermissionId->{
				AuthPermission authPermission = (AuthPermission) Mapstore.STORE.get("AuthPermission").get(authPermissionId);
				if (authPermission == null) {
					authPermission = (AuthPermission) mongoRepositoryReactive.findById(authPermissionId, AuthPermission.class).block();
					if (authPermission == null) {
						try {
							throw new FactNotFoundException("AuthPermission", authPermissionId);
						} catch (FactNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						Mapstore.STORE.get("AuthPermission").put(authPermission.getId(), authPermission);
					}
				}
				getAuthPermissions().add(authPermission);

			});

			//setAuthRole(authPer);
		}
	}

	public AuthRoleDto convertToDto() {
		AuthRoleDto authRoleDto = new AuthRoleDto();
		authRoleDto.setCode(getCode());
		authRoleDto.setDescription(getDescription());
		authRoleDto.setName(getName());
		authRoleDto.setSsoRoleMapping(getSsoRoleMapping());
		authRoleDto.setId(getId());
		getAuthPermissions().stream().forEach(entry->{
			authRoleDto.getAuthPermissions().add(entry.convertToDto());
		});

		return authRoleDto;
	}
}
