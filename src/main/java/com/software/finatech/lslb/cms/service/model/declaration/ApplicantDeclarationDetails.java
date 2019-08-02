package com.software.finatech.lslb.cms.service.model.declaration;

import com.software.finatech.lslb.cms.service.dto.CommentDetail;

import java.util.ArrayList;
import java.util.List;

public class ApplicantDeclarationDetails {
    private List<MemberDeclaration> memberDeclarationList;
    private List<CommentDetail> comments = new ArrayList<>();

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

    public List<MemberDeclaration> getMemberDeclarationList() {
        return memberDeclarationList;
    }

    public void setMemberDeclarationList(List<MemberDeclaration> memberDeclarationList) {
        this.memberDeclarationList = memberDeclarationList;
    }
}
