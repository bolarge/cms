package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.AuditAction;
import com.software.finatech.lslb.cms.service.domain.AuditTrail;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.domain.LicenseType;
import com.software.finatech.lslb.cms.service.dto.AuditActionDto;
import com.software.finatech.lslb.cms.service.dto.AuditTrailDto;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "AuditTrail", description = "", tags = "AuditTrail")
@RestController
@RequestMapping("/api/v1/auditTrail")
public class AuditTrailController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(AuditTrailController.class);

    /**
     * @return Roles full information
     */
    @ApiOperation(value = "Get AuditAction", response = ArrayList.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method = RequestMethod.GET, value = "/auditActions", produces = "application/json")
    public Mono<ResponseEntity> getAllAuditAction() {
        //List<FactObject> auditAction = Mapstore.STORE.get("AuditAction").values().stream().collect(Collectors.toList());
        ArrayList<AuditAction> auditAction = (ArrayList<AuditAction>) mongoRepositoryReactive.findAll(new Query(), AuditAction.class).toStream().collect(Collectors.toList());
        ArrayList<AuditActionDto> dto = new ArrayList<>();
        auditAction.forEach(entry -> {
            dto.add(((AuditAction) entry).convertToDto());
        });

        if (dto.size() == 0) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(dto, HttpStatus.OK));
    }

    /**
     * @param id AffectedFact id
     * @return AffectedFact full information
     */
    @ApiOperation(value = "Get AuditTrail By Id", response = AuditTrailDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Mono<ResponseEntity> getById(@PathVariable String id) {
        AuditTrail auditTrail = (AuditTrail) mongoRepositoryReactive.findById(id, AuditTrail.class).block();
        if (auditTrail == null) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(auditTrail.convertToDto(), HttpStatus.OK));
    }

    /**
     * @return All AuditTrails full information
     */
    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json", params = {"keyword", "auditActionId", "fromDate", "toDate", "page", "size", "sorting", "sortProperty"})
    @ApiOperation(value = "Get AuditTrails", response = AuditTrailDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    //For multi-search invoice we use json to receive the fields and param
    public Mono<ResponseEntity> getAuditTrails(@RequestParam("keyword") String keyword,
                                               @RequestParam("fromDate") String fromDate,
                                               @RequestParam("toDate") String toDate,
                                               @RequestParam("auditActionId") String auditActionId,
                                               @RequestParam("page") @NotNull int page,
                                               @RequestParam("size") @NotNull int size,
                                               @RequestParam("sortProperty") String sortProperty,
                                               @RequestParam("sorting") String sorting,
                                               HttpServletResponse response) {
        try {
            //@TODO validate request params
            Query query = new Query();

            /*if(healthInstitutionId != null && !healthInstitutionId.isEmpty()) {
                query.addCriteria(Criteria.where("healthInstitutionId").is(healthInstitutionId));
            }*/
            if (keyword != null && !keyword.isEmpty()) {
                //"*.ab.*"
                //keyword = "*."+keyword+".*";
                //keyword = "/.*"+keyword+".*/";
              query.addCriteria(new Criteria().orOperator(Criteria.where("actionPerformed").regex(keyword, "i"),
                        Criteria.where("performedBy").regex(keyword, "i"),
                        Criteria.where("remoteAddress").regex(keyword, "i"),
                        Criteria.where("owner").regex(keyword, "i")));
            }
            if (auditActionId != null && !auditActionId.isEmpty()) {
                query.addCriteria(Criteria.where("auditActionId").is(auditActionId));
            }
            if ((toDate != null && !toDate.isEmpty()) && (fromDate != null && !fromDate.isEmpty())) {
                //Parse to Date first
                LocalDate from = new LocalDate(fromDate);
                LocalDate to = new LocalDate(toDate);
                query.addCriteria(Criteria.where("auditDate").gte(from).lte(to));
            }

            //For the very firstPage we send the totalCount
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, AuditTrail.class).block();
                response.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort = null;
            if (!StringUtils.isEmpty(sorting) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, size, sort));
            query.with(sort);

            ArrayList<AuditTrail> auditTrails = (ArrayList<AuditTrail>) mongoRepositoryReactive.findAll(query, AuditTrail.class).toStream().collect(Collectors.toList());

            if (auditTrails == null || auditTrails.isEmpty()) {
                return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
            }

            ArrayList<AuditTrailDto> auditTrailsDTO = new ArrayList<>();
            for (AuditTrail auditTrail : auditTrails) {
                auditTrailsDTO.add(auditTrail.convertToDto());
            }
            return Mono.just(new ResponseEntity(auditTrailsDTO, HttpStatus.OK));

        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while getting audit trails", e);
        }
    }
}
