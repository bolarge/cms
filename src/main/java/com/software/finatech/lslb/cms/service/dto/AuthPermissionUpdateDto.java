package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class AuthPermissionUpdateDto {

    @NotEmpty(message = "Please provide id")
    protected String id;
    @NotEmpty(message = "Please provide Permission Name")
    protected String name;
    @NotEmpty(message = "Please provide Permission Description")
    protected String description;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
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
