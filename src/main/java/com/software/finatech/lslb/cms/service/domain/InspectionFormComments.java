package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.InspectionFormCommentDto;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "InspectionFormComments")
public class InspectionFormComments extends AbstractFact {

    private String inspectionFormId;
    private String comment;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInspectionFormId() {
        return inspectionFormId;
    }

    public void setInspectionFormId(String inspectionFormId) {
        this.inspectionFormId = inspectionFormId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public InspectionFormCommentDto convertToDto() {
        InspectionFormCommentDto inspectionFormCommentDto = new InspectionFormCommentDto();
        inspectionFormCommentDto.setComment(getComment());
        inspectionFormCommentDto.setInspectionFormId(getInspectionFormId());
        inspectionFormCommentDto.setCreatedAt(getCreatedAt().toString("dd/MM/yyyy"));
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
        if(authInfo!=null){
            inspectionFormCommentDto.setCommenter(authInfo.getFullName());
        }
        return inspectionFormCommentDto;
    }


    @Override
    public String getFactName() {
        return null;
    }
}
