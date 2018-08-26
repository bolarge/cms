package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.EnumeratedFactDto;
import com.software.finatech.lslb.cms.service.dto.LicenseCreateDto;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.dto.LicenseUpdateDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.service.contracts.LicenseService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.ExpirationList;
import com.software.finatech.lslb.cms.service.util.MapValues;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class LicenseServiceImpl implements LicenseService {
    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Autowired
    MapValues mapValues;
    @Autowired
    ExpirationList expirationList;
    @Override
    public Mono<ResponseEntity> findAllLicense(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               String licenseStatusId,
                                               String paymentRecordId, HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(licenseStatusId)) {
                query.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));
            }
            if (!StringUtils.isEmpty(paymentRecordId)) {
                query.addCriteria(Criteria.where("paymentRecordId").is(paymentRecordId));
            }

            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, License.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "id");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);

            ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
            if (licenses == null || licenses.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<LicenseDto> licenseDtos = new ArrayList<>();

            licenses.forEach(license -> {
                licenseDtos.add(license.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all licenses";
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
        }

    }

    @Override
    public Mono<ResponseEntity> findLicenseById(String licenseId) {
        return null;
    }

    public Mono<ResponseEntity> findLicenseByInstitutionId(String institutionId) {
          LocalDateTime dateTime = new LocalDateTime();
          dateTime.plusDays(90);
          Query queryLicence= new Query();

          queryLicence.addCriteria(Criteria.where("institutionId").is(institutionId));
          License license= (License) mongoRepositoryReactive.find(queryLicence,License.class).block();
          int days= Days.daysBetween(dateTime,license.getEndDate()).getDays();
          if(days>0){
              license.setRenewalStatus("true");
          }else{
              license.setRenewalStatus("false");
          }
          return Mono.just(new ResponseEntity<>(license.convertToDto(), HttpStatus.OK));
    }



    public Mono<ResponseEntity> getAllLicenseStatus() {
        return Mono.just(new ResponseEntity<>(mapValues.getLicenseStatus(), HttpStatus.OK));

    }
    public Mono<ResponseEntity> getExpiringLicenses() {

        return Mono.just(new ResponseEntity<>(expirationList.getExpiringLicences("controllerClass"), HttpStatus.NOT_FOUND));
    }

    public Mono<ResponseEntity> getExpiredLicenses() {

        return Mono.just(new ResponseEntity<>(expirationList.getExpiredLicences("controllerClass"), HttpStatus.NOT_FOUND));

    }
    public Mono<ResponseEntity> updateLicense(LicenseUpdateDto licenseUpdateDto) {
        try {
            String gameTypeId = licenseUpdateDto.getGameTypeId();
           // String paymentRecordId = licenseCreateDto.getPaymentRecordId();
            Query query = new Query();
            query.addCriteria(Criteria.where("institutionId").is(licenseUpdateDto.getInstitutionId()));
            query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
            License license;
            Query queryLicence= new Query();
            queryLicence.addCriteria(Criteria.where("gameTypeId").is(licenseUpdateDto.getGameTypeId()));
            queryLicence.addCriteria(Criteria.where("institutionId").is(licenseUpdateDto.getInstitutionId()));
            License licenseCheck = (License) mongoRepositoryReactive.find(queryLicence,License.class).block();
            if(licenseCheck==null){

                return Mono.just(new ResponseEntity<>("No Valid Payment Record", HttpStatus.BAD_REQUEST));

            }

            license=licenseCheck;
            LocalDateTime fromDate;
            String startDate=licenseUpdateDto.getStartDate();
            if ((startDate != "" && !startDate.isEmpty())) {
                if (!startDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") ) {
                    return Mono.just(new ResponseEntity("Invalid Date format. " +
                            "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));
                }
                fromDate = new LocalDateTime(startDate);

            } else {
                return Mono.just(new ResponseEntity("Invalid Date format. " +
                        "Standard Format: YYYY-MM-DD E.G 2018-02-02", HttpStatus.BAD_REQUEST));

            }
            license.setStartDate(fromDate);
            Query queryFee= new Query();
            queryFee.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
            Fee fee = (Fee) mongoRepositoryReactive.find(queryFee,Fee.class).block();
            int duration = Integer.parseInt(fee.getDuration());
            if(licenseUpdateDto.getLicenseStatusId()!="03" &&
                    licenseUpdateDto.getLicenseStatusId()!="04"){
                license.setEndDate(fromDate.plusDays(duration));
            }else{
                //license.setEndDate("");
            }

            license.setLicenseStatusId(licenseUpdateDto.getLicenseStatusId());


            mongoRepositoryReactive.saveOrUpdate(license);
            return Mono.just(new ResponseEntity<>(license.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while creating a License ";
            logger.error(e.getMessage());
            return logAndReturnError(logger, errorMsg, e);
        }
    }
}
