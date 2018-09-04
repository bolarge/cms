package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.domain.DocumentType;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.dto.DocumentCreateDto;
import com.software.finatech.lslb.cms.service.dto.DocumentDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.referencedata.DocumentPurposeReferenceData;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "Document", description = "For everything related to documents", tags = "")
@RestController
@RequestMapping("/api/v1/document")
public class DocumentController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/upload", produces = "application/json")
    @ApiOperation(value = "Upload Document", response = DocumentCreateDto.class, consumes = "application/json")
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

                        if(documentType!=null && documentType.getDocumentPurposeId().equals(DocumentPurposeReferenceData.RENEWAL_LICENSE_ID)){
                           // document.set
                            Query queryPreviousDocuments = new Query();
                            queryPreviousDocuments.addCriteria(Criteria.where("institutionId").is(documentDto.getInstitutionId()));
                            queryPreviousDocuments.addCriteria(Criteria.where("gameTypeId").is(documentDto.getGameTypeId()));
                            queryPreviousDocuments.addCriteria(Criteria.where("documentTypeId").is(documentDto.getDocumentTypeId()));
                            Document previousDocument = (Document) mongoRepositoryReactive.find(queryPreviousDocuments, Document.class).block();
                            previousDocument.setArchive(true);
                            mongoRepositoryReactive.saveOrUpdate(previousDocument);

                        }
                        document.setDescription(documentDto.getDescription());
                        document.setDocumentTypeId(documentDto.getDocumentTypeId());
                        document.setEntity(documentDto.getEntity());
                        document.setEntryDate(LocalDateTime.now());
                        document.setFilename(originalFilename);
                        document.setOriginalFilename(originalFilename);
                        document.setMimeType(file.getContentType());
                        document.setArchive(false);
                        document.setInstitutionId(documentDto.getInstitutionId());
                        document.setPreviousDocumentId(documentDto.getPreviousDocumentId());
                        //document.setValidFrom(new LocalDate(documentDto.getValidFrom()));
                        //document.setValidTo(new LocalDate(documentDto.getValidTo()));
                        document.setFile(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
                        documents.add(document);
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
            if (documents.size() != files.length) {
                return Mono.just(new ResponseEntity<>("Json & file length do not match. Please make sure the each file has a corresponding filename in the json.", HttpStatus.BAD_REQUEST));
            }
            documents.stream().forEach(doc -> {
                mongoRepositoryReactive.saveOrUpdate(doc);
            });

            return Mono.just(new ResponseEntity<>(documents, HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occured while uploading the documents", e);
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

    @RequestMapping(method = RequestMethod.GET, value = "/downloadById/{id}")
    @ApiOperation(value = "Download Bytes By Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public void downloadById(@PathVariable("id") String id, HttpServletResponse httpServletResponse) throws FactNotFoundException {

        Document document = (Document) mongoRepositoryReactive.findById((id), Document.class).block();

        if (document == null) {
            throw new FactNotFoundException("document", id);
        }

        Binary binary = document.getFile();
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
    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page","pageSize","sortType","sortProperty","id", "documentTypeId","institutionId","archive"}, produces = "application/json")
    public Mono<ResponseEntity> getById(@RequestParam("page") int page,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("sortType") String sortType,
                                        @RequestParam("sortProperty") String sortParam,
                                        @RequestParam("id") String id,
                                        @RequestParam("documentTypeId") String documentTypeId,
                                        @RequestParam("institutionId") String institutionId,
                                        @RequestParam("archive") boolean archive,
                                        HttpServletResponse httpServletResponse) {

        Query query = new Query();
        if(!StringUtils.isEmpty(institutionId)){
            query.addCriteria(Criteria.where("institutionId").is(institutionId));

        }
        if(!StringUtils.isEmpty(documentTypeId)){
            query.addCriteria(Criteria.where("documentTypeId").is(documentTypeId));

        }
        if(!StringUtils.isEmpty(id)){
            query.addCriteria(Criteria.where("id").is(id));
        }

            query.addCriteria(Criteria.where("archive").is(archive));


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

        List<Document> documents = (List<Document>)mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
        if (documents.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }
        List<DocumentDto> documentDtos= new ArrayList<>();
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
}
