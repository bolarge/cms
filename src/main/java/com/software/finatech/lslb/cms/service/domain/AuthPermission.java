package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CREATE, UPDATE, DELETE,READ,ROOT
 */
@SuppressWarnings("serial")
@Document(collection = "AuthPermissions")
public class AuthPermission extends EnumeratedFact  {

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

		return authPermissionDto;
	}
}
