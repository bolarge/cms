package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.controller.BaseController;
import com.software.finatech.lslb.cms.service.domain.ReportForm;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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

@Api(value = "ReportForm", description = "", tags = "")
@RestController
@RequestMapping("/api/v1/reportForm")
public class ReportFormController extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "gameTypeIds","institutionId","agentId","gamingMachineId"})
    @ApiOperation(value = "Get all Reports", response = ReportForm.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getAllReport(@RequestParam("page") int page,
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
                long count = mongoRepositoryReactive.count(query, ReportForm.class).block();
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
            ArrayList<ReportFormDto> reportFormDtos= new ArrayList<>();
            List<ReportForm> reportForms= (List<ReportForm>) mongoRepositoryReactive.findAll(query, ReportForm.class).toStream().collect(Collectors.toList());

             if(reportFormDtos.size()==0){
                 return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
             }

            reportForms.stream().forEach(reportForm -> {
             reportFormDtos.add(reportForm.convertToDto());
         });
            return Mono.just(new ResponseEntity<>(reportFormDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while fetching all institutions";
          return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));
      }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ApiOperation(value = "Create new Report Form", response = ReportForm.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> createReportForm(@RequestBody @Valid ReportFormCreateDto reportFormCreateDto) {

        try {

            LocalDate fromDate;
            String startDate = reportFormCreateDto.getReportedDate();
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
            ReportForm reportForm = new ReportForm();

            reportForm.setId(UUID.randomUUID().toString());
            reportForm.setAgentId(reportFormCreateDto.getAgentId());
            reportForm.setComment(reportFormCreateDto.getComment());
            reportForm.setGameTypeId(reportFormCreateDto.getGameTypeId());
            reportForm.setGamingMachineId(reportFormCreateDto.getGamingMachineId());

            reportForm.setReportedDate(fromDate);
            reportForm.setUserRoleId(reportFormCreateDto.getUserRoleId());
            reportForm.setInstitutionId(reportFormCreateDto.getInstitutionId());
            mongoRepositoryReactive.saveOrUpdate(reportForm);

            return Mono.just(new ResponseEntity<>(reportForm.convertToDto(), HttpStatus.OK));
        }catch (Exception ex){
            return Mono.just(new ResponseEntity<>("Hey Something Broke", HttpStatus.BAD_REQUEST));

        }

    }


}
