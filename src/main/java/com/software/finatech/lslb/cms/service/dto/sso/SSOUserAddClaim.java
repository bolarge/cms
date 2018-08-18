package com.software.finatech.lslb.cms.service.dto.sso;

import io.advantageous.boon.json.annotations.SerializedName;

import java.util.ArrayList;

public class SSOUserAddClaim {
    @SerializedName("UserId")
    protected String userId ;
    @SerializedName("Claims")
    protected ArrayList<SSOClaim> claims = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<SSOClaim> getClaims() {
        return claims;
    }

    public void setClaims(ArrayList<SSOClaim> claims) {
        this.claims = claims;
    }
}
