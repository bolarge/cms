package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.referencedata.ApplicationFormStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import com.software.finatech.lslb.cms.service.util.DatabaseLoaderUtils;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingAgentLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingMachineLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingGamingTerminalLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingOperatorLoader;
import com.software.finatech.lslb.cms.service.util.httpclient.MyFileManager;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private ExistingGamingMachineLoader existingGamingMachineLoader;
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


//    @RequestMapping(method = RequestMethod.POST, value = "/back-date")
//    public Mono<ResponseEntity> backDate() {
//        try {
//            existingOperatorLoader.init();
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
            /**
             Query query = new Query();
             query.addCriteria(Criteria.where("fromLiveData").is(true));
             ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
             for (Agent agent : agents) {
             for (String gameTypeId : agent.getGameTypeIds()) {
             GameType gameType = gameTypeService.findById(gameTypeId);
             License license = new License();
             license.setId(UUID.randomUUID().toString());
             license.setAgentId(agent.getId());
             license.setGameTypeId(gameTypeId);
             if (gameType.getAllowsGamingTerminal()) {
             license.setLicenseTypeId(LicenseTypeReferenceData.GAMING_TERMINAL_ID);
             } else {
             license.setLicenseTypeId(LicenseTypeReferenceData.AGENT_ID);
             }
             String licenseNumber = generateLicenseNumberForAgent(gameType);
             license.setEffectiveDate(LocalDate.now().withDayOfYear(1));
             license.setExpiryDate(LocalDate.now().withDayOfYear(365));
             license.setLicenseNumber(licenseNumber);
             license.setLicenseStatusId(LicenseStatusReferenceData.LICENSED_LICENSE_STATUS_ID);
             license.setParentLicenseId("1234");
             mongoRepositoryReactive.saveOrUpdate(license);

             query = new Query();
             query.addCriteria(Criteria.where("agentId").is(agent.getId()));
             query.addCriteria(Criteria.where("gameTypeId").is(gameTypeId));
             ArrayList<Machine> machines = (ArrayList<Machine>) mongoRepositoryReactive.findAll(query, Machine.class).toStream().collect(Collectors.toList());
             for (Machine machine : machines) {
             machine.setMachineStatusId(MachineStatusReferenceData.ACTIVE_ID);
             machine.setLicenseId(license.getId());
             mongoRepositoryReactive.saveOrUpdate(machine);
             }
             }
             }


             */
            Query query = new Query();
            query.addCriteria(Criteria.where("formStatusId").is(ApplicationFormStatusReferenceData.IN_REVIEW_STATUS_ID));
            query.addCriteria(Criteria.where("finalNotificationSent").is(false));
            ArrayList<AIPDocumentApproval> aipDocumentApprovals = (ArrayList<AIPDocumentApproval>) mongoRepositoryReactive.findAll(query, AIPDocumentApproval.class).toStream().collect(Collectors.toList());
            for (AIPDocumentApproval aipDocumentApproval : aipDocumentApprovals) {
                query = new Query();
                query.addCriteria(Criteria.where("entityId").is(aipDocumentApproval.getId()));
                query.addCriteria(Criteria.where("isCurrent").is(true));
                ArrayList<Document> documents = (ArrayList<Document>) mongoRepositoryReactive.findAll(query, Document.class).toStream().collect(Collectors.toList());
                if (!documents.isEmpty()) {
                    int countApproval = 0;

                    for (Document document : documents) {
                        if (document.requiresApproval()) {
                            countApproval = countApproval + 1;
                        }
                    }
                    logger.info("==== COUNT = {}", countApproval);
                    if (countApproval == 0) {
                        aipDocumentApproval.setReadyForApproval(true);
                        mongoRepositoryReactive.saveOrUpdate(aipDocumentApproval);
                    }
                }
            }
            /**
             query.addCriteria(Criteria.where("forTest").is(true));
             ArrayList<Institution> institutions = (ArrayList<Institution>) mongoRepositoryReactive.findAll(query, Institution.class).toStream().collect(Collectors.toList());
             for (Institution institution : institutions) {
             String institutionPresentId = institution.getId();
             String oldInstitutionName = institution.getInstitutionName().replace(" Test", "");
             query = new Query();
             query.addCriteria(Criteria.where("institutionName").regex(oldInstitutionName, "i"));
             Institution oldInstitution = (Institution) mongoRepositoryReactive.find(query, Institution.class).block();
             if (oldInstitution == null) {
             continue;
             }
             String oldInstitutionId = oldInstitution.getId();
             query = new Query();
             query.addCriteria(Criteria.where("institutionIds").in(oldInstitution.getId()));
             ArrayList<Agent> agents = (ArrayList<Agent>) mongoRepositoryReactive.findAll(query, Agent.class).toStream().collect(Collectors.toList());
             for (Agent agent : agents) {
             agent.getInstitutionIds().add(institutionPresentId);

             for (AgentInstitution agentInstitution : agent.getAgentInstitutions()) {
             if (StringUtils.equals(agentInstitution.getInstitutionId(), oldInstitutionId)) {
             agentInstitution.setInstitutionId(institutionPresentId);
             agentInstitution.setOldInstitutionId(oldInstitutionId);
             }
             }
             agent.getInstitutionIds().add(institutionPresentId);
             mongoRepositoryReactive.saveOrUpdate(agent);
             }

             query = new Query();
             query.addCriteria(Criteria.where("institutionId").is(oldInstitutionId));
             ArrayList<Machine> machines = (ArrayList<Machine>) mongoRepositoryReactive.findAll(query, Machine.class).toStream().collect(Collectors.toList());
             for (Machine machine : machines) {
             machine.setInstitutionId(institutionPresentId);
             machine.setOldInstitutionId(oldInstitutionId);
             mongoRepositoryReactive.saveOrUpdate(machine);
             }
             }
             */
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("An error occurred ", e);
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private String generateLicenseNumberForAgent(GameType gameType) {
        String prefix = "LSLB-";

        if (gameType.getAllowsGamingTerminal()) {
            prefix = prefix + "GT-";
        } else {
            prefix = prefix + "AG-";
        }
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(100, 1000));
        if (gameType != null && !StringUtils.isEmpty(gameType.getShortCode())) {
            prefix = prefix + gameType.getShortCode() + "-";
        }
        return String.format("%s%s%s", prefix, randomDigit, LocalDateTime.now().getSecondOfMinute());
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


    @RequestMapping(method = RequestMethod.POST, value = "/upload-machines")
    public Mono<ResponseEntity> uploadMachines(@RequestParam("institutionId") String institutionId,
                                               @RequestParam("gameTypeId") String gameTypeId,
                                               @RequestParam("file") MultipartFile multipartFile) {
        try {
            existingGamingMachineLoader.loadMachines(multipartFile, institutionId, gameTypeId);
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
    public Mono<ResponseEntity> readImage() throws UnsupportedEncodingException {
        try {
            String url = "https://partners.upnp.xyz/Auth/GetTokenLagosAuth";
            String username = "LagosLDAP";
            String password = "A0PN7F*x4CRNBGemU9z}Q0l{SeheTbx";
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("Login", username));
            urlParameters.add(new BasicNameValuePair("Password", password));
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            httpPost.addHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());

            if (responseCode == 200) {
                // everything is fine, handle the response
                String token = mapper.readValue(stringResponse, String.class);
                logger.info("TOken GOtten {}", token);
            }


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HashMap<String, String> map = new HashMap<>();
            map.put("Login", username);
            map.put("Password", password);
            String requestJson = mapper.writeValueAsString(map);
            HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                String responseBody = responseEntity.getBody();
                String splitted = responseBody.replace("\"", "");
                logger.info("Body gotten {}", responseBody);
                logger.info("Body Trimmed {}", splitted);
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private String generateLicenseNumberForOperator(String gameTypeId) {
        GameType gameType = gameTypeService.findById(gameTypeId);
        LocalDateTime time = LocalDateTime.now();
        String randomDigit = String.valueOf(NumberUtil.getRandomNumberInRange(10, 1000));
        return String.format("LSLB-OP-%s-%s%s", gameType.getShortCode(), randomDigit, time.getSecondOfMinute());
    }
}


