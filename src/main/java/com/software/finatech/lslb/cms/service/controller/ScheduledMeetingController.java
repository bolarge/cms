package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingCreateDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingUpdateDto;
import com.software.finatech.lslb.cms.service.service.contracts.ScheduledMeetingService;
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

@Api(value = "Scheduled meetings", description = "For everything related to scheduled meetings with gaming operators", tags = "Scheduled ")
@RestController
@RequestMapping("/api/v1/scheduled-meetings")
public class ScheduledMeetingController {

    private ScheduledMeetingService scheduledMeetingService;

    @Autowired
    public void setScheduledMeetingService(ScheduledMeetingService scheduledMeetingService) {
        this.scheduledMeetingService = scheduledMeetingService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "startDate", "endDate", "creatorId", "cancelerId"})
    @ApiOperation(value = "Get all Scheduled meetings", response = ScheduledMeetingDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllScheduledMeetings(@RequestParam("page") int page,
                                                        @RequestParam("pageSize") int pageSize,
                                                        @RequestParam("sortType") String sortType,
                                                        @RequestParam("sortProperty") String sortParam,
                                                        @RequestParam("institutionId") String institutionId,
                                                        @RequestParam("startDate") String startDate,
                                                        @RequestParam("endDate") String endDate,
                                                        @RequestParam("dateProperty") String dateProperty,
                                                        @RequestParam("creatorId") String creatorId,
                                                        @RequestParam("cancelerId") String cancelerId,
                                                        HttpServletResponse httpServletResponse) {
        return scheduledMeetingService.findAllScheduledMeetings(page,
                pageSize,
                sortType,
                sortParam,
                institutionId,
                startDate,
                endDate,
                dateProperty,
                creatorId,
                cancelerId,
                httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createScheduledMeeting(@RequestBody @Valid ScheduledMeetingCreateDto scheduledMeetingCreateDto) {
        return scheduledMeetingService.createScheduledMeeting(scheduledMeetingCreateDto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update a a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateScheduledMeeting(@RequestBody @Valid ScheduledMeetingUpdateDto scheduledMeetingCreateDto) {
        return scheduledMeetingService.updateScheduledMeting(scheduledMeetingCreateDto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cancel")
    @ApiOperation(value = "Cancel a scheduled meeting",response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> cancelScheduledMeeting(@RequestParam("meetingId") String meetingId, @RequestParam("cancelerId") String cancelerId) {
        return scheduledMeetingService.cancelScheduledMeeting(meetingId, cancelerId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/complete")
    @ApiOperation(value = "Complete a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> completeScheduledMeeting(@RequestParam("meetingId") String meetingId) {
        return scheduledMeetingService.completeScheduledMeeting(meetingId);
    }
}
