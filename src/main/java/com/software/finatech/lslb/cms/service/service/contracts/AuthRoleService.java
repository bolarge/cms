package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.domain.AuthRole;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthRoleService {

    AuthRole findRoleById(String authRoleId);

    AuthPermission findAuthPermissionById(String authPermissionId);
}
