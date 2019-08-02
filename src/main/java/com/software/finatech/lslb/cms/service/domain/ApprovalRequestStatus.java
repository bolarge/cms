package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ApprovalRequestStatuses")
public class ApprovalRequestStatus extends EnumeratedFact {
}
