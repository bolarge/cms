package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.referencedata.ApprovalRequestStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.DocumentPurposeReferenceData;
import com.software.finatech.lslb.cms.service.service.RenewalFormServiceImpl;
import com.software.finatech.lslb.cms.service.service.contracts.ApplicationFormService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Api(value = "Document", description = "For everything related to documents", tags = "Document Controller")
@RestController
@RequestMapping("/api/v1/document")
public class DocumentController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    @Autowired
    private AuditLogHelper auditLogHelper;
    @Autowired
    private ApplicationFormService applicationFormService;
    @Autowired
    private RenewalFormServiceImpl renewalFormService;


    @RequestMapping(method = RequestMethod.POST, value = "/upload", produces = "application/json")
    @ApiOperation(value = "Upload Document", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> upload(@RequestParam("json") String json,
                                       @RequestParam("files") @NotEmpty MultipartFile[] files) {

        //@TODO If its a file replace we have to validate that the old id comes with the json
        if (json == null || json.isEmpty()) {
            return Mono.just(new ResponseEntity<>("Please specify a json body", HttpStatus.BAD_REQUEST));
        }

        List<DocumentCreateDto> documentDtos;

        try {
            documentDtos = mapper.readValue(json, List.class, DocumentCreateDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>("Invalid Json", HttpStatus.BAD_REQUEST));
        }
        try {
            //Put the files in a map
            HashMap<String, MultipartFile> fileMap = new HashMap<>();
            for (MultipartFile file : files) {
                fileMap.put(file.getOriginalFilename(), file);
            }
            Boolean filenameValidationCheckFailed = false;
            //We then reconcile it with the document objects
            ArrayList<FactObject> documents = new ArrayList<>();
            ArrayList<FactObject> documentCheck = new ArrayList<>();
            documentDtos.stream().forEach(documentDto -> {
                try {
                    logger.info("Creating file : " + documentDto.getFilename());

                    MultipartFile file = fileMap.get(documentDto.getFilename());

                    if (file != null) {
                        String originalFilename = file.getOriginalFilename();
                        Document document = new Document();
                        document.setId(UUID.randomUUID().toString().replace("-", ""));
                        document.setEntityId(documentDto.getEntityId());
                        document.setCurrent(true);
                        DocumentType documentType = (DocumentType) mongoRepositoryReactive.findById((documentDto.getDocumentTypeId()), DocumentType.class).block();

                        if (documentType != null && documentType.getDocumentPurposeId().equals(DocumentPurposeReferenceData.RENEWAL_LICENSE_ID)) {
                            // document.set
                            Query queryPreviousDocuments = new Query();
                            queryPreviousDocuments.addCriteria(Criteria.where("institutionId").is(documentDto.getInstitutionId()));
                            queryPreviousDocuments.addCriteria(Criteria.where("gameTypeId").is(documentDto.getGameTypeId()));
                            queryPreviousDocuments.addCriteria(Criteria.where("documentTypeId").is(documentDto.getDocumentTypeId()));
                            Document previousDocument = (Document) mongoRepositoryReactive.find(queryPreviousDocuments, Document.class).block();
                            if (previousDocument != null) {
                                previousDocument.setArchive(true);
                                previousDocument.setCurrent(false);
                                //mongoRepositoryReactive.saveOrUpdate(previousDocument);
                                mongoRepositoryReactive.saveOrUpdate(previousDocument);
                            }

                        }
                        document.setDescription(documentDto.getDescription());
                        document.setDocumentTypeId(documentDto.getDocumentTypeId());
                        document.setEntity(documentDto.getEntity());
                        document.setEntryDate(LocalDateTime.now());
                        document.setGameTypeId(documentDto.getGameTypeId());
                        document.setFilename(originalFilename);
                        document.setOriginalFilename(originalFilename);
                        document.setMimeType(file.getContentType());
                        document.setArchive(false);
                        document.setInstitutionId(documentDto.getInstitutionId());
                        document.setAgentId(documentDto.getAgentId());
                        document.setPreviousDocumentId(documentDto.getPreviousDocumentId());
                        //document.setValidFrom(new LocalDate(documentDto.getValidFrom()));
                        //document.setValidTo(new LocalDate(documentDto.getValidTo()));
                        DocumentBinary documentBinary = new DocumentBinary();
                        documentBinary.setId(UUID.randomUUID().toString());
                        documentBinary.setFile(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
                        documentBinary.setDocumentId(document.getId());
                        mongoRepositoryReactive.saveOrUpdate(documentBinary);
                        if (document.requiresApproval()) {
                            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
                        }
                        documents.add(document);
                        documentCheck.add(document);
                        //If there is an existing doc we set it to false
                        if (documentDto.getPreviousDocumentId() != null) {
                            Document oldDocument = (Document) mongoRepositoryReactive.findById((documentDto.getPreviousDocumentId()), Document.class).block();
                            if (oldDocument != null) {
                                oldDocument.setCurrent(false);
                                //mongoRepositoryReactive.saveOrUpdate(oldDocument);
                                documents.add(oldDocument);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("An error occurred while saving document", e);
                }
            });

            //This has to be equal. The only time its not is because a file name does not match
            if (documentCheck.size() != files.length) {
                return Mono.just(new ResponseEntity<>("Json & file length do not match. Please make sure the each file has a corresponding filename in the json.", HttpStatus.BAD_REQUEST));
            }
            documents.stream().forEach(doc -> {

                mongoRepositoryReactive.saveOrUpdate(doc);
            });

            return Mono.just(new ResponseEntity<>("FileUpload Successful", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occured while uploading the documents", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getByEntity", params = {"entityId", "entity"})
    @ApiOperation(value = "Get Json By Entity and EntityId", response = ApplicationFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getByEntity(@RequestParam("entityId") String entityId, @RequestParam("entity") String entity, HttpServletResponse httpServletResponse) {

        Query query = new Query();

        if (entityId != null && !entityId.isEmpty()) {
            //Here for each entity type we call its Id field for lookup
            query.addCriteria(Criteria.where("entityId").is(entityId));
        }

        query.addCriteria(Criteria.where("isCurrent").is(true));
        query.addCriteria(Criteria.where("archive").is(false));

        ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());

        ArrayList<DocumentDto> documentsDto = new ArrayList<>();
        documents.forEach(entry -> {
            try {
                entry.setAssociatedProperties();
            } catch (FactNotFoundException e) {
                e.printStackTrace();
            }
            documentsDto.add(entry.convertToDto());
        });

        if (documentsDto.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(documentsDto, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find-by-id", params = {"id"})
    @ApiOperation(value = "Get Document by Id", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getDocumentById(@RequestParam("id") @NotEmpty String id, HttpServletResponse httpServletResponse) {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Document document = (Document) mongoRepositoryReactive.find(query, Document.class).block();
        if (document == null) {
            return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
        }
        return Mono.just(new ResponseEntity<>(document.convertToDto(), HttpStatus.OK));
    }


    @RequestMapping(method = RequestMethod.POST, value = "/reupload", produces = "application/json")
    @ApiOperation(value = "Get Document by Id", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> reUploadDocument(@RequestParam("json") String json,
                                                 @RequestParam("entityName") String entityName,
                                                 @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {

        try {
            DocumentCreateDto documentCreateDto;
            try {
                documentCreateDto = mapper.readValue(json, DocumentCreateDto.class);
                if (documentCreateDto == null) {
                    return Mono.just(new ResponseEntity<>("Invalid Json supplied", HttpStatus.BAD_REQUEST));
                }
            } catch (Exception e) {
                return logAndReturnError(logger, "An error occurred while parsing json", e);
            }
            String previousDocumentId = documentCreateDto.getPreviousDocumentId();
            if (multipartFile.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty file supplied ", HttpStatus.BAD_REQUEST));
            }
            String originalFilename = multipartFile.getOriginalFilename();
            Document document = new Document();
            document.setId(UUID.randomUUID().toString().replace("-", ""));
            document.setEntityId(documentCreateDto.getEntityId());
            document.setCurrent(true);
            document.setDescription(documentCreateDto.getDescription());
            document.setDocumentTypeId(documentCreateDto.getDocumentTypeId());
            document.setEntity(documentCreateDto.getEntity());
            document.setEntryDate(LocalDateTime.now());
            document.setGameTypeId(documentCreateDto.getGameTypeId());
            document.setFilename(originalFilename);
            document.setOriginalFilename(originalFilename);
            document.setMimeType(multipartFile.getContentType());
            document.setArchive(false);
            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            document.setInstitutionId(documentCreateDto.getInstitutionId());
            document.setAgentId(documentCreateDto.getAgentId());
            document.setPreviousDocumentId(documentCreateDto.getPreviousDocumentId());
            if (document.requiresApproval()) {
                document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            }
            try {
                DocumentBinary documentBinary = new DocumentBinary();
                documentBinary.setId(UUID.randomUUID().toString());
                documentBinary.setFile(new Binary(BsonBinarySubType.BINARY, multipartFile.getBytes()));
                documentBinary.setDocumentId(document.getId());
                mongoRepositoryReactive.saveOrUpdate(documentBinary);
            } catch (IOException e) {
                logger.error("An error occurred while setting bytes of document");
            }
            if (StringUtils.isEmpty(previousDocumentId)) {
                return Mono.just(new ResponseEntity<>("Please provide previous document id", HttpStatus.BAD_REQUEST));
            }
            Document oldDocument = (Document) mongoRepositoryReactive.findById(previousDocumentId, Document.class).block();
            if (oldDocument == null) {
                return Mono.just(new ResponseEntity<>("No record found for previous document", HttpStatus.BAD_REQUEST));
            }
            oldDocument.setCurrent(false);
            mongoRepositoryReactive.saveOrUpdate(oldDocument);
            mongoRepositoryReactive.saveOrUpdate(document);
            if (StringUtils.equalsIgnoreCase("applicationForm", entityName)) {
                doApplicationFormDocumentReupload(document);
            }
            if (StringUtils.equalsIgnoreCase("aipForm", entityName)) {
                doAIPFormDocumentReupload(document);
            }
            if (StringUtils.equalsIgnoreCase("renewalForm", entityName)) {
                doRenewalFormDocumentReupload(document);
            }

            String verbiage = String.format("Re uploaded document -> Document Type -> %s, File name -> %s,Entity name -> %s, Id -> %s ",
                    document.getDocumentType(), document.getFilename(), entityName, document.getId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.DOCUMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), document.getOwner(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(document.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while re uploading document", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-institution-document-aip", params = {"gameTypeId", "documentPurposeId", "institutionId"})
    @ApiOperation(value = "Get AIP Documents", response = ApplicationFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getByInstitution(@RequestParam("gameTypeId") String gameTypeId, @RequestParam("documentPurposeId") String documentPurposeId, @RequestParam("institutionId") String institutionId) {


        if (StringUtils.isEmpty(documentPurposeId)) {
            return Mono.just(new ResponseEntity("documentPurposeId is required", HttpStatus.NOT_FOUND));

        }
        if (StringUtils.isEmpty(gameTypeId)) {
            return Mono.just(new ResponseEntity("Provide GameTypeId is required", HttpStatus.NOT_FOUND));

        }
        if (StringUtils.isEmpty(institutionId)) {
            return Mono.just(new ResponseEntity("Provide InstitutionId is required", HttpStatus.NOT_FOUND));

        }

        Query queryDocumentType = new Query();
        queryDocumentType.addCriteria(Criteria.where("documentPurposeId").is(documentPurposeId));
        List<DocumentType> documentTypes = (List<DocumentType>) mongoRepositoryReactive.findAll(queryDocumentType, DocumentType.class).toStream().collect(Collectors.toList());
        List<String> documentTypeIds = new ArrayList<>();
        documentTypes.stream().forEach(documentType -> {
            documentTypeIds.add(documentType.getId());
        });

        Query queryDocument = new Query();
        queryDocument.addCriteria(Criteria.where("documentTypeId").in(documentTypeIds));
        queryDocument.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
        queryDocument.addCriteria(Criteria.where("institutionId").is(institutionId));

        List<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(queryDocument, Document.class).toStream().collect(Collectors.toList());
        ArrayList<DocumentDto> documentsDto = new ArrayList<>();
        documents.forEach(entry -> {
            try {
                entry.setAssociatedProperties();
            } catch (FactNotFoundException e) {
                e.printStackTrace();
            }
            documentsDto.add(entry.convertToDto());
        });

        if (documentsDto.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(documentsDto, HttpStatus.OK));
    }


    @RequestMapping(method = RequestMethod.GET, value = "/unlink-document", params = {"documentId"})
    @ApiOperation(value = "Delete unwanted document", response = String.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getByInstitution(@RequestParam("documentId") String documentId) {

        if (StringUtils.isEmpty(documentId)) {
            return Mono.just(new ResponseEntity("documentId is required", HttpStatus.BAD_REQUEST));

        }


        Document document = (Document) mongoRepositoryReactive.findById(documentId, Document.class).block();

        if (document == null) {
            return Mono.just(new ResponseEntity("No document found found", HttpStatus.NOT_FOUND));
        }

        document.setUnLinkedEntityId(document.getEntityId());
        document.setUnLinkedInstitutionId(document.getInstitutionId());
        document.setEntityId(null);
        document.setInstitutionId(null);

        mongoRepositoryReactive.saveOrUpdate(document);

        return Mono.just(new ResponseEntity("Success", HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/downloadById/{id}")
    @ApiOperation(value = "Download Bytes By Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public void downloadById(@PathVariable("id") String id, HttpServletResponse httpServletResponse) throws FactNotFoundException {

        Document document = (Document) mongoRepositoryReactive.findById((id), Document.class).block();
        DocumentBinary documentBinary = (DocumentBinary) mongoRepositoryReactive.find(new Query(Criteria.where("documentId").is(document.getId())), DocumentBinary.class).block();

        if (document == null) {
            throw new FactNotFoundException("document", id);
        }

        Binary binary = documentBinary.getFile();
        if (binary != null) {
            try {

                String filename = document.getFilename();
                httpServletResponse.setHeader("filename", filename);
                httpServletResponse.setHeader("Content-Disposition", String.format("inline; filename=\"" + filename + "\""));
                httpServletResponse.setContentType(document.getMimeType());
                httpServletResponse.setContentLength(binary.getData().length);

                //We are using Spring FileCopyUtils utility class to copy stream from source to destination.
                //Copy bytes from source to destination(outputstream in this example), closes both streams.
                FileCopyUtils.copy(binary.getData(), httpServletResponse.getOutputStream());

                httpServletResponse.flushBuffer();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }

        logger.info("FileDownload for -> " + id);
    }

    /**
     * @param id DocumentDto id
     * @return DocumentDto full information
     */
    @ApiOperation(value = "Get DocumentDto By Id,documentTypeId,institutionId", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "id", "documentTypeId", "institutionId", "archive", "isCurrent"}, produces = "application/json")
    public Mono<ResponseEntity> getById(@RequestParam("page") int page,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("sortType") String sortType,
                                        @RequestParam("sortProperty") String sortParam,
                                        @RequestParam("id") String id,
                                        @RequestParam("documentTypeId") String documentTypeId,
                                        @RequestParam("institutionId") String institutionId,
                                        @RequestParam("archive") boolean archive,
                                        @RequestParam("isCurrent") boolean isCurrent,
                                        HttpServletResponse httpServletResponse) {

        Query query = new Query();
        if (!StringUtils.isEmpty(institutionId)) {
            query.addCriteria(Criteria.where("institutionId").is(institutionId));

        }
        if (!StringUtils.isEmpty(documentTypeId)) {
            query.addCriteria(Criteria.where("documentTypeId").is(documentTypeId));

        }
        if (!StringUtils.isEmpty(id)) {
            query.addCriteria(Criteria.where("id").is(id));
        }

        if (archive) {
            query.addCriteria(Criteria.where("archive").is(archive));
        } else {
            query.addCriteria(Criteria.where("isCurrent").is(isCurrent));
        }

        if (page == 0) {
            long count = mongoRepositoryReactive.count(query, Document.class).block();
            httpServletResponse.setHeader("TotalCount", String.valueOf(count));
        }

        Sort sort;
        if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
            sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                    sortParam);
        } else {
            sort = new Sort(Sort.Direction.DESC, "id");
        }
        query.with(PageRequest.of(page, pageSize, sort));
        query.with(sort);

        List<Document> documents = (List<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
        if (documents.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }
        List<DocumentDto> documentDtos = new ArrayList<>();
        documents.stream().forEach(document -> {
            try {
                document.setAssociatedProperties();
            } catch (FactNotFoundException e) {
                e.printStackTrace();
            }
            documentDtos.add(document.convertToDto());
        });

        return Mono.just(new ResponseEntity(documentDtos, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getEntityDocuments", params = {"entityId", "purposeId"})
    @ApiOperation(value = "Get uploaded and new documents", response = DocumentDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getEntityDocuments(@RequestParam("entityId") String entityId, @RequestParam("purposeId") String purposeId, HttpServletResponse httpServletResponse) {

        Query queryDocumentType = new Query();
        queryDocumentType.addCriteria(Criteria.where("documentPurposeId").is(purposeId));
        queryDocumentType.addCriteria(Criteria.where("active").is(true));
        List<DocumentType> documentTypes = (List<DocumentType>) mongoRepositoryReactive.findAll(queryDocumentType, DocumentType.class).toStream().collect(Collectors.toList());
        List<String> documentTypeIds = new ArrayList<>();
        documentTypes.stream().forEach(documentType -> {
            documentTypeIds.add(documentType.getId());
        });

        Query queryDocument = new Query();
        queryDocument.addCriteria(Criteria.where("documentTypeId").in(documentTypeIds));
        queryDocument.addCriteria(Criteria.where("entityId").is(entityId));
        queryDocument.addCriteria(Criteria.where("isCurrent").is(true));

        ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(queryDocument, Document.class).toStream().collect(Collectors.toList());

        //We use this to temporarily store so that we can merge
        HashMap<String, EntityDocumentDto> entityDocuments = new HashMap<>();

        ArrayList<EntityDocumentDto> documentsDto = new ArrayList<>();
        documents.forEach(entry -> {
            try {
                entry.setAssociatedProperties();
            } catch (FactNotFoundException e) {
                e.printStackTrace();
            }
            EntityDocumentDto dto = new EntityDocumentDto();
            dto.setDescription(entry.getDescription());
            dto.setDocumentTypeId(entry.getDocumentTypeId());
            dto.setEntityId(entry.getEntityId());
            dto.setFilename(entry.getFilename());
            dto.setMimeType(entry.getMimeType());
            dto.setId(entry.getId());
            if (entry.getDocumentType() != null) {
                dto.setDocumentType(entry.getDocumentType().getName());
                dto.setActive(entry.getDocumentType().isActive());
                dto.setRequired(entry.getDocumentType().isRequired());
            }
            entityDocuments.put(entry.getDocumentTypeId(), dto);

            documentsDto.add(dto);
        });

        //ArrayList<DocumentType> documentTypes = (ArrayList<DocumentType>) mongoRepositoryReactive.findAll(new Query(Criteria.where("documentPurposeId").is(purposeId)), DocumentType.class).toStream().collect(Collectors.toList());
        documentTypes.forEach(entry -> {
            if (entityDocuments.get(entry.getId()) == null) {
                EntityDocumentDto dto = new EntityDocumentDto();
                dto.setDescription(entry.getDescription());
                dto.setDocumentType(entry.getName());
                dto.setDocumentTypeId(entry.getId());
                dto.setActive(entry.isActive());
                dto.setRequired(entry.isRequired());
                entityDocuments.put(entry.getId(), dto);
                documentsDto.add(dto);
            }
        });
        if (documentsDto.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(documentsDto, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getDocumentPurposes")
    @ApiOperation(value = "Get all document purpose ID", response = DocumentPurposeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getDocumentPurpose() {
        try {
            Map documentMap = Mapstore.STORE.get("DocumentPurpose");
            ArrayList<DocumentPurpose> documentPurposes = new ArrayList<DocumentPurpose>(documentMap.values());
            List<DocumentPurposeDto> documentPurposeDtoLists = new ArrayList<>();
            documentPurposes.forEach(factObject -> {
                DocumentPurpose documentPurpose = factObject;
                documentPurposeDtoLists.add(documentPurpose.convertToDto());
            });
            return Mono.just(new ResponseEntity(documentPurposeDtoLists, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while getting all document Types";
            return Mono.just(new ResponseEntity(errorMsg, HttpStatus.BAD_REQUEST));
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/open-upload", produces = "application/json")
    @ApiOperation(value = "Upload Document without authentication Document", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> openUpload(@RequestParam("json") String json,
                                           @RequestParam("files") @NotEmpty MultipartFile[] files) {

        //@TODO If its a file replace we have to validate that the old id comes with the json
        if (json == null || json.isEmpty()) {
            return Mono.just(new ResponseEntity<>("Please specify a json body", HttpStatus.BAD_REQUEST));
        }

        List<DocumentCreateDto> documentDtos;

        try {
            documentDtos = mapper.readValue(json, List.class, DocumentCreateDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>("Invalid Json", HttpStatus.BAD_REQUEST));
        }
        try {
            //Put the files in a map
            HashMap<String, MultipartFile> fileMap = new HashMap<>();
            for (MultipartFile file : files) {
                fileMap.put(file.getOriginalFilename(), file);
            }
            ArrayList<FactObject> documents = new ArrayList<>();
            ArrayList<FactObject> documentCheck = new ArrayList<>();
            documentDtos.stream().forEach(documentDto -> {
                try {
                    logger.info("Creating file : " + documentDto.getFilename());

                    MultipartFile file = fileMap.get(documentDto.getFilename());

                    if (file != null) {
                        String originalFilename = file.getOriginalFilename();
                        Document document = new Document();
                        document.setId(UUID.randomUUID().toString().replace("-", ""));
                        document.setEntityId(documentDto.getEntityId());
                        document.setCurrent(true);
                        document.setDescription(documentDto.getDescription());
                        document.setDocumentTypeId(documentDto.getDocumentTypeId());
                        document.setEntity(documentDto.getEntity());
                        document.setEntryDate(LocalDateTime.now());
                        document.setGameTypeId(documentDto.getGameTypeId());
                        document.setFilename(originalFilename);
                        document.setOriginalFilename(originalFilename);
                        document.setMimeType(file.getContentType());
                        document.setArchive(false);
                        document.setInstitutionId(documentDto.getInstitutionId());
                        document.setAgentId(documentDto.getAgentId());
                        document.setPreviousDocumentId(documentDto.getPreviousDocumentId());
                        DocumentBinary documentBinary = new DocumentBinary();
                        documentBinary.setId(UUID.randomUUID().toString());
                        documentBinary.setFile(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
                        documentBinary.setDocumentId(document.getId());
                        mongoRepositoryReactive.saveOrUpdate(documentBinary);
                        documents.add(document);
                        documentCheck.add(document);
                        //If there is an existing doc we set it to false

                    }
                } catch (Exception e) {
                    logger.error("An error occurred while saving document", e);
                }
            });

            if (documentCheck.size() != files.length) {
                return Mono.just(new ResponseEntity<>("Json & file length do not match. Please make sure the each file has a corresponding filename in the json.", HttpStatus.BAD_REQUEST));
            }
            documents.stream().forEach(doc -> {
                mongoRepositoryReactive.saveOrUpdate(doc);
            });

            return Mono.just(new ResponseEntity<>("FileUpload Successful", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while uploading the documents", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/add-comment", produces = "application/json")
    @ApiOperation(value = "Add Comment To  Document", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> addCommentToDocument(@RequestBody @Valid DocumentCommentDto documentCommentDto, HttpServletRequest request) {
        try {
            String documentId = documentCommentDto.getDocumentId();
            Document document = findDocumentById(documentId);
            if (document == null) {
                return Mono.just(new ResponseEntity<>(String.format("Document with id %s not found", documentId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            document.getComments().add(CommentDetail.fromCommentAndUser(documentCommentDto.getComment(), loggedInUser.getFullName()));
            mongoRepositoryReactive.saveOrUpdate(document);
            return Mono.just(new ResponseEntity<>(document.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment to document", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/approve", produces = "application/json", params = {"entityName"})
    @ApiOperation(value = "Approve Document", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> approveDocument(@RequestBody @Valid FormDocumentOperationDto documentOperationDto,
                                                @RequestParam("entityName") @NotNull String entityName, HttpServletRequest request) {
        try {
            String documentId = documentOperationDto.getDocumentId();
            Document document = findDocumentById(documentId);
            if (document == null) {
                return Mono.just(new ResponseEntity<>(String.format("Document with Id %s does not exist", documentId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            DocumentType documentType = document.getDocumentType();
            if (!StringUtils.equals(loggedInUser.getId(), documentType.getApproverId())) {
                return Mono.just(new ResponseEntity<>("Approving user should be document type approver", HttpStatus.BAD_REQUEST));
            }
            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.APPROVED_ID);
            document.getComments().add(CommentDetail.fromCommentAndUser(documentOperationDto.getComment(), loggedInUser.getFullName()));
            mongoRepositoryReactive.saveOrUpdate(document);

            if (StringUtils.equalsIgnoreCase("applicationForm", entityName)) {
                approveApplicationFormDocument(document, documentOperationDto.getComment());
            }
            if (StringUtils.equalsIgnoreCase("renewalForm", entityName)) {
                approveRenewalFormDocument(document);
            }
            if (StringUtils.equalsIgnoreCase("aipForm", entityName)) {
                approveAIPFormDocument(document);
            }

            String verbiage = String.format("Approved document -> Document Type -> %s, File name -> %s, Id -> %s ", document.getDocumentType(), document.getFilename(), documentId);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.DOCUMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), document.getOwner(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(document.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving document", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/archive-multiple", produces = "application/json")
    @ApiOperation(value = "Archive Multiple Documents", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> archiveDocuments(@RequestBody @Valid DocumentArchiveRequest documentArchiveRequest, HttpServletRequest request) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String documentId : documentArchiveRequest.getDocumentIds()) {
                builder.append(documentId);
                builder.append(",");
                Document document = findDocumentById(documentId);
                if (document != null) {
                    document.setArchive(true);
                    mongoRepositoryReactive.saveOrUpdate(document);
                }
            }

            String verbiage = String.format("Archived Multiple Documents  -> Document Ids -> %s ", builder.toString());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.DOCUMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>("Document Archived", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while archiving documents", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/restore-multiple", produces = "application/json")
    @ApiOperation(value = "Restore Multiple Documents", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> unArchiveDocuments(@RequestBody @Valid DocumentArchiveRequest documentArchiveRequest, HttpServletRequest request) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String documentId : documentArchiveRequest.getDocumentIds()) {
                builder.append(documentId);
                builder.append(",");
                Document document = findDocumentById(documentId);
                if (document != null) {
                    document.setArchive(false);
                    mongoRepositoryReactive.saveOrUpdate(document);
                }
            }

            String verbiage = String.format("Restored Multiple Documents  -> Document Ids -> %s ", builder.toString());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.DOCUMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>("Document Archived", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while un archiving documents", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/toggle-multiple-document-archive-status", produces = "application/json")
    @ApiOperation(value = "Toggle Multiple Documents Archive Status", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> toggleArchiveDocumentsStatus(@RequestBody @Valid DocumentArchiveRequest documentArchiveRequest, HttpServletRequest request) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String documentId : documentArchiveRequest.getDocumentIds()) {
                builder.append(documentId);
                builder.append(",");
                Document document = findDocumentById(documentId);
                if (document != null) {
                    boolean newArchiveStatus = !document.isArchive();
                    document.setArchive(newArchiveStatus);
                    mongoRepositoryReactive.saveOrUpdate(document);
                }
            }

            String verbiage = String.format("Toggled Multiple Documents Archive Status -> Document Ids -> %s ", builder.toString());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.DOCUMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            return Mono.just(new ResponseEntity<>("Document Archive Status Toggled", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while toggling documents", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject", produces = "application/json", params = {"entityName"})
    @ApiOperation(value = "Reject Document", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> rejectDocument(@RequestBody @Valid FormDocumentOperationDto documentOperationDto,
                                               @RequestParam("entityName") String entityName, HttpServletRequest request) {
        try {
            String documentId = documentOperationDto.getDocumentId();
            Document document = findDocumentById(documentId);
            if (document == null) {
                return Mono.just(new ResponseEntity<>(String.format("Document with Id %s does not exist", documentId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            DocumentType documentType = document.getDocumentType();
            if (!StringUtils.equals(loggedInUser.getId(), documentType.getApproverId())) {
                return Mono.just(new ResponseEntity<>("Rejecting user should be document type approver", HttpStatus.BAD_REQUEST));
            }
            document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.REJECTED_ID);
            document.getComments().add(CommentDetail.fromCommentAndUser(documentOperationDto.getComment(), loggedInUser.getFullName()));
            mongoRepositoryReactive.saveOrUpdate(document);

            if (StringUtils.equalsIgnoreCase("applicationForm", entityName)) {
                rejectApplicationFormDocument(document, documentOperationDto.getComment());
            }
            if (StringUtils.equalsIgnoreCase("renewalForm", entityName)) {
                rejectRenewalFormDocument(document, documentOperationDto.getComment());
            }
            if (StringUtils.equalsIgnoreCase("aipForm", entityName)) {
                rejectAIPFormDocument(document, documentOperationDto.getComment());
            }

            String verbiage = String.format("Rejected document -> Document Type -> %s, File name -> %s, Id -> %s ", document.getDocumentType(), document.getFilename(), documentId);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.DOCUMENT_ID,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), document.getOwner(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(document.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while approving document", e);
        }
    }

    private void rejectApplicationFormDocument(Document document, String latestComment) {
        applicationFormService.rejectApplicationFormDocument(document, latestComment);
    }

    private void rejectAIPFormDocument(Document document, String comment) {
        applicationFormService.rejectAIPFormDocument(document, comment);
    }

    private void rejectRenewalFormDocument(Document document, String comment) {
        renewalFormService.rejectRenewalFormDocument(document, comment);
    }

    private void approveApplicationFormDocument(Document document, String latestComment) {
        applicationFormService.approveApplicationFormDocument(document);
    }

    private void approveRenewalFormDocument(Document document) {
        renewalFormService.approveRenewalFormDocument(document);
    }

    private void approveAIPFormDocument(Document document) {
        applicationFormService.approveAIPFormDocument(document);
    }

    private void doRenewalFormDocumentReupload(Document document) {
        document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
        mongoRepositoryReactive.saveOrUpdate(document);
        renewalFormService.doDocumentReuploadNotification(document);
    }

    private void doApplicationFormDocumentReupload(Document document) {
        applicationFormService.doDocumentReuploadNotification(document);
    }

    private void doAIPFormDocumentReupload(Document document) {
        document.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
        mongoRepositoryReactive.saveOrUpdate(document);
        applicationFormService.doAIPDocumentReuploadNotification(document);
    }

    private Document findDocumentById(String documentId) {
        if (StringUtils.isEmpty(documentId)) {
            return null;
        }
        return (Document) mongoRepositoryReactive.findById(documentId, Document.class).block();
    }
}
