package com.software.finatech.lslb.cms.service.dto;

public class AuthPermissionDto extends EnumeratedFactDto {
    private Boolean usedBySystem;
    private boolean  belongsToUser;

    public boolean isBelongsToUser() {
        return belongsToUser;
    }

    public void setBelongsToUser(boolean belongsToUser) {
        this.belongsToUser = belongsToUser;
    }

    public Boolean getUsedBySystem() {
        return usedBySystem;
    }

    public void setUsedBySystem(Boolean usedBySystem) {
        this.usedBySystem = usedBySystem;
    }
}
