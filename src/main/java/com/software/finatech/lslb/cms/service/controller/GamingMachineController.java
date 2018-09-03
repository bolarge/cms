package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.GamingMachineCreateDto;
import com.software.finatech.lslb.cms.service.dto.GamingMachineDto;
import com.software.finatech.lslb.cms.service.service.contracts.GamingMachineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Gaming machines", description = "For everything related to gaming machines", tags = "")
@RestController
@RequestMapping("/api/v1/gaming-machines")
public class GamingMachineController {

    private GamingMachineService gamingMachineService;
    @Autowired
    public void setGamingMachineService(GamingMachineService gamingMachineService) {
        this.gamingMachineService = gamingMachineService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize", "sortType", "sortProperty", "institutionId", "agentId"})
    @ApiOperation(value = "Get all gaming machines", response = GamingMachineDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllGamingMachines(@RequestParam("page") int page,
                                                     @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("sortType") String sortType,
                                                     @RequestParam("sortProperty") String sortParam,
                                                     @RequestParam("agentId") String agentId,
                                                     @RequestParam("institutionId") String institutionId,
                                                     HttpServletResponse httpServletResponse) {
        return gamingMachineService.findAllGamingMachines(page, pageSize, sortType, sortParam,  institutionId, agentId, httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create an Gaming Machine", response = GamingMachineDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createGamingMachine(@RequestBody @Valid GamingMachineCreateDto gamingMachineCreateDto) {
        return gamingMachineService.createGamingMachine(gamingMachineCreateDto);
    }
}
