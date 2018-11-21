package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class FakeAgentUpdater {

    private static final Logger logger = LoggerFactory.getLogger(FakeAgentUpdater.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @PostConstruct
    public void updateAgents() {

        ArrayList<FakeAgent> fakeAgents = (ArrayList<FakeAgent>) mongoRepositoryReactive.findAll(new Query(), FakeAgent.class).toStream().collect(Collectors.toList());
        for (FakeAgent fakeAgent : fakeAgents) {
            logger.info("updating agent {}", fakeAgent.getFirstName());
            if (StringUtils.isEmpty(fakeAgent.getDateOfBirth())) {
                fakeAgent.setDateOfBirth(null);
                mongoRepositoryReactive.saveOrUpdate(fakeAgent);
            }
        }

        logger.info("\n\n\n\n\n Done \n\n\n\n\n");
    }
}
