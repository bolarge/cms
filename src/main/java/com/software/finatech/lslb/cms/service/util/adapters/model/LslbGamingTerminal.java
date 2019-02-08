package com.software.finatech.lslb.cms.service.util.adapters.model;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;

public class LslbGamingTerminal {
    private String agentTelephone;
    private String deviceMagicMobile;
    private String bvn;
    private String lassra;
    private String agentFullName;
    private String agentAddress;
    private String machineCount;
    private String machineId;
    private Agent agent;
    private String failReason;
    private Institution institution;
    private GameType gameType;
    private String licenseId;

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getAgentTelephone() {
        return agentTelephone;
    }

    public void setAgentTelephone(String agentTelephone) {
        this.agentTelephone = agentTelephone;
    }

    public String getDeviceMagicMobile() {
        return deviceMagicMobile;
    }

    public void setDeviceMagicMobile(String deviceMagicMobile) {
        this.deviceMagicMobile = deviceMagicMobile;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getLassra() {
        return lassra;
    }

    public void setLassra(String lassra) {
        this.lassra = lassra;
    }

    public String getAgentFullName() {
        return agentFullName;
    }

    public void setAgentFullName(String agentFullName) {
        this.agentFullName = agentFullName;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(String machineCount) {
        this.machineCount = machineCount;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
