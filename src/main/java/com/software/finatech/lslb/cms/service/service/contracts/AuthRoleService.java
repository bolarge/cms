package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthRole;

public interface AuthRoleService {

    AuthRole findRoleById(String authRoleId);
}
