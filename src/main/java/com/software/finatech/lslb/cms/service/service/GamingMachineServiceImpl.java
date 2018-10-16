package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
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
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class GamingMachineServiceImpl implements GamingMachineService {

    private static final Logger logger = LoggerFactory.getLogger(GamingMachineServiceImpl.class);
    private static final String gamingMachineAuditActionId = AuditActionReferenceData.GAMING_MACHINE_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseValidatorUtil licenseValidatorUtil;
    private InstitutionService institutionService;
    private FeeService feeService;
    private PaymentRecordService paymentRecordService;
    private AuthInfoService authInfoService;
    private VigipayService vigipayService;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;


    @Autowired
    public GamingMachineServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                    LicenseValidatorUtil licenseValidatorUtil,
                                    InstitutionService institutionService,
                                    FeeService feeService,
                                    PaymentRecordService paymentRecordService,
                                    AuthInfoService authInfoService,
                                    VigipayService vigipayService,
                                    SpringSecurityAuditorAware springSecurityAuditorAware,
                                    AuditLogHelper auditLogHelper) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseValidatorUtil = licenseValidatorUtil;
        this.institutionService = institutionService;
        this.feeService = feeService;
        this.paymentRecordService = paymentRecordService;
        this.authInfoService = authInfoService;
        this.vigipayService = vigipayService;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
    }

    @Override
    public Mono<ResponseEntity> findAllGamingMachines(int page,
                                                      int pageSize,
                                                      String sortDirection,
                                                      String sortProperty,
                                                      String institutionId,
                                                      HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, GamingMachine.class).block();
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

            ArrayList<GamingMachine> gamingMachines = (ArrayList<GamingMachine>) mongoRepositoryReactive.findAll(query, GamingMachine.class).toStream().collect(Collectors.toList());
            if (gamingMachines == null || gamingMachines.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<GamingMachineDto> gamingMachineDtos = new ArrayList<>();

            gamingMachines.forEach(gamingMachine -> {
                gamingMachineDtos.add(gamingMachine.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(gamingMachineDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while trying to get all gaming machines";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createGamingMachine(GamingMachineCreateDto gamingMachineCreateDto, HttpServletRequest request) {
        try {
            String institutionId = gamingMachineCreateDto.getInstitutionId();
            String gameTypeId = gamingMachineCreateDto.getGameTypeId();
            Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
            if (validateGamingMachineLicenseResponse != null) {
                return validateGamingMachineLicenseResponse;
            }
            GamingMachine gamingMachine = fromGamingMachineCreateDto(gamingMachineCreateDto);
            saveGamingMachine(gamingMachine);

            String verbiage = String.format("Created Gaming Machine, Serial number -> %s ", gamingMachine.getSerialNumber());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(gamingMachineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), gamingMachine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));


            return Mono.just(new ResponseEntity<>(gamingMachine.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving gaming machine", e);
        }
    }


    @Override
    public Mono<ResponseEntity> updateGamingMachine(GamingMachineUpdateDto gamingMachineUpdateDto, HttpServletRequest request) {
        try {
            String gamingMachineId = gamingMachineUpdateDto.getId();
            GamingMachine gamingMachine = findById(gamingMachineId);
            if (gamingMachine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
            }
            gamingMachine.setGameDetailsList(gamingMachineUpdateDto.getGameDetailsList());
            gamingMachine.setMachineNumber(gamingMachineUpdateDto.getGameMachineNumber());
            gamingMachine.setSerialNumber(gamingMachineUpdateDto.getSerialNumber());
            gamingMachine.setManufacturer(gamingMachineUpdateDto.getManufacturer());
            gamingMachine.setMachineAddress(gamingMachineUpdateDto.getMachineAddress());
            saveGamingMachine(gamingMachine);

            String verbiage = String.format("Updated Gaming Machine, Serial Number -> %s ", gamingMachine.getSerialNumber());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(gamingMachineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), gamingMachine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(gamingMachine.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating gaming machine", e);
        }
    }

    @Override
    public GamingMachine findById(String gamingMachineId) {
        return (GamingMachine) mongoRepositoryReactive.findById(gamingMachineId, GamingMachine.class).block();
    }


    //TODO: validate if its multiple or not
    @Override
    public Mono<ResponseEntity> uploadMultipleGamingMachinesForInstitution(String institutionId, String gameTypeId, MultipartFile multipartFile, HttpServletRequest request) {
        Institution institution = institutionService.findById(institutionId);
        if (institution == null) {
            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
        }
        Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
        if (validateGamingMachineLicenseResponse != null) {
            return validateGamingMachineLicenseResponse;
        }

        List<GamingMachine> gamingMachineList = new ArrayList<>();
        List<FailedLine> failedLines = new ArrayList<>();
        UploadTransactionResponse uploadTransactionResponse = new UploadTransactionResponse();
        if (!multipartFile.isEmpty()) {
            try {
                byte[] bytes = multipartFile.getBytes();
                String completeData = new String(bytes);
                String[] rows = completeData.split("\\r?\\n");
                Map<String, GamingMachine> gamingMachineMap = new HashMap<>();
                for (int i = 1; i < rows.length; i++) {
                    String[] columns = rows[i].split(",");
                    if (columns.length < 6) {
                        failedLines.add(FailedLine.fromLineAndReason(rows[i], "Line has less than 6 fields"));
                    } else {
                        try {
                            GamingMachine gamingMachine = getGamingMachineBySerialNumber(columns[0], gamingMachineMap);
                            if (gamingMachine == null) {
                                gamingMachine = new GamingMachine();
                                gamingMachine.setId(UUID.randomUUID().toString());
                                gamingMachine.setSerialNumber(columns[0]);
                                gamingMachine.setManufacturer(columns[1]);
                                gamingMachine.setMachineNumber(columns[2]);
                                gamingMachine.setMachineAddress(columns[3]);
                                gamingMachine.setGameTypeId(gameTypeId);
                                GamingMachineGameDetails gamingMachineGameDetails = new GamingMachineGameDetails();
                                gamingMachineGameDetails.setGameName(columns[4]);
                                gamingMachineGameDetails.setGameVersion(columns[5]);
                                Set<GamingMachineGameDetails> gamingMachineGameDetailsSet = new HashSet<>();
                                gamingMachineGameDetailsSet.add(gamingMachineGameDetails);
                                gamingMachine.setGameDetailsList(gamingMachineGameDetailsSet);
                            } else {
                                GamingMachineGameDetails gamingMachineGameDetails = new GamingMachineGameDetails();
                                gamingMachineGameDetails.setGameName(columns[4]);
                                gamingMachineGameDetails.setGameVersion(columns[5]);
                                Set<GamingMachineGameDetails> gamingMachineGameDetailsSet = gamingMachine.getGameDetailsList();
                                gamingMachineGameDetailsSet.add(gamingMachineGameDetails);
                            }
                            gamingMachine.setInstitutionId(institutionId);
                            gamingMachineList.add(gamingMachine);
                            gamingMachineMap.put(gamingMachine.getSerialNumber(), gamingMachine);
                        } catch (Exception e) {
                            logger.error(String.format("Error parsing line %s", rows[i]), e);
                            failedLines.add(FailedLine.fromLineAndReason(rows[i], "An error occurred while parsing line"));
                        }
                    }
                }

                if (!failedLines.isEmpty()) {
                    uploadTransactionResponse.setFailedLines(failedLines);
                    uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
                    uploadTransactionResponse.setMessage(String.format(
                            "Please review with sample file and re upload",
                            failedLines.size()));
                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.BAD_REQUEST));
                } else {
                    for (GamingMachine gamingMachine : gamingMachineList) {
                        try {
                            saveGamingMachine(gamingMachine);
                        } catch (Exception e) {
                            logger.error("An error occurred while saving gaming machine with serial number {}", gamingMachine.getSerialNumber());
                            String line = String.format("%s,%s,%s,%s", gamingMachine.getSerialNumber(), gamingMachine.getManufacturer(), gamingMachine.getMachineNumber(), gamingMachine.getMachineAddress());
                            failedLines.add(FailedLine.fromLineAndReason(line, "An error occurred while saving line"));
                        }
                    }
                    if (!failedLines.isEmpty()) {
                        uploadTransactionResponse.setMessage("Upload partially successful, please see failed records");
                        uploadTransactionResponse.setFailedLines(failedLines);
                        uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
                    } else {
                        uploadTransactionResponse.setMessage("Upload successful");
                    }

                    String verbiage = "Uploaded multiple gaming machines ";
                    auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(gamingMachineAuditActionId,
                            springSecurityAuditorAware.getCurrentAuditorNotNull(), institution.getInstitutionName(),
                            LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.OK));
                }
            } catch (IOException e) {
                return logAndReturnError(logger, "An error occurred while parsing the file", e);
            }
        } else {
            return Mono.just(new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST));
        }
    }

    private GamingMachine findBySerialNumber(String serialNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("serialNumber").is(serialNumber));
        return (GamingMachine) mongoRepositoryReactive.find(query, GamingMachine.class).block();
    }

    private GamingMachine getGamingMachineBySerialNumber(String serialNumber, Map<String, GamingMachine> gamingMachineMap) {
        GamingMachine gamingMachine = gamingMachineMap.get(serialNumber);
        if (gamingMachine != null) {
            return gamingMachine;
        }
        gamingMachine = findBySerialNumber(serialNumber);
        if (gamingMachine != null) {
            gamingMachineMap.put(serialNumber, gamingMachine);
        }
        return gamingMachine;
    }


    private GamingMachine fromGamingMachineCreateDto(GamingMachineCreateDto gamingMachineCreateDto) {
        GamingMachine gamingMachine = new GamingMachine();
        gamingMachine.setInstitutionId(gamingMachineCreateDto.getInstitutionId());
        gamingMachine.setManufacturer(gamingMachineCreateDto.getManufacturer());
        gamingMachine.setSerialNumber(gamingMachineCreateDto.getSerialNumber());
        gamingMachine.setMachineNumber(gamingMachineCreateDto.getGameMachineNumber());
        gamingMachine.setGameDetailsList(gamingMachineCreateDto.getGameDetailsList());
        gamingMachine.setMachineAddress(gamingMachineCreateDto.getMachineAddress());
        gamingMachine.setGameTypeId(gamingMachineCreateDto.getGameTypeId());
        return gamingMachine;
    }

    private void saveGamingMachine(GamingMachine gamingMachine) {
        mongoRepositoryReactive.saveOrUpdate(gamingMachine);
    }

    @Override
    public Mono<ResponseEntity> validateMultipleGamingMachineLicensePayment(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
        try {
            GamingMachineMultiplePaymentResponse gamingMachineMultiplePaymentResponse = new GamingMachineMultiplePaymentResponse();
            Set<String> machineIds = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
            if (machineIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty machine ids supplied", HttpStatus.BAD_REQUEST));
            }

            List<ValidGamingMachinePayment> validGamingMachinePayments = new ArrayList<>();
            List<InvalidGamingMachinePayment> invalidGamingMachinePayments = new ArrayList<>();
            List<String> validGamingMachines = new ArrayList<>();

            double totalAmount = 0;
            FeePaymentType feePaymentType = feeService.getFeePaymentTypeById(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
            if (feePaymentType == null) {
                return Mono.just(new ResponseEntity<>("Licence fee payment type not found on system", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            for (String gamingMachineId : machineIds) {
                Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> machinePaymentPair = getMachinePaymentPairForLicensePayment(gamingMachineId, feePaymentType.getName());
                ValidGamingMachinePayment validGamingMachinePayment = machinePaymentPair.getLeft();
                InvalidGamingMachinePayment invalidGamingMachinePayment = machinePaymentPair.getRight();
                if (validGamingMachinePayment == null && invalidGamingMachinePayment != null) {
                    invalidGamingMachinePayments.add(invalidGamingMachinePayment);
                } else if (validGamingMachinePayment != null && invalidGamingMachinePayment == null) {
                    validGamingMachinePayments.add(validGamingMachinePayment);
                    totalAmount = totalAmount + validGamingMachinePayment.getAmount();
                    validGamingMachines.add(gamingMachineId);
                } else {
                    logger.info("Invalid validation for license payment for gaming machine with id {} ", gamingMachineId);
                }
            }
            gamingMachineMultiplePaymentResponse.setAmountTotal(totalAmount);
            gamingMachineMultiplePaymentResponse.setInvalidGamingMachinePaymentList(invalidGamingMachinePayments);
            gamingMachineMultiplePaymentResponse.setValidGamingMachinesList(validGamingMachines);
            gamingMachineMultiplePaymentResponse.setValidGamingMachinePaymentList(validGamingMachinePayments);
            return Mono.just(new ResponseEntity<>(gamingMachineMultiplePaymentResponse, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while validating multiple gaming machine license payment ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> validateMultipleGamingMachineLicenseRenewalPayment(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
        try {
            GamingMachineMultiplePaymentResponse gamingMachineMultiplePaymentResponse = new GamingMachineMultiplePaymentResponse();
            Set<String> machineIds = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
            if (machineIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty machine ids supplied", HttpStatus.BAD_REQUEST));
            }

            List<ValidGamingMachinePayment> validGamingMachinePayments = new ArrayList<>();
            List<InvalidGamingMachinePayment> invalidGamingMachinePayments = new ArrayList<>();
            List<String> validGamingMachines = new ArrayList<>();

            double totalAmount = 0;
            FeePaymentType feePaymentType = feeService.getFeePaymentTypeById(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID);
            if (feePaymentType == null) {
                return Mono.just(new ResponseEntity<>("Licence renewal fee payment type not found on system", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            for (String gamingMachineId : machineIds) {
                Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> machinePaymentPair = getMachinePaymentPairForLicenseRenewalPayment(gamingMachineId, feePaymentType.getName());
                ValidGamingMachinePayment validGamingMachinePayment = machinePaymentPair.getLeft();
                InvalidGamingMachinePayment invalidGamingMachinePayment = machinePaymentPair.getRight();
                if (validGamingMachinePayment == null && invalidGamingMachinePayment != null) {
                    invalidGamingMachinePayments.add(invalidGamingMachinePayment);
                } else if (validGamingMachinePayment != null && invalidGamingMachinePayment == null) {
                    validGamingMachinePayments.add(validGamingMachinePayment);
                    totalAmount = totalAmount + validGamingMachinePayment.getAmount();
                    validGamingMachines.add(gamingMachineId);
                } else {
                    logger.info("Invalid validation for license renewal payment for gaming machine with id {} ", gamingMachineId);
                }
            }
            gamingMachineMultiplePaymentResponse.setAmountTotal(totalAmount);
            gamingMachineMultiplePaymentResponse.setInvalidGamingMachinePaymentList(invalidGamingMachinePayments);
            gamingMachineMultiplePaymentResponse.setValidGamingMachinesList(validGamingMachines);
            gamingMachineMultiplePaymentResponse.setValidGamingMachinePaymentList(validGamingMachinePayments);
            return Mono.just(new ResponseEntity<>(gamingMachineMultiplePaymentResponse, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while validating multiple gaming machine license renewal payment ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> findGamingMachineBySearchKey(String searchKey) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(searchKey)) {
                query.addCriteria(Criteria.where("serialNumber").regex(searchKey, "i"));
            }
            query.with(PageRequest.of(0, 20));
            ArrayList<GamingMachine> gamingMachines = (ArrayList<GamingMachine>) mongoRepositoryReactive.findAll(query, GamingMachine.class).toStream().collect(Collectors.toList());
            if (gamingMachines == null || gamingMachines.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<GamingMachineDto> gamingMachineDtos = new ArrayList<>();
            gamingMachines.forEach(gamingMachine -> {
                GamingMachineDto dto = new GamingMachineDto();
                dto.setId(gamingMachine.getId());
                dto.setSerialNumber(gamingMachine.getSerialNumber());
                gamingMachineDtos.add(dto);
            });
            return Mono.just(new ResponseEntity<>(gamingMachineDtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while searching gaming machines by key", e);
        }
    }


    private Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> getMachinePaymentPairForLicensePayment(String gamingMachineId, String feePaymentTypeName) {
        ValidGamingMachinePayment validGamingMachinePayment = new ValidGamingMachinePayment();
        InvalidGamingMachinePayment invalidGamingMachinePayment = new InvalidGamingMachinePayment();
        GamingMachine gamingMachine = findById(gamingMachineId);
        if (gamingMachine == null) {
            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
            invalidGamingMachinePayment.setReason(String.format("Gaming Machine with id %s does not exist", gamingMachineId));
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }
        String machineNumber = gamingMachine.getMachineNumber();
        String institutionId = gamingMachine.getInstitutionId();
        GameType gameType = gamingMachine.getGameType();
        if (gameType == null) {
            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
                    machineNumber,
                    String.format("No category found for machine with machine number %s", gamingMachine.getMachineNumber()));
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }
        String revenueNameId = RevenueNameReferenceData.GAMING_MACHINE_ID;
        String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
        String gameTypeName = gameType.getName();
        String gameTypeId = gameType.getId();
        Fee fee = feeService.findFeeByRevenueNameGameTypeAndFeePaymentType(revenueNameId, gameTypeId, feePaymentTypeId);
        if (fee == null) {
            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
                    machineNumber,
                    String.format("No licence fee found for gaming machines for category %s", gameTypeName));
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }

        PaymentRecord existingLicensePayment = paymentRecordService.findPaymentRecordForGamingMachine(gamingMachineId, gameTypeId, institutionId, feePaymentTypeId, revenueNameId);
        if (existingLicensePayment != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePayment.getPaymentStatusId())) {
            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
            invalidGamingMachinePayment.setGameTypeName(gameTypeName);
            invalidGamingMachinePayment.setMachineNumber(machineNumber);
            invalidGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
            invalidGamingMachinePayment.setReason("You have an existing licence payment for the machine");
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }

        if (existingLicensePayment != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePayment.getPaymentStatusId())) {
            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
            invalidGamingMachinePayment.setGameTypeName(gameTypeName);
            invalidGamingMachinePayment.setMachineNumber(machineNumber);
            invalidGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
            invalidGamingMachinePayment.setReason("Please complete payment for licence for this machine");
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }
        double amount = fee.getAmount();
        validGamingMachinePayment.setAmount(amount);
        validGamingMachinePayment.setGameTypeName(gameTypeName);
        validGamingMachinePayment.setGamingMachineId(gamingMachineId);
        validGamingMachinePayment.setMachineNumber(machineNumber);
        validGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
        return new ImmutablePair<>(validGamingMachinePayment, null);
    }

    private Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> getMachinePaymentPairForLicenseRenewalPayment(String gamingMachineId, String feePaymentTypeName) {
        ValidGamingMachinePayment validGamingMachinePayment = new ValidGamingMachinePayment();
        InvalidGamingMachinePayment invalidGamingMachinePayment = new InvalidGamingMachinePayment();
        GamingMachine gamingMachine = findById(gamingMachineId);
        if (gamingMachine == null) {
            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
            invalidGamingMachinePayment.setReason(String.format("Gaming Machine with id %s does not exist", gamingMachineId));
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }
        String machineNumber = gamingMachine.getMachineNumber();
        String institutionId = gamingMachine.getInstitutionId();
        GameType gameType = gamingMachine.getGameType();
        if (gameType == null) {
            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
                    machineNumber, String.format("No category found for machine with machine number %s",
                    gamingMachine.getMachineNumber()));
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }
        String revenueNameId = RevenueNameReferenceData.GAMING_MACHINE_ID;
        String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID;
        String gameTypeName = gameType.getName();
        String gameTypeId = gameType.getId();
        Fee fee = feeService.findFeeByRevenueNameGameTypeAndFeePaymentType(revenueNameId, gameTypeId, feePaymentTypeId);
        if (fee == null) {
            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
                    machineNumber,
                    String.format("No licence renewal fee found for gaming machines for category %s", gameTypeName));
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }
        PaymentRecord paymentRecord = paymentRecordService.findPaymentRecordForGamingMachine(gamingMachineId, gameTypeId, institutionId, FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, revenueNameId);
        if (paymentRecord == null) {
            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
            invalidGamingMachinePayment.setGameTypeName(gameTypeName);
            invalidGamingMachinePayment.setMachineNumber(machineNumber);
            invalidGamingMachinePayment.setReason("Please pay licence payment for this machine before paying for licence renewal");
            return new ImmutablePair<>(null, invalidGamingMachinePayment);
        }

        double amount = fee.getAmount();
        validGamingMachinePayment.setAmount(amount);
        validGamingMachinePayment.setGameTypeName(gameTypeName);
        validGamingMachinePayment.setGamingMachineId(gamingMachineId);
        validGamingMachinePayment.setMachineNumber(machineNumber);
        validGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
        return new ImmutablePair<>(validGamingMachinePayment, null);
    }

    private Mono<ResponseEntity> makeMultipleGamingMachineLicensePaymentWeb(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
        try {
            double amountTotal = 0;
            Set<String> gamingMachineIdList = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
            List<PaymentRecordDetailCreateDto> paymentRecordDetailCreateDtoList = new ArrayList<>();
            String revenueNameId = RevenueNameReferenceData.GAMING_MACHINE_ID;
            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
            String institutionId = null;

            if (gamingMachineIdList.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty gaming machine ids supplied", HttpStatus.BAD_REQUEST));
            }
            BatchPayment batchPayment = new BatchPayment();
            batchPayment.setId(UUID.randomUUID().toString());
            for (String gamingMachineId : gamingMachineIdList) {
                GamingMachine gamingMachine = findById(gamingMachineId);
                if (gamingMachine == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
                }
                institutionId = gamingMachine.getInstitutionId();
                String gameTypeId = gamingMachine.getGameTypeId();
                PaymentRecord existingLicensePayment = findLicensePaymentForGamingMachine(gamingMachineId, institutionId, gameTypeId);
                if (existingLicensePayment != null) {
                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s has a license payment already", gamingMachineId), HttpStatus.BAD_REQUEST));
                }
                Fee fee = feeService.findFeeByRevenueNameGameTypeAndFeePaymentType(revenueNameId, gameTypeId, feePaymentTypeId);
                if (fee == null) {
                    GameType gameType = gamingMachine.getGameType();
                    String gameTypeName = gameType != null ? gameType.getName() : gameTypeId;
                    return Mono.just(new ResponseEntity<>(String.format("Licencing fees for gaming machines for category %s does not exist", gameTypeName), HttpStatus.BAD_REQUEST));
                }

                double paymentAmount = fee.getAmount();
                amountTotal = amountTotal + paymentAmount;

                PaymentRecordDetailCreateDto paymentRecordDetailCreateDto = new PaymentRecordDetailCreateDto();
                paymentRecordDetailCreateDto.setFeeId(fee.getId());
                paymentRecordDetailCreateDto.setInstitutionId(institutionId);
                paymentRecordDetailCreateDto.setGamingMachineId(gamingMachineId);
                paymentRecordDetailCreateDtoList.add(paymentRecordDetailCreateDto);
                batchPayment.setInstitutionId(institutionId);
            }
            if (amountTotal != gamingMachineMultiplePaymentRequest.getTotalAmount()) {
                return Mono.just(new ResponseEntity<>("Amount supplied is not equal to total amount", HttpStatus.BAD_REQUEST));
            }

            batchPayment.setAmountTotal(amountTotal);
            batchPayment.setGamingMachineIds(gamingMachineIdList);
            batchPayment.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);

            paymentRecordDetailCreateDtoList.forEach(paymentRecordDetailCreateDto -> {
                PaymentRecord paymentRecord = new PaymentRecord();
                paymentRecord.setId(UUID.randomUUID().toString());
                Fee fee = feeService.findFeeById(paymentRecordDetailCreateDto.getFeeId());
                if (fee == null) {
                    return;
                }
                paymentRecord.setGameTypeId(fee.getGameTypeId());
                paymentRecord.setFeeId(fee.getId());
                paymentRecord.setAmount(fee.getAmount());
                paymentRecord.setAmountPaid(0);
                paymentRecord.setAmountOutstanding(fee.getAmount());
                paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
                paymentRecord.setInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
                paymentRecord.setGamingMachineId(paymentRecordDetailCreateDto.getGamingMachineId());
                paymentRecord.setGameTypeId(fee.getGameTypeId());
                paymentRecord.setFeePaymentTypeId(fee.getFeePaymentTypeId());
                paymentRecord.setRevenueNameId(fee.getRevenueNameId());
                paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
                paymentRecord.setBatchPaymentId(batchPayment.getId());

                PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
                paymentRecordDetail.setId(UUID.randomUUID().toString());
                paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
                paymentRecordDetail.setAmount(fee.getAmount());
                paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.WEB_PAYMENT_ID);
                paymentRecord.getPaymentRecordDetailIds().add(paymentRecordDetail.getId());
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
            });

            mongoRepositoryReactive.saveOrUpdate(batchPayment);
            return Mono.just(new ResponseEntity<>(batchPayment.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating multiple gaming machine license payments ", e);
        }
    }

    private Mono<ResponseEntity> makeMultipleGamingMachineLicensePaymentInBranch(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
        try {
            double amountTotal = 0;
            Set<String> gamingMachineIdList = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
            List<PaymentRecordDetailCreateDto> paymentRecordDetailCreateDtoList = new ArrayList<>();
            String revenueNameId = RevenueNameReferenceData.GAMING_MACHINE_ID;
            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
            String institutionId = null;

            if (gamingMachineIdList.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty gaming machine ids supplied", HttpStatus.BAD_REQUEST));
            }
            BatchPayment batchPayment = new BatchPayment();
            batchPayment.setId(UUID.randomUUID().toString());
            List<FeeAndDescription> feeDescriptions = new ArrayList<>();
            for (String gamingMachineId : gamingMachineIdList) {
                GamingMachine gamingMachine = findById(gamingMachineId);
                if (gamingMachine == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
                }
                institutionId = gamingMachine.getInstitutionId();
                String gameTypeId = gamingMachine.getGameTypeId();
                PaymentRecord existingLicensePayment = findLicensePaymentForGamingMachine(gamingMachineId, institutionId, gameTypeId);
                if (existingLicensePayment != null) {
                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s has a license payment already", gamingMachineId), HttpStatus.BAD_REQUEST));
                }
                Fee fee = feeService.findFeeByRevenueNameGameTypeAndFeePaymentType(revenueNameId, gameTypeId, feePaymentTypeId);
                if (fee == null) {
                    GameType gameType = gamingMachine.getGameType();
                    String gameTypeName = gameType != null ? gameType.getName() : gameTypeId;
                    return Mono.just(new ResponseEntity<>(String.format("Licencing fees for gaming machines for category %s does not exist", gameTypeName), HttpStatus.BAD_REQUEST));
                }

                double paymentAmount = fee.getAmount();
                amountTotal = amountTotal + paymentAmount;

                FeeDto feeDto = fee.convertToDto();
                String feeName = feeDto.getFeePaymentTypeName();
                String gameTypeName = feeDto.getGameTypeName();
                String revenueName = feeDto.getRevenueName();
                String suffix = String.format(" for Machine %s", gamingMachine.getMachineNumber());

                String feeDescription = String.format("%s for %ss for category : %s %s", feeName, revenueName, gameTypeName, suffix);
                feeDescription = StringCapitalizer.convertToTitleCaseIteratingChars(feeDescription);
                FeeAndDescription feeAndDescription = new FeeAndDescription();
                feeAndDescription.setAmount(fee.getAmount());
                feeAndDescription.setFeeDescription(feeDescription);
                feeDescriptions.add(feeAndDescription);

                PaymentRecordDetailCreateDto paymentRecordDetailCreateDto = new PaymentRecordDetailCreateDto();
                paymentRecordDetailCreateDto.setFeeId(fee.getId());
                paymentRecordDetailCreateDto.setInstitutionId(institutionId);
                paymentRecordDetailCreateDto.setGamingMachineId(gamingMachineId);
                paymentRecordDetailCreateDtoList.add(paymentRecordDetailCreateDto);
                batchPayment.setInstitutionId(institutionId);
            }
            if (amountTotal != gamingMachineMultiplePaymentRequest.getTotalAmount()) {
                return Mono.just(new ResponseEntity<>("Amount supplied is not equal to total amount", HttpStatus.BAD_REQUEST));
            }
            Institution institution = institutionService.findById(institutionId);
            if (institution == null) {
                return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
            }


            List<VigipayInvoiceItem> vigipayInvoiceItems = vigiPayInvoiceItemsFromFeeDescriptions(feeDescriptions);
            List<AuthInfo> institutionAdmins = authInfoService.getAllActiveGamingOperatorAdminsForInstitution(institutionId);

            String invoiceNumber = vigipayService.createInBranchMultipleItemInvoiceForInstitution(institution, institutionAdmins, vigipayInvoiceItems);

            if (invoiceNumber == null) {
                return Mono.just(new ResponseEntity<>("An error occurred while creating invoice with Vigipay", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            batchPayment.setAmountTotal(amountTotal);
            batchPayment.setGamingMachineIds(gamingMachineIdList);
            batchPayment.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);

            paymentRecordDetailCreateDtoList.forEach(paymentRecordDetailCreateDto -> {
                PaymentRecord paymentRecord = new PaymentRecord();
                paymentRecord.setId(UUID.randomUUID().toString());
                Fee fee = feeService.findFeeById(paymentRecordDetailCreateDto.getFeeId());
                if (fee == null) {
                    return;
                }
                paymentRecord.setGameTypeId(fee.getGameTypeId());
                paymentRecord.setFeeId(fee.getId());
                paymentRecord.setAmount(fee.getAmount());
                paymentRecord.setAmountPaid(0);
                paymentRecord.setAmountOutstanding(fee.getAmount());
                paymentRecord.setPaymentStatusId(PaymentStatusReferenceData.UNPAID_STATUS_ID);
                paymentRecord.setInstitutionId(paymentRecordDetailCreateDto.getInstitutionId());
                paymentRecord.setGamingMachineId(paymentRecordDetailCreateDto.getGamingMachineId());
                paymentRecord.setGameTypeId(fee.getGameTypeId());
                paymentRecord.setFeePaymentTypeId(fee.getFeePaymentTypeId());
                paymentRecord.setRevenueNameId(fee.getRevenueNameId());
                paymentRecord.setPaymentReference(NumberUtil.generateTransactionReferenceForPaymentRecord());
                paymentRecord.setBatchPaymentId(batchPayment.getId());

                PaymentRecordDetail paymentRecordDetail = new PaymentRecordDetail();
                paymentRecordDetail.setId(UUID.randomUUID().toString());
                paymentRecordDetail.setPaymentRecordId(paymentRecord.getId());
                paymentRecordDetail.setAmount(fee.getAmount());
                paymentRecordDetail.setInvoiceNumber(invoiceNumber);
                paymentRecordDetail.setModeOfPaymentId(ModeOfPaymentReferenceData.IN_BRANCH_ID);
                paymentRecord.getPaymentRecordDetailIds().add(paymentRecordDetail.getId());
                mongoRepositoryReactive.saveOrUpdate(paymentRecord);
                mongoRepositoryReactive.saveOrUpdate(paymentRecordDetail);
            });

            mongoRepositoryReactive.saveOrUpdate(batchPayment);
            return Mono.just(new ResponseEntity<>(batchPayment.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating multiple gaming machine license payments ", e);
        }
    }


    private Mono<ResponseEntity> updateWebMultiplePayment(BatchPaymentUpdateDto batchPaymentUpdateDto) {
        try {
            String batchPaymentId = batchPaymentUpdateDto.getBatchPaymentId();
            BatchPayment existingBatchPayment = findBatchPaymentById(batchPaymentId);
            if (existingBatchPayment == null) {
                return Mono.just(new ResponseEntity<>(String.format("Batch payment with id %s does not exist", batchPaymentId), HttpStatus.BAD_REQUEST));
            }
            String newPaymentStatusId = batchPaymentUpdateDto.getPaymentStatusId();
            String completedPaymentStatusId = PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
            if (!StringUtils.equals(completedPaymentStatusId, existingBatchPayment.getAgentId()) && StringUtils.equals(completedPaymentStatusId, newPaymentStatusId)) {
                updateLicensePaymentsInBatchToSuccessful(existingBatchPayment);
            }


            return null;
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating payment", e);
        }
    }

    private void updateLicensePaymentsInBatchToSuccessful(BatchPayment existingBatchPayment) {
        String completedPaymentStatusId = PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
        for (PaymentRecord paymentRecord : existingBatchPayment.getPaymentRecords()) {
            paymentRecord.setPaymentStatusId(completedPaymentStatusId);
            paymentRecord.setAmountPaid(paymentRecord.getAmount());
            paymentRecord.setAmountOutstanding(0);
        }
    }

    private PaymentRecord findLicensePaymentForGamingMachine(String gamingMachineId, String institutionId, String gameTypeId) {
        return paymentRecordService.findPaymentRecordForGamingMachine(gamingMachineId, gameTypeId, institutionId, FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID, RevenueNameReferenceData.GAMING_MACHINE_ID);
    }

    private List<VigipayInvoiceItem> vigiPayInvoiceItemsFromFeeDescriptions(List<FeeAndDescription> feeDescriptions) {
        List<VigipayInvoiceItem> vigipayInvoiceItems = new ArrayList<>();
        for (FeeAndDescription feeDescription : feeDescriptions) {
            VigipayInvoiceItem vigipayInvoiceItem = new VigipayInvoiceItem();
            vigipayInvoiceItem.setAmount(feeDescription.getAmount());
            vigipayInvoiceItem.setDetail(feeDescription.getFeeDescription());
            vigipayInvoiceItem.setQuantity(1);
            vigipayInvoiceItem.setProductCode("");
            vigipayInvoiceItems.add(vigipayInvoiceItem);
        }
        return vigipayInvoiceItems;
    }

    private BatchPayment findBatchPaymentById(String batchPaymentId) {
        if (StringUtils.isEmpty(batchPaymentId)) {
            return null;
        }
        return (BatchPayment) mongoRepositoryReactive.findById(batchPaymentId, BatchPayment.class).block();
    }
}
