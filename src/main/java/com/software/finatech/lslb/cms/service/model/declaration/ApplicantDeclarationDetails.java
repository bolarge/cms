package com.software.finatech.lslb.cms.service.model.declaration;

import java.util.List;

public class ApplicantDeclarationDetails {
    private List<MemberDeclaration> memberDeclarationList;

    public List<MemberDeclaration> getMemberDeclarationList() {
        return memberDeclarationList;
    }

    public void setMemberDeclarationList(List<MemberDeclaration> memberDeclarationList) {
        this.memberDeclarationList = memberDeclarationList;
    }
}
