package com.software.finatech.lslb.cms.service.dto;

public class AuthPermissionDto extends EnumeratedFactDto {
    private Boolean usedBySystem;

    public Boolean getUsedBySystem() {
        return usedBySystem;
    }

    public void setUsedBySystem(Boolean usedBySystem) {
        this.usedBySystem = usedBySystem;
    }
}
