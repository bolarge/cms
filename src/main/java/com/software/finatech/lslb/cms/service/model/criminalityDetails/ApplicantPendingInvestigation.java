package com.software.finatech.lslb.cms.service.model.criminalityDetails;

public class ApplicantPendingInvestigation {
    private String nameOfInvestigatingBody;
    private String investigationDetails;

    public String getNameOfInvestigatingBody() {
        return nameOfInvestigatingBody;
    }

    public void setNameOfInvestigatingBody(String nameOfInvestigatingBody) {
        this.nameOfInvestigatingBody = nameOfInvestigatingBody;
    }

    public String getInvestigationDetails() {
        return investigationDetails;
    }

    public void setInvestigationDetails(String investigationDetails) {
        this.investigationDetails = investigationDetails;
    }
}
