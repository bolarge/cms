package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.InspectionForm;
import com.software.finatech.lslb.cms.service.dto.InspectionFormCreateDto;
import com.software.finatech.lslb.cms.service.dto.InspectionFormDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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

@Api(value = "InspectionForm", description = "", tags = "")
@RestController
@RequestMapping("/api/v1/inspectionForm")
public class InspectionFormController extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds","institutionId","agentId","gamingMachineId"})
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
          }if (!StringUtils.isEmpty(gamingMachineId)) {
              query.addCriteria(Criteria.where("gamingMachineId").in(gamingMachineId));
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
                sort = new Sort(Sort.Direction.DESC, "id");
            }
           query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);
            ArrayList<InspectionFormDto> inspectionFormDtos = new ArrayList<>();
            List<InspectionForm> inspectionForms = ( List<InspectionForm>) mongoRepositoryReactive.findAll(query, InspectionForm.class).toStream().collect(Collectors.toList());

             if(inspectionForms.size()==0){
                 return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
             }

            inspectionForms.stream().forEach(inspectionForm -> {
             inspectionFormDtos.add(inspectionForm.convertToDto());
         });
            return Mono.just(new ResponseEntity<>(inspectionFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
          return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));
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
                    return Mono.just(new ResponseEntity("Invalid Date format. " +
                            "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
                }
                fromDate = new LocalDate(startDate);

            } else {

                return Mono.just(new ResponseEntity("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));

            }
            InspectionForm inspectionForm = new InspectionForm();

            inspectionForm.setId(UUID.randomUUID().toString());
            inspectionForm.setAgentId(inspectionFormCreateDto.getAgentId());
            inspectionForm.setComment(inspectionFormCreateDto.getComment());
            inspectionForm.setGameTypeId(inspectionFormCreateDto.getGameTypeId());
            inspectionForm.setGamingMachineId(inspectionFormCreateDto.getGamingMachineId());

            inspectionForm.setInspectionDate(fromDate);
            inspectionForm.setUserRoleId(inspectionFormCreateDto.getUserRoleId());
            inspectionForm.setInstitutionId(inspectionFormCreateDto.getInstitutionId());
            mongoRepositoryReactive.saveOrUpdate(inspectionForm);

            return Mono.just(new ResponseEntity<>(inspectionForm.convertToDto(), HttpStatus.OK));
        }catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }

    }


}
