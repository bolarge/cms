package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class CustomerComplainReviewRequest {
    @NotEmpty(message = "Please provide customer complain id")
    private String customerComplainId;
    @NotEmpty(message = "Please provide category id")
    private String categoryId;
    @NotEmpty(message = "Please provide type id")
    private String typeId;

    public String getCustomerComplainId() {
        return customerComplainId;
    }

    public void setCustomerComplainId(String customerComplainId) {
        this.customerComplainId = customerComplainId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
