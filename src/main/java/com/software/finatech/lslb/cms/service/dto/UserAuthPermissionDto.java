package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class UserAuthPermissionDto {

    @NotEmpty(message = "Please provide user id")
    private String userId;
    @NotEmpty(message = "Please provide auth permission ids")
    private Set<String> authPermissionIds = new HashSet<>();
    @NotEmpty(message = "Please provide logged in user id ")
    private String loggedInUserId;

    public String getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getAuthPermissionIds() {
        return authPermissionIds;
    }

    public void setAuthPermissionIds(Set<String> authPermissionIds) {
        this.authPermissionIds = authPermissionIds;
    }
}
