package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.DocumentTypeDto;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.referencedata.ApplicationFormStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.DocumentPurposeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseStatusReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LicenseTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import com.software.finatech.lslb.cms.service.util.DatabaseLoaderUtils;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingAgentLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingTerminalLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingOperatorLoader;
import com.software.finatech.lslb.cms.service.util.httpclient.MyFileManager;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController {
    @Autowired
    private VigipayService vigipayService;
    @Autowired
    private PaymentRecordDetailService paymentRecordDetailService;
    @Autowired
    private ExistingOperatorLoader existingOperatorLoader;
    @Autowired
    private ExistingAgentLoader existingAgentLoader;
    @Autowired
    private DatabaseLoaderUtils databaseLoaderUtils;
    @Autowired
    private ExistingGamingTerminalLoader existingGamingTerminalLoader;
    @Autowired
    private Environment environment;
    @Autowired
    private MyFileManager myFileManager;
    @Autowired
    private GameTypeService gameTypeService;

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/create-customer")
    public String testGetCustomerCode(@RequestParam("inst") String institutionId) {
        Institution institution = (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
        String customerCode = vigipayService.createCustomerCodeForInstitution(institution);
        if (customerCode != null) {
            institution.setVgPayCustomerCode(customerCode);
            mongoRepositoryReactive.saveOrUpdate(institution);
        }
        return "Hello";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create-invoice")
    public Mono<ResponseEntity> testCreateInvoice(@RequestBody PaymentRecordDetailCreateDto paymentRecordDetailCreateDto, HttpServletRequest request) {
        return paymentRecordDetailService.createInBranchPaymentRecordDetail(paymentRecordDetailCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/load-existing-operators")
    public Mono<ResponseEntity> create(@RequestParam("file") MultipartFile multipartFile,
                                       @RequestParam("type") String type) {
        try {
            if (StringUtils.equalsIgnoreCase("licenced", type)) {
                existingOperatorLoader.loadFromCsv(multipartFile);
            }
            if (StringUtils.equalsIgnoreCase("aip", type) || StringUtils.equalsIgnoreCase("suspended", type)) {
                existingOperatorLoader.loadAIPOrSuspendedFromCsv(multipartFile);
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (LicenseServiceException e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/load-existing-agents")
    public Mono<ResponseEntity> createAgents() {
        try {
            existingAgentLoader.loadExistingAgents();
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/load-existing-agents-from-file")
    public Mono<ResponseEntity> createAgentFromCSV(@RequestParam("file") MultipartFile multipartFile) {
        try {
            existingAgentLoader.loadAgentsFromCSV(multipartFile);
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/load-referenceData")
    public Mono<ResponseEntity> loadReferenceData() {
        try {
            databaseLoaderUtils.runSeedData(environment);
            databaseLoaderUtils.runLoadData();
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete-operators")
    public Mono<ResponseEntity> delete() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("forTest").is(true));
            ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
            for (Institution institution : institutions) {
                query = new Query();
                query.addCriteria(Criteria.where("institutionId").is(institution.getId()));
                ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
                for (License license : licenses) {
                    mongoRepositoryReactive.delete(license);
                }
                ArrayList<InstitutionCategoryDetails> categoryDetails = (ArrayList<InstitutionCategoryDetails>) mongoRepositoryReactive.findAll(query, InstitutionCategoryDetails.class).toStream().collect(Collectors.toList());
                for (InstitutionCategoryDetails categoryDetails1 : categoryDetails) {
                    mongoRepositoryReactive.delete(categoryDetails1);
                }
                ArrayList<AIPDocumentApproval> documentApproval = (ArrayList<AIPDocumentApproval>) mongoRepositoryReactive.findAll(query, AIPDocumentApproval.class).toStream().collect(Collectors.toList());
                for (AIPDocumentApproval aipDocumentApproval : documentApproval) {
                    mongoRepositoryReactive.delete(aipDocumentApproval);
                }
                mongoRepositoryReactive.delete(institution);
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @RequestMapping(method = RequestMethod.GET, value = "/checkApproval")
    public Mono<ResponseEntity> checkApproval(){
            Query query = new Query();
            query.addCriteria(Criteria.where("documentPurposeId").is(DocumentPurposeReferenceData.RENEWAL_LICENSE_ID));
            query.addCriteria(Criteria.where("active").is(true));
            //  query.addCriteria(Criteria.where("approverId").is(null));
         query.addCriteria(Criteria.where("gameTypeIds").in("02"));

        List<DocumentType> documentTypes = (List<DocumentType>) mongoRepositoryReactive.findAll(query, DocumentType.class).toStream().collect(Collectors.toList());
//            int notApprrovalRequired=0;
////            for(DocumentType documentType: documentTypes){
////                if(documentType.getApproverId()==null){
////                    notApprrovalRequired=+1;
////                }
////            }
        List<DocumentTypeDto> documentTypeDtos= new ArrayList<>();
        documentTypes.stream().forEach(documentType -> {
            documentType.setApproverId("");
            mongoRepositoryReactive.saveOrUpdate(documentType);
            documentTypeDtos.add(documentType.convertToDto());
        });

        return Mono.just(new ResponseEntity<>(documentTypeDtos, HttpStatus.OK));


    }
//    @RequestMapping(method = RequestMethod.POST, value = "/back-date")
//    public Mono<ResponseEntity> backDate() {
//        try {
//            Query query = new Query();
//            query.addCriteria(Criteria.where("institutionId").is("7c905dbb-9183-405e-92aa-6065f36e03e2"));
//            query.addCriteria(Criteria.where("licenseTypeId").is(LicenseTypeReferenceData.INSTITUTION_ID));
//            query.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID));
//
//            License license = (License) mongoRepositoryReactive.find(query, License.class).block();
//
//
//            license.setEffectiveDate(new LocalDate("2019-01-04"));
//            license.setExpiryDate(new LocalDate("2019-01-30"));
//            license.setRenewalStatus("true");
//            mongoRepositoryReactive.saveOrUpdate(license);
//            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
//        } catch (Exception e) {
//            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }

    @RequestMapping(method = RequestMethod.POST, value = "/backdate-licence")
    public Mono<ResponseEntity> moviedocus() {
        try {
            String[] ids = new String[]{"8dfa3c88-8f56-4946-89b3-6f2904a35656"};
            for (String id : ids) {
                License license = (License) mongoRepositoryReactive.findById(id, License.class).block();
                if (license != null) {
                    if (license.isInstitutionLicense()) {
                        license.setExpiryDate(LocalDate.now().withDayOfYear(365));
                        license.setEffectiveDate(LocalDate.now().withDayOfYear(1));
                        license.setRenewalStatus("true");
                        license.setRenewalInProgress(false);
                        license.setPaymentRecordId(null);
                    }
                    if (license.isGamingMachineLicense()) {
                        LocalDate effectiveDate = license.getEffectiveDate();
                        effectiveDate = effectiveDate.minusDays(365);
                        LocalDate expiryDate = license.getExpiryDate();
                        expiryDate = expiryDate.minusDays(365);
                        license.setExpiryDate(expiryDate);
                        license.setEffectiveDate(effectiveDate);
                    }
                    mongoRepositoryReactive.saveOrUpdate(license);
                }
            }

            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/delete-payment")
    public Mono<ResponseEntity> deletePayment() {
        try {
            Institution institution = new Institution();
            institution.setId(UUID.randomUUID().toString());
            institution.setInstitutionName("Legacy Payment Operator");
            institution.setGameTypeIds(new HashSet<>(Arrays.asList("01", "02")));
            institution.setEmailAddress("test@mailinator.com");
            institution.setFromLiveData(true);
            mongoRepositoryReactive.saveOrUpdate(institution);

            License license = new License();
            license.setId(UUID.randomUUID().toString());
            license.setLicenseStatusId(LicenseStatusReferenceData.LICENSE_RUNNING);
            license.setEffectiveDate(new LocalDate("2016-08-01"));
            license.setExpiryDate(new LocalDate("2016-11-01"));
            license.setInstitutionId(institution.getId());
            license.setGameTypeId("02");
            license.setLicenseTypeId(LicenseTypeReferenceData.INSTITUTION_ID);
            mongoRepositoryReactive.saveOrUpdate(license);


            AIPDocumentApproval aipDocumentApproval = new AIPDocumentApproval();
            aipDocumentApproval.setFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
            aipDocumentApproval.setGameTypeId(license.getGameTypeId());
            aipDocumentApproval.setInstitutionId(license.getInstitutionId());
            aipDocumentApproval.setId(UUID.randomUUID().toString());
            aipDocumentApproval.setReadyForApproval(false);
            mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
            /**
             ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
             for (Institution institution : institutions) {
             logger.info("Changing institution email {}", institution.getEmailAddress());
             institution.setEmailAddress("test@mailinator.com");
             mongoRepositoryReactive.saveOrUpdate(institution);
             }
             */

            /**
             List<String> aipStatuses = new ArrayList<>(LicenseStatusReferenceData.getAIPLicenseStatues());
             aipStatuses.add(LicenseStatusReferenceData.LICENSE_RUNNING);
             Query query = new Query();
             query.addCriteria(Criteria.where("licenseStatusId").in(aipStatuses));
             ArrayList<License> licenses = (ArrayList<License>) mongoRepositoryReactive.findAll(query, License.class).toStream().collect(Collectors.toList());
             for (License license : licenses) {
             Institution institution = license.getInstitution();
             if (institution != null && institution.isFromLiveData()) {
             Query query1 = new Query();
             query1.addCriteria(Criteria.where("institutionId").is(license.getInstitutionId()));
             query1.addCriteria(Criteria.where("licenseStatusId").is(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID));
             query1.addCriteria(Criteria.where("licenseTypeId").is(license.getLicenseTypeId()));
             query1.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
             License mainLicense = (License) mongoRepositoryReactive.find(query1, License.class).block();
             if (mainLicense != null) {
             mainLicense.setLicenseNumber(generateLicenseNumberForOperator(license.getGameTypeId()));
             mongoRepositoryReactive.saveOrUpdate(mainLicense);
             }
             license.setLicenseNumber(null);
             mongoRepositoryReactive.saveOrUpdate(license);

             Query query2 = new Query();
             query2.addCriteria(Criteria.where("institutionId").is(license.getInstitutionId()));
             query2.addCriteria(Criteria.where("gameTypeId").is(license.getGameTypeId()));
             query2.addCriteria(Criteria.where("fromStatusId").is(ApplicationFormStatusReferenceData.CREATED_STATUS_ID));
             ArrayList<AIPDocumentApproval> aipDocumentApprovals = (ArrayList<AIPDocumentApproval>) mongoRepositoryReactive.findAll(query2, AIPDocumentApproval.class).toStream().collect(Collectors.toList());
             for (AIPDocumentApproval documentApproval : aipDocumentApprovals) {
             mongoRepositoryReactive.delete(documentApproval);
             }

             AIPDocumentApproval aipDocumentApproval = new AIPDocumentApproval();
             aipDocumentApproval.setFormStatusId(ApplicationFormStatusReferenceData.CREATED_STATUS_ID);
             aipDocumentApproval.setGameTypeId(license.getGameTypeId());
             aipDocumentApproval.setInstitutionId(license.getInstitutionId());
             aipDocumentApproval.setId(UUID.randomUUID().toString());
             aipDocumentApproval.setReadyForApproval(false);
             mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
             }
             }

             */
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("An error occurred ", e);
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-terminals")
    public Mono<ResponseEntity> uploadTerminals(@RequestParam("institutionId") String institutionId,
                                                @RequestParam("gameTypeId") String gameTypeId,
                                                @RequestParam("file") MultipartFile multipartFile) {
        try {
            existingGamingTerminalLoader.loadMachinesFromFile(institutionId, gameTypeId, multipartFile);
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/save-image")
    public ResponseEntity saveImage(@RequestParam("file") MultipartFile multipartFile) {
        try {
            myFileManager.writeImageToFile(multipartFile);
            return new ResponseEntity<>("Done", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/read-image")
    public ResponseEntity readImage(@RequestParam("fileName") String fileName) {
        String fileBase64 = myFileManager.readImage(fileName);
        if (fileBase64 == null) {
            return new ResponseEntity<>("Error occurred while reading", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(fileBase64, HttpStatus.OK);
        }
    }

    private String generateLicenseNumberForOperator(String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        LocalDateTime time = LocalDateTime.now();
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10, 1000));
        return String.format("LSLB-OP-%s-%s%s", gameType.getShortCode(), randomDigit, time.getSecondOfMinute());
    }

}

