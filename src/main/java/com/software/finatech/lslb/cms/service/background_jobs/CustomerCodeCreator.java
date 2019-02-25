package com.software.finatech.lslb.cms.service.background_jobs;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerCodeCreator {

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    private AuthInfoService authInfoService;
    @Autowired
    private VigipayService vigipayService;

    private static final int FIVE_MIN = 5 * 60 * 1000;

    private static final Logger logger = LoggerFactory.getLogger(CustomerCodeCreator.class);


    @Scheduled(fixedRate = 3000,initialDelay = 500)
    @SchedulerLock(name = "Create Customer Code for Institutions and Agents without Customer code", lockAtMostFor = FIVE_MIN, lockAtLeastFor = FIVE_MIN)
    public void createCustomers() {
        Query query = new Query();
        query.addCriteria(Criteria.where("vgPayCustomerCode").is(null));
        ArrayList<Institution> institutionsWithoutVigiPayCustomerCode = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
        for (Institution institution : institutionsWithoutVigiPayCustomerCode) {
            try {
                List<AuthInfo> gamingOperatorUsers = authInfoService.findAllEnabledUsersForInstitution(institution.getId());
                if (gamingOperatorUsers.isEmpty()) {
                    logger.info("Institution does not have gaming operator users");
                    continue;
                }
                logger.info("Attempting to create customer for {}", institution.getInstitutionName());
                String customerCode = vigipayService.createCustomerCodeForInstitution(institution);
                if (!StringUtils.isEmpty(customerCode)) {
                    logger.info("Gotten customer code {}", customerCode);
                    institution.setVgPayCustomerCode(customerCode);
                    mongoRepositoryReactive.saveOrUpdate(institution);
                    logger.info("Finished for institution {} -> {}", institution.getInstitutionName(), institution.getId());
                }
            } catch (Throwable e) {
                logger.error("An error occurred while creating customer for {}", institution.getInstitutionName());
            }
        }


        query.addCriteria(Criteria.where("skipVigipay").is(false));
        ArrayList<Agent> agentWithoutVigiPayCustomerCode = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
        for (Agent agent : agentWithoutVigiPayCustomerCode) {
            String customerCode = vigipayService.createCustomerCodeForAgent(agent);
            if (!StringUtils.isEmpty(customerCode)) {
                logger.info("Gotten customer code {}", customerCode);
                agent.setVgPayCustomerCode(customerCode);
                mongoRepositoryReactive.saveOrUpdate(agent);
                logger.info("Finished for agent {} -> {}", agent.getFullName(), agent.getId());
            }
        }
    }
}
