package com.software.finatech.lslb.cms.service.model.criminalityDetails;

import java.util.List;

public class ApplicantCriminalityDetails {
    private List<MemberCriminalityDetail> memberCriminalityDetailList;
    private ApplicantPendingInvestigation applicantPendingInvestigation;
    private Boolean applicantHasPendingInvestigation;
    private Boolean applicantMemberHasCriminalOffence;

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
