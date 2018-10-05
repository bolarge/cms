package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.AuditAction;
import com.software.finatech.lslb.cms.service.domain.AuditTrail;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.dto.AuditActionDto;
import com.software.finatech.lslb.cms.service.dto.AuditTrailDto;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "AuditTrail", description = "", tags = "AuditTrail")
@RestController
@RequestMapping("/api/v1/auditTrail")
public class AuditTrailController extends BaseController{
    private static Logger logger = LoggerFactory.getLogger(AuditTrailController.class);

    /**
     * @return Roles full information
     */
    @ApiOperation(value = "Get AuditAction", response = ArrayList.class, consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method= RequestMethod.GET, value="/auditActions", produces ="application/json")
    public Mono<ResponseEntity> getAllAuditAction() {
        List<FactObject> auditAction = Mapstore.STORE.get("AuditAction").values().stream().collect(Collectors.toList());
        //ArrayList<FactObject> authRoles = (ArrayList<FactObject>) mongoRepositoryReactive.findAll(AuthRole.class).toStream().collect(Collectors.toList());
        ArrayList<AuditActionDto> dto =  new ArrayList<>();
        auditAction.forEach(entry->{
            dto.add(((AuditAction)entry).convertToDto());
        });

        if(dto.size() == 0){
            return  Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(dto, HttpStatus.OK));
    }

    /**
     * @param id AffectedFact id
     * @return AffectedFact full information
     */
    @ApiOperation(value = "Get AuditTrail By Id", response = AuditTrailDto.class, consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method= RequestMethod.GET, value="/{id}", produces ="application/json")
    public Mono<ResponseEntity> getById(@PathVariable String id) {
        AuditTrail auditTrail = (AuditTrail) mongoRepositoryReactive.findById(id,AuditTrail.class).block();
        if(auditTrail == null){
            return  Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(auditTrail.convertToDto(), HttpStatus.OK));
    }

    /**
     * @return All AuditTrails full information
     */
    @RequestMapping(method= RequestMethod.GET, value="/all",produces ="application/json" ,params = {"keyword","auditActionId","fromDate","toDate", "page", "size","sorting","sortProperty" })
    @ApiOperation(value = "Get AuditTrails", response = AuditTrailDto.class, consumes="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    //For multi-search invoice we use json to receive the fields and param
    public Mono<ResponseEntity> getAuditTrails(@Param( "keyword" ) String keyword,
                                               @Param( "fromDate" ) String fromDate,
                                               @Param( "toDate" ) String toDate,
                                               @Param( "auditActionId" ) String auditActionId,
                                               @Param( "page" ) @NotNull int page, @Param( "size" )  @NotNull int size, @Param( "size" )  String sortProperty, @Param( "sorting" ) String sorting, HttpServletResponse response) {
        try{
            //@TODO validate request params
            Query query = new Query();
            Criteria criteria = new Criteria();

            /*if(healthInstitutionId != null && !healthInstitutionId.isEmpty()) {
                query.addCriteria(Criteria.where("healthInstitutionId").is(healthInstitutionId));
            }*/
            if(keyword != null && !keyword.isEmpty()) {
                //"*.ab.*"
                //keyword = "*."+keyword+".*";
                //keyword = "/.*"+keyword+".*/";
                criteria.orOperator(Criteria.where("actionPerformed").regex(keyword,"i"),Criteria.where("performedBy").regex(keyword,"i"),Criteria.where("remoteAddress").regex(keyword,"i"));
            }
            if(auditActionId != null && !auditActionId.isEmpty()) {
                criteria.andOperator(Criteria.where("auditActionId").is(auditActionId));
            }
            if((toDate != null && !toDate.isEmpty()) && (fromDate != null && !fromDate.isEmpty())) {
                //Parse to Date first
                LocalDate from = new LocalDate(fromDate);
                LocalDate to = new LocalDate(toDate);
                criteria.andOperator(Criteria.where("auditDate").gte(from).lte(to));
            }

            query.addCriteria(criteria);

            //For the very firstPage we send the totalCount
            if(page == 0){
                long count = mongoRepositoryReactive.count(query,AuditTrail.class).block();
                response.setHeader("TotalCount",String.valueOf(count));
            }

            Sort sort = null;
            if(sorting != null  && !sorting.isEmpty() && sortProperty != null  && !sortProperty.isEmpty()) {
                sort = new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortProperty);
            }else{
                sort = new Sort(Sort.Direction.ASC, "id");
            }
            query.with(PageRequest.of(page,size,sort));
            query.with(sort);

            ArrayList<AuditTrail> auditTrails = (ArrayList<AuditTrail>) mongoRepositoryReactive.findAll(query,AuditTrail.class).toStream().collect(Collectors.toList());

            ArrayList<AuditTrailDto> auditTrailsDTO =  new ArrayList<>();

            if(auditTrailsDTO.size() == 0){
                return  Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
            }

            return Mono.just(new ResponseEntity(auditTrailsDTO, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
