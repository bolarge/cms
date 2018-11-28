package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class CasePenaltyParams {
    @NotEmpty
    private String clause;
    @NotEmpty
    private String relevantSection;
    @NotEmpty
    private String amount;

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public String getRelevantSection() {
        return relevantSection;
    }

    public void setRelevantSection(String relevantSection) {
        this.relevantSection = relevantSection;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
