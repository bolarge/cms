package com.software.finatech.lslb.cms.service.model.criminalityDetails;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;

import java.util.ArrayList;
import java.util.List;

public class ApplicantCriminalityDetails {
    private List<MemberCriminalityDetail> memberCriminalityDetailList;
    private ApplicantPendingInvestigation applicantPendingInvestigation;
    private Boolean applicantHasPendingInvestigation;
    private Boolean applicantMemberHasCriminalOffence;
    private List<CommentDetail> comments = new ArrayList<>();

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

    public Boolean getApplicantHasPendingInvestigation() {
        return applicantHasPendingInvestigation;
    }

    public void setApplicantHasPendingInvestigation(Boolean applicantHasPendingInvestigation) {
        this.applicantHasPendingInvestigation = applicantHasPendingInvestigation;
    }

    public Boolean getApplicantMemberHasCriminalOffence() {
        return applicantMemberHasCriminalOffence;
    }

    public void setApplicantMemberHasCriminalOffence(Boolean applicantMemberHasCriminalOffence) {
        this.applicantMemberHasCriminalOffence = applicantMemberHasCriminalOffence;
    }

    public List<MemberCriminalityDetail> getMemberCriminalityDetailList() {
        return memberCriminalityDetailList;
    }

    public void setMemberCriminalityDetailList(List<MemberCriminalityDetail> memberCriminalityDetailList) {
        this.memberCriminalityDetailList = memberCriminalityDetailList;
    }

    public ApplicantPendingInvestigation getApplicantPendingInvestigation() {
        return applicantPendingInvestigation;
    }

    public void setApplicantPendingInvestigation(ApplicantPendingInvestigation applicantPendingInvestigation) {
        this.applicantPendingInvestigation = applicantPendingInvestigation;
    }
}
