package com.software.finatech.lslb.cms.service.dto.sso;

public class SSOClaim {
    protected String Type ;
    protected String Value ;

    public String getType() {
        return this.Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getValue() {
        return this.Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }
}
