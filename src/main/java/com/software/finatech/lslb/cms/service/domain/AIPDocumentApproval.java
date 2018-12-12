package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AIPDocumentApprovalDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
@Document(collection = "AIPDocumentApprovals")
public class AIPDocumentApproval extends AbstractFact {
    protected String institutionId;
    protected String gameTypeId;
    protected String formStatusId;
    protected LslbAdminComment lslbAdminComment;
    protected String approverId;
    protected String rejectorId;
    protected String reasonForRejection;
    protected FormDocumentApproval documentApproval;
    protected LocalDate submissionDate;
    protected Boolean readyForApproval;
    private boolean finalNotificationSent;

    public boolean isFinalNotificationSent() {
        return finalNotificationSent;
    }

    public void setFinalNotificationSent(boolean finalNotificationSent) {
        this.finalNotificationSent = finalNotificationSent;
    }

    public Boolean getReadyForApproval() {
        return readyForApproval;
    }

    public void setReadyForApproval(Boolean readyForApproval) {
        this.readyForApproval = readyForApproval;
    }

    public LslbAdminComment getLslbAdminComment() {
        return lslbAdminComment;
    }

    public void setLslbAdminComment(LslbAdminComment lslbAdminComment) {
        this.lslbAdminComment = lslbAdminComment;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public String getRejectorId() {
        return rejectorId;
    }

    public void setRejectorId(String rejectorId) {
        this.rejectorId = rejectorId;
    }


    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public FormDocumentApproval getDocumentApproval() {
        return documentApproval;
    }

    public void setDocumentApproval(FormDocumentApproval documentApproval) {
        this.documentApproval = documentApproval;
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


    public String getFormStatusId() {
        return formStatusId;
    }

    public void setFormStatusId(String formStatusId) {
        this.formStatusId = formStatusId;
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
        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
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
        if (StringUtils.isEmpty(this.formStatusId)) {
            return null;
        }
        Map applicationFormStatusMap = Mapstore.STORE.get("ApplicationFormStatus");
        ApplicationFormStatus applicationFormStatus = null;
        if (applicationFormStatusMap != null) {
            applicationFormStatus = (ApplicationFormStatus) applicationFormStatusMap.get(formStatusId);
        }
        if (applicationFormStatus == null) {
            applicationFormStatus = (ApplicationFormStatus) mongoRepositoryReactive.findById(formStatusId, ApplicationFormStatus.class).block();
            if (applicationFormStatus != null && applicationFormStatusMap != null) {
                applicationFormStatusMap.put(formStatusId, applicationFormStatus);
            }
        }
        return applicationFormStatus;
    }

    public AIPDocumentApprovalDto convertToDto() {
        AIPDocumentApprovalDto aipDocumentApprovalDto = new AIPDocumentApprovalDto();
        aipDocumentApprovalDto.setAipFormId(getId());
        aipDocumentApprovalDto.setRejectionReason(getReasonForRejection());
        aipDocumentApprovalDto.setReadyForApproval(getReadyForApproval());
        ApplicationFormStatus applicationFormStatus = getStatus();
        if (applicationFormStatus != null) {
            aipDocumentApprovalDto.setStatusName(applicationFormStatus.getName());
            aipDocumentApprovalDto.setStatusId(getFormStatusId());
        }

        LslbAdminComment lslbAdminComment = getLslbAdminComment();
        if (lslbAdminComment != null) {
            aipDocumentApprovalDto.setLslbAdminComment(lslbAdminComment.getComment());
            aipDocumentApprovalDto.setLslbAdminCommented(true);
            AuthInfo admin = getAuthInfo(lslbAdminComment.getUserId());
            if (admin != null) {
                aipDocumentApprovalDto.setLslbAdminName(admin.getFullName());
            }
        }

        AuthInfo approver = getAuthInfo(approverId);
        if (approver != null) {
            aipDocumentApprovalDto.setApproverId(approverId);
            aipDocumentApprovalDto.setApproverName(approver.getFullName());
        }
        AuthInfo rejector = getAuthInfo(rejectorId);
        if (rejector != null) {
            aipDocumentApprovalDto.setRejectorId(rejectorId);
            aipDocumentApprovalDto.setRejectorName(rejector.getFullName());
        }

        return aipDocumentApprovalDto;

    }

    public boolean hasInspectionForm() {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(this.institutionId));
        query.addCriteria(Criteria.where("gameTypeId").is(this.gameTypeId));
        ArrayList<InspectionForm> inspectionForms = (ArrayList<InspectionForm>) mongoRepositoryReactive.findAll(query, InspectionForm.class).toStream().collect(Collectors.toList());
        return inspectionForms.size() > 0;
    }

    @Override
    public String getFactName() {
        return "AIPDocumentApproval";
    }
}
