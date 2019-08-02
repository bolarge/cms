package com.software.finatech.lslb.cms.service.dto;

/**
 * @author adeyi.adebolu
 * created on 20/06/2019
 */
public class CreateCustomerCodeDto {
    private String institutionId;
    private String agentId;
    private boolean forceUpdate;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
