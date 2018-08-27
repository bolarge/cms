package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.RenewalFormAnswerDto;
import com.software.finatech.lslb.cms.service.dto.RenewalFormQuestionDto;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Document(collection = "RenewalFormAnswer")
public class RenewalFormAnswer extends AbstractFact{
    protected String questionId;
    protected String answer;
    protected String paymentRecordId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
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
    public RenewalFormAnswerDto convertToDto(){
        RenewalFormAnswerDto renewalFormAnswerDto = new RenewalFormAnswerDto();
        Query queryRenewalQuestion = new Query();
        queryRenewalQuestion.addCriteria(Criteria.where("questionId").is(getQuestionId()));
        RenewalFormQuestion renewalFormQuestion= (RenewalFormQuestion) mongoRepositoryReactive.find(queryRenewalQuestion, RenewalFormQuestion.class).block();
        renewalFormAnswerDto.setRenewalFormQuestion(renewalFormQuestion);
        renewalFormAnswerDto.setPaymentRecordId(getPaymentRecordId());
        return renewalFormAnswerDto;
    }

    @Override
    public String getFactName() {
        return "RenewalFormAnswer";
    }
}
