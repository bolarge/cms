package com.software.finatech.lslb.cms.service.domain;


import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "ScheduledMeetingStatus")
public class ScheduledMeetingStatus extends EnumeratedFact {
}
