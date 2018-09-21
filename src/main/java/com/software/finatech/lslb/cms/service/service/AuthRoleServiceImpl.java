package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.AuthRoleService;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthRoleServiceImpl implements AuthRoleService {
    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Override
    public AuthRole findRoleById(String authRoleId) {
        if (StringUtils.isEmpty(authRoleId)){
            return null;
        }
        Map authRoleMap = Mapstore.STORE.get("AuthRole");
        AuthRole authRole = null;
        if (authRoleMap != null) {
            authRole = (AuthRole) authRoleMap.get(authRoleId);
        }
        if (authRole == null) {
                authRole = (AuthRole) mongoRepositoryReactive.findById(authRoleId, AuthRole.class).block();
                if (authRole != null && authRoleMap != null){
                authRoleMap.put(authRoleId, authRole);
                }
            }
            return authRole;
        }
    }

