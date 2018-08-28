package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.DocumentType;
import com.software.finatech.lslb.cms.service.dto.DocumentTypeCreateDto;
import com.software.finatech.lslb.cms.service.dto.DocumentTypeDto;
import com.software.finatech.lslb.cms.service.dto.DocumentTypeUpdateDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "Document", description = "For everything related to documents", tags = "")
@RestController
@RequestMapping("/api/v1/documentType")
public class DocumentTypeController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(DocumentTypeController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/documentTypes", params = {"purposeId","status","gameTypeId"})
    @ApiOperation(value = "Get Document Type By Purpose, Status", response = DocumentTypeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getDocumentTypeByPurpose(@RequestParam("purposeId") String purposeId, @RequestParam("status") String status, @RequestParam("gameTypeId") String gameTypeId) {
        Query query = new Query();
        if(!StringUtils.isEmpty(purposeId)){
            query.addCriteria(Criteria.where("documentPurposeId").is(purposeId));
        }
        if(!StringUtils.isEmpty(status)){
            query.addCriteria(Criteria.where("status").is(status));
        }
        if(!StringUtils.isEmpty(gameTypeId)){
            query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeId));
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
    public Mono<ResponseEntity> createDocumentType(@RequestBody @Valid DocumentTypeCreateDto documentTypeCreateDto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("documentPurposeId").is(documentTypeCreateDto.getDocumentPurposeId()));
        DocumentType checkForDocumentType= (DocumentType) mongoRepositoryReactive.findById(documentTypeCreateDto.getDocumentPurposeId(), DocumentType.class).block();
        if(checkForDocumentType!=null){
            return Mono.just(new ResponseEntity("Document Type exist, try make an update", HttpStatus.OK));

        }
        DocumentType documentType = new DocumentType();

        documentType.setId(UUID.randomUUID().toString());
        documentType.setDocumentPurposeId(documentTypeCreateDto.getDocumentPurposeId());
        documentType.setActive(documentTypeCreateDto.isActive());
        documentType.setRequired(documentTypeCreateDto.isRequired());
        documentType.setName(documentTypeCreateDto.getName());
        documentType.setDescription(documentTypeCreateDto.getDescription());
        mongoRepositoryReactive.saveOrUpdate(documentType);
        return Mono.just(new ResponseEntity(documentType.convertToDto(), HttpStatus.OK));

    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update Document Type", response = DocumentType.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateDocumentType(@RequestBody @Valid DocumentTypeUpdateDto documentTypeUpdateDto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("documentPurposeId").is(documentTypeUpdateDto.getDocumentPurposeId()));
        DocumentType checkForDocumentType= (DocumentType) mongoRepositoryReactive.findById(documentTypeUpdateDto.getDocumentPurposeId(), DocumentType.class).block();
        if(checkForDocumentType==null){
            return Mono.just(new ResponseEntity("Document Type does not exist", HttpStatus.BAD_REQUEST));
        }
        DocumentType documentType = new DocumentType();
        documentType.setDocumentPurposeId(documentTypeUpdateDto.getDocumentPurposeId());
        documentType.setActive(documentTypeUpdateDto.isActive());
        documentType.setRequired(documentTypeUpdateDto.isRequired());
        documentType.setName(documentTypeUpdateDto.getName());
        documentType.setDescription(documentTypeUpdateDto.getDescription());
        mongoRepositoryReactive.saveOrUpdate(documentType);
        return Mono.just(new ResponseEntity(documentType.convertToDto(), HttpStatus.OK));

    }
}
