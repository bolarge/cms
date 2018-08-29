package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.dto.ApplicationFormDto;
import com.software.finatech.lslb.cms.service.dto.DocumentCreateDto;
import com.software.finatech.lslb.cms.service.dto.DocumentDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "Document", description = "For everything related to documents", tags = "")
@RestController
@RequestMapping("/api/v1/document")
public class DocumentController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/upload", produces ="application/json")
    @ApiOperation(value = "Upload Document", response = DocumentCreateDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> upload(@RequestParam("json") String json, @RequestParam("files") @NotEmpty MultipartFile[] files) {

        //@TODO If its a file replace we have to validate that the old id comes with the json
        if (json == null || json.isEmpty()) {
            return Mono.just(new ResponseEntity<>("Please specify a json body", HttpStatus.BAD_REQUEST));
        }

        List<DocumentCreateDto> documentDtos;

        try{
            documentDtos = mapper.readValue(json, List.class,DocumentCreateDto.class);
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

            //We then reconcile it with the document objects
            //ArrayList<FactObject> documents = new ArrayList<>();
            documentDtos.stream().forEach(documentDto -> {
                try {
                    logger.info("Creating file : " + documentDto.getFilename());

                    MultipartFile file = fileMap.get(documentDto.getFilename());

                    if(file != null) {
                        String originalFilename = file.getOriginalFilename();
                        Document document = new Document();
                        document.setId(UUID.randomUUID().toString().replace("-", ""));
                        document.setEntity(documentDto.getEntityId());
                        document.setCurrent(true);
                        document.setDescription(documentDto.getDescription());
                        document.setDocumentTypeId(documentDto.getDocumentTypeId());
                        document.setEntity(documentDto.getEntity());
                        document.setEntryDate(LocalDateTime.now());
                        document.setFilename(originalFilename);
                        document.setOriginalFilename(originalFilename);
                        document.setMimeType(file.getContentType());
                        document.setPreviousDocumentId(documentDto.getPreviousDocumentId());
                        document.setValidFrom(new LocalDate(documentDto.getValidFrom()));
                        document.setValidTo(new LocalDate(documentDto.getValidTo()));
                        document.setFile(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
                        mongoRepositoryReactive.saveOrUpdate(document);
                        //If there is an existing doc we set it to false
                        if (documentDto.getPreviousDocumentId() != null) {
                            Document oldDocument = (Document) mongoRepositoryReactive.findById((documentDto.getPreviousDocumentId()), Document.class).block();
                            if (oldDocument != null) {
                                oldDocument.setCurrent(false);
                                mongoRepositoryReactive.saveOrUpdate(oldDocument);
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.error("An error occurred while saving document", e);
                }
            });
            return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
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

        ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());

        ArrayList<DocumentDto> documentsDto = new ArrayList<>();
        documents.forEach(entry -> {
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
    @ApiOperation(value = "Get DocumentDto By Id", response = DocumentDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Mono<ResponseEntity> getById(@PathVariable String id) {
        Document document = (Document) mongoRepositoryReactive.findById(id, Document.class).block();
        if (document == null) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(document.convertToDto(), HttpStatus.OK));
    }
}
