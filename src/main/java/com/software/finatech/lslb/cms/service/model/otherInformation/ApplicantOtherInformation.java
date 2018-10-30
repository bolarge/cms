package com.software.finatech.lslb.cms.service.model.otherInformation;

import com.software.finatech.lslb.cms.service.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class ApplicantOtherInformation {
    private String otherInformation;
    private List<CommentDto> comments = new ArrayList<>();

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public String getOtherInformation() {
        return otherInformation;
    }

    public void setOtherInformation(String otherInformation) {
        this.otherInformation = otherInformation;
    }
}
