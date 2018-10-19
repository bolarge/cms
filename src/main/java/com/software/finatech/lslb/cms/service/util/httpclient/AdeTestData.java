package com.software.finatech.lslb.cms.service.util.httpclient;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;

import java.util.HashSet;
import java.util.Set;

public class AdeTestData {

    public static void LoadTestData(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        Institution institution = new Institution();
        institution.setInstitutionName("NairaBet");
        institution.setId("1234");
        institution.setEmailAddress("adeboludeyi@gmail.com");
        institution.setPhoneNumber("08109481835");
        Set<String> gameTypesIds = new HashSet<>();
        gameTypesIds.add("01");
        institution.setGameTypeIds(gameTypesIds);
        institution.setDescription("NairaBet Nigeria");
        institution.setAddress("109 Rasheed Baruwa Street Ikeja");
       // mongoRepositoryReactive.saveOrUpdate(institution);


        AuthInfo gAdmin1 = new AuthInfo();
        gAdmin1.setId("1234");
        gAdmin1.setEnabled(true);
        gAdmin1.setEmailAddress("adeboludeyi@gmail.com");
        gAdmin1.setPhoneNumber("08109481835");
        gAdmin1.setFirstName("Adeyiwunmi");
        gAdmin1.setTitle("Mr");
        gAdmin1.setLastName("Adebolu");
        gAdmin1.setEnabled(true);
        gAdmin1.setAuthRoleId(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID);
        gAdmin1.setFullName("Adeyiwunmi Adebolu");
        gAdmin1.setInstitutionId("1234");
      //  mongoRepositoryReactive.saveOrUpdate(gAdmin1);

        AuthInfo gAdmin2 = new AuthInfo();
        gAdmin2.setId("123");
        gAdmin2.setEnabled(true);
        gAdmin2.setEmailAddress("adeboludeyi@gmail.com");
        gAdmin2.setPhoneNumber("08109481835");
        gAdmin2.setFirstName("Omolola");
        gAdmin2.setTitle("Miss");
        gAdmin2.setLastName("Akande");
        gAdmin2.setEnabled(true);
        gAdmin2.setAuthRoleId(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID);
        gAdmin2.setFullName("Adeyiwunmi Adebolu");
        gAdmin2.setInstitutionId("1234");
     //   mongoRepositoryReactive.saveOrUpdate(gAdmin2);


        Agent agent = new Agent();
        agent.setId("1234");
        agent.setPhoneNumber("08109481835");
        agent.setFirstName("Adeyiwunmi");
        agent.setLastName("Adebolu");
        agent.setEmailAddress("Adeboludeyi@gmail.com");
        agent.setTitle("Mr");
        agent.setFullName("Adeyiwunmi Adebolu");
        agent.setResidentialAddress("Block 30 Shitta Surulere");
   //     mongoRepositoryReactive.saveOrUpdate(agent);
    }
}
