package com.software.finatech.lslb.cms.service.model.outletInformation;

import com.software.finatech.lslb.cms.service.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class ApplicantOutletInformation {
    private String businessName;
    private int numberOfOutlets;
    private List<CommentDto> comments = new ArrayList<>();

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public int getNumberOfOutlets() {
        return numberOfOutlets;
    }

    public void setNumberOfOutlets(int numberOfOutlets) {
        this.numberOfOutlets = numberOfOutlets;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
