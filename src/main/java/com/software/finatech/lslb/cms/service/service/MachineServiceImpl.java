package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.model.MachineGameDetails;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.service.contracts.MachineService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.MachineApprovalRequestMailSenderAsync;
import org.apache.commons.lang3.StringUtils;
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

import static com.software.finatech.lslb.cms.service.model.MachineGameDetails.machineGameUpgradeToString;
import static com.software.finatech.lslb.cms.service.model.MachineGameDetails.machineGamesToString;
import static com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil.getAllEnumeratedEntity;
import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class MachineServiceImpl implements MachineService {

    private static final Logger logger = LoggerFactory.getLogger(MachineServiceImpl.class);
    private static final String machineAuditActionId = AuditActionReferenceData.MACHINE_ID;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseValidatorUtil licenseValidatorUtil;
    private InstitutionService institutionService;
    private AgentService agentService;
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    private AuditLogHelper auditLogHelper;
    private MachineApprovalRequestMailSenderAsync machineApprovalRequestMailSenderAsync;

    @Autowired
    public MachineServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                              LicenseValidatorUtil licenseValidatorUtil,
                              InstitutionService institutionService,
                              AgentService agentService,
                              SpringSecurityAuditorAware springSecurityAuditorAware,
                              AuditLogHelper auditLogHelper,
                              MachineApprovalRequestMailSenderAsync machineApprovalRequestMailSenderAsync) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseValidatorUtil = licenseValidatorUtil;
        this.institutionService = institutionService;
        this.agentService = agentService;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
        this.auditLogHelper = auditLogHelper;
        this.machineApprovalRequestMailSenderAsync = machineApprovalRequestMailSenderAsync;
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
    public Mono<ResponseEntity> createMachine(MachineCreateDto machineCreateDto, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String institutionId = machineCreateDto.getInstitutionId();
            String gameTypeId = machineCreateDto.getGameTypeId();
            Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
            if (validateGamingMachineLicenseResponse != null) {
                return validateGamingMachineLicenseResponse;
            }
            if (!(machineCreateDto.isCreateGamingMachine() || machineCreateDto.isCreateGamingTerminal())) {
                return Mono.just(new ResponseEntity<>(String.format("Machine type with id %s does not exist on the system", machineCreateDto.getMachineTypeId()), HttpStatus.BAD_REQUEST));
            }
            PendingMachine pendingGamingMachine = fromGamingMachineCreateDto(machineCreateDto);
            mongoRepositoryReactive.saveOrUpdate(pendingGamingMachine);
            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setInitiatedByInstitution(true);
            approvalRequest.setPendingMachineId(pendingGamingMachine.getId());
            if (machineCreateDto.isCreateGamingTerminal()) {
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.CREATE_GAMING_TERMINAL_ID);
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
            }
            if (machineCreateDto.isCreateGamingMachine()) {
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_MACHINE_ID);
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.CREATE_GAMING_MACHINE_ID);
            }
            approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);

            String verbiage = String.format("Created Machine Request, Type  -> %s, Serial Number -> %s", approvalRequest.getMachineApprovalRequestType(), pendingGamingMachine.getSerialNumber());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), pendingGamingMachine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));
            machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);

            return Mono.just(new ResponseEntity<>(approvalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while saving gaming machine", e);
        }
    }


    @Override
    public Mono<ResponseEntity> updateMachine(MachineUpdateDto gamingMachineUpdateDto, HttpServletRequest request) {
        try {
            String gamingMachineId = gamingMachineUpdateDto.getId();
            Machine gamingMachine = findMachineById(gamingMachineId);
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
            Machine machine = findMachineById(machineGameUpdateDto.getMachineId());
            if (machine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Gaming machine with id %s not found", gamingMachineId), HttpStatus.BAD_REQUEST));
            }

            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            if (machine.isGamingMachine()) {
                approvalRequest.setInitiatedByInstitution(true);
                approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());

                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_MACHINE_ID);
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.ADD_GAMES_TO_GAMING_MACHINE_ID);
                machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
            }
            if (machine.isGamingTerminal()) {
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.ADD_GAMES_TO_GAMING_TERMINAL_ID);
                if (loggedInUser.isGamingOperator()) {
                    approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
                    approvalRequest.setInitiatedByInstitution(true);
                    machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
                }
                if (loggedInUser.isAgent()) {
                    makeRequestFromAgent(approvalRequest, loggedInUser);
                }
            }
            approvalRequest.setNewMachineGames(newGameDetails);
            approvalRequest.setMachineId(gamingMachineId);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);


            String verbiage = String.format("Created Gaming Machine Request, Type  -> %s, Serial Number -> %s, New Games -> %s",
                    approvalRequest.getMachineApprovalRequestType(), machine.getSerialNumber(), machineGamesToString(newGameDetails));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), machine.getInstitutionName(),
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
            Machine machine = findMachineById(machineGameUpdateDto.getMachineId());
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

            String verbiage = String.format("Disabled games from machines, Serial Number -> %s,Machine Type -> %s, Disabled Games -> %s",
                    machine.getSerialNumber(), machine.getMachineType(), machineGamesToString(gameDetails));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), machine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>("Game successfully disabled", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while disabling games from machines", e);
        }
    }

    @Override
    public Machine findMachineById(String machineId) {
        return (Machine) mongoRepositoryReactive.findById(machineId, Machine.class).block();
    }


    @Override
    public Mono<ResponseEntity> updateMachineStatus(MachineStatusUpdateDto statusUpdateDto, HttpServletRequest request) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String machineId = statusUpdateDto.getMachineId();
            String machineStatusId = statusUpdateDto.getMachineStatusId();
            Machine machine = findMachineById(machineId);
            if (machine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Machine with id %s does not exist", machineId), HttpStatus.BAD_REQUEST));
            }

            MachineStatus newStatus = getMachineStatus(machineStatusId);
            if (newStatus == null) {
                return Mono.just(new ResponseEntity<>(String.format("Machine status with id %s not found", machineStatusId), HttpStatus.BAD_REQUEST));
            }

            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setMachineId(machineId);
            approvalRequest.setNewMachineStatusId(statusUpdateDto.getMachineStatusId());
            if (machine.isGamingMachine()) {
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.CHANGE_GAMING_MACHINE_STATUS);
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_MACHINE_ID);
                approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
                approvalRequest.setInitiatedByInstitution(true);
                machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
            }
            if (machine.isGamingTerminal()) {
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.CHANGE_GAMING_TERMINAL_STATUS);
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
                if (loggedInUser.isAgent()) {
                    makeRequestFromAgent(approvalRequest, loggedInUser);
                }
                if (loggedInUser.isGamingOperator()) {
                    approvalRequest.setInitiatedByInstitution(true);
                    approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
                    machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
                }
            }
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);

            String verbiage = String.format("Created machine approval request,Type -> %s, Serial Number -> %s, Old Status -> %s, New Status -> %s",
                    approvalRequest.getMachineApprovalRequestType(), machine.getSerialNumber(), machine.getMachineStatus(), newStatus);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), machine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            return Mono.just(new ResponseEntity<>(approvalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating machine status", e);
        }
    }

    @Override
    public Mono<ResponseEntity> upgradeMachineGames(MachineGameUpgradeRequest machineGameUpgradeRequest, HttpServletRequest request) {
        try {

            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            String machineId = machineGameUpgradeRequest.getMachineId();
            Machine machine = findMachineById(machineId);
            if (machine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Machine with id %s not found", machineId), HttpStatus.BAD_REQUEST));
            }
            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setMachineId(machineId);
            List<GameUpgrade> gameUpgrades = machineGameUpgradeRequest.getGameUpgradeList();
            for (GameUpgrade gameUpgrade : gameUpgrades) {
                String gameId = gameUpgrade.getGameId();
                MachineGame machineGame = findMachineGameById(gameId);
                if (machineGame == null) {
                    return Mono.just(new ResponseEntity<>(String.format("Machine game with id %s not found", gameId), HttpStatus.BAD_REQUEST));
                }
                gameUpgrade.setGameName(machineGame.getGameName());
                gameUpgrade.setOldGameVersion(machineGame.getGameVersion());
            }
            approvalRequest.setMachineGameUpgrades(new HashSet<>(gameUpgrades));
            if (machine.isGamingMachine()) {
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.UPGRADE_GAMING_MACHINE_GAMES);
                approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
                approvalRequest.setInitiatedByInstitution(true);
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_MACHINE_ID);
                machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
            }
            if (machine.isGamingTerminal()) {
                approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.UPGRADE_GAMING_TERMINAL_GAMES);
                approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
                if (loggedInUser.isAgent()) {
                    makeRequestFromAgent(approvalRequest, loggedInUser);
                }
                if (loggedInUser.isGamingOperator()) {
                    approvalRequest.setInitiatedByInstitution(true);
                    approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
                    machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
                }
            }

            mongoRepositoryReactive.saveOrUpdate(approvalRequest);
            String verbiage = String.format("Created machine approval request,Type -> %s, Serial Number -> %s, Upgrade Games Versions -> %s",
                    approvalRequest.getMachineApprovalRequestType(), machine.getSerialNumber(), machineGameUpgradeToString(gameUpgrades));
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), machine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
            return Mono.just(new ResponseEntity<>(approvalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while upgrading machine games", e);
        }
    }


    @Override
    public Mono<ResponseEntity> getAllMachineTypes() {
        return getAllEnumeratedEntity("MachineType");
    }

    @Override
    public Mono<ResponseEntity> getAllMachineStatus() {
        return getAllEnumeratedEntity("MachineStatus");
    }

    @Override
    public Mono<ResponseEntity> getMachineFullDetail(String id) {
        try {
            Machine machine = findMachineById(id);
            if (machine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Machine with id %s not found", id), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(machine.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while getting machine by id %s", id), e);
        }
    }

    //TODO: validate if its multiple or not
    @Override
    public Mono<ResponseEntity> uploadMultipleMachinesForInstitution(String institutionId, String gameTypeId, MultipartFile multipartFile, HttpServletRequest request) {
        Institution institution = institutionService.findByInstitutionId(institutionId);
        if (institution == null) {
            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
        }
        Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
        if (validateGamingMachineLicenseResponse != null) {
            return validateGamingMachineLicenseResponse;
        }

        List<Machine> machineList = new ArrayList<>();
        List<MachineGame> machineGames = new ArrayList<>();

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
                    if (columns.length < 8) {
                        failedLines.add(FailedLine.fromLineAndReason(rows[i], "Line has less than 6 fields"));
                    } else {
                        try {
                            Machine machine = getGamingMachineBySerialNumberFromMap(columns[0], gamingMachineMap);
                            MachineGame machineGame = new MachineGame();
                            if (machine == null) {
                                machine = new Machine();
                                machine.setId(UUID.randomUUID().toString());
                                machine.setSerialNumber(columns[0]);
                                machine.setManufacturer(columns[1]);
                                machine.setMachineAddress(columns[3]);
                                String machineTypeId = columns[4];
                                MachineType machineType = getMachineType(machineTypeId);
                                if (machineType == null) {
                                    failedLines.add(FailedLine.fromLineAndReason(rows[i], String.format("Machine type with id %s not found", machineTypeId)));
                                    continue;
                                }
                                machine.setMachineTypeId(machineTypeId);
                                String machineStatusId = columns[5];
                                MachineStatus machineStatus = getMachineStatus(machineStatusId);
                                if (machineStatus == null) {
                                    failedLines.add(FailedLine.fromLineAndReason(rows[i], String.format("Machine status with id %s not found", machineStatusId)));
                                    continue;
                                }
                                machine.setMachineStatusId(machineStatusId);

                            }
                            machine.setGameTypeId(gameTypeId);
                            machineGame.setId(UUID.randomUUID().toString());
                            machineGame.setMachineId(machine.getId());
                            machineGame.setGameName(columns[6]);
                            machineGame.setGameVersion(columns[7]);
                            machineGame.setActive(true);
                            machine.setInstitutionId(institutionId);
                            machineList.add(machine);
                            machineGames.add(machineGame);
                            gamingMachineMap.put(machine.getSerialNumber(), machine);
                        } catch (Exception e) {
                            logger.error(String.format("Error parsing line %s", rows[i]), e);
                            failedLines.add(FailedLine.fromLineAndReason(rows[i], "An error occurred while parsing line"));
                        }
                    }
                }

                if (!failedLines.isEmpty()) {
                    uploadTransactionResponse.setFailedLines(failedLines);
                    uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
                    uploadTransactionResponse.setMessage("Please review with sample file and re upload");
                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.BAD_REQUEST));
                } else {
                    for (Machine gamingMachine : machineList) {
                        try {
                            mongoRepositoryReactive.saveOrUpdate(gamingMachine);
                        } catch (Exception e) {
                            logger.error("An error occurred while saving gaming machine with serial number {}", gamingMachine.getSerialNumber());
                            String line = String.format("%s,%s,%s", gamingMachine.getSerialNumber(), gamingMachine.getManufacturer(), gamingMachine.getMachineAddress());
                            failedLines.add(FailedLine.fromLineAndReason(line, "An error occurred while saving line"));
                        }
                        for (MachineGame machineGame : machineGames) {
                            mongoRepositoryReactive.saveOrUpdate(machineGame);
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

    private Machine getGamingMachineBySerialNumberFromMap(String serialNumber, @NotNull Map<String, Machine> gamingMachineMap) {
        return gamingMachineMap.get(serialNumber);
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

    @Override
    public Mono<ResponseEntity> assignMachineToAgent(MachineAgentAddDto machineAgentAddDto, HttpServletRequest request) {
        try {
            String agentId = machineAgentAddDto.getAgentId();
            String machineId = machineAgentAddDto.getMachineId();
            Machine machine = findMachineById(machineId);
            if (machine == null) {
                return Mono.just(new ResponseEntity<>(String.format("Gaming Terminal with id %s does not exist", machineId), HttpStatus.BAD_REQUEST));
            }
            if (!machine.isGamingTerminal()) {
                return Mono.just(new ResponseEntity<>(String.format("Machine with id %s is not a gaming terminal", machineId), HttpStatus.BAD_REQUEST));
            }
            Agent agent = agentService.findAgentById(agentId);
            if (agent == null) {
                return Mono.just(new ResponseEntity<>(String.format("Agent with Id %s not found", agentId), HttpStatus.BAD_REQUEST));
            }
            if (!agent.isEnabled() || agent.getAuthInfo().isInactive() || !agent.getAuthInfo().getEnabled()) {
                return Mono.just(new ResponseEntity<>(String.format("Agent with Id %s is disbaled on our platform", agentId), HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            MachineApprovalRequest approvalRequest = new MachineApprovalRequest();
            approvalRequest.setId(UUID.randomUUID().toString());
            approvalRequest.setMachineTypeId(MachineTypeReferenceData.GAMING_TERMINAL_ID);
            approvalRequest.setAgentId(agentId);
            approvalRequest.setMachineId(machineId);
            approvalRequest.setInitiatedByInstitution(true);
            approvalRequest.setInstitutionId(loggedInUser.getInstitutionId());
            approvalRequest.setMachineApprovalRequestTypeId(MachineApprovalRequestTypeReferenceData.ASSIGN_TERMINAL_TO_AGENT);
            mongoRepositoryReactive.saveOrUpdate(approvalRequest);


            String verbiage = String.format("Created Machine Request, Type  -> %s, Serial Number -> %s, Agent Name -> %s",
                    approvalRequest.getMachineApprovalRequestType(), machine.getSerialNumber(), agent.getFullName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(machineAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), machine.getInstitutionName(),
                    LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), verbiage));

            machineApprovalRequestMailSenderAsync.sendMachineApprovalInitialNotificationToLSLBAdmins(approvalRequest);
            return Mono.just(new ResponseEntity<>(approvalRequest.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding machine to agent", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getMachineByParam(String agentId, String institutionId) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("machineStatusId").is(MachineStatusReferenceData.ACTIVE_ID));
            String machineTypeId = "";
            if (StringUtils.isEmpty(agentId) && !StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
                machineTypeId = MachineTypeReferenceData.GAMING_MACHINE_ID;
            }
            if (!StringUtils.isEmpty(agentId) && StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("agentId").is(agentId));
                machineTypeId = MachineTypeReferenceData.GAMING_TERMINAL_ID;
            }
            query.addCriteria(Criteria.where("machineTypeId").is(machineTypeId));

            ArrayList<Machine> machines = (ArrayList<Machine>) mongoRepositoryReactive.findAll(query, Machine.class).toStream().collect(Collectors.toList());
            if (machines.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }

            List<MachineDto> dtos = new ArrayList<>();
            for (Machine machine : machines) {
                MachineDto dto = new MachineDto();
                dto.setId(machine.getId());
                dto.setSerialNumber(machine.getSerialNumber());
                dtos.add(dto);
            }
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting machines by params", e);
        }
    }

    private MachineGame findMachineGameById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return (MachineGame) mongoRepositoryReactive.findById(id, MachineGame.class).block();
    }

    private MachineStatus getMachineStatus(String machineStatusId) {
        if (StringUtils.isEmpty(machineStatusId)) {
            return null;
        }
        Map machineStatusMap = Mapstore.STORE.get("MachineStatus");
        MachineStatus machineStatus = null;
        if (machineStatus != null) {
            machineStatus = (MachineStatus) machineStatusMap.get(machineStatusId);
        }
        if (machineStatus == null) {
            machineStatus = (MachineStatus) mongoRepositoryReactive.findById(machineStatusId, MachineStatus.class).block();
            if (machineStatus != null && machineStatusMap != null) {
                machineStatusMap.put(machineStatusId, machineStatus);
            }
        }
        return machineStatus;
    }

    private MachineType getMachineType(String machineTypeId) {
        if (StringUtils.isEmpty(machineTypeId)) {
            return null;
        }
        Map machineTypeMap = Mapstore.STORE.get("MachineType");
        MachineType machineType = null;
        if (machineType != null) {
            machineType = (MachineType) machineTypeMap.get(machineTypeId);
        }
        if (machineType == null) {
            machineType = (MachineType) mongoRepositoryReactive.findById(machineTypeId, MachineType.class).block();
            if (machineType != null && machineTypeMap != null) {
                machineTypeMap.put(machineTypeId, machineType);
            }
        }
        return machineType;
    }

    private void makeRequestFromAgent(MachineApprovalRequest approvalRequest, AuthInfo loggedInUser) {
        approvalRequest.setInitiatedByInstitution(false);
        approvalRequest.setInitiatorId(loggedInUser.getId());
        approvalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_OPERATOR_APPROVAL_ID);
        machineApprovalRequestMailSenderAsync.sendInitialMachineStateUpdateToAgentOperators(approvalRequest);
    }
}
