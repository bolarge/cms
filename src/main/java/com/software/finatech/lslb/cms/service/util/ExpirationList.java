package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.LicenseDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactive;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpirationList {
    @Autowired
    MongoRepositoryReactive mongoRepositoryReactive;
    private static Logger logger = LoggerFactory.getLogger(ExpirationList.class);

    public List<License> getExpiringLicences(int duration, String licenseStatusId ){
        LocalDateTime dateTime = new LocalDateTime();
        dateTime=dateTime.plusMonths(duration);
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("endDate").lt(dateTime));
        queryLicence.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));
        List<License> licenses= (List<License>) mongoRepositoryReactive.findAll(queryLicence,License.class).toStream().collect(Collectors.toList());
        if(licenses.size()==0){
            return null;
        }
        for(License license: licenses) {
            if(licenseStatusId.equals("02")){
                license.setRenewalStatus("true");
                mongoRepositoryReactive.saveOrUpdate(license);
            }

        }

        return licenses;

        }

    public List<License> getExpiredLicences( String licenseStatusId){
        LocalDateTime dateTime = new LocalDateTime();
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("endDate").lte(dateTime));
        if(licenseStatusId==LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID){
            queryLicence.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));//orOperator(Criteria.where("licenseStatusId").is("03")));
        }else{
            queryLicence.addCriteria(Criteria.where("licenseStatusId").is(licenseStatusId));
        }
         List<License> licenses= (List<License>) mongoRepositoryReactive.findAll(queryLicence,License.class).toStream().collect(Collectors.toList());
        if(licenses.size()==0){
        return  null;
        }
        if(licenseStatusId.equals(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID)){
            for(License license: licenses){
                license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_REVOKED_LICENSE_STATUS_ID);
                license.setRenewalStatus("true");
                mongoRepositoryReactive.saveOrUpdate(license);
            }
        }

        Query queryExpiredLicence= new Query();
        queryExpiredLicence.addCriteria(Criteria.where("endDate").lte(dateTime));
        if(licenseStatusId==LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID){
            queryExpiredLicence.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSE_REVOKED_LICENSE_STATUS_ID));

        }else{
            queryExpiredLicence.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.AIP_LICENSE_STATUS_ID));
        }
         List<License> expiredLicenses= (List<License>) mongoRepositoryReactive.findAll(queryExpiredLicence,License.class).toStream().collect(Collectors.toList());

            return expiredLicenses;

        }
    }

