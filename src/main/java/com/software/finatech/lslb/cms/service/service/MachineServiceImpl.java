package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayInvoiceItem;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.AuditActionReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.FeePaymentTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.MachineApprovalRequestTypeReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.PaymentStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.*;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.model.MachineGameDetails.machineGamesToString;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class MachineServiceImpl implements MachineService {

    private static final Logger logger = LoggerFactory.getLogger(MachineServiceImpl.class);
    private static final String machineAuditActionId = AuditActionReferenceData.MACHINE_ID;

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
    public MachineServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
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
    public Mono<ResponseEntity> findAllMachines(int page,
                                                int pageSize,
                                                String sortDirection,
                                                String sortProperty,
                                                String institutionId,
                                                String agentId,
                                                String machineTypeId,
                                                String machineStatusId,
                                                HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(agentId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
            }
            if (!StringUtils.isEmpty(machineTypeId)) {
                query.addCriteria(Criteria.where("machineTypeId").is(machineTypeId));
            }
            if (!StringUtils.isEmpty(machineStatusId)) {
                query.addCriteria(Criteria.where("machineStatusId").is(machineStatusId));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, Machine.class).block();
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

            ArrayList<Machine> gamingMachines = (ArrayList<Machine>) mongoRepositoryReactive.findAll(query, Machine.class).toStream().collect(Collectors.toList());
            if (gamingMachines == null || gamingMachines.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<MachineDto> gamingMachineDtos = new ArrayList<>();

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
    public Mono<ResponseEntity> createMachine(MachineCreateDto gamingMachineCreateDto, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String institutionId = gamingMachineCreateDto.getInstitutionId();
            String gameTypeId = gamingMachineCreateDto.getGameTypeId();
            Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
            if (validateGamingMachineLicenseResponse != null) {
                return validateGamingMachineLicenseResponse;
            }
            PendingMachine pendingGamingMachine = fromGamingMachineCreateDto(gamingMachineCreateDto);
            mongoRepositoryReactive.saveOrUpdate(pendingGamingMachine);
            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setPendingMachineId(pendingGamingMachine.getId());
            approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.CREATE_MACHINE_ID);
            approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);

            String verbiage = String.format("Created Machine Request, Type  -> %s, Serial Number -> %s", approvalRequest.getMachineApprovalRequestType(), pendingGamingMachine.getSerialNumber());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), pendingGamingMachine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));


            return Mono.just(new ResponseEntity<>(approvalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving gaming machine", e);
        }
    }


    @Override
    public Mono<ResponseEntity> updateMachine(MachineUpdateDto gamingMachineUpdateDto, HttpServletRequest request) {
        try {
            String gamingMachineId = gamingMachineUpdateDto.getId();
            Machine gamingMachine = findById(gamingMachineId);
            if (gamingMachine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
            }
            String oldAdress = gamingMachine.getMachineAddress();
            gamingMachine.setMachineAddress(gamingMachineUpdateDto.getMachineAddress());
            mongoRepositoryReactive.saveOrUpdate(gamingMachine);

            String verbiage = String.format("Updated Gaming Machine, Serial Number -> %s , Old Address -> %s, New Address -> %s", gamingMachine.getSerialNumber(), oldAdress, gamingMachineUpdateDto.getMachineAddress());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), gamingMachine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(gamingMachine.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating gaming machine", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addGamesToMachine(MachineGameUpdateDto machineGameUpdateDto, HttpServletRequest request) {
        try {
            Set<MachineGameDetails> newGameDetails = machineGameUpdateDto.getMachineGameDetails();
            if (newGameDetails.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty game details supplied", HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String gamingMachineId = machineGameUpdateDto.getMachineId();
            Machine gamingMachine = findById(machineGameUpdateDto.getMachineId());
            if (gamingMachine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s not found", gamingMachineId), HttpStatus.BAD_REQUEST));
            }

            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.ADD_GAMES_TO_MACHINE_ID);
            approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
            approvalRequest.setNewMachineGames(newGameDetails);
            approvalRequest.setMachineId(gamingMachineId);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);


            String verbiage = String.format("Created Gaming Machine Request, Type  -> %s, Serial Number -> %s, New Games -> %s", approvalRequest.getMachineApprovalRequestType(), machineGamesToString(newGameDetails));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), gamingMachine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(approvalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding games to machine", e);
        }
    }

    @Override
    public Mono<ResponseEntity> removeGamesFromMachine(MachineGameUpdateDto machineGameUpdateDto, HttpServletRequest request) {
        try {
            Set<String> removedGameDetailIds = machineGameUpdateDto.getRemovedGameIds();
            if (removedGameDetailIds.isEmpty()) {
                return Mono.just(new ResponseEntity<>("Empty game details supplied", HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String machineId = machineGameUpdateDto.getMachineId();
            Machine machine = findById(machineGameUpdateDto.getMachineId());
            if (machine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Machine with id %s not found", machineId), HttpStatus.BAD_REQUEST));
            }
            List<MachineGameDetails> gameDetails = new ArrayList<>();
            for (String gameId : removedGameDetailIds) {
                MachineGame machineGame = findMachineGameById(gameId);
                if (machineGame == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Game with Id %s not found", gameId), HttpStatus.BAD_REQUEST));
                }
                if (!StringUtils.equals(machineId, machineGame.getMachineId())) {
                    return Mono.just(new ResponseEntity<>(String.format("Game with id %s is not for machine with id %s", gameId, machineId), HttpStatus.BAD_REQUEST));
                }
                machineGame.setActive(false);
                gameDetails.add(MachineGameDetails.fromGameNameAndVersion(machineGame.getGameName(), machineGame.getGameVersion()));
                mongoRepositoryReactive.saveOrUpdate(machineGame);
            }

            String verbiage = String.format("Disabled games from machines, Serial Number -> %s, Disabled Games -> %s",
                    machine.getSerialNumber(), machineGamesToString(gameDetails));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), machine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>("Game successfully disabled", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while disabling games from machines", e);
        }
    }

    @Override
    public Machine findById(String machineId) {
        return (Machine) mongoRepositoryReactive.findById(machineId, Machine.class).block();
    }


    @Override
    public Mono<ResponseEntity> updateMachineStatus(MachineStatusUpdateDto statusUpdateDto, HttpServletRequest request) {
        try {

            return null;
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating machine status", e);
        }
    }

    //TODO: validate if its multiple or not
    @Override
    public Mono<ResponseEntity> uploadMultipleMachinesForInstitution(String institutionId, String gameTypeId, MultipartFile multipartFile, HttpServletRequest request) {
        Institution institution = institutionService.findById(institutionId);
        if (institution == null) {
            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
        }
        Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
        if (validateGamingMachineLicenseResponse != null) {
            return validateGamingMachineLicenseResponse;
        }

        List<Machine> gamingMachineList = new ArrayList<>();
        List<FailedLine> failedLines = new ArrayList<>();
        UploadTransactionResponse uploadTransactionResponse = new UploadTransactionResponse();
        if (!multipartFile.isEmpty()) {
            try {
                byte[] bytes = multipartFile.getBytes();
                String completeData = new String(bytes);
                String[] rows = completeData.split("\\r?\\n");
                Map<String, Machine> gamingMachineMap = new HashMap<>();
                for (int i = 1; i < rows.length; i++) {
                    String[] columns = rows[i].split(",");
                    if (columns.length < 6) {
                        failedLines.add(FailedLine.fromLineAndReason(rows[i], "Line has less than 6 fields"));
                    } else {
                        try {
                            Machine gamingMachine = getGamingMachineBySerialNumber(columns[0], gamingMachineMap);
                            if (gamingMachine == null) {
                                gamingMachine = new Machine();
                                gamingMachine.setId(UUID.randomUUID().toString());
                                gamingMachine.setSerialNumber(columns[0]);
                                gamingMachine.setManufacturer(columns[1]);
                                gamingMachine.setMachineAddress(columns[3]);
                                gamingMachine.setGameTypeId(gameTypeId);
                                MachineGameDetails gamingMachineGameDetails = new MachineGameDetails();
                                gamingMachineGameDetails.setGameName(columns[4]);
                                gamingMachineGameDetails.setGameVersion(columns[5]);
                                Set<MachineGameDetails> gamingMachineGameDetailsSet = new HashSet<>();
                                gamingMachineGameDetailsSet.add(gamingMachineGameDetails);
                                //    gamingMachine.setGameDetailsList(gamingMachineGameDetailsSet);
                            } else {
                                MachineGameDetails gamingMachineGameDetails = new MachineGameDetails();
                                gamingMachineGameDetails.setGameName(columns[4]);
                                gamingMachineGameDetails.setGameVersion(columns[5]);
                                //  Set<MachineGameDetails> gamingMachineGameDetailsSet = gamingMachine.getGameDetailsList();
                                //     gamingMachineGameDetailsSet.add(gamingMachineGameDetails);
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
                    for (Machine gamingMachine : gamingMachineList) {
                        try {
                            //          saveGamingMachine(gamingMachine);
                        } catch (Exception e) {
                            logger.error("An error occurred while saving gaming machine with serial number {}", gamingMachine.getSerialNumber());
                            String line = String.format("%s,%s,%s", gamingMachine.getSerialNumber(), gamingMachine.getManufacturer(), gamingMachine.getMachineAddress());
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
                    auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
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

    private Machine findBySerialNumber(String serialNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("serialNumber").is(serialNumber));
        return (Machine) mongoRepositoryReactive.find(query, Machine.class).block();
    }

    private Machine getGamingMachineBySerialNumber(String serialNumber, @NotNull Map<String, Machine> gamingMachineMap) {
        Machine gamingMachine = gamingMachineMap.get(serialNumber);
        if (gamingMachine != null) {
            return gamingMachine;
        }
        gamingMachine = findBySerialNumber(serialNumber);
        if (gamingMachine != null) {
            gamingMachineMap.put(serialNumber, gamingMachine);
        }
        return gamingMachine;
    }


    private PendingMachine fromGamingMachineCreateDto(MachineCreateDto machineCreateDto) {
        PendingMachine pendingMachine = new PendingMachine();
//        pendingMachine.setInstitutionId(machineCreateDto.getInstitutionId());
//        pendingMachine.setManufacturer(machineCreateDto.getManufacturer());
//        pendingMachine.setSerialNumber(machineCreateDto.getSerialNumber());
//        pendingMachine.setGameDetailsList(machineCreateDto.getGameDetailsList());
//        pendingMachine.setMachineAddress(machineCreateDto.getMachineAddress());
//        pendingMachine.setGameTypeId(machineCreateDto.getGameTypeId());
//        pendingMachine.setMachineTypeId(machineCreateDto.getMachineTypeId());
        BeanUtils.copyProperties(machineCreateDto, pendingMachine);
        return pendingMachine;
    }

    @Override
    public Mono<ResponseEntity> validateMultipleGamingMachineLicensePayment(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
//        try {
//            GamingMachineMultiplePaymentResponse gamingMachineMultiplePaymentResponse = new GamingMachineMultiplePaymentResponse();
//            Set<String> machineIds = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
//            if (machineIds.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty machine ids supplied", HttpStatus.BAD_REQUEST));
//            }
//
//            List<ValidGamingMachinePayment> validGamingMachinePayments = new ArrayList<>();
//            List<InvalidGamingMachinePayment> invalidGamingMachinePayments = new ArrayList<>();
//            List<String> validGamingMachines = new ArrayList<>();
//
//            double totalAmount = 0;
//            FeePaymentType feePaymentType = feeService.getFeePaymentTypeById(FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
//            if (feePaymentType == null) {
//                return Mono.just(new ResponseEntity<>("Licence fee payment type not found on system", HttpStatus.INTERNAL_SERVER_ERROR));
//            }
//
//            for (String gamingMachineId : machineIds) {
//                Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> machinePaymentPair = getMachinePaymentPairForLicensePayment(gamingMachineId, feePaymentType.getName());
//                ValidGamingMachinePayment validGamingMachinePayment = machinePaymentPair.getLeft();
//                InvalidGamingMachinePayment invalidGamingMachinePayment = machinePaymentPair.getRight();
//                if (validGamingMachinePayment == null && invalidGamingMachinePayment != null) {
//                    invalidGamingMachinePayments.add(invalidGamingMachinePayment);
//                } else if (validGamingMachinePayment != null && invalidGamingMachinePayment == null) {
//                    validGamingMachinePayments.add(validGamingMachinePayment);
//                    totalAmount = totalAmount + validGamingMachinePayment.getAmount();
//                    validGamingMachines.add(gamingMachineId);
//                } else {
//                    logger.info("Invalid validation for license payment for gaming machine with id {} ", gamingMachineId);
//                }
//            }
//            gamingMachineMultiplePaymentResponse.setAmountTotal(totalAmount);
//            gamingMachineMultiplePaymentResponse.setInvalidGamingMachinePaymentList(invalidGamingMachinePayments);
//            gamingMachineMultiplePaymentResponse.setValidGamingMachinesList(validGamingMachines);
//            gamingMachineMultiplePaymentResponse.setValidGamingMachinePaymentList(validGamingMachinePayments);
//            return Mono.just(new ResponseEntity<>(gamingMachineMultiplePaymentResponse, HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while validating multiple gaming machine license payment ", e);
//        }
        return null;
    }

    @Override
    public Mono<ResponseEntity> validateMultipleGamingMachineLicenseRenewalPayment(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
//        try {
//            GamingMachineMultiplePaymentResponse gamingMachineMultiplePaymentResponse = new GamingMachineMultiplePaymentResponse();
//            Set<String> machineIds = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
//            if (machineIds.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty machine ids supplied", HttpStatus.BAD_REQUEST));
//            }
//
//            List<ValidGamingMachinePayment> validGamingMachinePayments = new ArrayList<>();
//            List<InvalidGamingMachinePayment> invalidGamingMachinePayments = new ArrayList<>();
//            List<String> validGamingMachines = new ArrayList<>();
//
//            double totalAmount = 0;
//            FeePaymentType feePaymentType = feeService.getFeePaymentTypeById(FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID);
//            if (feePaymentType == null) {
//                return Mono.just(new ResponseEntity<>("Licence renewal fee payment type not found on system", HttpStatus.INTERNAL_SERVER_ERROR));
//            }
//
//            for (String gamingMachineId : machineIds) {
//                Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> machinePaymentPair = getMachinePaymentPairForLicenseRenewalPayment(gamingMachineId, feePaymentType.getName());
//                ValidGamingMachinePayment validGamingMachinePayment = machinePaymentPair.getLeft();
//                InvalidGamingMachinePayment invalidGamingMachinePayment = machinePaymentPair.getRight();
//                if (validGamingMachinePayment == null && invalidGamingMachinePayment != null) {
//                    invalidGamingMachinePayments.add(invalidGamingMachinePayment);
//                } else if (validGamingMachinePayment != null && invalidGamingMachinePayment == null) {
//                    validGamingMachinePayments.add(validGamingMachinePayment);
//                    totalAmount = totalAmount + validGamingMachinePayment.getAmount();
//                    validGamingMachines.add(gamingMachineId);
//                } else {
//                    logger.info("Invalid validation for license renewal payment for gaming machine with id {} ", gamingMachineId);
//                }
//            }
//            gamingMachineMultiplePaymentResponse.setAmountTotal(totalAmount);
//            gamingMachineMultiplePaymentResponse.setInvalidGamingMachinePaymentList(invalidGamingMachinePayments);
//            gamingMachineMultiplePaymentResponse.setValidGamingMachinesList(validGamingMachines);
//            gamingMachineMultiplePaymentResponse.setValidGamingMachinePaymentList(validGamingMachinePayments);
//            return Mono.just(new ResponseEntity<>(gamingMachineMultiplePaymentResponse, HttpStatus.OK));
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while validating multiple gaming machine license renewal payment ", e);
//        }
        return null;
    }

    @Override
    public Mono<ResponseEntity> findMachineBySearchKey(String searchKey) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(searchKey)) {
                query.addCriteria(Criteria.where("serialNumber").regex(searchKey, "i"));
            }
            query.with(PageRequest.of(0, 20));
            ArrayList<Machine> gamingMachines = (ArrayList<Machine>) mongoRepositoryReactive.findAll(query, Machine.class).toStream().collect(Collectors.toList());
            if (gamingMachines == null || gamingMachines.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<MachineDto> gamingMachineDtos = new ArrayList<>();
            gamingMachines.forEach(gamingMachine -> {
                MachineDto dto = new MachineDto();
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
//        ValidGamingMachinePayment validGamingMachinePayment = new ValidGamingMachinePayment();
//        InvalidGamingMachinePayment invalidGamingMachinePayment = new InvalidGamingMachinePayment();
//        Machine gamingMachine = findById(gamingMachineId);
//        if (gamingMachine == null) {
//            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
//            invalidGamingMachinePayment.setReason(String.format("Gaming Machine with id %s does not exist", gamingMachineId));
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//        String machineNumber = gamingMachine.getMachineNumber();
//        String institutionId = gamingMachine.getInstitutionId();
//        GameType gameType = gamingMachine.getGameType();
//        if (gameType == null) {
//            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
//                    machineNumber,
//                    String.format("No category found for machine with machine number %s", gamingMachine.getMachineNumber()));
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//        String licenseTypeId = LicenseTypeReferenceData.GAMING_MACHINE_ID;
//        String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
//        String gameTypeName = gameType.getName();
//        String gameTypeId = gameType.getId();
//        Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//        if (fee == null) {
//            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
//                    machineNumber,
//                    String.format("No licence fee found for gaming machines for category %s", gameTypeName));
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//
//        PaymentRecord existingLicensePayment = paymentRecordService.findPaymentRecordForGamingMachine(gamingMachineId, gameTypeId, institutionId, feePaymentTypeId);
//        if (existingLicensePayment != null && StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePayment.getPaymentStatusId())) {
//            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
//            invalidGamingMachinePayment.setGameTypeName(gameTypeName);
//            invalidGamingMachinePayment.setMachineNumber(machineNumber);
//            invalidGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
//            invalidGamingMachinePayment.setReason("You have an existing licence payment for the machine");
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//
//        if (existingLicensePayment != null && !StringUtils.equals(PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID, existingLicensePayment.getPaymentStatusId())) {
//            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
//            invalidGamingMachinePayment.setGameTypeName(gameTypeName);
//            invalidGamingMachinePayment.setMachineNumber(machineNumber);
//            invalidGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
//            invalidGamingMachinePayment.setReason("Please complete payment for licence for this machine");
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//        double amount = fee.getAmount();
//        validGamingMachinePayment.setAmount(amount);
//        validGamingMachinePayment.setGameTypeName(gameTypeName);
//        validGamingMachinePayment.setGamingMachineId(gamingMachineId);
//        validGamingMachinePayment.setMachineNumber(machineNumber);
//        validGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
//        return new ImmutablePair<>(validGamingMachinePayment, null);
        return null;
    }

    private Pair<ValidGamingMachinePayment, InvalidGamingMachinePayment> getMachinePaymentPairForLicenseRenewalPayment(String gamingMachineId, String feePaymentTypeName) {
//        ValidGamingMachinePayment validGamingMachinePayment = new ValidGamingMachinePayment();
//        InvalidGamingMachinePayment invalidGamingMachinePayment = new InvalidGamingMachinePayment();
//        Machine gamingMachine = findById(gamingMachineId);
//        if (gamingMachine == null) {
//            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
//            invalidGamingMachinePayment.setReason(String.format("Gaming Machine with id %s does not exist", gamingMachineId));
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//        //   String machineNumber = gamingMachine.getMachineNumber();
//        String institutionId = gamingMachine.getInstitutionId();
//        GameType gameType = gamingMachine.getGameType();
//        if (gameType == null) {
//            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
//                    machineNumber, String.format("No category found for machine with machine number %s",
//                    gamingMachine.getMachineNumber()));
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//        String licenseTypeId = LicenseTypeReferenceData.GAMING_MACHINE_ID;
//        String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_RENEWAL_FEE_TYPE_ID;
//        String gameTypeName = gameType.getName();
//        String gameTypeId = gameType.getId();
//        Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//        if (fee == null) {
//            invalidGamingMachinePayment = new InvalidGamingMachinePayment(gamingMachineId,
//                    machineNumber,
//                    String.format("No licence renewal fee found for gaming machines for category %s", gameTypeName));
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//        PaymentRecord paymentRecord = paymentRecordService.findPaymentRecordForGamingMachine(gamingMachineId, gameTypeId, institutionId, FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
//        if (paymentRecord == null) {
//            invalidGamingMachinePayment.setGamingMachineId(gamingMachineId);
//            invalidGamingMachinePayment.setGameTypeName(gameTypeName);
//            //   invalidGamingMachinePayment.setMachineNumber(machineNumber);
//            invalidGamingMachinePayment.setReason("Please pay licence payment for this machine before paying for licence renewal");
//            return new ImmutablePair<>(null, invalidGamingMachinePayment);
//        }
//
//        double amount = fee.getAmount();
//        validGamingMachinePayment.setAmount(amount);
//        validGamingMachinePayment.setGameTypeName(gameTypeName);
//        validGamingMachinePayment.setGamingMachineId(gamingMachineId);
//        // validGamingMachinePayment.setMachineNumber(machineNumber);
//        validGamingMachinePayment.setFeePaymentTypeName(feePaymentTypeName);
//        return new ImmutablePair<>(validGamingMachinePayment, null);
        return null;
    }

    private Mono<ResponseEntity> makeMultipleGamingMachineLicensePaymentWeb(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
//        try {
//            double amountTotal = 0;
//            Set<String> gamingMachineIdList = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
//            List<PaymentRecordDetailCreateDto> paymentRecordDetailCreateDtoList = new ArrayList<>();
//            String licenseTypeId = LicenseTypeReferenceData.GAMING_MACHINE_ID;
//            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
//            String institutionId = null;
//
//            if (gamingMachineIdList.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty gaming machine ids supplied", HttpStatus.BAD_REQUEST));
//            }
//            BatchPayment batchPayment = new BatchPayment();
//            batchPayment.setId(UUID.randomUUID().toString());
//            for (String gamingMachineId : gamingMachineIdList) {
//                Machine gamingMachine = findById(gamingMachineId);
//                if (gamingMachine == null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
//                }
//                institutionId = gamingMachine.getInstitutionId();
//                String gameTypeId = gamingMachine.getGameTypeId();
//                PaymentRecord existingLicensePayment = findLicensePaymentForGamingMachine(gamingMachineId, institutionId, gameTypeId);
//                if (existingLicensePayment != null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s has a license payment already", gamingMachineId), HttpStatus.BAD_REQUEST));
//                }
//                Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//                if (fee == null) {
//                    GameType gameType = gamingMachine.getGameType();
//                    String gameTypeName = gameType != null ? gameType.getName() : gameTypeId;
//                    return Mono.just(new ResponseEntity<>(String.format("Licencing fees for gaming machines for category %s does not exist", gameTypeName), HttpStatus.BAD_REQUEST));
//                }
//
//                double paymentAmount = fee.getAmount();
//                amountTotal = amountTotal + paymentAmount;
//
//                PaymentRecordDetailCreateDto paymentRecordDetailCreateDto = new PaymentRecordDetailCreateDto();
//                paymentRecordDetailCreateDto.setFeeId(fee.getId());
//                paymentRecordDetailCreateDto.setInstitutionId(institutionId);
//                paymentRecordDetailCreateDto.setGamingMachineId(gamingMachineId);
//                paymentRecordDetailCreateDtoList.add(paymentRecordDetailCreateDto);
//                batchPayment.setInstitutionId(institutionId);
//            }
//            if (amountTotal != gamingMachineMultiplePaymentRequest.getTotalAmount()) {
//                return Mono.just(new ResponseEntity<>("Amount supplied is not equal to total amount", HttpStatus.BAD_REQUEST));
//            }
//
//            batchPayment.setAmountTotal(amountTotal);
//            batchPayment.setGamingMachineIds(gamingMachineIdList);
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
//                paymentRecord.setGamingMachineId(paymentRecordDetailCreateDto.getGamingMachineId());
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
//            return logAndReturnError(logger, "An error occurred while creating multiple gaming machine license payments ", e);
//        }
        return null;
    }

    private Mono<ResponseEntity> makeMultipleGamingMachineLicensePaymentInBranch(GamingMachineMultiplePaymentRequest gamingMachineMultiplePaymentRequest) {
//        try {
//            double amountTotal = 0;
//            Set<String> gamingMachineIdList = gamingMachineMultiplePaymentRequest.getGamingMachineIdList();
//            List<PaymentRecordDetailCreateDto> paymentRecordDetailCreateDtoList = new ArrayList<>();
//            String licenseTypeId = LicenseTypeReferenceData.GAMING_MACHINE_ID;
//            String feePaymentTypeId = FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID;
//            String institutionId = null;
//
//            if (gamingMachineIdList.isEmpty()) {
//                return Mono.just(new ResponseEntity<>("Empty gaming machine ids supplied", HttpStatus.BAD_REQUEST));
//            }
//            BatchPayment batchPayment = new BatchPayment();
//            batchPayment.setId(UUID.randomUUID().toString());
//            List<FeeAndDescription> feeDescriptions = new ArrayList<>();
//            for (String gamingMachineId : gamingMachineIdList) {
//                Machine gamingMachine = findById(gamingMachineId);
//                if (gamingMachine == null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s does not exist", gamingMachineId), HttpStatus.BAD_REQUEST));
//                }
//                institutionId = gamingMachine.getInstitutionId();
//                String gameTypeId = gamingMachine.getGameTypeId();
//                PaymentRecord existingLicensePayment = findLicensePaymentForGamingMachine(gamingMachineId, institutionId, gameTypeId);
//                if (existingLicensePayment != null) {
//                    return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s has a license payment already", gamingMachineId), HttpStatus.BAD_REQUEST));
//                }
//                Fee fee = feeService.findActiveFeeByLicenseTypeGameTypeAndFeePaymentType(licenseTypeId, gameTypeId, feePaymentTypeId);
//                if (fee == null) {
//                    GameType gameType = gamingMachine.getGameType();
//                    String gameTypeName = gameType != null ? gameType.getName() : gameTypeId;
//                    return Mono.just(new ResponseEntity<>(String.format("Licencing fees for gaming machines for category %s does not exist", gameTypeName), HttpStatus.BAD_REQUEST));
//                }
//
//                double paymentAmount = fee.getAmount();
//                amountTotal = amountTotal + paymentAmount;
//
//                FeeDto feeDto = fee.convertToDto();
//                String feeName = feeDto.getFeePaymentTypeName();
//                String gameTypeName = feeDto.getGameTypeName();
//                String revenueName = feeDto.getRevenueName();
//                //        String suffix = String.format(" for Machine %s", gamingMachine.getMachineNumber());
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
//                paymentRecordDetailCreateDto.setGamingMachineId(gamingMachineId);
//                paymentRecordDetailCreateDtoList.add(paymentRecordDetailCreateDto);
//                batchPayment.setInstitutionId(institutionId);
//            }
//            if (amountTotal != gamingMachineMultiplePaymentRequest.getTotalAmount()) {
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
//            batchPayment.setGamingMachineIds(gamingMachineIdList);
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
//                paymentRecord.setGamingMachineId(paymentRecordDetailCreateDto.getGamingMachineId());
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
//            return logAndReturnError(logger, "An error occurred while creating multiple gaming machine license payments ", e);
//        }
        return null;
    }


    private Mono<ResponseEntity> updateWebMultiplePayment(BatchPaymentUpdateDto batchPaymentUpdateDto) {
//        try {
//            String batchPaymentId = batchPaymentUpdateDto.getBatchPaymentId();
//            BatchPayment existingBatchPayment = findBatchPaymentById(batchPaymentId);
//            if (existingBatchPayment == null) {
//                return Mono.just(new ResponseEntity<>(String.format("Batch payment with id %s does not exist", batchPaymentId), HttpStatus.BAD_REQUEST));
//            }
//            String newPaymentStatusId = batchPaymentUpdateDto.getPaymentStatusId();
//            String completedPaymentStatusId = PaymentStatusReferenceData.COMPLETED_PAYMENT_STATUS_ID;
//            if (!StringUtils.equals(completedPaymentStatusId, existingBatchPayment.getAgentId()) && StringUtils.equals(completedPaymentStatusId, newPaymentStatusId)) {
//                updateLicensePaymentsInBatchToSuccessful(existingBatchPayment);
//            }
//
//
//            return null;
//        } catch (Exception e) {
//            return logAndReturnError(logger, "An error occurred while updating payment", e);
//        }
        return null;
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
        return paymentRecordService.findPaymentRecordForGamingMachine(gamingMachineId, gameTypeId, institutionId, FeePaymentTypeReferenceData.LICENSE_FEE_TYPE_ID);
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

    private MachineGame findMachineGameById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return (MachineGame) mongoRepositoryReactive.findById(id, MachineGame.class).block();
    }
}
