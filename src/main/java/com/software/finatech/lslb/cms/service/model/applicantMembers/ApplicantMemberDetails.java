package com.software.finatech.lslb.cms.service.model.applicantMembers;

import com.software.finatech.lslb.cms.service.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class ApplicantMemberDetails {
    private List<ManagementDetail> managementDetailList;
    private List<Shareholder> shareholderList;
    private List<CommentDto> comments = new ArrayList<>();

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
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
