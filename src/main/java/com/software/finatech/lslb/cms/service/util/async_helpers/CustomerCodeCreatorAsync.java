package com.software.finatech.lslb.cms.service.util.async_helpers;


import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerCodeCreatorAsync {
    private static final Logger logger = LoggerFactory.getLogger(CustomerCodeCreatorAsync.class);

    private VigipayService vigipayService;
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;

    @Autowired
    public CustomerCodeCreatorAsync(VigipayService vigipayService,
                                    AuthInfoService authInfoService,
                                    MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.vigipayService = vigipayService;
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.authInfoService = authInfoService;
    }

    @Async
    public void createVigipayCustomerCodeForInstitution(Institution institution) {
        try {
            logger.info("Trying to get customer code for institution {} -> {}", institution.getInstitutionName(), institution.getInstitutionName());
            List<AuthInfo> gamingOperatorUsers = authInfoService.findAllEnabledUsersForInstitution(institution.getId());
            if (gamingOperatorUsers.isEmpty()) {
                logger.info("Institution does not have gaming operator users");
                return;
            }
            String customerCode = vigipayService.createCustomerCodeForInstitution(institution);
            if (!StringUtils.isEmpty(customerCode)) {
                logger.info("Gotten customer code {}", customerCode);
                institution.setVgPayCustomerCode(customerCode);
                mongoRepositoryReactive.saveOrUpdate(institution);
                logger.info("Finished for institution {} -> {}", institution.getInstitutionName(), institution.getId());
            } else {
                logger.info("Unable to oget customer code for institution {} -> {}", institution.getInstitutionName(), institution.getInstitutionName());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Async
    public void createVigipayCustomerCodeForAgent(Agent agent) {
        try {
            logger.info("Trying to get customer code for agent {} -> {}", agent.getFullName(), agent.getId());
            String customerCode = vigipayService.createCustomerCodeForAgent
                    (agent);
            if (!StringUtils.isEmpty(customerCode)) {
                logger.info("Gotten customer code {}", customerCode);
                agent.setVgPayCustomerCode(customerCode);
                mongoRepositoryReactive.saveOrUpdate(agent);
                logger.info("Finished for agent {} -> {}", agent.getFullName(), agent.getId());
            } else {
                logger.info("Unable to get customer code for agent {} -> {}", agent.getFullName(), agent.getId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
