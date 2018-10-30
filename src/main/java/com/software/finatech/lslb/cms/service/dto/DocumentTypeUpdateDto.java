package com.software.finatech.lslb.cms.service.dto;


public class DocumentTypeUpdateDto extends EnumeratedFactDto {
    protected boolean active;
    protected boolean required;
    protected String id;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
