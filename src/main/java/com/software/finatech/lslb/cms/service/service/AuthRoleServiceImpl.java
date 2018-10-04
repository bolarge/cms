package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthRoleService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthRoleServiceImpl implements AuthRoleService {
    private static final Logger logger = LoggerFactory.getLogger(AuthInfoServiceImpl.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Override
    public AuthRole findRoleById(String authRoleId) {
        if (StringUtils.isEmpty(authRoleId)) {
            return null;
        }
        Map authRoleMap = Mapstore.STORE.get("AuthRole");
        AuthRole authRole = null;
        if (authRoleMap != null) {
            authRole = (AuthRole) authRoleMap.get(authRoleId);
        }
        if (authRole == null) {
            authRole = (AuthRole) mongoRepositoryReactive.findById(authRoleId, AuthRole.class).block();
            if (authRole != null && authRoleMap != null) {
                authRoleMap.put(authRoleId, authRole);
            }
        }
        return authRole;
    }

    public Mono<ResponseEntity> getAllPermissions() {
        try {
            ArrayList<AuthPermission> authPermissions = (ArrayList<AuthPermission>) mongoRepositoryReactive.findAll(new Query(), AuthPermission.class).toStream().collect(Collectors.toList());
            if (authPermissions == null || authPermissions.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
            for (AuthPermission authPermission : authPermissions) {
                enumeratedFactDtos.add(authPermission.convertToDto());
            }
            return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while getting all permissions on system", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllCodePermissions() {
        try {
            List<String> codePermissions = LSLBAuthPermissionReferenceData.getCodeUsedPermissions();
            if (codePermissions == null || codePermissions.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
            for (String permissionId : codePermissions) {
                AuthPermission permission = findAuthPermissionById(permissionId);
                if (permission != null) {
                    enumeratedFactDtos.add(permission.convertToDto());
                }
            }
            return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while getting all permissions used in code", e);
        }
    }

    @Override
    public AuthPermission findAuthPermissionById(String authPermissionId) {
        AuthPermission authPermission = (AuthPermission) Mapstore.STORE.get("AuthPermission").get(authPermissionId);
        if (authPermission == null) {
            authPermission = (AuthPermission) mongoRepositoryReactive.findById(authPermissionId, AuthPermission.class).block();
            if (authPermission != null) {
                Mapstore.STORE.get("AuthPermission").put(authPermission.getId(), authPermission);
            }
        }
        return authPermission;
    }
}

