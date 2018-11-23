package com.software.finatech.lslb.cms.service.model.applicantMembers;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;

import java.util.ArrayList;
import java.util.List;

public class OperatorMemberDetails {
    private List<ManagementDetail> managementDetailList;
    private List<Shareholder> shareholderList;
    private List<CommentDetail> comments = new ArrayList<>();

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

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
