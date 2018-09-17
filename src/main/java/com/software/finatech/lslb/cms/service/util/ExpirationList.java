package com.software.finatech.lslb.cms.service.util;

import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.domain.DocumentType;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactive;
import com.software.finatech.lslb.cms.service.referencedata.DocumentPurposeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpirationList {
    @Autowired
    MongoRepositoryReactive mongoRepositoryReactive;
    private static Logger logger = LoggerFactory.getLogger(ExpirationList.class);

    public List<License> getExpiringLicences(int duration, ArrayList<String> licenseStatusIds ){
        LocalDateTime dateTime = new LocalDateTime();
        dateTime=dateTime.plusDays(duration);
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("expiryDate").lt(dateTime));
        queryLicence.addCriteria(Criteria.where("licenseStatusId").in(licenseStatusIds));
        List<License> licenses= (List<License>) mongoRepositoryReactive.findAll(queryLicence,License.class).toStream().collect(Collectors.toList());
        if(licenses.size()==0){
            return null;
        }
        for(License license: licenses) {
            if(license.getLicenseStatusId().equalsIgnoreCase(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID)){
            license.setRenewalStatus("true");
                mongoRepositoryReactive.saveOrUpdate(license);
            }
         }

        return licenses;

        }

    public List<License> getExpiredLicences( ArrayList<String> licenseStatuses){
        LocalDateTime dateTime = new LocalDateTime();
        Query queryLicence= new Query();
        queryLicence.addCriteria(Criteria.where("expiryDate").lte(dateTime));
        queryLicence.addCriteria(Criteria.where("licenseStatusId").in(licenseStatuses));//orOperator(Criteria.where("licenseStatusId").is("03")));

         List<License> licenses= (List<License>) mongoRepositoryReactive.findAll(queryLicence,License.class).toStream().collect(Collectors.toList());
        if(licenses.size()==0){
        return  null;
        }
        if(licenseStatuses.get(0).equals(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID)){
            for(License license: licenses){
                license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_EXPIRED_STATUS_ID);
                license.setRenewalStatus("true");
                mongoRepositoryReactive.saveOrUpdate(license);
                Query queryRenewalDocuments= new Query();
                queryRenewalDocuments.addCriteria(Criteria.where("institutionId").is(license.getInstitutionId()));
                queryRenewalDocuments.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
                List<Document> documents = (List<Document>)mongoRepositoryReactive.findAll(queryRenewalDocuments, Document.class).toStream().collect(Collectors.toList());
                if(documents.size()!=0) {
                    List<DocumentType> documentTypes = new ArrayList<>();
                    documents.stream().forEach(document -> {
                        documentTypes.add(document.getDocumentType());
                    });
                    for (DocumentType documentType : documentTypes) {
                        if (documentType != null && documentType.getDocumentPurposeId().equals(DocumentPurposeReferenceData.RENEWAL_LICENSE_ID)) {

                            org.springframework.data.mongodb.core.query.Query queryPreviousDocuments = new org.springframework.data.mongodb.core.query.Query();
                            queryPreviousDocuments.addCriteria(Criteria.where("institutionId").is(license.getInstitutionId()));
                            queryPreviousDocuments.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
                            queryPreviousDocuments.addCriteria(Criteria.where("documentTypeId").is(documentType.getId()));
                            Document previousDocument = (Document) mongoRepositoryReactive.find(queryPreviousDocuments, Document.class).block();
                            if(previousDocument.isArchive()==false){
                                previousDocument.setArchive(true);
                            }
                            mongoRepositoryReactive.saveOrUpdate(previousDocument);

                        }
                    }
                }

            }
        }

        Query queryExpiredLicence= new Query();
        queryExpiredLicence.addCriteria(Criteria.where("expiryDate").lte(dateTime));
        queryExpiredLicence.addCriteria(Criteria.where("licenseStatusId").in(licenseStatuses));
        List<License> expiredLicenses= (List<License>) mongoRepositoryReactive.findAll(queryExpiredLicence,License.class).toStream().collect(Collectors.toList());
            return expiredLicenses;
        }
    }