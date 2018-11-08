package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.DocumentApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.DocumentType;
import com.software.finatech.lslb.cms.service.domain.PendingDocumentType;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.DocumentApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "Document", description = "For everything related to documents", tags = "Document Type Controller")
@RestController
@RequestMapping("/api/v1/documentType")
public class DocumentTypeController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DocumentTypeController.class);
    private static final String configAuditActionId = AuditActionReferenceData.CONFIGURATIONS_ID;

    @Autowired
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;
    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    @Autowired
    private AuthInfoService authInfoService;
    @Autowired
    private AuditLogHelper auditLogHelper;

    @RequestMapping(method = RequestMethod.GET, value = "/documentTypes", params = {"purposeId", "active", "gameTypeIds", "approverId"})
    @ApiOperation(value = "Get Document Type By Purpose, Status", response = DocumentTypeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getDocumentTypeByPurpose(@RequestParam("purposeId") String purposeId,
                                                         @RequestParam("active") String active,
                                                         @RequestParam("gameTypeIds") String gameTypeIds,
                                                         @RequestParam("approverId") String approverId) {
        Query query = new Query();
        if (!StringUtils.isEmpty(purposeId)) {
            query.addCriteria(Criteria.where("documentPurposeId").is(purposeId));
        }
        if (!StringUtils.isEmpty(active)) {
            query.addCriteria(Criteria.where("active").is(active));
        }
        if (!StringUtils.isEmpty(gameTypeIds)) {
            List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("-"));
            query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeIdList));
        }
        if (!StringUtils.isEmpty(approverId)) {
            query.addCriteria(Criteria.where("approverId").is(approverId));
        }

        ArrayList<DocumentType> documentTypes = (ArrayList<DocumentType>) mongoRepositoryReactive.findAll(query, DocumentType.class).toStream().collect(Collectors.toList());

        ArrayList<DocumentTypeDto> documentTypesDto = new ArrayList<>();
        documentTypes.forEach(entry -> {
            documentTypesDto.add(entry.convertToDto());
        });

        if (documentTypesDto.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(documentTypesDto, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create Document Type", response = DocumentType.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createDocumentType(@RequestBody @Valid DocumentTypeCreateDto documentTypeCreateDto, HttpServletRequest request) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(documentTypeCreateDto.getName()));
        DocumentType checkForDocumentType = (DocumentType) mongoRepositoryReactive.find(query, DocumentType.class).block();
        if (checkForDocumentType != null) {
            return Mono.just(new ResponseEntity<>("Document Type exist, try make an update", HttpStatus.BAD_REQUEST));

        }
        String loggedInUserName = springSecurityAuditorAware.getCurrentAuditorNotNull();
        if (StringUtils.isEmpty(documentTypeCreateDto.getApproverId())) {
            DocumentType documentType = new DocumentType();
            documentType.setId(UUID.randomUUID().toString());
            documentType.setDocumentPurposeId(documentTypeCreateDto.getDocumentPurposeId());
            documentType.setActive(true);
            documentType.setRequired(documentTypeCreateDto.isRequired());
            documentType.setName(documentTypeCreateDto.getName());
            documentType.setDescription(documentTypeCreateDto.getDescription());
            mongoRepositoryReactive.saveOrUpdate(documentType);

            String verbiage = String.format("Created document type -> Ticket Name: -> %s, Id -> %s ", documentType, documentType.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(configAuditActionId,
                    loggedInUserName, String.format("%s--%s", loggedInUserName, documentType),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity(documentType.convertToDto(), HttpStatus.OK));
        }
        AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
        if (loggedInUser == null) {
            return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
        }
        PendingDocumentType pendingDocumentType = new PendingDocumentType();
        pendingDocumentType.setId(UUID.randomUUID().toString());
        pendingDocumentType.setDocumentPurposeId(documentTypeCreateDto.getDocumentPurposeId());
        pendingDocumentType.setActive(documentTypeCreateDto.isActive());
        pendingDocumentType.setRequired(true);
        pendingDocumentType.setName(documentTypeCreateDto.getName());
        pendingDocumentType.setDescription(documentTypeCreateDto.getDescription());
        pendingDocumentType.setApproverId(documentTypeCreateDto.getApproverId());
        mongoRepositoryReactive.saveOrUpdate(pendingDocumentType);
        DocumentApprovalRequest documentApprovalRequest = new DocumentApprovalRequest();
        documentApprovalRequest.setId(UUID.randomUUID().toString());
        documentApprovalRequest.setPendingDocumentTypeId(pendingDocumentType.getId());
        documentApprovalRequest.setInitiatorId(loggedInUser.getId());
        documentApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
        documentApprovalRequest.setDocumentApprovalRequestTypeId(DocumentApprovalRequestTypeReferenceData.CREATE_DOCUMENT_TYPE_ID);
        mongoRepositoryReactive.saveOrUpdate(documentApprovalRequest);
        approvalRequestNotifierAsync.sendNewDocumentApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, documentApprovalRequest);
        return Mono.just(new ResponseEntity<>(documentApprovalRequest.convertToHalfDto(), HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update Document Type", response = DocumentType.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateDocumentType(@RequestBody @Valid DocumentTypeUpdateDto documentTypeUpdateDto) {
        DocumentType documentType = (DocumentType) mongoRepositoryReactive.findById(documentTypeUpdateDto.getId(), DocumentType.class).block();
        if (documentType == null) {
            return Mono.just(new ResponseEntity<>("Document Type does not exist", HttpStatus.BAD_REQUEST));
        }
        documentType.setActive(documentTypeUpdateDto.isActive());
        documentType.setRequired(documentTypeUpdateDto.isRequired());
        documentType.setDescription(documentTypeUpdateDto.getDescription());
        documentType.setName(documentTypeUpdateDto.getName());
        documentTypeUpdateDto.setDescription(documentTypeUpdateDto.getDescription());
        mongoRepositoryReactive.saveOrUpdate(documentType);
        return Mono.just(new ResponseEntity(documentType.convertToDto(), HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/set-approver")
    @ApiOperation(value = "Set Approver For Document Type", response = DocumentApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> setApprover(@RequestBody @Valid SetApproverRequest setApproverRequest) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            DocumentType documentType = (DocumentType) mongoRepositoryReactive.findById(setApproverRequest.getDocumentTypeId(), DocumentType.class).block();
            if (documentType == null) {
                return Mono.just(new ResponseEntity<>("Document Type does not exist", HttpStatus.BAD_REQUEST));
            }
            AuthInfo newApprover = authInfoService.getUserById(setApproverRequest.getApproverId());
            if (newApprover == null) {
                return Mono.just(new ResponseEntity<>(String.format("user with id %s does not exist", setApproverRequest.getApproverId()), HttpStatus.BAD_REQUEST));
            }
            DocumentApprovalRequest approvalRequest = new DocumentApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setDocumentApprovalRequestTypeId(DocumentApprovalRequestTypeReferenceData.SET_APPROVER_ID);
            approvalRequest.setDocumentTypeId(documentType.getId());
            approvalRequest.setInitiatorId(loggedInUser.getId());
            approvalRequest.setNewApproverId(newApprover.getId());
            approvalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            approvalRequestNotifierAsync.sendNewDocumentApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, approvalRequest);
            return Mono.just(new ResponseEntity<>(approvalRequest.convertToHalfDto(), HttpStatus.OK));

        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while setting document approver id", e);
        }
    }
}
