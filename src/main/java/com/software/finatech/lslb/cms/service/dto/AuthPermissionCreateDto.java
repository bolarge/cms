package com.software.finatech.lslb.cms.userservice.dto;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class AuthPermissionCreateDto {

    @NotEmpty(message = "Please provide Permission Name")
    protected String name;
    @NotEmpty(message = "Please provide Permission Description")
    protected String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

 }
