package com.software.finatech.lslb.cms.service.util;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by djaiyeola on 5/27/16.
 */
@Component
public class CORSFilter implements Filter {
    //static Logger logger = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3000");
        //response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, origin, authorization, accept, client-security-token,x-xsrf-token,TenantId");
        final HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        chain.doFilter(httpServletRequest, response);
    }

    public void destroy() {}
}