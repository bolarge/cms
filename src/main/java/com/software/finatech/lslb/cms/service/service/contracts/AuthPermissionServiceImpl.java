package com.software.finatech.lslb.cms.service.service.contracts;


import com.software.finatech.lslb.cms.service.domain.AuthPermission;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthPermissionServiceImpl implements AuthPermissionService {

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
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
