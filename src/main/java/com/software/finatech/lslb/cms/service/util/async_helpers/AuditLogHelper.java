package com.software.finatech.lslb.cms.service.util.async_helpers;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuditTrail;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by djaiyeola on 5/9/16.
 */
@Component("auditLogHelper")
public class AuditLogHelper {
    private static Logger logger = LoggerFactory.getLogger(AuditLogHelper.class);
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
    @Autowired
    protected SpringSecurityAuditorAware springSecurityAuditorAware;

    @Async("threadPoolTaskExecutor")
    public  void auditFact(AuditTrail auditTrail) {
        logger.info("Logging Action  - " + auditTrail.getActionPerformed());
        //auditTrail.setPerformedBy(springSecurityAuditorAware.getCurrentAuditor().get());
        mongoRepositoryReactive.saveOrUpdate(auditTrail);
    }
}
