package com.software.finatech.lslb.cms.service.model.otherInformation;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;

import java.util.ArrayList;
import java.util.List;

public class ApplicantOtherInformation {
    private String otherInformation;
    private List<CommentDetail> comments = new ArrayList<>();

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

    public String getOtherInformation() {
        return otherInformation;
    }

    public void setOtherInformation(String otherInformation) {
        this.otherInformation = otherInformation;
    }
}
