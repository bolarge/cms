package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactive;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
@Component
public class ExpirationList {
    @Autowired
    MongoRepositoryReactive mongoRepositoryReactive;

    public Mono<ResponseEntity> getExpiringLicences(String check){
        LocalDateTime dateTime = new LocalDateTime();
        dateTime.plusDays(90);
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("endDate").lte(dateTime));
        queryLicence.addCriteria(Criteria.where("licenseStatusId").is("01"));
        List<License> licenses= (List<License>) mongoRepositoryReactive.findAll(queryLicence,License.class);
       List<LicenseDto> licenseDtos = new ArrayList<>();
        licenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
       });
        if(check=="schedulerClass"){
            return Mono.just(new ResponseEntity<>(licenses, HttpStatus.NOT_FOUND));

        }else {
            return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.NOT_FOUND));

        }

    }
    public Mono<ResponseEntity>  getExpiredLicences(String check){
        LocalDateTime dateTime = new LocalDateTime();
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("endDate").lte(dateTime));
        queryLicence.addCriteria(Criteria.where("licenseStatusId").is("01"));
        List<License> licenses= (List<License>) mongoRepositoryReactive.findAll(queryLicence,License.class);
        for(License license: licenses){
            license.setRenewalStatus("true");
            license.setLicenseStatusId("03");
            mongoRepositoryReactive.saveOrUpdate(license);
        }
        Query queryExpiredLicence= new Query();
        queryExpiredLicence.addCriteria(Criteria.where("endDate").lte(dateTime));
        queryExpiredLicence.addCriteria(Criteria.where("licenseStatusId").is("03"));
        List<License> expiredLicenses= (List<License>) mongoRepositoryReactive.findAll(queryExpiredLicence,License.class);
        List<LicenseDto> licenseDtos = new ArrayList<>();
        expiredLicenses.stream().forEach(license -> {
            licenseDtos.add(license.convertToDto());
        });
        if(check=="schedulerClass"){
            return Mono.just(new ResponseEntity<>(expiredLicenses, HttpStatus.NOT_FOUND));

        }else {
            return Mono.just(new ResponseEntity<>(licenseDtos, HttpStatus.NOT_FOUND));

        }
    }
}
