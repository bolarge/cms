package com.software.finatech.lslb.cms.service.config;

import com.software.finatech.jjwt.AuthenticatedUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("System Admin");
        }
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String){
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        }else{
            AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Optional.ofNullable(user.getUsername());
        }
    }

}
