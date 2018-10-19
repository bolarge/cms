package com.software.finatech.lslb.cms.service.dto;

public class AuthPermissionDto extends EnumeratedFactDto {
    private Boolean usedBySystem;
    private Boolean  belongsToUser;
    private String authRoleName;
    private String authRoleId;

    public String getAuthRoleName() {
        return authRoleName;
    }

    public void setAuthRoleName(String authRoleName) {
        this.authRoleName = authRoleName;
    }

    public String getAuthRoleId() {
        return authRoleId;
    }

    public void setAuthRoleId(String authRoleId) {
        this.authRoleId = authRoleId;
    }

    public Boolean getBelongsToUser() {
        return belongsToUser;
    }

    public void setBelongsToUser(Boolean belongsToUser) {
        this.belongsToUser = belongsToUser;
    }

    public Boolean getUsedBySystem() {
        return usedBySystem;
    }

    public void setUsedBySystem(Boolean usedBySystem) {
        this.usedBySystem = usedBySystem;
    }
}
