package com.software.finatech.lslb.cms.service.dto;

import javax.validation.constraints.NotEmpty;

public class CustomerComplainReviewRequest {
    @NotEmpty(message = "Please provide customer complain id")
    private String customerComplainId;
    @NotEmpty(message = "Please provide category id")
    private String categoryId;
    @NotEmpty(message = "Please provide type id")
    private String typeId;
    private String otherCategoryName;
    private String otherTypeName;

    public String getOtherCategoryName() {
        return otherCategoryName;
    }

    public void setOtherCategoryName(String otherCategoryName) {
        this.otherCategoryName = otherCategoryName;
    }

    public String getOtherTypeName() {
        return otherTypeName;
    }

    public void setOtherTypeName(String otherTypeName) {
        this.otherTypeName = otherTypeName;
    }

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
