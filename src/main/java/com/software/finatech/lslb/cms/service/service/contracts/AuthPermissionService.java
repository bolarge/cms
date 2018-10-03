package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthPermission;

public interface AuthPermissionService {
    AuthPermission findAuthPermissionById(String authPermissionId);
}
