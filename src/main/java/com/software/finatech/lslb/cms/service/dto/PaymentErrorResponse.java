package com.software.finatech.lslb.cms.service.dto;

/**
 * @author adeyi.adebolu
 * created on 20/06/2019
 */
public class PaymentErrorResponse {
    private String errorMessage;
    private String exceptionMessage;

    public PaymentErrorResponse() {
    }

    public PaymentErrorResponse(String errorMessage, String exceptionMessage) {
        this.errorMessage = errorMessage;
        this.exceptionMessage = exceptionMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
