//package com.software.finatech.lslb.cms.service.service;
//
//import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
//import com.software.finatech.lslb.cms.service.domain.*;
//import com.software.finatech.lslb.cms.service.dto.*;
//import com.software.finatech.lslb.cms.service.model.GamingTerminalGameDetails;
//import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
//import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
//import com.software.finatech.lslb.cms.service.referencedata.*;
//import com.software.finatech.lslb.cms.service.service.contracts.*;
//import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
//import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
//import com.software.finatech.lslb.cms.service.util.NumberUtil;
//import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
//import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.tuple.ImmutablePair;
//import org.apache.commons.lang3.tuple.Pair;
//import org.joda.time.LocalDate;
//import org.joda.time.LocalDateTime;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import reactor.core.publisher.Mono;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;
//
//@Service
//public class GamingTerminalServiceImpl implements GamingTerminalService {
//
//    private static final Logger logger = LoggerFactory.getLogger(GamingTerminalServiceImpl.class);
//    private static final String gamingTerminalAuditActionId = AuditActionReferenceData.GAMING_TERMINAL_ID;
//
//    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
//    private LicenseValidatorUtil licenseValidatorUtil;
//    private InstitutionService institutionService;
//    private FeeService feeService;
//    private PaymentRecordService paymentRecordService;
//    private AuthInfoService authInfoService;
//    private VigipayService vigipayService;
//    private SpringSecurityAuditorAware springSecurityAuditorAware;
//    private AuditLogHelper auditLogHelper;
//
//    @Autowired
//    public GamingTerminalServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
//                                     LicenseValidatorUtil licenseValidatorUtil,
//                                     InstitutionService institutionService,
//                                     FeeService feeService,
//                                     PaymentRecordService paymentRecordService,
//                                     AuthInfoService authInfoService,
//                                     VigipayService vigipayService,
//                                     SpringSecurityAuditorAware springSecurityAuditorAware,
//                                     AuditLogHelper auditLogHelper) {
//        this.mongoRepositoryReactive = mongoRepositoryReactive;
//        this.licenseValidatorUtil = licenseValidatorUtil;
//        this.institutionService = institutionService;
//        this.feeService = feeService;
//        this.paymentRecordService = paymentRecordService;
//        this.authInfoService = authInfoService;
//        this.vigipayService = vigipayService;
//        this.springSecurityAuditorAware = springSecurityAuditorAware;
//        this.auditLogHelper = auditLogHelper;
//    }
//
//    @Override
//    public Mono<ResponseEntity> findAllGamingTerminals(int page,
//                                                      int pageSize,
//                                                      String sortDirection,
//                                                      String sortProperty,
//                                                      String institutionId,
//                                                      HttpServletResponse httpServletResponse) {
//
//        try {
//            Query query = new Query();
//            if (!StringUtils.isEmpty(institutionId)) {
//                query.addCriteria(Criteria.where("institutionId").is(institutionId));
//            }
//            if (page == 0) {
//                long count = mongoRepositoryReactive.count(query, GamingTerminal.class).block();
//                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
//            }
//
//            Sort sort;
//            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
//                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
//                        sortProperty);
//            } else {
//                sort = new Sort(Sort.Direction.DESC, "id");
//            }
//            query.with(PageRequest.of(page, pageSize, sort));
//            query.with(sort);
//
//            ArrayList<GamingTerminal> gamingTerminals = (ArrayList<GamingTerminal>) mongoRepositoryReactive.findAll(query, GamingTerminal.class).toStream().collect(Collectors.toList());
//            if (gamingTerminals == null || gamingTerminals.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
//            }
//            ArrayList<GamingTerminalDto> gamingTerminalDtos = new ArrayList<>();
//
//            gamingTerminals.forEach(gamingTerminal -> {
//                gamingTerminalDtos.add(gamingTerminal.convertToDto());
//            });
//
//            return Mono.just(new ResponseEntity<>(gamingTerminalDtos, HttpStatus.OK));
//        } catch (Exception e) {
//            String errorMsg = "An error occurred while trying to get all gaming terminals";
//            return logAndReturnError(logger, errorMsg, e);
//        }
//    }
//
//    @Override
//    public Mono<ResponseEntity> createGamingTerminal(GamingTerminalCreateDto gamingTerminalCreateDto, HttpServletRequest request) {
//        try {
//            String institutionId = gamingTerminalCreateDto.getInstitutionId();
//            String gameTypeId = gamingTerminalCreateDto.getGameTypeId();
//            Mono<ResponseEntity> validateGamingTerminalTaxResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
//            if (validateGamingTerminalTaxResponse != null) {
//                return validateGamingTerminalTaxResponse;
//            }
//            GamingTerminal gamingTerminal = fromGamingTerminalCreateDto(gamingTerminalCreateDto);
//            saveGamingTerminal(gamingTerminal);
//
//            String verbiage = String.format("Created Gaming Terminal, Serial number -> %s ", gamingTerminal.getSerialNumber());
//            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(gamingTerminalAuditActionId,
//                    springSecurityAuditorAware.getCurrentAuditorNotNull(), gamingTerminal.getInstitutionName(),
//                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
//
//
//            return Mono.just(new ResponseEntity<>(gamingTerminal.convertToDto(), HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while saving gaming terminal", e);
//        }
//    }
//
//
//    @Override
//    public Mono<ResponseEntity> updateGamingTerminal(GamingTerminalUpdateDto gamingTerminalUpdateDto, HttpServletRequest request) {
//        try {
//            String gamingTerminalId = gamingTerminalUpdateDto.getId();
//            GamingTerminal gamingTerminal = findById(gamingTerminalId);
//            if (gamingTerminal == null) {
//                return Mono.just(new ResponseEntity<>(String.format("Gaming terminal with id %s does not exist", gamingTerminalId), HttpStatus.BAD_REQUEST));
//            }
//        //    gamingTerminal.setGameDetailsList(gamingTerminalUpdateDto.getGameDetailsList());
//            //gamingTerminal.setTerminalNumber(gamingTerminalUpdateDto.getGameTerminalNumber());
//            gamingTerminal.setSerialNumber(gamingTerminalUpdateDto.getSerialNumber());
////            gamingTerminal.setManufacturer(gamingTerminalUpdateDto.getManufacturer());
////            gamingTerminal.setTerminalAddress(gamingTerminalUpdateDto.getTerminalAddress());
//            saveGamingTerminal(gamingTerminal);
//
//            String verbiage = String.format("Updated Gaming Terminal, Serial Number -> %s ", gamingTerminal.getSerialNumber());
//            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(gamingTerminalAuditActionId,
//                    springSecurityAuditorAware.getCurrentAuditorNotNull(), gamingTerminal.getInstitutionName(),
//                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
//
//            return Mono.just(new ResponseEntity<>(gamingTerminal.convertToDto(), HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while updating gaming terminal", e);
//        }
//    }
//
//    @Override
//    public GamingTerminal findById(String gamingTerminalId) {
//        return (GamingTerminal) mongoRepositoryReactive.findById(gamingTerminalId, GamingTerminal.class).block();
//    }
//
//
//    //TODO: validate if its multiple or not
//    @Override
//    public Mono<ResponseEntity> uploadMultipleGamingTerminalsForInstitution(String institutionId, String gameTypeId, MultipartFile multipartFile, HttpServletRequest request) {
//        Institution institution = institutionService.findById(institutionId);
//        if (institution == null) {
//            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
//        }
//        Mono<ResponseEntity> validateGamingTerminalLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
//        if (validateGamingTerminalLicenseResponse != null) {
//            return validateGamingTerminalLicenseResponse;
//        }
//
//        List<GamingTerminal> gamingTerminalList = new ArrayList<>();
//        List<FailedLine> failedLines = new ArrayList<>();
//        UploadTransactionResponse uploadTransactionResponse = new UploadTransactionResponse();
//        if (!multipartFile.isEmpty()) {
//            try {
//                byte[] bytes = multipartFile.getBytes();
//                String completeData = new String(bytes);
//                String[] rows = completeData.split("\\r?\\n");
//                Map<String, GamingTerminal> gamingTerminalMap = new HashMap<>();
//                for (int i = 1; i < rows.length; i++) {
//                    String[] columns = rows[i].split(",");
//                    if (columns.length < 6) {
//                        failedLines.add(FailedLine.fromLineAndReason(rows[i], "Line has less than 6 fields"));
//                    } else {
//                        try {
//                            GamingTerminal gamingTerminal = getGamingTerminalBySerialNumber(columns[0], institutionId, gamingTerminalMap);
//                            if (gamingTerminal == null) {
//                                gamingTerminal = new GamingTerminal();
//                                gamingTerminal.setId(UUID.randomUUID().toString());
//                                gamingTerminal.setSerialNumber(columns[0]);
//                                gamingTerminal.setAssigned(false);
//                                //gamingTerminal.setTerminalNumber(columns[1]);
//                                gamingTerminal.setGameTypeId(gameTypeId);
//                                GamingTerminalGameDetails gamingTerminalGameDetails = new GamingTerminalGameDetails();
//                                gamingTerminalGameDetails.setGameName(columns[1]);
//                                gamingTerminalGameDetails.setGameVersion(columns[2]);
//                                Set<GamingTerminalGameDetails> gamingTerminalGameDetailsSet = new HashSet<>();
//                                gamingTerminalGameDetailsSet.add(gamingTerminalGameDetails);
////                                gamingTerminal.getGameDetailsList().stream().forEach(gameList->{
////                                    MachineGame machineGame= new MachineGame();
////                                    machineGame.setGamingTerminalId(gamingTerminal.getId());
////                                    machineGame.setGameName(gameList.getGameName());
////                                    machineGame.setGameName(gameList.getGameVersion());
////                                    machineGame.setActive(true);
////                                    mongoRepositoryReactive.saveOrUpdate(machineGame);
////                                });
//                                gamingTerminal.setGameDetailsList(gamingTerminalGameDetailsSet);
//                            } else {
//                                GamingTerminalGameDetails gamingTerminalGameDetails = new GamingTerminalGameDetails();
//                                gamingTerminalGameDetails.setGameName(columns[1]);
//                                gamingTerminalGameDetails.setGameVersion(columns[2]);
//                            }
//                            gamingTerminal.setInstitutionId(institutionId);
//                            gamingTerminalList.add(gamingTerminal);
//                            gamingTerminalMap.put(gamingTerminal.getSerialNumber(), gamingTerminal);
//                        } catch (Exception e) {
//                            logger.error(String.format("Error parsing line %s", rows[i]), e);
//                            failedLines.add(FailedLine.fromLineAndReason(rows[i], "An error occurred while parsing line"));
//                        }
//                    }
//                }
//
//                if (!failedLines.isEmpty()) {
//                    uploadTransactionResponse.setFailedLines(failedLines);
//                    uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
//                    uploadTransactionResponse.setMessage(String.format(
//                            "Please review with sample file and re upload",
//                            failedLines.size()));
//                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.BAD_REQUEST));
//                } else {
//                    for (GamingTerminal gamingTerminal : gamingTerminalList) {
//                        try {
//                            saveGamingTerminal(gamingTerminal);
//                        } catch (Exception e) {
//                            logger.error("An error occurred while saving gaming terminal with serial number {}", gamingTerminal.getSerialNumber());
//                            String line = String.format("%s", gamingTerminal.getSerialNumber());
//                            failedLines.add(FailedLine.fromLineAndReason(line, "An error occurred while saving line"));
//                        }
//                    }
//                    if (!failedLines.isEmpty()) {
//                        uploadTransactionResponse.setMessage("Upload partially successful, please see failed records");
//                        uploadTransactionResponse.setFailedLines(failedLines);
//                        uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
//                    } else {
//                        uploadTransactionResponse.setMessage("Upload successful");
//                    }
//
//                    String verbiage = "Uploaded multiple gaming terminals ";
//                    auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(gamingTerminalAuditActionId,
//                            springSecurityAuditorAware.getCurrentAuditorNotNull(), institution.getInstitutionName(),
//                            LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
//
//                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.OK));
//                }
//            } catch (IOException e) {
//                return logAndReturnError(logger, "An error occurred while parsing the file", e);
//            }
//        } else {
//            return Mono.just(new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST));
//        }
//    }
//
//    private GamingTerminal findBySerialNumber(String serialNumber, String institutionId) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("serialNumber").is(serialNumber));
//        query.addCriteria(Criteria.where("institutionId").is(institutionId));
//        return (GamingTerminal) mongoRepositoryReactive.find(query, GamingTerminal.class).block();
//    }
//
//    private GamingTerminal getGamingTerminalBySerialNumber(String serialNumber, String institutionId, Map<String, GamingTerminal> gamingTerminalMap) {
//        GamingTerminal gamingTerminal = gamingTerminalMap.get(serialNumber);
//        if (gamingTerminal != null) {
//            return gamingTerminal;
//        }
//        gamingTerminal = findBySerialNumber(serialNumber, institutionId);
//        if (gamingTerminal != null) {
//            gamingTerminalMap.put(serialNumber, gamingTerminal);
//        }
//        return gamingTerminal;
//    }
//
//
//    private GamingTerminal fromGamingTerminalCreateDto(GamingTerminalCreateDto gamingTerminalCreateDto) {
//        GamingTerminal gamingTerminal = new GamingTerminal();
//        gamingTerminal.setId(UUID.randomUUID().toString());
//        gamingTerminal.setInstitutionId(gamingTerminalCreateDto.getInstitutionId());
//        gamingTerminal.setSerialNumber(gamingTerminalCreateDto.getSerialNumber());
//        gamingTerminal.setManufacturer(gamingTerminalCreateDto.getManufacturer());
//        gamingTerminal.setGameDetailsList(gamingTerminalCreateDto.getGameDetailsList());
//        gamingTerminal.getGameDetailsList().stream().forEach(gameList->{
//            MachineGame machineGame= new MachineGame();
//            machineGame.setGamingTerminalId(gamingTerminal.getId());
//            machineGame.setGameName(gameList.getGameName());
//            machineGame.setGameName(gameList.getGameVersion());
//            machineGame.setActive(true);
//            mongoRepositoryReactive.saveOrUpdate(machineGame);
//        });
//
//        gamingTerminal.setAssigned(false);
//        gamingTerminal.setGameTypeId(gamingTerminalCreateDto.getGameTypeId());
//        return gamingTerminal;
//    }
//
//    private void saveGamingTerminal(GamingTerminal gamingTerminal) {
//        mongoRepositoryReactive.saveOrUpdate(gamingTerminal);
//    }
//
//    @Override
//    public Mono<ResponseEntity> validateMultipleGamingTerminalTaxPayment(GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest) {
//        try {
//            GamingTerminalMultiplePaymentResponse gamingTerminalMultiplePaymentResponse = new GamingTerminalMultiplePaymentResponse();
//            Set<String> terminalIds = gamingTerminalMultiplePaymentRequest.getGamingTerminalIdList();
//            if (terminalIds.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty terminal ids supplied", HttpStatus.BAD_REQUEST));
//            }
//
//            List<ValidGamingTerminalPayment> validGamingTerminalPayments = new ArrayList<>();
//            List<InvalidGamingTerminalPayment> invalidGamingTerminalPayments = new ArrayList<>();
//            List<String> validGamingTerminal = new ArrayList<>();
//
//            double totalAmount = 0;
//            FeePaymentType feePaymentType = feeService.getFeePaymentTypeById(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
//            if (feePaymentType == null) {
//                return Mono.just(new ResponseEntity<>("Licence fee payment type not found on system", HttpStatus.INTERNAL_SERVER_ERROR));
//            }
//
//            for (String gamingTerminalId : terminalIds) {
//                Pair<ValidGamingTerminalPayment, InvalidGamingTerminalPayment> terminalPaymentPair = getTerminalPaymentPairForTaxPayment(gamingTerminalId, feePaymentType.getName());
//                ValidGamingTerminalPayment validGamingTerminalPayment = terminalPaymentPair.getLeft();
//                InvalidGamingTerminalPayment invalidGamingTerminalPayment = terminalPaymentPair.getRight();
//                if (validGamingTerminalPayment == null && invalidGamingTerminalPayment != null) {
//                    invalidGamingTerminalPayments.add(invalidGamingTerminalPayment);
//                } else if (validGamingTerminalPayment != null && invalidGamingTerminalPayment == null) {
//                    validGamingTerminalPayments.add(validGamingTerminalPayment);
//                    totalAmount = totalAmount + validGamingTerminalPayment.getAmount();
//                    validGamingTerminal.add(gamingTerminalId);
//                } else {
//                    logger.info("Invalid validation for license payment for gaming terminal with id {} ", gamingTerminalId);
//                }
//            }
//            gamingTerminalMultiplePaymentResponse.setAmountTotal(totalAmount);
//            gamingTerminalMultiplePaymentResponse.setInvalidGamingTerminalPaymentList(invalidGamingTerminalPayments);
//            gamingTerminalMultiplePaymentResponse.setValidGamingTerminalsList(validGamingTerminal);
//            gamingTerminalMultiplePaymentResponse.setValidGamingTerminalPaymentList(validGamingTerminalPayments);
//            return Mono.just(new ResponseEntity<>(gamingTerminalMultiplePaymentResponse, HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while validating multiple gaming terminal license payment ", e);
//        }
//    }
//
//    @Override
//    public Mono<ResponseEntity> validateMultipleGamingTerminalTaxRenewalPayment(GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest) {
//        try {
//            GamingTerminalMultiplePaymentResponse gamingTerminalMultiplePaymentResponse = new GamingTerminalMultiplePaymentResponse();
//            Set<String> terminalIds = gamingTerminalMultiplePaymentRequest.getGamingTerminalIdList();
//            if (terminalIds.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty terminal ids supplied", HttpStatus.BAD_REQUEST));
//            }
//
//            List<ValidGamingTerminalPayment> validGamingTerminalPayments = new ArrayList<>();
//            List<InvalidGamingTerminalPayment> invalidGamingTerminalPayments = new ArrayList<>();
//            List<String> validGamingTerminal = new ArrayList<>();
//
//            double totalAmount = 0;
//            FeePaymentType feePaymentType = feeService.getFeePaymentTypeById(FeePaymentTypeReferenceData.TAX_RENEWAL_FEE_TYPE_ID);
//            if (feePaymentType == null) {
//                return Mono.just(new ResponseEntity<>("Licence renewal fee payment type not found on system", HttpStatus.INTERNAL_SERVER_ERROR));
//            }
//
//            for (String gamingTerminalId : terminalIds) {
//                Pair<ValidGamingTerminalPayment, InvalidGamingTerminalPayment> terminalPaymentPair = getTerminalPaymentPairForTaxRenewalPayment(gamingTerminalId, feePaymentType.getName());
//                ValidGamingTerminalPayment validGamingTerminalPayment = terminalPaymentPair.getLeft();
//                InvalidGamingTerminalPayment invalidGamingTerminalPayment = terminalPaymentPair.getRight();
//                if (validGamingTerminalPayment == null && invalidGamingTerminalPayment != null) {
//                    invalidGamingTerminalPayments.add(invalidGamingTerminalPayment);
//                } else if (validGamingTerminalPayment != null && invalidGamingTerminalPayment == null) {
//                    validGamingTerminalPayments.add(validGamingTerminalPayment);
//                    totalAmount = totalAmount + validGamingTerminalPayment.getAmount();
//                    validGamingTerminal.add(gamingTerminalId);
//                } else {
//                    logger.info("Invalid validation for license renewal payment for gaming terminal with id {} ", gamingTerminalId);
//                }
//            }
//            gamingTerminalMultiplePaymentResponse.setAmountTotal(totalAmount);
//            gamingTerminalMultiplePaymentResponse.setInvalidGamingTerminalPaymentList(invalidGamingTerminalPayments);
//            gamingTerminalMultiplePaymentResponse.setValidGamingTerminalsList(validGamingTerminal);
//            gamingTerminalMultiplePaymentResponse.setValidGamingTerminalPaymentList(validGamingTerminalPayments);
//            return Mono.just(new ResponseEntity<>(gamingTerminalMultiplePaymentResponse, HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while validating multiple gaming terminal license renewal payment ", e);
//        }
//    }
//
//    @Override
//    public Mono<ResponseEntity> findGamingTerminalBySearchKey(String searchKey) {
//        try {
//            Query query = new Query();
//            if (!StringUtils.isEmpty(searchKey)) {
//                query.addCriteria(Criteria.where("serialNumber").regex(searchKey, "i"));
//            }
//            query.with(PageRequest.of(0, 20));
//            ArrayList<GamingTerminal> gamingTerminals = (ArrayList<GamingTerminal>) mongoRepositoryReactive.findAll(query, GamingTerminal.class).toStream().collect(Collectors.toList());
//            if (gamingTerminals == null || gamingTerminals.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
//            }
//            ArrayList<GamingTerminalDto> gamingTerminalDtos = new ArrayList<>();
//            gamingTerminals.forEach(gamingTerminal -> {
//                GamingTerminalDto dto = new GamingTerminalDto();
//                dto.setId(gamingTerminal.getId());
//                dto.setSerialNumber(gamingTerminal.getSerialNumber());
//                gamingTerminalDtos.add(dto);
//            });
//            return Mono.just(new ResponseEntity<>(gamingTerminalDtos, HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while searching gaming terminals by key", e);
//        }
//    }
//
//    @Override
//    public Mono<ResponseEntity> getUnAssignedInstitutionTerminals(String institutionId) {
//        Query queryTerminals= new Query();
//        queryTerminals.addCriteria(Criteria.where("assigned").is(false));
//        queryTerminals.addCriteria(Criteria.where("institutionId").is(institutionId));
//        List<GamingTerminal> gamingTerminals= (List<GamingTerminal>)mongoRepositoryReactive.findAll(queryTerminals, GamingTerminal.class).toStream().collect(Collectors.toList());
//        List<GamingTerminalDto> gamingTerminalDtos= new ArrayList<>();
//        gamingTerminals.stream().forEach(gamingTerminal -> {
//            gamingTerminalDtos.add(gamingTerminal.convertToDto());
//        });
//        if(gamingTerminalDtos.size()==0){
//            return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.BAD_REQUEST));
//        }
//        return Mono.just(new ResponseEntity<>(gamingTerminalDtos, HttpStatus.OK));
//
//    }
//
//    @Override
//    public Mono<ResponseEntity> assignGamingTerminals(AgentGamingTeriminalsUpdateDto agentGamingTeriminalsUpdateDto, HttpServletRequest request) {
//
//        agentGamingTeriminalsUpdateDto.getSerialNumbers().stream().forEach(serialNumber->{
//            Query query= new Query();
//            query.addCriteria(Criteria.where("institutionId").is(agentGamingTeriminalsUpdateDto.getInstitutionId()));
//            query.addCriteria(Criteria.where("serialNumber").is(serialNumber));
//            query.addCriteria(Criteria.where("assigned").is(false));
//            GamingTerminal gamingTerminal =(GamingTerminal)mongoRepositoryReactive.find(query, GamingTerminal.class).block();
//            gamingTerminal.setAgentId(agentGamingTeriminalsUpdateDto.getAgentId());
//            gamingTerminal.setAssigned(true);
//            mongoRepositoryReactive.saveOrUpdate(gamingTerminal);
//        });
//        return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
//    }
//
//
//    private Pair<ValidGamingTerminalPayment, InvalidGamingTerminalPayment> getTerminalPaymentPairForTaxPayment(String gamingTerminalId, String feePaymentTypeName) {
//        ValidGamingTerminalPayment validGamingTerminalPayment = new ValidGamingTerminalPayment();
//        InvalidGamingTerminalPayment invalidGamingTerminalPayment = new InvalidGamingTerminalPayment();
//        GamingTerminal gamingTerminal = findById(gamingTerminalId);
//        if (gamingTerminal == null) {
//            invalidGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//            invalidGamingTerminalPayment.setReason(String.format("Gaming Terminal with id %s does not exist", gamingTerminalId));
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//        String serialNumber = gamingTerminal.getSerialNumber();
//        String institutionId = gamingTerminal.getInstitutionId();
//        GameType gameType = gamingTerminal.getGameType();
//        if (gameType == null) {
//            invalidGamingTerminalPayment = new InvalidGamingTerminalPayment(gamingTerminalId,serialNumber,
//                   String.format("No category found for terminal with serial number %s", gamingTerminal.getSerialNumber()));
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//        String licenseTypeId = LicenseTypeReferenceData.GAMING_TERMINAL_ID;
//        String feePaymentTypeId = FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID;
//        String gameTypeName = gameType.getName();
//        String gameTypeId = gameType.getId();
//        Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//        if (fee == null) {
//            invalidGamingTerminalPayment = new InvalidGamingTerminalPayment(gamingTerminalId,
//                    serialNumber,
//                    String.format("No licence fee found for gaming terminals for category %s", gameTypeName));
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//
//        PaymentRecord existingLicensePayment = paymentRecordService.findPaymentRecordForGamingTerminal(gamingTerminalId, gameTypeId, institutionId, feePaymentTypeId);
//        if (existingLicensePayment != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePayment.getPaymentStatusId())) {
//            invalidGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//            invalidGamingTerminalPayment.setGameTypeName(gameTypeName);
//            invalidGamingTerminalPayment.setSerialNumber(serialNumber);
//            invalidGamingTerminalPayment.setFeePaymentTypeName(feePaymentTypeName);
//            invalidGamingTerminalPayment.setReason("You have an existing licence payment for the terminal");
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//
//        if (existingLicensePayment != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePayment.getPaymentStatusId())) {
//            invalidGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//            invalidGamingTerminalPayment.setGameTypeName(gameTypeName);
//            invalidGamingTerminalPayment.setSerialNumber(serialNumber);
//            invalidGamingTerminalPayment.setFeePaymentTypeName(feePaymentTypeName);
//            invalidGamingTerminalPayment.setReason("Please complete payment for licence for this terminal");
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//        double amount = fee.getAmount();
//        validGamingTerminalPayment.setAmount(amount);
//        validGamingTerminalPayment.setGameTypeName(gameTypeName);
//        validGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//        validGamingTerminalPayment.setSerialNumber(serialNumber);
//        validGamingTerminalPayment.setFeePaymentTypeName(feePaymentTypeName);
//        return new ImmutablePair<>(validGamingTerminalPayment, null);
//    }
//
//    private Pair<ValidGamingTerminalPayment, InvalidGamingTerminalPayment> getTerminalPaymentPairForTaxRenewalPayment(String gamingTerminalId, String feePaymentTypeName) {
//        ValidGamingTerminalPayment validGamingTerminalPayment = new ValidGamingTerminalPayment();
//        InvalidGamingTerminalPayment invalidGamingTerminalPayment = new InvalidGamingTerminalPayment();
//        GamingTerminal gamingTerminal = findById(gamingTerminalId);
//        if (gamingTerminal == null) {
//            invalidGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//            invalidGamingTerminalPayment.setReason(String.format("Gaming Terminal with id %s does not exist", gamingTerminalId));
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//        String serialNumber = gamingTerminal.getSerialNumber();
//        String institutionId = gamingTerminal.getInstitutionId();
//        GameType gameType = gamingTerminal.getGameType();
//        if (gameType == null) {
//            invalidGamingTerminalPayment = new InvalidGamingTerminalPayment(gamingTerminalId,
//                    serialNumber, String.format("No category found for terminal with serial number %s",
//                    gamingTerminal.getSerialNumber()));
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//        String licenseTypeId = LicenseTypeReferenceData.GAMING_TERMINAL_ID;
//        String feePaymentTypeId = FeePaymentTypeReferenceData.TAX_FEE_TYPE_ID;
//        String gameTypeName = gameType.getName();
//        String gameTypeId = gameType.getId();
//        Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//        if (fee == null) {
//            invalidGamingTerminalPayment = new InvalidGamingTerminalPayment(gamingTerminalId,
//                    serialNumber,
//                    String.format("No licence renewal fee found for gaming terminals for category %s", gameTypeName));
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//        PaymentRecord paymentRecord = paymentRecordService.findPaymentRecordForGamingTerminal(gamingTerminalId, gameTypeId, institutionId, FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
//        if (paymentRecord == null) {
//            invalidGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//            invalidGamingTerminalPayment.setGameTypeName(gameTypeName);
//            invalidGamingTerminalPayment.setSerialNumber(serialNumber);
//            invalidGamingTerminalPayment.setReason("Please pay licence payment for this terminal before paying for licence renewal");
//            return new ImmutablePair<>(null, invalidGamingTerminalPayment);
//        }
//
//        double amount = fee.getAmount();
//        validGamingTerminalPayment.setAmount(amount);
//        validGamingTerminalPayment.setGameTypeName(gameTypeName);
//        validGamingTerminalPayment.setGamingTerminalId(gamingTerminalId);
//        validGamingTerminalPayment.setSerialNumber(serialNumber);
//        validGamingTerminalPayment.setFeePaymentTypeName(feePaymentTypeName);
//        return new ImmutablePair<>(validGamingTerminalPayment, null);
//    }
//
//    private Mono<ResponseEntity> makeMultipleGamingTerminalTaxPaymentWeb(GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest) {
//        try {
//            double amountTotal = 0;
//            Set<String> gamingTerminalIdList = gamingTerminalMultiplePaymentRequest.getGamingTerminalIdList();
//            List<PaymentRecordDetailCreateDto> paymentRecordDetailCreateDtoList = new ArrayList<>();
//            String licenseTypeId = LicenseTypeReferenceData.GAMING_MACHINE_ID;
//            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
//            String institutionId = null;
//
//            if (gamingTerminalIdList.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty gaming terminal ids supplied", HttpStatus.BAD_REQUEST));
//            }
//            BatchPayment batchPayment = new BatchPayment();
//            batchPayment.setId(UUID.randomUUID().toString());
//            for (String gamingTerminalId : gamingTerminalIdList) {
//                GamingTerminal gamingTerminal = findById(gamingTerminalId);
//                if (gamingTerminal == null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming terminal with id %s does not exist", gamingTerminalId), HttpStatus.BAD_REQUEST));
//                }
//                institutionId = gamingTerminal.getInstitutionId();
//                String gameTypeId = gamingTerminal.getGameTypeId();
//                PaymentRecord existingLicensePayment = findTaxPaymentForGamingTerminal(gamingTerminalId, institutionId, gameTypeId);
//                if (existingLicensePayment != null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming terminal with id %s has a license payment already", gamingTerminalId), HttpStatus.BAD_REQUEST));
//                }
//                Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//                if (fee == null) {
//                    GameType gameType = gamingTerminal.getGameType();
//                    String gameTypeName = gameType != null ? gameType.getName() : gameTypeId;
//                    return Mono.just(new ResponseEntity<>(String.format("Tax fees for gaming terminals for category %s does not exist", gameTypeName), HttpStatus.BAD_REQUEST));
//                }
//
//                double paymentAmount = fee.getAmount();
//                amountTotal = amountTotal + paymentAmount;
//
//                PaymentRecordDetailCreateDto paymentRecordDetailCreateDto = new PaymentRecordDetailCreateDto();
//                paymentRecordDetailCreateDto.setFeeId(fee.getId());
//                paymentRecordDetailCreateDto.setInstitutionId(institutionId);
//                paymentRecordDetailCreateDto.setGamingTerminalId(gamingTerminalId);
//                paymentRecordDetailCreateDtoList.add(paymentRecordDetailCreateDto);
//                batchPayment.setInstitutionId(institutionId);
//            }
//            if (amountTotal != gamingTerminalMultiplePaymentRequest.getTotalAmount()) {
//                return Mono.just(new ResponseEntity<>("Amount supplied is not equal to total amount", HttpStatus.BAD_REQUEST));
//            }
//
//            batchPayment.setAmountTotal(amountTotal);
//            batchPayment.setGamingTerminalIds(gamingTerminalIdList);
//            batchPayment.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
//
//            paymentRecordDetailCreateDtoList.forEach(paymentRecordDetailCreateDto -> {
//                PaymentRecord paymentRecord = new PaymentRecord();
//                paymentRecord.setId(UUID.randomUUID().toString());
//                Fee fee = feeService.findFeeById(paymentRecordDetailCreateDto.getFeeId());
//                if (fee == null) {
//                    return;
//                }
//                paymentRecord.setGameTypeId(fee.getGameTypeId());
//                paymentRecord.setFeeId(fee.getId());
//                paymentRecord.setAmount(fee.getAmount());
//                paymentRecord.setAmountPaid(0);
//                paymentRecord.setAmountOutstanding(fee.getAmount());
//                paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
//                paymentRecord.setInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
//                paymentRecord.setGamingTerminalId(paymentRecordDetailCreateDto.getGamingTerminalId());
//                paymentRecord.setGameTypeId(fee.getGameTypeId());
//                paymentRecord.setFeePaymentTypeId(fee.getFeePaymentTypeId());
//                paymentRecord.setLicenseTypeId(fee.getLicenseTypeId());
//                paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
//                paymentRecord.setBatchPaymentId(batchPayment.getId());
//
//                PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
//                paymentRecordDetail.setId(UUID.randomUUID().toString());
//                paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
//                paymentRecordDetail.setAmount(fee.getAmount());
//                paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.WEB_PAYMENT_ID);
//                paymentRecord.getPaymentRecordDetailIds().add(paymentRecordDetail.getId());
//                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
//                mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
//            });
//
//            mongoRepositoryReactive.saveOrUpdate(batchPayment);
//            return Mono.just(new ResponseEntity<>(batchPayment.convertToFullDto(), HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while creating multiple gaming terminal license payments ", e);
//        }
//    }
//
//    private Mono<ResponseEntity> makeMultipleGamingTerminalLTaxPaymentInBranch(GamingTerminalMultiplePaymentRequest gamingTerminalMultiplePaymentRequest) {
//        try {
//            double amountTotal = 0;
//            Set<String> gamingTerminalIdList = gamingTerminalMultiplePaymentRequest.getGamingTerminalIdList();
//            List<PaymentRecordDetailCreateDto> paymentRecordDetailCreateDtoList = new ArrayList<>();
//            String licenseTypeId = LicenseTypeReferenceData.GAMING_MACHINE_ID;
//            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
//            String institutionId = null;
//
//            if (gamingTerminalIdList.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty gaming terminal ids supplied", HttpStatus.BAD_REQUEST));
//            }
//            BatchPayment batchPayment = new BatchPayment();
//            batchPayment.setId(UUID.randomUUID().toString());
//            List<FeeAndDescription> feeDescriptions = new ArrayList<>();
//            for (String gamingTerminalId : gamingTerminalIdList) {
//                GamingTerminal gamingTerminal = findById(gamingTerminalId);
//                if (gamingTerminal == null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming terminal with id %s does not exist", gamingTerminalId), HttpStatus.BAD_REQUEST));
//                }
//                institutionId = gamingTerminal.getInstitutionId();
//                String gameTypeId = gamingTerminal.getGameTypeId();
//                PaymentRecord existingLicensePayment = findTaxPaymentForGamingTerminal(gamingTerminalId, institutionId, gameTypeId);
//                if (existingLicensePayment != null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming terminal with id %s has a license payment already", gamingTerminalId), HttpStatus.BAD_REQUEST));
//                }
//                Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//                if (fee == null) {
//                    GameType gameType = gamingTerminal.getGameType();
//                    String gameTypeName = gameType != null ? gameType.getName() : gameTypeId;
//                    return Mono.just(new ResponseEntity<>(String.format("Licencing fees for gaming terminals for category %s does not exist", gameTypeName), HttpStatus.BAD_REQUEST));
//                }
//
//                double paymentAmount = fee.getAmount();
//                amountTotal = amountTotal + paymentAmount;
//
//                FeeDto feeDto = fee.convertToDto();
//                String feeName = feeDto.getFeePaymentTypeName();
//                String gameTypeName = feeDto.getGameTypeName();
//                String revenueName = feeDto.getRevenueName();
//                String suffix = String.format(" for Terminal %s", gamingTerminal.getSerialNumber());
//
//                String feeDescription = String.format("%s for %ss for category : %s %s", feeName, revenueName, gameTypeName, suffix);
//                feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(feeDescription);
//                FeeAndDescription feeAndDescription = new FeeAndDescription();
//                feeAndDescription.setAmount(fee.getAmount());
//                feeAndDescription.setFeeDescription(feeDescription);
//                feeDescriptions.add(feeAndDescription);
//
//                PaymentRecordDetailCreateDto paymentRecordDetailCreateDto = new PaymentRecordDetailCreateDto();
//                paymentRecordDetailCreateDto.setFeeId(fee.getId());
//                paymentRecordDetailCreateDto.setInstitutionId(institutionId);
//                paymentRecordDetailCreateDto.setGamingTerminalId(gamingTerminalId);
//                paymentRecordDetailCreateDtoList.add(paymentRecordDetailCreateDto);
//                batchPayment.setInstitutionId(institutionId);
//            }
//            if (amountTotal != gamingTerminalMultiplePaymentRequest.getTotalAmount()) {
//                return Mono.just(new ResponseEntity<>("Amount supplied is not equal to total amount", HttpStatus.BAD_REQUEST));
//            }
//            Institution institution = institutionService.findById(institutionId);
//            if (institution == null) {
//                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
//            }
//
//
//            List<VigipayInvoiceItem> vigipayInvoiceItems = vigiPayInvoiceItemsFromFeeDescriptions(feeDescriptions);
//            List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorUsersForInstitution(institutionId);
//
//            String invoiceNumber = vigipayService.createInBranchMultipleItemInvoiceForInstitution(institution, institutionAdmins, vigipayInvoiceItems);
//
//            if (invoiceNumber == null) {
//                return Mono.just(new ResponseEntity<>("An error occurred while creating invoice with Vigipay", HttpStatus.INTERNAL_SERVER_ERROR));
//            }
//            batchPayment.setAmountTotal(amountTotal);
//            batchPayment.setGamingTerminalIds(gamingTerminalIdList);
//            batchPayment.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
//
//            paymentRecordDetailCreateDtoList.forEach(paymentRecordDetailCreateDto -> {
//                PaymentRecord paymentRecord = new PaymentRecord();
//                paymentRecord.setId(UUID.randomUUID().toString());
//                Fee fee = feeService.findFeeById(paymentRecordDetailCreateDto.getFeeId());
//                if (fee == null) {
//                    return;
//                }
//                paymentRecord.setGameTypeId(fee.getGameTypeId());
//                paymentRecord.setFeeId(fee.getId());
//                paymentRecord.setAmount(fee.getAmount());
//                paymentRecord.setAmountPaid(0);
//                paymentRecord.setAmountOutstanding(fee.getAmount());
//                paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
//                paymentRecord.setInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
//                paymentRecord.setGamingTerminalId(paymentRecordDetailCreateDto.getGamingTerminalId());
//                paymentRecord.setGameTypeId(fee.getGameTypeId());
//                paymentRecord.setFeePaymentTypeId(fee.getFeePaymentTypeId());
//                paymentRecord.setLicenseTypeId(fee.getLicenseTypeId());
//                paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
//                paymentRecord.setBatchPaymentId(batchPayment.getId());
//
//                PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
//                paymentRecordDetail.setId(UUID.randomUUID().toString());
//                paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
//                paymentRecordDetail.setAmount(fee.getAmount());
//                paymentRecordDetail.setInvoiceNumber(invoiceNumber);
//                paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.IN_BRANCH_ID);
//                paymentRecord.getPaymentRecordDetailIds().add(paymentRecordDetail.getId());
//                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
//                mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
//            });
//
//            mongoRepositoryReactive.saveOrUpdate(batchPayment);
//            return Mono.just(new ResponseEntity<>(batchPayment.convertToFullDto(), HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while creating multiple gaming terminal license payments ", e);
//        }
//    }
//
//
//    private Mono<ResponseEntity> updateWebMultiplePayment(BatchPaymentUpdateDto batchPaymentUpdateDto) {
//        try {
//            String batchPaymentId = batchPaymentUpdateDto.getBatchPaymentId();
//            BatchPayment existingBatchPayment = findBatchPaymentById(batchPaymentId);
//            if (existingBatchPayment == null) {
//                return Mono.just(new ResponseEntity<>(String.format("Batch payment with id %s does not exist", batchPaymentId), HttpStatus.BAD_REQUEST));
//            }
//            String newPaymentStatusId = batchPaymentUpdateDto.getPaymentStatusId();
//            String completedPaymentStatusId = PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
//            if (!StringUtils.equals(completedPaymentStatusId, existingBatchPayment.getAgentId()) && StringUtils.equals(completedPaymentStatusId, newPaymentStatusId)) {
//                updateTaxPaymentsInBatchToSuccessful(existingBatchPayment);
//            }
//
//
//            return null;
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while updating payment", e);
//        }
//    }
//
//    private void updateTaxPaymentsInBatchToSuccessful(BatchPayment existingBatchPayment) {
//        String completedPaymentStatusId = PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
//        for (PaymentRecord paymentRecord : existingBatchPayment.getPaymentRecords()) {
//            paymentRecord.setPaymentStatusId(completedPaymentStatusId);
//            paymentRecord.setAmountPaid(paymentRecord.getAmount());
//            paymentRecord.setAmountOutstanding(0);
//        }
//    }
//
//    private PaymentRecord findTaxPaymentForGamingTerminal(String gamingTerminalId, String institutionId, String gameTypeId) {
//        return paymentRecordService.findPaymentRecordForGamingTerminal(gamingTerminalId, gameTypeId, institutionId, FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
//    }
//
//    private List<VigipayInvoiceItem> vigiPayInvoiceItemsFromFeeDescriptions(List<FeeAndDescription> feeDescriptions) {
//        List<VigipayInvoiceItem> vigipayInvoiceItems = new ArrayList<>();
//        for (FeeAndDescription feeDescription : feeDescriptions) {
//            VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
//            vigipayInvoiceItem.setAmount(feeDescription.getAmount());
//            vigipayInvoiceItem.setDetail(feeDescription.getFeeDescription());
//            vigipayInvoiceItem.setQuantity(1);
//            vigipayInvoiceItem.setProductCode("");
//            vigipayInvoiceItems.add(vigipayInvoiceItem);
//        }
//        return vigipayInvoiceItems;
//    }
//
//    private BatchPayment findBatchPaymentById(String batchPaymentId) {
//        if (StringUtils.isEmpty(batchPaymentId)) {
//            return null;
//        }
//        return (BatchPayment) mongoRepositoryReactive.findById(batchPaymentId, BatchPayment.class).block();
//    }
//}
