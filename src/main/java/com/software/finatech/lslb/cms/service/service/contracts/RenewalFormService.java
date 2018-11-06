package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.dto.FormCreateCommentDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormRejectDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RenewalFormService {

    Mono<ResponseEntity> approveRenewalForm(String renewalFormId, String approverId, HttpServletRequest request);
    Mono<ResponseEntity> addCommentsToFormFromLslbAdmin(String renewalId, FormCreateCommentDto formCreateCommentDto, HttpServletRequest request);
    void approveRenewalFormDocument(Document document);
    void rejectRenewalFormDocument(Document document, String comment);
    void doDocumentReuploadNotification(Document document);
    Mono<ResponseEntity> completeRenewalForm(String renewalFormId, boolean isResubmit, HttpServletRequest request);
    Mono<ResponseEntity> rejectRenewalForm(String renewalFormId, RenewalFormRejectDto renewalFormRejectDto, HttpServletRequest request);
    Mono<ResponseEntity> createRenewalForm(RenewalFormCreateDto renewalFormCreateDto);
    Mono<ResponseEntity>  getAllRenewalForms(int page, int pageSize, String sortType, String sortParam, String institutionId, String formStatusId, String gameTypeIds, HttpServletResponse httpServletResponse);
    Mono<ResponseEntity> updateRenewalForm(RenewalFormUpdateDto renewalFormUpdateDto);
    Mono<ResponseEntity> getRenewalFormStatus();
    Mono<ResponseEntity> getAllRenewalForms(String institutionId);
}
