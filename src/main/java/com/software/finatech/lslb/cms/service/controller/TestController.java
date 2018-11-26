package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.Document;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.InstitutionCategoryDetails;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.referencedata.DocumentTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingAgentLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingOperatorLoader;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping("/test")
public class TestController extends BaseController {
    @Autowired
    private VigipayService vigipayService;
    @Autowired
    private PaymentRecordDetailService paymentRecordDetailService;
    @Autowired
    private ExistingOperatorLoader existingOperatorLoader;
    @Autowired
    private ExistingAgentLoader existingAgentLoader;

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
    public Mono<ResponseEntity> create(@RequestParam("file") MultipartFile multipartFile) {
        try {
            existingOperatorLoader.loadFromCsv(multipartFile);
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

    @RequestMapping(method = RequestMethod.POST, value = "/delete-old-documents")
    public Mono<ResponseEntity> deleteOldDocuments() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("createdAt").gte(LocalDate.now()));
            query.addCriteria(Criteria.where("documentTypeId").is(DocumentTypeReferenceData.AGENT_PASSPORT_ID));
            ArrayList<Document> documentArrayList = (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
            for (Document document : documentArrayList) {
                Agent agent = (Agent) mongoRepositoryReactive.findById(document.getEntityId(), Agent.class).block();
                if (agent == null) {
                    mongoRepositoryReactive.delete(document);
                    logger.info("Deleted document with id {}, agent with id {} not found",
                            document.getId(), document.getEntityId());
                } else {
                    LoggerFactory.getLogger("DOCUMENT DELETER").info("------------Not Deleting document with id {}, agent with id {} found--------",
                            document.getId(), document.getEntityId());
                }
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete-old-agents")
    public Mono<ResponseEntity> deleteOldAgents() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("createdAt").gte(LocalDate.now()));
            query.addCriteria(Criteria.where("submissionId").is(null));
            ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
            for (Agent agent : agents) {
                logger.info("Deleting  agent with Name {} , Id {}", agent.getFullName(), agent.getId());
                mongoRepositoryReactive.delete(agent);
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete-new-agents")
    public Mono<ResponseEntity> deleteNewAgents() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("createdAt").gte(LocalDate.now()));
            ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
            for (Agent agent : agents) {
                query = new Query();
                query.addCriteria(Criteria.where("fullName").is(agent.getFullName()));
                ArrayList<Agent> agentArrayList = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
                if (agentArrayList.size() > 1) {
                    logger.info("Deleting  agent with Name {} ", agent.getFullName());
                    mongoRepositoryReactive.delete(agent);
                }
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update-new-operators")
    public Mono<ResponseEntity> updateOperators() {
        try {
            ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(new Query(), Institution.class).toStream().collect(Collectors.toList());
            for (Institution institution : institutions) {
                for (String categoryDetailsId : institution.getInstitutionCategoryDetailIds()) {
                    InstitutionCategoryDetails categoryDetails = (InstitutionCategoryDetails) mongoRepositoryReactive.findById(categoryDetailsId, InstitutionCategoryDetails.class).block();
                    if (categoryDetails != null && !institution.getGameTypeIds().contains(categoryDetails.getGameTypeId())) {
                        institution.getGameTypeIds().add(categoryDetails.getGameTypeId());
                        if (!StringUtils.startsWith(institution.getPhoneNumber(), "0")) {
                            institution.setPhoneNumber(String.format("0%s", institution.getPhoneNumber()));
                        }
                        mongoRepositoryReactive.saveOrUpdate(institution);
                        logger.info("Added gametype id {} to operator {}", categoryDetails.getGameTypeId(), institution.getInstitutionName());
                    }
                }
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}

