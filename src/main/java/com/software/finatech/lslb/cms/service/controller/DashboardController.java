package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            queryAgentTotalCount.addCriteria(Criteria.where("gameTypeIds").in(gameTypeId));
        }if(!StringUtils.isEmpty(institutionId)){
            query.addCriteria(Criteria.where("institutionId").is(institutionId));
            queryAgentTotalCount.addCriteria(Criteria.where("institutionIds").in(institutionId));

        }
        long institutionTotalCount=mongoRepositoryReactive.count(query, Institution.class).block();
        long gamingMachineTotalCount=mongoRepositoryReactive.count(query, GamingMachine.class).block();
        long agentTotalCount=mongoRepositoryReactive.count(query, Agent.class).block();
        long casesTotalCount=mongoRepositoryReactive.count(queryCasesTotalCount, CustomerComplain.class).block();

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

    @RequestMapping(method = RequestMethod.GET, value = "/payment-summary", params = {"licenseTypeId","gameTypeId"})
    @ApiOperation(value = "Get dashboard payment summary ", response = PaymentRecordDashboardSummaryStatusDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getPaymentSummary(@RequestParam("licenseTypeId") String revenueNameId,
                                                  @RequestParam("gameTypeId") String gameTypeId) {

       Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();
        if (gameTypeId != null && !gameTypeId.isEmpty()) {
            filterCriteria.add(Criteria.where("gameTypeId").is(gameTypeId));
        }

        if (revenueNameId != null && !revenueNameId.isEmpty()) {
            filterCriteria.add(Criteria.where("revenueNameId").is(revenueNameId));
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
            if(result.getPaymentStatusId().equals(PaymentStatusReferenceData.FAILED_PAYMENT_STATUS_ID)){
                paymentRecordDashboardSummaryStatusDto.setFailedPaymentTotalAmount(
                        result==null?0.00: result.getPaymentTotalSum());
                paymentRecordDashboardSummaryStatusDto.setFailedPaymentTotalCount(
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

}
