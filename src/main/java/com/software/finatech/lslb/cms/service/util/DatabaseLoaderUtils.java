package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.GameTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthPermissionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by djaiyeola on 9/21/17.
 */
@Component("databaseLoaderUtils")
public class DatabaseLoaderUtils {
    private static Logger logger = LoggerFactory.getLogger(DatabaseLoaderUtils.class);
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
    protected io.advantageous.boon.json.ObjectMapper mapper;

    public void runSeedData(){
        //Seed AuthInfo
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById("1",AuthInfo.class).block();
        if(authInfo==null){
            authInfo = new AuthInfo();
            authInfo.setId("1");
        }
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId("2");
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress("David_J");
        authInfo.setAttachmentId(null);
        authInfo.setPhoneNumber("");
        authInfo.setFirstName("David");
        authInfo.setLastName("Jaiyeola");
        authInfo.setFullName("David" + " " + "Jaiyeola");
        authInfo.setInstitutionId(null);
        mongoRepositoryReactive.saveOrUpdate(authInfo);

        AuthRoleReferenceData.load(mongoRepositoryReactive);
        LSLBAuthPermissionReferenceData.load(mongoRepositoryReactive);
        LSLBAuthRoleReferenceData.load(mongoRepositoryReactive);
        GameTypeReferenceData.load(mongoRepositoryReactive);
    }

   // @Profile("test")
    public void runLoadTestData(){
        //TestData.generateInvoices(mongoRepositoryReactive,auditLogHelper,TestData.generateHealthInstitution(mongoRepositoryReactive),1000);
    }

    public void generateAuthTestData(){
        TestData.generateAuthTestData(mongoRepositoryReactive);
    }

    // @PostConstruct
    public void runLoadData(){

        ConcurrentHashMap<String, Class> factEnums = new ConcurrentHashMap<>();
        //factEnums.put("AccountRole",AccountRole.class);
        factEnums.put("AuthRole",AuthRole.class);

        factEnums.put("AuthPermission",AuthPermission.class);
        factEnums.put("GameType", GameType.class);

        for (Map.Entry<String, Class> entry : factEnums.entrySet()) {
            logger.info("Importing ReferenceMasterData for > "+ entry.getKey());
            Long startTime = System.nanoTime();
            HashSet<FactObject> factObjects  =
                    (HashSet<FactObject>) mongoRepositoryReactive.findAll(entry.getValue()).toStream().collect(Collectors.toSet());

            ConcurrentHashMap<String, FactObject> facts =  new ConcurrentHashMap<>();
            factObjects.forEach(fact-> {
                facts.put(fact.getId(),fact);
            });

            Mapstore.STORE.put(entry.getKey(),facts);
            Long endTime = System.nanoTime() - startTime;
            Double timeMills = (double) (Double.valueOf(endTime)/Double.valueOf(1000000));
            logger.info("Importing took "+" -> "+ endTime + "ns" + " >>> " + timeMills +"ms");
        }

    }
}
