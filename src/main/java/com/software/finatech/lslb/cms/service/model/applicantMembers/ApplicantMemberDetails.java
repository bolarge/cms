package com.software.finatech.lslb.cms.service.model.applicantMembers;

import java.util.List;

public class ApplicantMemberDetails {
    private List<ManagementDetail> managementDetailList;
    private List<Shareholder> shareholderList;

    public List<ManagementDetail> getManagementDetailList() {
        return managementDetailList;
    }

    public void setManagementDetailList(List<ManagementDetail> managementDetailList) {
        this.managementDetailList = managementDetailList;
    }

    public List<Shareholder> getShareholderList() {
        return shareholderList;
    }

    public void setShareholderList(List<Shareholder> shareholderList) {
        this.shareholderList = shareholderList;
    }
}
