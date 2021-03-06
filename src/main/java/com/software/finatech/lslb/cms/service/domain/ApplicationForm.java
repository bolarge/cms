package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.dto.CommentDetail;
import com.software.finatech.lslb.cms.service.model.applicantDetails.ApplicantDetails;
import com.software.finatech.lslb.cms.service.model.applicantMembers.OperatorMemberDetails;
import com.software.finatech.lslb.cms.service.model.contactDetails.ApplicantContactDetails;
import com.software.finatech.lslb.cms.service.model.criminalityDetails.ApplicantCriminalityDetails;
import com.software.finatech.lslb.cms.service.model.declaration.ApplicantDeclarationDetails;
import com.software.finatech.lslb.cms.service.model.otherInformation.ApplicantOtherInformation;
import com.software.finatech.lslb.cms.service.model.outletInformation.ApplicantOutletInformation;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@SuppressWarnings("serial")
@Document(collection = "ApplicationForms")
public class ApplicationForm extends AbstractFact {

    protected String institutionId;
    protected String gameTypeId;
    protected String applicationFormStatusId;
    protected Set<String> paymentRecordIds;
    protected String applicationFormTypeId;
    protected String formName;
    protected String rejectorId;
    protected ApplicantDetails applicantDetails;
    protected OperatorMemberDetails applicantMemberDetails;
    protected ApplicantCriminalityDetails applicantCriminalityDetails;
    protected ApplicantDeclarationDetails applicantDeclarationDetails;
    protected ApplicantOutletInformation applicantOutletInformation;
    protected ApplicantOtherInformation applicantOtherInformation;
    protected ApplicantContactDetails applicantContactDetails;
    protected LslbAdminComment lslbAdminComment;
    protected String reasonForRejection;
    protected String applicationFormId;
    protected String approverId;
    protected FormDocumentApproval documentApproval;
    protected Boolean readyForApproval;
    protected List<FormComment> formComments = new ArrayList<>();
    protected LocalDate creationDate;
    protected LocalDate submissionDate;

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public List<FormComment> getFormComments() {
        return formComments;
    }

    public void setFormComments(List<FormComment> formComments) {
        this.formComments = formComments;
    }

    public Boolean getReadyForApproval() {
        return readyForApproval;
    }

    public void setReadyForApproval(Boolean readyForApproval) {
        this.readyForApproval = readyForApproval;
    }

    public FormDocumentApproval getDocumentApproval() {
        return documentApproval;
    }

    public void setDocumentApproval(FormDocumentApproval documentApproval) {
        this.documentApproval = documentApproval;
    }

    public String getApplicationFormId() {
        return applicationFormId;
    }

    public void setApplicationFormId(String applicationFormId) {
        this.applicationFormId = applicationFormId;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public LslbAdminComment getLslbAdminComment() {
        return lslbAdminComment;
    }

    public void setLslbAdminComment(LslbAdminComment lslbAdminComment) {
        this.lslbAdminComment = lslbAdminComment;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }


    public ApplicantDetails getApplicantDetails() {
        return applicantDetails;
    }

    public void setApplicantDetails(ApplicantDetails applicantDetails) {
        this.applicantDetails = applicantDetails;
    }

    public OperatorMemberDetails getApplicantMemberDetails() {
        return applicantMemberDetails;
    }

    public void setApplicantMemberDetails(OperatorMemberDetails applicantMemberDetails) {
        this.applicantMemberDetails = applicantMemberDetails;
    }

    public ApplicantCriminalityDetails getApplicantCriminalityDetails() {
        return applicantCriminalityDetails;
    }

    public void setApplicantCriminalityDetails(ApplicantCriminalityDetails applicantCriminalityDetails) {
        this.applicantCriminalityDetails = applicantCriminalityDetails;
    }

    public ApplicantDeclarationDetails getApplicantDeclarationDetails() {
        return applicantDeclarationDetails;
    }

    public void setApplicantDeclarationDetails(ApplicantDeclarationDetails applicantDeclarationDetails) {
        this.applicantDeclarationDetails = applicantDeclarationDetails;
    }

    public ApplicantOutletInformation getApplicantOutletInformation() {
        return applicantOutletInformation;
    }

    public void setApplicantOutletInformation(ApplicantOutletInformation applicantOutletInformation) {
        this.applicantOutletInformation = applicantOutletInformation;
    }

    public ApplicantOtherInformation getApplicantOtherInformation() {
        return applicantOtherInformation;
    }

    public void setApplicantOtherInformation(ApplicantOtherInformation applicantOtherInformation) {
        this.applicantOtherInformation = applicantOtherInformation;
    }

    public ApplicantContactDetails getApplicantContactDetails() {
        return applicantContactDetails;
    }

    public void setApplicantContactDetails(ApplicantContactDetails applicantContactDetails) {
        this.applicantContactDetails = applicantContactDetails;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getApplicationFormStatusId() {
        return applicationFormStatusId;
    }

    public void setApplicationFormStatusId(String applicationFormStatusId) {
        this.applicationFormStatusId = applicationFormStatusId;
    }

    public Set<String> getPaymentRecordIds() {
        return paymentRecordIds;
    }

    public void setPaymentRecordIds(Set<String> paymentRecordIds) {
        this.paymentRecordIds = paymentRecordIds;
    }

    public String getApplicationFormTypeId() {
        return applicationFormTypeId;
    }

    public void setApplicationFormTypeId(String applicationFormTypeId) {
        this.applicationFormTypeId = applicationFormTypeId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public AuthInfo getAuthInfo(String authInfoId) {
        if (StringUtils.isEmpty(authInfoId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(authInfoId, AuthInfo.class).block();
    }

    public GameType getGameType() {
        if (StringUtils.isEmpty(this.gameTypeId)) {
            return null;
        }
        return (GameType) mongoRepositoryReactive.findById(this.gameTypeId, GameType.class).block();
    }
    public String getGameTypeName() {
        GameType gameType = getGameType();
        if (gameType == null) {
            return null;
        } else {
            return gameType.getName();
        }
    }

    private ApplicationFormStatus getStatus() {
        if (StringUtils.isEmpty(this.applicationFormStatusId)) {
            return null;
        }
        Map applicationFormStatusMap = Mapstore.STORE.get("ApplicationFormStatus");
        ApplicationFormStatus applicationFormStatus = null;
        if (applicationFormStatusMap != null) {
            applicationFormStatus = (ApplicationFormStatus) applicationFormStatusMap.get(applicationFormStatusId);
        }
        if (applicationFormStatus == null) {
            applicationFormStatus = (ApplicationFormStatus) mongoRepositoryReactive.findById(applicationFormStatusId, ApplicationFormStatus.class).block();
            if (applicationFormStatus != null && applicationFormStatusMap != null) {
                applicationFormStatusMap.put(applicationFormStatusId, applicationFormStatus);
            }
        }
        return applicationFormStatus;
    }

    public Institution getInstitution() {
        if (StringUtils.isEmpty(institutionId)) {
            return null;
        }
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return "";
    }

    public ApplicationFormDto convertToDto() {
        ApplicationFormDto applicationFormDto = new ApplicationFormDto();
        GameType gameType = getGameType();
        if (gameType != null) {
            applicationFormDto.setGameTypeDescription(gameType.getDescription());
            applicationFormDto.setGameTypeName(gameType.getName());
            applicationFormDto.setGameTypeId(gameTypeId);
        }
        ApplicationFormStatus applicationFormStatus = getStatus();
        if (applicationFormStatus != null) {
            applicationFormDto.setStatusName(applicationFormStatus.getName());
            applicationFormDto.setStatusId(getApplicationFormStatusId());
        }

        Institution institution = getInstitution();
        if (institution != null) {
            applicationFormDto.setInstitutionName(institution.getInstitutionName());
            applicationFormDto.setInstitutionId(institutionId);
        }
        AuthInfo approver = getAuthInfo(approverId);
        if (approver != null) {
            applicationFormDto.setApproverId(approverId);
            applicationFormDto.setApproverName(approver.getFullName());
        }
        AuthInfo rejector = getAuthInfo(rejectorId);
        if (rejector != null) {
            applicationFormDto.setRejectorId(rejectorId);
            applicationFormDto.setRejectorName(rejector.getFullName());
        }
        applicationFormDto.setRejectionReason(getReasonForRejection());
        applicationFormDto.setId(getId());
        applicationFormDto.setFormName(getFormName());

        LslbAdminComment lslbAdminComment = getLslbAdminComment();
        if (lslbAdminComment != null) {
            applicationFormDto.setLslbAdminComment(lslbAdminComment.getComment());
            applicationFormDto.setLslbAdminCommented(true);
            AuthInfo admin = getAuthInfo(lslbAdminComment.getUserId());
            if (admin != null) {
                applicationFormDto.setLslbAdminName(admin.getFullName());
            }
        }
        applicationFormDto.setFilledApplicantContactDetails(getApplicantContactDetails() != null);
        applicationFormDto.setFilledApplicantCriminalityDetails(getApplicantCriminalityDetails() != null);
        applicationFormDto.setFilledApplicantDeclarationDetails(getApplicantDeclarationDetails() != null);
        applicationFormDto.setFilledApplicantDetails(getApplicantDetails() != null);
        applicationFormDto.setFilledApplicantOtherInformation(getApplicantOtherInformation() != null);
        applicationFormDto.setFilledApplicantOutletInformation(getApplicantOutletInformation() != null);
        applicationFormDto.setFilledApplicantDetails(getApplicantDetails() != null);
        applicationFormDto.setApplicationFormId(getApplicationFormId());
        applicationFormDto.setReadyForApproval(getReadyForApproval());
        List<CommentDetail> comments = getComments();
        Collections.reverse(comments);
        applicationFormDto.setComments(comments);
        applicationFormDto.setCreationDate(getDateString(getCreationDate()));
        applicationFormDto.setSubmissionDate(getDateString(getSubmissionDate()));
        return applicationFormDto;
    }


    private String getDateString(LocalDate localDate) {
        if (localDate != null) {
            return localDate.toString("dd-MM-yyyy");
        }
        return null;
    }

    private List<CommentDetail> getComments() {
        List<CommentDetail> comments = new ArrayList<>();
        for (FormComment comment : this.formComments) {
            CommentDetail dto = new CommentDetail();
            dto.setComment(comment.getComment());
            dto.setUserFullName(comment.getUserFullName());
            dto.setCommentDate(comment.getTimeCreated().toString("dd-MM-yyyy"));
            dto.setCommentTime(comment.getTimeCreated().toString("hh:mm a"));
            comments.add(dto);
        }
        return comments;
    }

    @Override
    public String getFactName() {
        return "ApplicationForm";
    }
}
