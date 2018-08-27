package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.RenewalFormQuestionDto;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "RenewalFormQuestion")
public class RenewalFormQuestion extends AbstractFact{
    protected String questionId;
    protected String question;
    protected String gameTypeId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }
public RenewalFormQuestionDto convertToDto(){

        RenewalFormQuestionDto renewalFormQuestionDto = new RenewalFormQuestionDto();
        renewalFormQuestionDto.setId(getId());
        renewalFormQuestionDto.setGameTypeId(getGameTypeId());
        renewalFormQuestionDto.setQuestion(getQuestion());
        return renewalFormQuestionDto;

}
    @Override
    public String getFactName() {
        return "RenewalFormQuestion";
    }
}
