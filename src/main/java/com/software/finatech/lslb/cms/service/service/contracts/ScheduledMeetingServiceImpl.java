package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.ScheduledMeeting;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingCreateDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingDto;
import com.software.finatech.lslb.cms.service.dto.ScheduledMeetingUpdateDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class ScheduledMeetingServiceImpl implements ScheduledMeetingService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledMeetingServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private MongoRepositoryReactiveImpl mongoRepositoryReactive;

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    @Override
    public Mono<ResponseEntity> findAllScheduledMeetings(int page,
                                                         int pageSize,
                                                         String sortDirection,
                                                         String sortProperty,
                                                         String institutionId,
                                                         String startDate,
                                                         String endDate,
                                                         String dateProperty,
                                                         String creatorId,
                                                         String cancelerId,
                                                         HttpServletResponse httpServletResponse) {
        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(institutionId)) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }
            if (!StringUtils.isEmpty(creatorId)) {
                query.addCriteria(Criteria.where("creatorId").is(creatorId));
            }
            if (!StringUtils.isEmpty(cancelerId)) {
                query.addCriteria(Criteria.where("cancelerId").is(cancelerId));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, ScheduledMeeting.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                DateTime startDateTime = FORMATTER.parseDateTime(startDate);
                DateTime endDateTime = FORMATTER.parseDateTime(endDate);
                if (StringUtils.isEmpty(dateProperty)) {
                    dateProperty = "meetingDate";
                }
                query.addCriteria(Criteria.where(dateProperty).gte(startDateTime).lte(endDateTime));
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

            ArrayList<ScheduledMeeting> scheduledMeetings = (ArrayList<ScheduledMeeting>) mongoRepositoryReactive.findAll(query, ScheduledMeeting.class).toStream().collect(Collectors.toList());
            if (scheduledMeetings == null || scheduledMeetings.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<ScheduledMeetingDto> scheduledMeetingDtos = new ArrayList<>();
            scheduledMeetings.forEach(scheduledMeeting -> {
                scheduledMeetingDtos.add(scheduledMeeting.convertToDto());
            });
            return Mono.just(new ResponseEntity<>(scheduledMeetingDtos, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while finding scheduled meetings", e);
        }
    }

    @Override
    public Mono<ResponseEntity> createScheduledMeeting(ScheduledMeetingCreateDto scheduledMeetingCreateDto) {
        try {
            AuthInfo creator = getUser(scheduledMeetingCreateDto.getCreatorId());
            if (creator == null) {
                return Mono.just(new ResponseEntity<>("Creating user does not exist", HttpStatus.BAD_REQUEST));
            }
            ScheduledMeeting scheduledMeeting = fromCreateDto(scheduledMeetingCreateDto);
            saveScheduledMeeting(scheduledMeeting);
            return Mono.just(new ResponseEntity<>("Scheduled meeting created successfully", HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> cancelScheduledMeeting(String scheduledMeetingId, String cancelerId) {
        try {
            ScheduledMeeting scheduledMeeting = findScheduledMeetingById(scheduledMeetingId);
            if (scheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            AuthInfo canceler = getUser(cancelerId);
            if (canceler == null) {
                return Mono.just(new ResponseEntity<>("Canceling user does not exist", HttpStatus.BAD_REQUEST));
            }
            String canceledMeetingStatusId = "3";
            scheduledMeeting.setScheduledMeetingStatusId(canceledMeetingStatusId);
            scheduledMeeting.setCancelerId(cancelerId);
            saveScheduledMeeting(scheduledMeeting);
            return Mono.just(new ResponseEntity<>("Meeting canceled successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while canceling the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> completeScheduledMeeting(String scheduledMeetingId) {
        try {
            ScheduledMeeting scheduledMeeting = findScheduledMeetingById(scheduledMeetingId);
            if (scheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            String completedMeetingStatusId = "2";
            scheduledMeeting.setScheduledMeetingStatusId(completedMeetingStatusId);
            saveScheduledMeeting(scheduledMeeting);
            return Mono.just(new ResponseEntity<>("Meeting completed successfully", HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while completing the scheduled meeting", e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateScheduledMeting(ScheduledMeetingUpdateDto scheduledMeetingUpdateDto) {
        try {
            ScheduledMeeting existingScheduledMeeting = findScheduledMeetingById(scheduledMeetingUpdateDto.getId());
            if (existingScheduledMeeting == null) {
                return Mono.just(new ResponseEntity<>("Scheduled meeting does not exist", HttpStatus.BAD_REQUEST));
            }
            existingScheduledMeeting.setCreatorId(scheduledMeetingUpdateDto.getCreatorId());
            existingScheduledMeeting.setMeetingTitle(scheduledMeetingUpdateDto.getMeetingTitle());
            existingScheduledMeeting.setVenue(scheduledMeetingUpdateDto.getVenue());
            existingScheduledMeeting.setAdditionalNotes(scheduledMeetingUpdateDto.getAdditionalNotes());
            existingScheduledMeeting.setInstitutionId(scheduledMeetingUpdateDto.getInstitutionId());
            existingScheduledMeeting.setMeetingDate(FORMATTER.parseDateTime(scheduledMeetingUpdateDto.getMeetingDate()));
            return Mono.just(new ResponseEntity<>("Meeting updated successfully", HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format for meeting date , please use yyyy-MM-dd HH:mm:ss", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating the scheduled meeting", e);
        }
    }

    private ScheduledMeeting fromCreateDto(ScheduledMeetingCreateDto scheduledMeetingCreateDto) {
        ScheduledMeeting scheduledMeeting = new ScheduledMeeting();
        scheduledMeeting.setInstitutionId(scheduledMeetingCreateDto.getInstitutionId());
        scheduledMeeting.setId(UUID.randomUUID().toString());
        scheduledMeeting.setVenue(scheduledMeetingCreateDto.getVenue());
        scheduledMeeting.setMeetingTitle(scheduledMeetingCreateDto.getMeetingTitle());
        scheduledMeeting.setCreatorId(scheduledMeetingCreateDto.getCreatorId());
        String pendingScheduledMeetingStatusId = "1";
        scheduledMeeting.setScheduledMeetingStatusId(pendingScheduledMeetingStatusId);
        DateTime meetingDate = FORMATTER.parseDateTime(scheduledMeetingCreateDto.getMeetingDate());
        scheduledMeeting.setMeetingDate(meetingDate);
        return scheduledMeeting;
    }

    private void saveScheduledMeeting(ScheduledMeeting scheduledMeeting) {
        mongoRepositoryReactive.saveOrUpdate(scheduledMeeting);
    }

    private ScheduledMeeting findScheduledMeetingById(String scheduledMeetingId) {
        return (ScheduledMeeting) mongoRepositoryReactive.findById(scheduledMeetingId, ScheduledMeeting.class).block();
    }

    private AuthInfo getUser(String userId) {
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }
}
