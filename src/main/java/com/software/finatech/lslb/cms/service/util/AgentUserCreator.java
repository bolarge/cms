package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.async_helpers.CustomerCodeCreatorAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AgentUserCreator {

    private static final Logger logger = LoggerFactory.getLogger(AgentUserCreator.class);

    private AuthInfoService authInfoService;
    private FrontEndPropertyHelper frontEndPropertyHelper;
    private CustomerCodeCreatorAsync customerCodeCreatorAsync;

    @Autowired
    public AgentUserCreator(AuthInfoService authInfoService,
                            FrontEndPropertyHelper frontEndPropertyHelper,
                            CustomerCodeCreatorAsync customerCodeCreatorAsync) {
        this.authInfoService = authInfoService;
        this.frontEndPropertyHelper = frontEndPropertyHelper;
        this.customerCodeCreatorAsync = customerCodeCreatorAsync;
    }

    public void createUserAndCustomerCodeForAgent(Agent agent, HttpServletRequest httpServletRequest) {
        try {
            AuthInfoCreateDto agentUserCreateDto = createAuthInfoDtoFromAgent(agent);
            logger.info("Agent User being created is : " + agentUserCreateDto);
            authInfoService.createAuthInfo(agentUserCreateDto, frontEndPropertyHelper.getFrontEndUrl()).block();
        } catch (Exception e) {
            logger.error("An error occurred while creating user for agent {} -> {}", agent.getId(), agent.getFullName(), e);
        }
        try {
            customerCodeCreatorAsync.createVigipayCustomerCodeForAgent(agent);
        } catch (Exception e) {
            logger.error("An error occurred while creating vigipay customer code for agent {} -> {}", agent.getId(), agent.getFullName(), e);
        }
    }

    private AuthInfoCreateDto createAuthInfoDtoFromAgent(Agent agent) {
        AuthInfoCreateDto authInfoCreateDto = new AuthInfoCreateDto();
        authInfoCreateDto.setAuthRoleId(LSLBAuthRoleReferenceData.AGENT_ROLE_ID);
        authInfoCreateDto.setEmailAddress(agent.getEmailAddress());
        authInfoCreateDto.setPhoneNumber(agent.getPhoneNumber());
        authInfoCreateDto.setFirstName(agent.getFirstName());
        authInfoCreateDto.setLastName(agent.getLastName());
        authInfoCreateDto.setTitle(agent.getTitle());
        authInfoCreateDto.setAgentId(agent.getId());
        return authInfoCreateDto;
    }
}
