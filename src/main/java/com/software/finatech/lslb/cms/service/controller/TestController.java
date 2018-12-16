package com.software.finatech.lslb.cms.service.controller;


import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.domain.License;
import com.software.finatech.lslb.cms.service.dto.PaymentRecordDetailCreateDto;
import com.software.finatech.lslb.cms.service.exception.LicenseServiceException;
import com.software.finatech.lslb.cms.service.service.contracts.PaymentRecordDetailService;
import com.software.finatech.lslb.cms.service.service.contracts.VigipayService;
import com.software.finatech.lslb.cms.service.util.DatabaseLoaderUtils;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingAgentLoader;
import com.software.finatech.lslb.cms.service.util.data_updater.ExistingOperatorLoader;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

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
    private Environment environment;

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
            existingOperatorLoader.init();
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
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById("5596936a-eb26-4614-a013-49742cd8037b", AuthInfo.class).block();
            if (authInfo != null) {
                authInfo.setInstitutionId(null);
                mongoRepositoryReactive.saveOrUpdate(authInfo);
            }
            return Mono.just(new ResponseEntity<>("Done", HttpStatus.OK));
        } catch (Exception e) {
            return Mono.just(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}

