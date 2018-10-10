package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;

/**
 * Created by davidjaiyeola on 3/6/16.
 */
public class EnumeratedFact extends AbstractFact {
    protected String name;
    protected String description;
    protected String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EnumeratedFactDto convertToDto() {
        EnumeratedFactDto enumeratedFactDto = new EnumeratedFactDto();
        enumeratedFactDto.setCode(getCode());
        enumeratedFactDto.setDescription(getDescription());
        enumeratedFactDto.setName(getName());
        enumeratedFactDto.setId(getId());

        return enumeratedFactDto;
    }

    @Override
    public String getFactName() {
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
