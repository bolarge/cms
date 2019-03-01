package com.software.finatech.lslb.cms.service.config;

import com.software.finatech.jjwt.AuthenticatedUser;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Autowired
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("System Admin");
        }
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        } else {
            AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Optional.ofNullable(user.getUsername());
        }
    }

    public String getCurrentAuditorNotNull() {
        try {
            Optional<String> stringOptional = getCurrentAuditor();
            return stringOptional.orElse(null);
        } catch (Exception e) {
            logger.error("An error occurred while getting current auditor ", e);
            return null;
        }
    }

    public AuthInfo getLoggedInUser() {
        String currentAuditorFullName = getCurrentAuditorNotNull();
        if (StringUtils.isEmpty(currentAuditorFullName)) {
            return null;
        }
        try {
            String[] parts = currentAuditorFullName.split(" - ");
            String email = parts[1];
            return (AuthInfo) mongoRepositoryReactive.find(Query.query(Criteria.where("emailAddress").is(email)), AuthInfo.class).block();
        } catch (Exception e) {
            logger.error("An error occurred while getting logged in user", e);
            return null;
        }
    }
}

