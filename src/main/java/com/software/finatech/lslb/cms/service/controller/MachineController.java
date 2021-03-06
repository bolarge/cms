package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.MachineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Machines", description = "For everything related to Machines (Gaming terminals and Gaming Machines)", tags = "Machine Controller(Gaming Machines and Terminals)")
@RestController
@RequestMapping("/api/v1/machines")
public class MachineController extends BaseController {

    private MachineService machineService;

    @Autowired
    public void setMachineService(MachineService machineService) {
        this.machineService = machineService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all")
    @ApiOperation(value = "Get all gaming machines", response = MachineDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllGamingMachines(@RequestParam("page") int page,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("sortType") String sortType,
                                                     @RequestParam("sortProperty") String sortParam,
                                                     @RequestParam("institutionId") String institutionId,
                                                     @RequestParam("agentId") String agentId,
                                                     @RequestParam("machineTypeId") String machineTypeId,
                                                     @RequestParam("machineStatusId") String machineStatusId,
                                                     @RequestParam(value = "forAgentAssignment", required = false) boolean forAgentAssignment,
                                                     @RequestParam(value = "licenseNumber", required = false) String licenseNumber,
                                                     @RequestParam(value = "machineNumber", required = false) String machineNumber,HttpServletResponse httpServletResponse) {
        return machineService.findAllMachines(page, pageSize, sortType, sortParam,
                institutionId, agentId, machineTypeId, machineStatusId,
                forAgentAssignment, licenseNumber, machineNumber,httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create a Machine", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createMachine(@RequestBody @Valid MachineCreateDto machineCreateDto, HttpServletRequest request) {
        return machineService.createMachine(machineCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update a Machine", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateMachine(@RequestBody @Valid MachineUpdateDto machineUpdateDto, HttpServletRequest request) {
        return machineService.updateMachine(machineUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update-status")
    @ApiOperation(value = "Update a Machine Status", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateMachineStatus(@RequestBody @Valid MachineStatusUpdateDto statusUpdateDto, HttpServletRequest request) {
        return machineService.updateMachineStatus(statusUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-multiple", params = {"institutionId", "gameTypeId"})
    @ApiOperation(value = "Upload multiple gaming machines for institution", response = UploadTransactionResponse.class,
            notes = "User uploading the gaming machines must specify the category the machines are operating under via the request param \"gameTypeId\"" +
                    " and also supply the institution id via the request param \"institutionId\" ")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server error(error occurred while parsing file)")})
    public Mono<ResponseEntity> uploadMachinesFromCsv(@RequestParam("institutionId") String institutionId,
                                                      @RequestParam("gameTypeId") String gameTypeId,
                                                      @RequestParam("machineTypeId") String machineTypeId,
                                                      @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        return machineService.uploadMultipleMachinesForInstitution(institutionId, gameTypeId, machineTypeId, multipartFile, request);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/search", params = {"searchKey"})
    @ApiOperation(value = "Search for gaming machines on system", response = MachineDto.class, responseContainer = "List", consumes = "application/json",
            notes = "Search for agent on system using a search key that matches machine serial number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> searchGamingMachines(@RequestParam("searchKey") String searchKey) {
        return machineService.findMachineBySearchKey(searchKey);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/all-machine-types")
    @ApiOperation(value = "Get All Machine Types", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllMachineTypes() {
        return machineService.getAllMachineTypes();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/all-machine-status")
    @ApiOperation(value = "Get All Machine Status", response = EnumeratedFactDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllMachineStatus() {
        return machineService.getAllMachineStatus();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add-games-to-machine")
    @ApiOperation(value = "Add Games to Machine", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAddGamesToMachine(@RequestBody MachineGameUpdateDto machineGameUpdateDto, HttpServletRequest request) {
        return machineService.addGamesToMachine(machineGameUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/assign-machine-to-agent")
    @ApiOperation(value = "Assign machine to agent", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> assignMachineToAgent(@RequestBody MachineAgentAddDto dto, HttpServletRequest request) {
        return machineService.assignMachineToAgent(dto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/assign-multiple-machine-to-agent")
    @ApiOperation(value = "Assign machine to agent", response = MachineApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> assignMultipleMachineToAgent(@RequestBody MachineAgentAddDto dto, HttpServletRequest request) {
        return machineService.assignMultipleMachinesToAgent(dto, request);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/remove-games-from-machine")
    @ApiOperation(value = "Remove Games From Machine", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> removeGamesFromMachine(@RequestBody MachineGameUpdateDto gameUpdateDto, HttpServletRequest request) {
        return machineService.removeGamesFromMachine(gameUpdateDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiOperation(value = "Get Machine full detail", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findMachine(@PathVariable("id") String id) {
        return machineService.getMachineFullDetail(id);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/machine-types-by-category", params = {"id"})
    @ApiOperation(value = "Get Machine Type By Game Type ", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findMachineTypesByGameType(@RequestParam("id") String id) {
        return machineService.getMachineTypesByGameType(id);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/find-machines-by-params")
    @ApiOperation(value = "Get Machines By Params ", response = MachineDto.class, consumes = "application/json",
            notes = "Used from the payment page to get machines for institution or agent")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> findMachineByAgentOrInstitution(@RequestParam("agentId") String agentId, @RequestParam("institutionId") String institutionId) {
        return machineService.getMachineByParam(agentId, institutionId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upgrade-machine-games")
    @ApiOperation(value = "Upgrade Machine Games", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> upgradeMachineGames(@RequestBody @Valid MachineGameUpgradeRequest machineGameUpgradeRequest, HttpServletRequest request) {
        return machineService.upgradeMachineGames(machineGameUpgradeRequest, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-machines-by-agent-number", params = {"agentNumber"})
    @ApiOperation(value = "Get Terminals By Agent Number", response = MachineDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getMachinesByAgentId(@RequestParam("agentNumber") String agentNumber) {
        return machineService.getMachinesByAgentNumber(agentNumber);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-full-detail-by-serial-number", params = {"serialNumber", "machineTypeId"})
    @ApiOperation(value = "Get Full Detail From Serial Number", response = MachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getMachineFullDetailBySerialNumber(@RequestParam("serialNumber") String serialNumber,
                                                                   @RequestParam("machineTypeId") String machineTypeId) {
        return machineService.getMachineFullDetailBySerialNumber(serialNumber, machineTypeId);
    }
}
