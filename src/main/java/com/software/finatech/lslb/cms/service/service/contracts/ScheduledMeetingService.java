package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingCreateDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingUpdateDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

public interface ScheduledMeetingService {

    Mono<ResponseEntity> createScheduledMeeting(ScheduledMeetingCreateDto scheduledMeetingCreateDto);
    Mono<ResponseEntity> cancelScheduledMeeting(String scheduledMeetingId, String cancelerId);
    Mono<ResponseEntity> completeScheduledMeeting(String scheduledMeetingId);
    Mono<ResponseEntity> updateScheduledMeting(ScheduledMeetingUpdateDto scheduledMeetingUpdateDto);
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
                                                  HttpServletResponse httpServletResponse);

    void sendMeetingNotificationEmailToAttendee(String mailSubject,String templateName, AuthInfo invitee, ScheduledMeeting scheduledMeeting);

    void sendMeetingNotificationEmailToMeetingCreator(String mailSubject,String templateName, ScheduledMeeting scheduledMeeting);
}

