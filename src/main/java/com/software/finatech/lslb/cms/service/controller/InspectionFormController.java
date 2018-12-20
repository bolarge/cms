package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.InspectionForm;
import com.software.finatech.lslb.cms.service.domain.InspectionFormComments;
import com.software.finatech.lslb.cms.service.domain.InspectionStatus;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.InspectionCommentCreateDto;
import com.software.finatech.lslb.cms.service.dto.InspectionFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.InspectionFormDto;
import com.software.finatech.lslb.cms.service.referencedata.InspectionStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.InspectionFormMailSenderAsync;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(value = "InspectionForm", description = "", tags = "Inspection Form Controller")
@RestController
@RequestMapping("/api/v1/inspectionForm")
public class InspectionFormController extends BaseController {

    @Autowired
    private InspectionFormMailSenderAsync inspectionFormMailSenderAsync;

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds", "institutionId", "agentId", "gamingMachineId", "dateProperty", "status"})
    @ApiOperation(value = "Get all Inspections", response = InspectionForm.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getAllInspection(@RequestParam("page") int page,
                                                 @RequestParam("pageSize") int pageSize,
                                                 @RequestParam("sortType") String sortType,
                                                 @RequestParam("sortProperty") String sortParam,
                                                 @RequestParam("institutionId") String institutionId,
                                                 @RequestParam("agentId") String agentId,
                                                 @RequestParam("status") String status,
                                                 @RequestParam("dateProperty") String dateProperty,
                                                 @RequestParam("fromDate") String fromDate,
                                                 @RequestParam("toDate") String toDate,
                                                 @RequestParam("gamingMachineId") String gamingMachineId,
                                                 @RequestParam("gameTypeIds") String gameTypeIds,
                                                 HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(gameTypeIds)) {
                List<String> gameTypeIdList = Arrays.asList(gameTypeIds.split("-"));
                query.addCriteria(Criteria.where("gameTypeId").in(gameTypeIdList));
            }
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").in(institutionId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").in(agentId));
            }
            if (!StringUtils.isEmpty(status)) {
                query.addCriteria(Criteria.where("status").in(status));
            }
            if (!StringUtils.isEmpty(gamingMachineId)) {
                query.addCriteria(Criteria.where("gamingMachineId").in(gamingMachineId));
            }
            if (!StringUtils.isEmpty(fromDate) && !StringUtils.isEmpty(toDate)) {
                if (StringUtils.isEmpty(dateProperty)) {
                    dateProperty = "createdAt";
                }
                if (dateProperty.equalsIgnoreCase("createdAt")) {

                    fromDate = fromDate + " 00:00:00";
                    toDate = toDate + " 23:59:59";
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startDate = new LocalDateTime(formatter.parseLocalDateTime(fromDate));
                    LocalDateTime endDate = new LocalDateTime(formatter.parseLocalDateTime(toDate));
                    query.addCriteria(Criteria.where(dateProperty).gte(startDate).lte(endDate));

                } else {
                    LocalDate startDate = new LocalDate(fromDate);
                    LocalDate endDate = new LocalDate(toDate);
                    query.addCriteria(Criteria.where(dateProperty).gte(startDate).lte(endDate));
                }


            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, InspectionForm.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }
            Sort sort;
            if (!StringUtils.isEmpty(sortType) && !StringUtils.isEmpty(sortParam)) {
                sort = new Sort((sortType.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortParam);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);
            ArrayList<InspectionFormDto> inspectionFormDtos = new ArrayList<>();


            List<InspectionForm> inspectionForms = (List<InspectionForm>) mongoRepositoryReactive.findAll(query, InspectionForm.class).toStream().collect(Collectors.toList());

            if (inspectionForms.size() == 0) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
            }

            inspectionForms.stream().forEach(inspectionForm -> {
                inspectionFormDtos.add(inspectionForm.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(inspectionFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
            return Mono.just(new ResponseEntity<>("An error occurred while fetching data", HttpStatus.BAD_REQUEST));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ApiOperation(value = "Create new Inspection Form", response = InspectionForm.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createInspectionForm(@RequestBody @Valid InspectionFormCreateDto inspectionFormCreateDto) {

        try {

            LocalDate fromDate;
            String startDate = inspectionFormCreateDto.getInspectionDate();
            if ((startDate != "" && !startDate.isEmpty())) {
                if (!startDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                    return Mono.just(new ResponseEntity<>("Invalid Date format. " +
                            "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
                }
                fromDate = new LocalDate(startDate);

            } else {
                return Mono.just(new ResponseEntity<>("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));

            }
            InspectionForm inspectionForm = new InspectionForm();
            inspectionForm.setId(UUID.randomUUID().toString());
            inspectionForm.setAgentId(inspectionFormCreateDto.getAgentId());
            inspectionForm.setBody(inspectionFormCreateDto.getBody());
            inspectionForm.setGameTypeId(inspectionFormCreateDto.getGameTypeId());
            inspectionForm.setGamingMachineId(inspectionFormCreateDto.getGamingMachineId());
            inspectionForm.setSubject(inspectionFormCreateDto.getSubject());
            inspectionForm.setInspectionDate(fromDate);
            inspectionForm.setAgentBusinessAddress(inspectionFormCreateDto.getAgentBusinessAddress());
            inspectionForm.setStatus(InspectionStatusReferenceData.NEW);
            //inspectionForm.setUserRoleId(inspectionFormCreateDto.getUserRoleId());
            inspectionForm.setInstitutionId(inspectionFormCreateDto.getInstitutionId());
            mongoRepositoryReactive.saveOrUpdate(inspectionForm);
            inspectionFormMailSenderAsync.sendNewInspectionFormNotificationToLSLBAdmins(inspectionForm);

            return Mono.just(new ResponseEntity<>(inspectionForm.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please Contact Admin", HttpStatus.BAD_REQUEST));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-by-id", params = {"id"})
    @ApiOperation(value = "Get Inspection Form By Id", response = InspectionFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getInspectionForm(@RequestParam("id") String id) {
        try {
            InspectionForm inspectionForm = (InspectionForm) mongoRepositoryReactive.findById(id, InspectionForm.class).block();

            if (inspectionForm == null) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(inspectionForm.convertToDto(), HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/update-to-in-progress", params = {"id"})
    @ApiOperation(value = "Update New Inspection form from New to In-progress", response = InspectionFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateInspectionForm(@RequestParam("id") String id) {
        try {
            InspectionForm inspectionForm = (InspectionForm) mongoRepositoryReactive.findById(id, InspectionForm.class).block();

            if (inspectionForm == null) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
            }
            if (inspectionForm.getStatus().equals(InspectionStatusReferenceData.NEW)) {
                inspectionForm.setStatus(InspectionStatusReferenceData.IN_PROGRESS);
                mongoRepositoryReactive.saveOrUpdate(inspectionForm);
            }
            return Mono.just(new ResponseEntity<>(inspectionForm.convertToDto(), HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/update-to-closed", params = {"id"})
    @ApiOperation(value = "Update New Inspection form from New to In-progress", response = InspectionFormDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateInspectionFormToClosed(@RequestParam("id") String id) {
        try {
            InspectionForm inspectionForm = (InspectionForm) mongoRepositoryReactive.findById(id, InspectionForm.class).block();

            if (inspectionForm == null) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
            }
            if (inspectionForm.getStatus().equals(InspectionStatusReferenceData.IN_PROGRESS)) {
                inspectionForm.setStatus(InspectionStatusReferenceData.CLOSED);
                mongoRepositoryReactive.saveOrUpdate(inspectionForm);
            } else {
                return Mono.just(new ResponseEntity<>("Inspection status is not In Progress", HttpStatus.BAD_REQUEST));

            }
            return Mono.just(new ResponseEntity<>(inspectionForm.convertToDto(), HttpStatus.OK));

        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please contact admin", HttpStatus.BAD_REQUEST));

        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add-comment")
    @ApiOperation(value = "Add comment to Inspection Form", response = InspectionForm.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createComment(@RequestBody @Valid InspectionCommentCreateDto inspectionCommentCreateDto) {

        try {

            InspectionFormComments inspectionFormComments = new InspectionFormComments();
            inspectionFormComments.setComment(inspectionCommentCreateDto.getComment());
            inspectionFormComments.setInspectionFormId(inspectionCommentCreateDto.getInspectionFormId());
            inspectionFormComments.setUserId(inspectionCommentCreateDto.getUserId());

            mongoRepositoryReactive.saveOrUpdate(inspectionFormComments);
            InspectionForm inspectionForm = (InspectionForm) mongoRepositoryReactive.findById(inspectionCommentCreateDto.getInspectionFormId(), InspectionForm.class).block();
            if (inspectionForm == null) {
                return Mono.just(new ResponseEntity<>("Invalid Inspection Form", HttpStatus.BAD_REQUEST));

            }
            return Mono.just(new ResponseEntity<>(inspectionForm.convertToDto(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please Contact Admin", HttpStatus.BAD_REQUEST));

        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/agent-addresses", params = {"agentId"})
    @ApiOperation(value = "Get Agent Addresses", response = ArrayList.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getAgentAddresses(@RequestParam("agentId") String agentId) {

        try {
            Agent agent = (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
            if (agent == null) {
                return Mono.just(new ResponseEntity<>("Invalid Agent", HttpStatus.BAD_REQUEST));

            }
            return Mono.just(new ResponseEntity<>(agent.getBusinessAddresses(), HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please Contact Admin", HttpStatus.BAD_REQUEST));

        }

    }


    @RequestMapping(method = RequestMethod.GET, value = "/inspection-status")
    @ApiOperation(value = "Get Agent Addresses", response = ArrayList.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getAllStatuses() {

        try {
            Query query = new Query();
            List<InspectionStatus> inspectionStatuses = (List<InspectionStatus>) mongoRepositoryReactive.findAll(query, InspectionStatus.class).toStream().collect(Collectors.toList());
            if (inspectionStatuses.size() == 0) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
            }
            ArrayList<EnumeratedFactDto> enumeratedFactDtos = new ArrayList<>();
            inspectionStatuses.stream().forEach(inspectionStatus -> {
                enumeratedFactDtos.add(inspectionStatus.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(enumeratedFactDtos, HttpStatus.OK));
        } catch (Exception ex) {
            return Mono.just(new ResponseEntity<>("Error! Please Contact Admin", HttpStatus.BAD_REQUEST));

        }

    }


}
