package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.GamingMachine;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.dto.GamingMachineCreateDto;
import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.dto.GamingMachineUpdateDto;
import com.software.finatech.lslb.cms.service.dto.UploadTransactionResponse;
import com.software.finatech.lslb.cms.service.model.GamingMachineGameDetails;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.GameTypeReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.GamingMachineService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.LicenseValidatorUtil;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GamingMachineServiceImpl implements GamingMachineService {

    private static final Logger logger = LoggerFactory.getLogger(GamingMachineServiceImpl.class);
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private LicenseValidatorUtil licenseValidatorUtil;
    private InstitutionService institutionService;


    @Autowired
    public GamingMachineServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                    LicenseValidatorUtil licenseValidatorUtil,
                                    InstitutionService institutionService) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.licenseValidatorUtil = licenseValidatorUtil;
        this.institutionService = institutionService;
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
            return ErrorResponseUtil.logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> createGamingMachine(GamingMachineCreateDto gamingMachineCreateDto) {
        try {
            String institutionId = gamingMachineCreateDto.getInstitutionId();
            String gameTypeId = gamingMachineCreateDto.getGameTypeId();
            Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
            if (validateGamingMachineLicenseResponse != null) {
                return validateGamingMachineLicenseResponse;
            }
            GamingMachine gamingMachine = fromGamingMachineCreateDto(gamingMachineCreateDto);
            saveGamingMachine(gamingMachine);
            return Mono.just(new ResponseEntity<>(gamingMachine.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while saving gaming machine", e);
        }
    }


    @Override
    public Mono<ResponseEntity> updateGamingMachine(GamingMachineUpdateDto gamingMachineUpdateDto) {
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
        return Mono.just(new ResponseEntity<>(gamingMachine.convertToDto(), HttpStatus.OK));
    }

    @Override
    public GamingMachine findById(String gamingMachineId) {
        return (GamingMachine) mongoRepositoryReactive.findById(gamingMachineId, GamingMachine.class).block();
    }


    //TODO: validate if its multiple or not
    @Override
    public Mono<ResponseEntity> uploadMultipleGamingMachinesForInstitution(String institutionId,String gameTypeId ,MultipartFile multipartFile) {
        Institution institution = institutionService.findById(institutionId);
        if (institution == null) {
            return Mono.just(new ResponseEntity<>(String.format("Institution with id %s does not exist", institutionId), HttpStatus.BAD_REQUEST));
        }
        Mono<ResponseEntity> validateGamingMachineLicenseResponse = licenseValidatorUtil.validateInstitutionLicenseForGameType(institutionId, gameTypeId);
        if (validateGamingMachineLicenseResponse != null) {
            return validateGamingMachineLicenseResponse;
        }

        List<GamingMachine> gamingMachineList = new ArrayList<>();
        List<String> failedLines = new ArrayList<>();
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
                        failedLines.add(rows[i]);
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
                            failedLines.add(rows[i]);
                        }
                    }
                }

                if (!failedLines.isEmpty()) {
                    uploadTransactionResponse.setFailedTransactions(failedLines);
                    uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
                    uploadTransactionResponse.setMessage(String.format(
                            "Upload Failed, You have %s lines with invalid format, please review and re upload",
                            failedLines.size()));
                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.BAD_REQUEST));
                } else {
                    for (GamingMachine gamingMachine : gamingMachineList) {
                        try {
                            saveGamingMachine(gamingMachine);
                        } catch (Exception e) {
                            logger.error("An error occurred while saving gaming machine with serial number {}", gamingMachine.getSerialNumber());
                            failedLines.add(String.format("%s,%s,%s,%s", gamingMachine.getSerialNumber(), gamingMachine.getManufacturer(), gamingMachine.getMachineNumber(), gamingMachine.getMachineAddress()));
                        }
                    }
                    if (!failedLines.isEmpty()) {
                        uploadTransactionResponse.setMessage("Upload partially successful, please see failed records");
                        uploadTransactionResponse.setFailedTransactions(failedLines);
                        uploadTransactionResponse.setFailedTransactionCount(failedLines.size());
                    } else {
                        uploadTransactionResponse.setMessage("Upload successful");
                    }
                    return Mono.just(new ResponseEntity<>(uploadTransactionResponse, HttpStatus.OK));
                }
            } catch (IOException e) {
                return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while parsing the file", e);
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
}
