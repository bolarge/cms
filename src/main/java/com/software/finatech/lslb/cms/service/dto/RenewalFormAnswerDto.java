package com.software.finatech.lslb.cms.service.dto;

public class RenewalFormAnswerDto {

    protected RenewalFormQuestion renewalFormQuestion;
    protected String answer;
    protected String paymentRecordId;
    protected String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public RenewalFormQuestion getRenewalFormQuestion() {
        return renewalFormQuestion;
    }

    public void setRenewalFormQuestion(RenewalFormQuestion renewalFormQuestion) {
        this.renewalFormQuestion = renewalFormQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(String paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

}
