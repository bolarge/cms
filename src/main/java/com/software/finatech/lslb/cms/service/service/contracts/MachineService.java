package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.Machine;
import com.software.finatech.lslb.cms.service.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MachineService {

    Mono<ResponseEntity> findAllMachines(int page,
                                               int pageSize,
                                               String sortDirection,
                                               String sortProperty,
                                               String institutionId,
                                               String agentId,
                                               String machineTypeId,
                                               String machineStatusId,
                                               HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> createMachine(MachineCreateDto gamingMachineCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> updateMachine(MachineUpdateDto gamingMachineUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> addGamesToMachine(MachineGameUpdateDto machineGameUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> removeGamesFromMachine(MachineGameUpdateDto machineGameUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> uploadMultipleMachinesForInstitution(String institutionId, String gameTypeId, MultipartFile multipartFile, HttpServletRequest request);

    Machine findById(String machineId);

    Mono<ResponseEntity> findMachineBySearchKey(String searchKey);

    Mono<ResponseEntity> updateMachineStatus(MachineStatusUpdateDto statusUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> getAllMachineTypes();

    Mono<ResponseEntity> getAllMachineStatus();

    Mono<ResponseEntity> getMachineFullDetail(String machineId);
}
