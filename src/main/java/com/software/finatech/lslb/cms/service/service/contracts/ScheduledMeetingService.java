package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.dto.AddCommentDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingCreateDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ScheduledMeetingService {

    Mono<ResponseEntity> createScheduledMeeting(ScheduledMeetingCreateDto scheduledMeetingCreateDto, HttpServletRequest request);

    Mono<ResponseEntity> cancelScheduledMeeting(String scheduledMeetingId, String cancelerId, HttpServletRequest httpServletRequest);

    Mono<ResponseEntity> completeScheduledMeeting(String scheduledMeetingId, HttpServletRequest request);

    Mono<ResponseEntity> updateScheduledMeting(ScheduledMeetingUpdateDto scheduledMeetingUpdateDto, HttpServletRequest request);

    Mono<ResponseEntity> findAllScheduledMeetings(int page,
                                                  int pageSize,
                                                  String sortDirection,
                                                  String sortProperty,
                                                  String institutionId,
                                                  String startDate,
                                                  String endDate,
                                                  String dateProperty,
                                                  String creatorId,
                                                  String cancelerId,
                                                  String purposeId,
                                                  HttpServletResponse httpServletResponse);

    Mono<ResponseEntity> getScheduledMeetingById(String meetingId);

    Mono<ResponseEntity> addCommentsToMeeting(String meetingId, AddCommentDto addCommentDto, HttpServletRequest request);

    Mono<ResponseEntity> getAllMeetingPurposes();

    ScheduledMeeting findScheduledMeetingByEntityId(String entityId);

    ScheduledMeeting findCompletedMeetingForEntityAndPurpose(String entityId, String meetingPurposeId);
}

