package com.software.finatech.lslb.cms.service.util.async_helpers;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AgentUserCreatorAsync {

    private static final Logger logger = LoggerFactory.getLogger(AgentUserCreatorAsync.class);

    private AuthInfoService authInfoService;
    private FrontEndPropertyHelper frontEndPropertyHelper;
    private CustomerCodeCreatorAsync customerCodeCreatorAsync;

    @Autowired
    public AgentUserCreatorAsync(AuthInfoService authInfoService,
                                 FrontEndPropertyHelper frontEndPropertyHelper,
                                 CustomerCodeCreatorAsync customerCodeCreatorAsync) {
        this.authInfoService = authInfoService;
        this.frontEndPropertyHelper = frontEndPropertyHelper;
        this.customerCodeCreatorAsync = customerCodeCreatorAsync;
    }

    @Async
    public void createUserAndCustomerCodeForAgentAsync(Agent agent) {
        try {
            AuthInfoCreateDto agentUserCreateDto = createAuthInfoDtoFromAgent(agent);
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
        authInfoCreateDto.setAgentId(agent.getId());
        return authInfoCreateDto;
    }
}
