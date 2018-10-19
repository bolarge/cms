package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LoggedCaseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Api(value = "Dashboard", description = "For everything related to dashboard requests", tags = "")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    public MongoTemplate mongoTemplate;
    private static Logger logger = LoggerFactory.getLogger(DocumentController.class);


    @RequestMapping(method = RequestMethod.GET, value = "/license-status-count-summary", params = {"licenseTypeId","gameTypeId"})
    @ApiOperation(value = "Get all license summary count", response = LicenseStatusSummaryDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLicenseSummary(@RequestParam("licenseTypeId") String licenseTypeId,
                                                  @RequestParam("gameTypeId") String gameTypeId) {

             Criteria criteria = new Criteria();
            List<Criteria> filterCriteria = new ArrayList<>();
            if (gameTypeId != null && !gameTypeId.isEmpty()) {
                filterCriteria.add(Criteria.where("gameTypeId").is(gameTypeId));
            }

            if (licenseTypeId != null && !licenseTypeId.isEmpty()) {
            filterCriteria.add(Criteria.where("licenseTypeId").is(licenseTypeId));
            }

            if (filterCriteria.size() > 0) {
                criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
            }

            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group("licenseStatusId").count().as("licenseStatusCount"),
                    Aggregation.project("licenseStatusCount").and("licenseStatusId").previousOperation()
            );
            List<LicenseStatusSummaryDto> licenseSummaryValueDtoAggregationResults = mongoTemplate.aggregate(agg, License.class, LicenseStatusSummaryDto.class).getMappedResults();
             List<LicenseStatusSummaryDto> licenseStatusSummaryDtos = new ArrayList<>();

            licenseSummaryValueDtoAggregationResults.stream().forEach(result -> {

                LicenseStatusSummaryDto licenseStatusSummaryDto = new LicenseStatusSummaryDto();
                LicenseStatus licenseStatus=getLicenseStatus(result.getLicenseStatusId());
                licenseStatusSummaryDto.setLicenseStatus(licenseStatus.getName());
                licenseStatusSummaryDto.setLicenseStatusId(result.getLicenseStatusId());
                licenseStatusSummaryDto.setLicenseStatusCount(result.getLicenseStatusCount());
                licenseStatusSummaryDtos.add(licenseStatusSummaryDto);
            });
            return Mono.just(new ResponseEntity<>(licenseStatusSummaryDtos, HttpStatus.OK));

        }

    @RequestMapping(method = RequestMethod.GET, value = "/loggedcases-summary")
    @ApiOperation(value = "Get dashboard Logged Cases summary ", response = CasesDashboardStatusCountDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllCasesSummary() {

        Aggregation agg = Aggregation.newAggregation(
                 Aggregation.group("loggedCaseStatusId").count().as("loggedStatusCount"),
                Aggregation.project("loggedStatusCount").and("loggedCaseStatusId").previousOperation()

        );
        List<DashboardLoggedCaseStatusDto> dashboardLoggedCaseStatusDtos = mongoTemplate.aggregate(agg, LoggedCase.class, DashboardLoggedCaseStatusDto.class).getMappedResults();
        CasesDashboardStatusCountDto casesDashboardStatusCountDto = new CasesDashboardStatusCountDto();

        dashboardLoggedCaseStatusDtos.stream().forEach(result->{

            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.OPEN_ID)){
                casesDashboardStatusCountDto.setOpenedCount(result==null?0:result.getLoggedStatusCount());
            }
            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.CLOSED_ID)){
                casesDashboardStatusCountDto.setClosedCount(result==null?0:result.getLoggedStatusCount());
            }

            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.OPEN_ID)){
                casesDashboardStatusCountDto.setPendingCount(result==null?0:result.getLoggedStatusCount());
            }

        });
        Query query = new Query();
        long totalCount=mongoRepositoryReactive.count(query, LoggedCase.class).block();
        casesDashboardStatusCountDto.setTotalCount(totalCount);

        return Mono.just(new ResponseEntity<>(casesDashboardStatusCountDto, HttpStatus.OK));

    }
    @RequestMapping(method = RequestMethod.GET, value = "/dashboard-summary", params = {"institutionId","gameTypeId"})
    @ApiOperation(value = "Get all dashboard summary count", response = DashboardSummaryDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getDashBoardSummary(@RequestParam("institutionId") String institutionId,
                                                  @RequestParam("gameTypeId") String gameTypeId) {
        Query query = new Query();
        Query queryCasesTotalCount = new Query();
        Query queryAgentTotalCount = new Query();
        if(!StringUtils.isEmpty(gameTypeId)){
            query.addCriteria(Criteria.where("gameTypeIds").in(gameTypeId));
            queryAgentTotalCount.addCriteria(Criteria.where("gameTypeIds").in(gameTypeId));
        }if(!StringUtils.isEmpty(institutionId)){
            query.addCriteria(Criteria.where("id").is(institutionId));
            queryAgentTotalCount.addCriteria(Criteria.where("institutionIds").in(institutionId));
            queryCasesTotalCount.addCriteria(Criteria.where("institutionId").is(institutionId));
        }
        long institutionTotalCount=mongoRepositoryReactive.count(query, Institution.class).block();
        long gamingMachineTotalCount=mongoRepositoryReactive.count(query, GamingMachine.class).block();
        long agentTotalCount=mongoRepositoryReactive.count(queryAgentTotalCount, Agent.class).block();
        long casesTotalCount=mongoRepositoryReactive.count(queryCasesTotalCount, LoggedCase.class).block();

        // long gamingTerminalTotalCount=mongoRepositoryReactive.count(query, GamingTerminal.class).block();
        DashboardSummaryDto dashboardSummaryDto = new DashboardSummaryDto();
        dashboardSummaryDto.setAgentTotalCount(agentTotalCount);
        dashboardSummaryDto.setCasesTotalCount(casesTotalCount);
        dashboardSummaryDto.setGamingMachineTotalCount(gamingMachineTotalCount);
        dashboardSummaryDto.setInstitutionTotalCount(institutionTotalCount);
        dashboardSummaryDto.setGamingTerminalTotalCount(0);


        return Mono.just(new ResponseEntity<>(dashboardSummaryDto, HttpStatus.OK));

    }
    public LicenseStatus getLicenseStatus(String licenseStatusId) {

        Map licenseStatusMap = Mapstore.STORE.get("LicenseStatus");
        LicenseStatus licenseStatus = null;
        if (licenseStatusMap != null) {
            licenseStatus = (LicenseStatus) licenseStatusMap.get(licenseStatusId);
        }
        if (licenseStatus == null) {
            licenseStatus = (LicenseStatus) mongoRepositoryReactive.findById(licenseStatusId, LicenseStatus.class).block();
            if (licenseStatus != null && licenseStatusMap != null) {
                licenseStatusMap.put(licenseStatusId, licenseStatus);
            }
        }
        return licenseStatus;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/payment-summary", params = {"institutionId","licenseTypeId","gameTypeId"})
    @ApiOperation(value = "Get dashboard payment summary ", response = PaymentRecordDashboardSummaryStatusDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getPaymentSummary(@RequestParam("licenseTypeId") String revenueNameId,
                                                  @RequestParam("gameTypeId") String gameTypeId,
                                                  @RequestParam("institutionId") String institutionId) {

       Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();
        if (gameTypeId != null && !gameTypeId.isEmpty()) {
            filterCriteria.add(Criteria.where("gameTypeId").is(gameTypeId));
        }

        if (!StringUtils.isEmpty(revenueNameId)) {
            filterCriteria.add(Criteria.where("licenseTypeId").is(revenueNameId));
        }
        if (institutionId != null && !institutionId.isEmpty()) {
            filterCriteria.add(Criteria.where("institutionId").is(institutionId));
        }

        if (filterCriteria.size() > 0) {
            criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
        }

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("paymentStatusId").count().as("paymentStatusCount")
                .sum("amountPaid").as("paymentTotalSum")
                .sum("amountOutstanding").as("paymentOutstandingTotalSum"),
                Aggregation.project("paymentStatusCount","paymentTotalSum","paymentOutstandingTotalSum").and("paymentStatusId").previousOperation()

        );
        List<PaymentRecordStatusDto> paymentRecordStatusDtoAggregationResults = mongoTemplate.aggregate(agg, PaymentRecord.class, PaymentRecordStatusDto.class).getMappedResults();
        PaymentRecordDashboardSummaryStatusDto paymentRecordDashboardSummaryStatusDto = new PaymentRecordDashboardSummaryStatusDto();

        paymentRecordStatusDtoAggregationResults.stream().forEach(result->{

            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID)){
                paymentRecordDashboardSummaryStatusDto.setFullPaymentTotalAmount(
                        result==null?0.00: result.getPaymentTotalSum());
                paymentRecordDashboardSummaryStatusDto.setFullPaymentTotalCount(
                        result==null?0: result.getPaymentStatusCount());
            }
            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID)){
                paymentRecordDashboardSummaryStatusDto.setPartPaymentTotalAmount(
                        result==null?0.00: result.getPaymentTotalSum());
                paymentRecordDashboardSummaryStatusDto.setPartPaymentTotalCount(
                        result==null?0:result.getPaymentStatusCount());
            }

            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.UNPAID_STATUS_ID)){
                paymentRecordDashboardSummaryStatusDto.setUnPaidTotalAmount(
                        result==null?0.00: result.getPaymentOutstandingTotalSum());
                paymentRecordDashboardSummaryStatusDto.setUnPaidTotalCount(
                        result==null?0:result.getPaymentStatusCount());
            }
        });

        /**/

        return Mono.just(new ResponseEntity<>(paymentRecordDashboardSummaryStatusDto, HttpStatus.OK));

    }
    @RequestMapping(method = RequestMethod.GET, value = "/institution-summary", params={"institutionId"})
    @ApiOperation(value = "Get operator summary ", response = InstitutionDashboardSummaryDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getInstitutionSummary(@RequestParam ("institutionId") String institutionId) {
       Query queryLicense= new Query();
       queryLicense.addCriteria(Criteria.where("institutionId").is(institutionId));
        Sort sort = new Sort(Sort.Direction.DESC,"expiryDate");
        queryLicense.with(sort);
        Map<String, InstitutionDashboardSummaryDto> institutionDashboardSummaryDtoHashMap= new HashMap<>();

        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryLicense, License.class).toStream().collect(Collectors.toList());
         if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));

        }
        for (License license : licenses) {
            if (institutionDashboardSummaryDtoHashMap.get(license.getGameTypeId()) == null) {
                InstitutionDashboardSummaryDto institutionDashboardSummaryDto = new InstitutionDashboardSummaryDto();

                institutionDashboardSummaryDto.setInstitutionId(institutionId);
                institutionDashboardSummaryDto.setLicenseNumber(license.getLicenseNumber());
                institutionDashboardSummaryDto.setInstitutionName(getInstitution(institutionId).getInstitutionName());
                institutionDashboardSummaryDto.setLicenseStatus(license.getLicenseStatusId());
                institutionDashboardSummaryDto.setLicenseStatus(getLicenseStatus(license.getLicenseStatusId()).getName());
                institutionDashboardSummaryDto.setEffectiveDate(license.getEffectiveDate().toString("dd-MM-yyyy"));
                institutionDashboardSummaryDto.setExpiryDate(license.getExpiryDate().toString("dd-MM-yyyy"));
                institutionDashboardSummaryDto.setGameType(getGameType(license.getGameTypeId()).getName());
                institutionDashboardSummaryDto.setNumberOfAgents(getAgentCountForInstitution(institutionId));
                institutionDashboardSummaryDto.setNumberOfGamingMachines(getGamingMachineCountForInstitution(institutionId));
                institutionDashboardSummaryDtoHashMap.put(license.getGameTypeId(), institutionDashboardSummaryDto);

            }
        }
        if(institutionDashboardSummaryDtoHashMap.values().size()==0){
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }

        return Mono.just(new ResponseEntity<>(institutionDashboardSummaryDtoHashMap.values(), HttpStatus.OK));
    }


    public GameType getGameType(String gameTypeId) {

        Map gameTypeMap = Mapstore.STORE.get("GameType");
        GameType gameType = null;
        if (gameTypeMap != null) {
            gameType = (GameType) gameTypeMap.get(gameTypeId);
        }
        if (gameType == null) {
            gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null && gameTypeMap != null) {
                gameTypeMap.put(gameTypeId, gameType);
            }
        }
        return gameType;
    }
    public Institution getInstitution(String institutionId) {

        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }
    public long getAgentCountForInstitution(String institutionId){
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionIds").in(institutionId));
        return mongoRepositoryReactive.count(query, Agent.class).block();
    }
    public long getGamingMachineCountForInstitution(String institutionId){
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").in(institutionId));
        return mongoRepositoryReactive.count(query, GamingMachine.class).block();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/institution-invoice-summary", params = {"institutionId"})
    @ApiOperation(value = "Get dashboard invoice summary ", response = PaymentRecordDashboardStatusCountDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getPaymentSummary(@RequestParam("institutionId") String institutionId) {

        Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();

        if (!StringUtils.isEmpty(institutionId)) {
            filterCriteria.add(Criteria.where("institutionId").is(institutionId));
        }

        if (filterCriteria.size() > 0) {
            criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
        }

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("paymentStatusId").count().as("paymentStatusCount"),
                Aggregation.project("paymentStatusCount").and("paymentStatusId").previousOperation()

        );
        List<PaymentRecordStatusDto> paymentRecordStatusDtoAggregationResults = mongoTemplate.aggregate(agg, PaymentRecord.class, PaymentRecordStatusDto.class).getMappedResults();
        PaymentRecordDashboardStatusCountDto paymentRecordDashboardStatusCountDto = new PaymentRecordDashboardStatusCountDto();

        paymentRecordStatusDtoAggregationResults.stream().forEach(result->{

            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID)){
                paymentRecordDashboardStatusCountDto.setFullPaymentTotalCount(
                        result==null?0: result.getPaymentStatusCount());
            }
            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID)){
                paymentRecordDashboardStatusCountDto.setPartPaymentTotalCount(
                        result==null?0:result.getPaymentStatusCount());
            }

            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.UNPAID_STATUS_ID)){
                paymentRecordDashboardStatusCountDto.setUnPaidTotalCount(
                        result==null?0:result.getPaymentStatusCount());
            }

        });
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        long totalCount=mongoRepositoryReactive.count(query, PaymentRecord.class).block();
        paymentRecordDashboardStatusCountDto.setTotalInvoices(totalCount);

        return Mono.just(new ResponseEntity<>(paymentRecordDashboardStatusCountDto, HttpStatus.OK));

    }


    @RequestMapping(method = RequestMethod.GET, value = "/institution-loggedcases-summary", params = {"institutionId"})
    @ApiOperation(value = "Get Institution dashboard Logged Cases summary ", response = CasesDashboardStatusCountDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getCasesSummary(@RequestParam("institutionId") String institutionId) {

        Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();

        if (!StringUtils.isEmpty(institutionId)) {
            filterCriteria.add(Criteria.where("institutionId").is(institutionId));
        }

        if (filterCriteria.size() > 0) {
            criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
        }

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("loggedCaseStatusId").count().as("loggedStatusCount"),
                Aggregation.project("loggedStatusCount").and("loggedCaseStatusId").previousOperation()

        );
        List<DashboardLoggedCaseStatusDto> dashboardLoggedCaseStatusDtos = mongoTemplate.aggregate(agg, LoggedCase.class, DashboardLoggedCaseStatusDto.class).getMappedResults();
        CasesDashboardStatusCountDto casesDashboardStatusCountDto = new CasesDashboardStatusCountDto();

        dashboardLoggedCaseStatusDtos.stream().forEach(result->{

            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.OPEN_ID)){
                casesDashboardStatusCountDto.setOpenedCount(result==null?0:result.getLoggedStatusCount());
            }
            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.CLOSED_ID)){
                casesDashboardStatusCountDto.setClosedCount(result==null?0:result.getLoggedStatusCount());
            }

            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.OPEN_ID)){
                casesDashboardStatusCountDto.setPendingCount(result==null?0:result.getLoggedStatusCount());
            }

        });
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        long totalCount=mongoRepositoryReactive.count(query, LoggedCase.class).block();
        casesDashboardStatusCountDto.setTotalCount(totalCount);

        return Mono.just(new ResponseEntity<>(casesDashboardStatusCountDto, HttpStatus.OK));

    }

    ///////////////////////////////

    @RequestMapping(method = RequestMethod.GET, value = "/agent-loggedcases-summary", params = {"agentId"})
    @ApiOperation(value = "Get agent dashboard Logged Cases summary ", response = CasesDashboardStatusCountDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAgentCasesSummary(@RequestParam("agentId") String agentId) {

        Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();

        if (!StringUtils.isEmpty(agentId)) {
            filterCriteria.add(Criteria.where("agentId").is(agentId));
        }

        if (filterCriteria.size() > 0) {
            criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
        }

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("loggedCaseStatusId").count().as("loggedStatusCount"),
                Aggregation.project("loggedStatusCount").and("loggedCaseStatusId").previousOperation()

        );
        List<DashboardLoggedCaseStatusDto> dashboardLoggedCaseStatusDtos = mongoTemplate.aggregate(agg, LoggedCase.class, DashboardLoggedCaseStatusDto.class).getMappedResults();
        CasesDashboardStatusCountDto casesDashboardStatusCountDto = new CasesDashboardStatusCountDto();

        dashboardLoggedCaseStatusDtos.stream().forEach(result->{

            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.OPEN_ID)){
                casesDashboardStatusCountDto.setOpenedCount(result==null?0:result.getLoggedStatusCount());
            }
            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.CLOSED_ID)){
                casesDashboardStatusCountDto.setClosedCount(result==null?0:result.getLoggedStatusCount());
            }

            if(result.getLoggedCaseStatusId().equals(LoggedCaseStatusReferenceData.OPEN_ID)){
                casesDashboardStatusCountDto.setPendingCount(result==null?0:result.getLoggedStatusCount());
            }

        });
        Query query = new Query();
        query.addCriteria(Criteria.where("agentId").is(agentId));
        long totalCount=mongoRepositoryReactive.count(query, LoggedCase.class).block();
        casesDashboardStatusCountDto.setTotalCount(totalCount);

        return Mono.just(new ResponseEntity<>(casesDashboardStatusCountDto, HttpStatus.OK));

    }

    @RequestMapping(method = RequestMethod.GET, value = "/agent-invoice-summary", params = {"agentId"})
    @ApiOperation(value = "Get Agent dashboard invoice summary ", response = PaymentRecordDashboardStatusCountDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAgentPaymentSummary(@RequestParam("agentId") String agentId) {

        Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();

        if (!StringUtils.isEmpty(agentId)) {
            filterCriteria.add(Criteria.where("agentId").is(agentId));
        }

        if (filterCriteria.size() > 0) {
            criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
        }

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("paymentStatusId").count().as("paymentStatusCount"),
                Aggregation.project("paymentStatusCount").and("paymentStatusId").previousOperation()

        );
        List<PaymentRecordStatusDto> paymentRecordStatusDtoAggregationResults = mongoTemplate.aggregate(agg, PaymentRecord.class, PaymentRecordStatusDto.class).getMappedResults();
        PaymentRecordDashboardStatusCountDto paymentRecordDashboardStatusCountDto = new PaymentRecordDashboardStatusCountDto();

        paymentRecordStatusDtoAggregationResults.stream().forEach(result->{

            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID)){
                paymentRecordDashboardStatusCountDto.setFullPaymentTotalCount(
                        result==null?0: result.getPaymentStatusCount());
            }
            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.PARTIALLY_PAID_STATUS_ID)){
                paymentRecordDashboardStatusCountDto.setPartPaymentTotalCount(
                        result==null?0:result.getPaymentStatusCount());
            }

            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.UNPAID_STATUS_ID)){
                paymentRecordDashboardStatusCountDto.setUnPaidTotalCount(
                        result==null?0:result.getPaymentStatusCount());
            }

        });
        Query query = new Query();
        query.addCriteria(Criteria.where("agentId").is(agentId));
        long totalCount=mongoRepositoryReactive.count(query, PaymentRecord.class).block();
        paymentRecordDashboardStatusCountDto.setTotalInvoices(totalCount);

        return Mono.just(new ResponseEntity<>(paymentRecordDashboardStatusCountDto, HttpStatus.OK));

    }

    @RequestMapping(method = RequestMethod.GET, value = "/agent-summary", params={"agentId"})
    @ApiOperation(value = "Get operator summary ", response = AgentDashboardSummaryDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAgentSummary(@RequestParam ("agentId") String agentId) {
        Query queryLicense= new Query();
        queryLicense.addCriteria(Criteria.where("agentId").is(agentId));
        Sort sort = new Sort(Sort.Direction.DESC,"expiryDate");
        queryLicense.with(sort);
        Map<String, AgentDashboardSummaryDto> agentDashboardSummaryDtoHashMap= new HashMap<>();
        List<License> licenses = (List<License>) mongoRepositoryReactive.findAll(queryLicense, License.class).toStream().collect(Collectors.toList());
        if (licenses.size() == 0) {
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));

        }

        for (License license : licenses) {
            if(agentDashboardSummaryDtoHashMap.get(license.getGameTypeId())==null){
                AgentDashboardSummaryDto agentDashboardSummaryDto = new AgentDashboardSummaryDto();
                agentDashboardSummaryDto.setAgentId(agentId);
                agentDashboardSummaryDto.setLicenseNumber(license.getLicenseNumber());
                agentDashboardSummaryDto.setAgentName(getAgent(agentId).getFullName());
                agentDashboardSummaryDto.setLicenseStatus(license.getLicenseStatusId());
                agentDashboardSummaryDto.setLicenseStatus(getLicenseStatus(license.getLicenseStatusId()).getName());
                agentDashboardSummaryDto.setEffectiveDate(license.getEffectiveDate().toString("dd-MM-yyyy"));
                agentDashboardSummaryDto.setExpiryDate(license.getExpiryDate().toString("dd-MM-yyyy"));
                agentDashboardSummaryDto.setGameType(getGameType(license.getGameTypeId()).getName());
                agentDashboardSummaryDto.setNumberOfInstitutions(getInstitutionCountForAgent(agentId));
                agentDashboardSummaryDtoHashMap.put(license.getGameTypeId(), agentDashboardSummaryDto);

            }

        }
        if(agentDashboardSummaryDtoHashMap.values().size()==0){
            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
        }



        return Mono.just(new ResponseEntity<>(agentDashboardSummaryDtoHashMap.values(), HttpStatus.OK));
    }
    public Agent getAgent(String agentId) {

        return (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();
    }
    public long getInstitutionCountForAgent(String agentId){

        Agent agent= (Agent) mongoRepositoryReactive.findById(agentId, Agent.class).block();

        return agent.getInstitutionIds().size();
    }



}
