package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.domain.LicenseStatus;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "Dashboard", description = "For everything related to dashbard requests", tags = "")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    public MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, value = "/license-status-count-summary", params = {"licenseTypeId","fromDate", "toDate","gameTypeId","licenseStatusId"})
    @ApiOperation(value = "Get all license summary count", response = FeeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getLicenseSummary(@RequestParam("licenseTypeId") String licenseTypeId,
                                           @RequestParam("gameTypeId") String gameTypeId,
                                           @RequestParam("fromDate") String fromDate,
                                           @RequestParam("endDate") String toDate,
                                          @RequestParam("licenseStatusId") String licenseStatusId) {
        LocalDate startDate;
        LocalDate endDate;

        if ((fromDate != "" && !fromDate.isEmpty()) && (toDate != "" && !toDate.isEmpty())) {
            if (!fromDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") ||
                    !toDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                return Mono.just(new ResponseEntity("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.OK));
            }
            startDate = new LocalDate(fromDate);
            endDate = new LocalDate(toDate);

        } else {
            return Mono.just(new ResponseEntity("Invalid Date format. " +
                    "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.OK));
        }
            Criteria criteria = new Criteria();
            List<Criteria> filterCriteria = new ArrayList<>();
            if (licenseStatusId != null && !licenseStatusId.isEmpty()) {
                filterCriteria.add(Criteria.where("licenseStatusId").is(licenseStatusId));
            }


            if (gameTypeId != null && !gameTypeId.isEmpty()) {
                filterCriteria.add(Criteria.where("gameTypeId").is(gameTypeId));
            }

            if (licenseTypeId != null && !licenseTypeId.isEmpty()) {
            filterCriteria.add(Criteria.where("licenseTypeId").is(licenseTypeId));
            }
            if (startDate != null) {
                filterCriteria.add(Criteria.where("effectiveDate").gte(startDate).lt(endDate));

            }
            if (endDate != null) {
                filterCriteria.add(Criteria.where("expiryDate").lte(endDate));

            }

            if (filterCriteria.size() > 0) {
                criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
            }

            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group("licenseStatusId").count().as("licenseStatusCount"),
                    Aggregation.project("licenseStatusId","licenseStatusCount")
            );
            AggregationResults<LicenseStatusSummaryDto> licenseSummaryValueDtoAggregationResults = mongoTemplate.aggregate(agg, License.class, LicenseStatusSummaryDto.class);
            LicenseStatusSummaryDto licenseStatusSummaryDto = new LicenseStatusSummaryDto();
            List<LicenseStatusSummaryDto> licenseStatusSummaryDtos = new ArrayList<>();

            licenseSummaryValueDtoAggregationResults.getMappedResults().stream().forEach(result -> {
                LicenseStatus licenseStatus=getLicenseStatus(result.getLicenseStatusId());
                licenseStatusSummaryDto.setLicenseStatus(licenseStatus.getName());
                licenseStatusSummaryDto.setLicenseStatusId(result.getLicenseStatusId());
                licenseStatusSummaryDto.setLicenseStatusCount(result.getLicenseStatusCount());
                licenseStatusSummaryDtos.add(licenseStatusSummaryDto);
            });
            return Mono.just(new ResponseEntity<>(licenseStatusSummaryDtos, HttpStatus.OK));

        }
        @RequestMapping(method = RequestMethod.GET, value = "/payment-record-summary", params = {"fromDate", "toDate","gameTypeId","revenueNameId"})
    @ApiOperation(value = "Get all license summary count", response = FeeDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getInstitutionLicenseSummary(@RequestParam("gameTypeId") String gameTypeId,
                                                             @RequestParam("fromDate") String fromDate,
                                                             @RequestParam("endDate") String toDate,
                                                             @RequestParam("revenueNameId") String revenueNameId) {
        LocalDate startDate;
        LocalDate endDate;

        if ((fromDate != "" && !fromDate.isEmpty()) && (toDate != "" && !toDate.isEmpty())) {
            if (!fromDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") ||
                    !toDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                return Mono.just(new ResponseEntity("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.OK));
            }
            startDate = new LocalDate(fromDate);
            endDate = new LocalDate(toDate);

        } else {
            return Mono.just(new ResponseEntity("Invalid Date format. " +
                    "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.OK));
        }
        Criteria criteria = new Criteria();
        List<Criteria> filterCriteria = new ArrayList<>();
        if (revenueNameId != null && !revenueNameId.isEmpty()) {
            filterCriteria.add(Criteria.where("revenueNameId").is(revenueNameId));
        }


        if (gameTypeId != null && !gameTypeId.isEmpty()) {
            filterCriteria.add(Criteria.where("gameTypeId").is(gameTypeId));
        }


        if (startDate != null&& endDate != null) {
            filterCriteria.add(Criteria.where("createdAt").gte(startDate).lte(endDate));

        }

        if (filterCriteria.size() > 0) {
            criteria.andOperator(filterCriteria.toArray(new Criteria[filterCriteria.size()]));
        }


        AggregationResults<LicenseStatusSummaryDto> licenseSummaryValueDtoAggregationResults = mongoTemplate.aggregate(agg, License.class, LicenseStatusSummaryDto.class);
        LicenseStatusSummaryDto licenseStatusSummaryDto = new LicenseStatusSummaryDto();
        List<LicenseStatusSummaryDto> licenseStatusSummaryDtos = new ArrayList<>();

        licenseSummaryValueDtoAggregationResults.getMappedResults().stream().forEach(result -> {
            LicenseStatus licenseStatus=getLicenseStatus(result.getLicenseStatusId());
            licenseStatusSummaryDto.setLicenseStatus(licenseStatus.getName());
            licenseStatusSummaryDto.setLicenseStatusId(result.getLicenseStatusId());
            licenseStatusSummaryDto.setLicenseStatusCount(result.getLicenseStatusCount());
            licenseStatusSummaryDtos.add(licenseStatusSummaryDto);
        });
        return Mono.just(new ResponseEntity<>(licenseStatusSummaryDtos, HttpStatus.OK));

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


}
