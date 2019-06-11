package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author adeyi.adebolu
 * created on 11/06/2019
 */

@Document(collection = "VigipayPaymentNotification")
public class VigipayPaymentNotification extends EnumeratedFact {
    private String request;
    private String response;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}