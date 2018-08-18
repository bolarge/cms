package com.software.finatech.lslb.cms.service.dto.sso;

import java.util.ArrayList;

public class SSOUserDetail {
    protected ArrayList<SSOUserDetailInfo> Data = new ArrayList<>();

    public ArrayList<SSOUserDetailInfo> getData() {
        return Data;
    }

    public void setData(ArrayList<SSOUserDetailInfo> data) {
        Data = data;
    }
}
