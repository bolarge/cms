package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("serial")
@Document(collection = "PaymentStatus")
public class PaymentStatus extends EnumeratedFact {
}
