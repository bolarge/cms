package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.service.contracts.ScheduledMeetingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "Scheduled meetings", description = "For everything related to scheduled meetings with gaming operators", tags = "Scheduled Meetings")
@RestController
@RequestMapping("/api/v1/scheduled-meetings")
public class ScheduledMeetingController  extends BaseController{

    private ScheduledMeetingService scheduledMeetingService;

    @Autowired
    public void setScheduledMeetingService(ScheduledMeetingService scheduledMeetingService) {
        this.scheduledMeetingService = scheduledMeetingService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"page", "pageSize",
            "sortType", "sortProperty", "startDate", "endDate", "creatorId", "cancelerId", "meetingPurposeId"})
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
                                                        @RequestParam("meetingPurposeId") String meetingPurposeId,
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
                meetingPurposeId,
                httpServletResponse);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-meeting-purpose")
    @ApiOperation(value = "Get all meeting Purposes", response = EnumeratedFactDto.class, responseContainer = "list", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllMeetingPurposes() {
        return scheduledMeetingService.getAllMeetingPurposes();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/create")
    @ApiOperation(value = "Create a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> createScheduledMeeting(@RequestBody @Valid ScheduledMeetingCreateDto scheduledMeetingCreateDto, HttpServletRequest request) {
        return scheduledMeetingService.createScheduledMeeting(scheduledMeetingCreateDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    @ApiOperation(value = "Update a a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> updateScheduledMeeting(@RequestBody @Valid ScheduledMeetingUpdateDto scheduledMeetingCreateDto, HttpServletRequest httpServletRequest) {
        return scheduledMeetingService.updateScheduledMeting(scheduledMeetingCreateDto, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cancel")
    @ApiOperation(value = "Cancel a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> cancelScheduledMeeting(@RequestParam("meetingId") String meetingId, @RequestParam("cancelerId") String cancelerId, HttpServletRequest request) {
        return scheduledMeetingService.cancelScheduledMeeting(meetingId, cancelerId, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/complete")
    @ApiOperation(value = "Complete a scheduled meeting", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> completeScheduledMeeting(@RequestParam("meetingId") String meetingId, HttpServletRequest request) {
        return scheduledMeetingService.completeScheduledMeeting(meetingId, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiOperation(value = "Get a scheduled meeting full details", response = ScheduledMeetingDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getById(@PathVariable("id") String meetingId) {
        return scheduledMeetingService.getScheduledMeetingById(meetingId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/add-comment")
    @ApiOperation(value = "Add Comment to Scheduled Meeting", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> addCommentToApplicationForm(@PathVariable("id") String meetingId, @RequestBody AddCommentDto addCommentDto, HttpServletRequest request) {
        return scheduledMeetingService.addCommentsToMeeting(meetingId, addCommentDto, request);
    }
}
