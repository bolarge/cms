package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.CustomerComplainAction;

import java.util.ArrayList;
import java.util.List;

public class CustomerComplainDto {
    private String id;
    private String customerFullName;
    private String customerEmail;
    private String customerPhone;
    private String complainSubject;
    private String complainDetails;
    private String customerComplainStatusId;
    private String customerComplainStatusName;
    private List<CustomerComplainActionDto> customerComplainActions;
    private String ticketId;
    private String timeReported;
    private String dateOfIncident;
    private String timeOfIncident;
    private String address;
    private String stateOfResidence;
    private String nameOfOperator;
    private String typeId;
    private String type;
    private String categoryId;
    private String category;
    private String otherCategoryName;
    private String otherTypeName;
    private List<CommentDetail> comments = new ArrayList<>();
    private String loggedCaseId;

    public String getLoggedCaseId() {
        return loggedCaseId;
    }

    public void setLoggedCaseId(String loggedCaseId) {
        this.loggedCaseId = loggedCaseId;
    }

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
    }

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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNameOfOperator() {
        return nameOfOperator;
    }

    public void setNameOfOperator(String nameOfOperator) {
        this.nameOfOperator = nameOfOperator;
    }

    public String getDateOfIncident() {
        return dateOfIncident;
    }

    public void setDateOfIncident(String dateOfIncident) {
        this.dateOfIncident = dateOfIncident;
    }

    public String getTimeOfIncident() {
        return timeOfIncident;
    }

    public void setTimeOfIncident(String timeOfIncident) {
        this.timeOfIncident = timeOfIncident;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStateOfResidence() {
        return stateOfResidence;
    }

    public void setStateOfResidence(String stateOfResidence) {
        this.stateOfResidence = stateOfResidence;
    }

    public String getTimeReported() {
        return timeReported;
    }

    public void setTimeReported(String timeReported) {
        this.timeReported = timeReported;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getComplainSubject() {
        return complainSubject;
    }

    public void setComplainSubject(String complainSubject) {
        this.complainSubject = complainSubject;
    }

    public String getComplainDetails() {
        return complainDetails;
    }

    public void setComplainDetails(String complainDetails) {
        this.complainDetails = complainDetails;
    }

    public String getCustomerComplainStatusId() {
        return customerComplainStatusId;
    }

    public void setCustomerComplainStatusId(String customerComplainStatusId) {
        this.customerComplainStatusId = customerComplainStatusId;
    }

    public String getCustomerComplainStatusName() {
        return customerComplainStatusName;
    }

    public void setCustomerComplainStatusName(String customerComplainStatusName) {
        this.customerComplainStatusName = customerComplainStatusName;
    }

    public List<CustomerComplainActionDto> getCustomerComplainActions() {
        return customerComplainActions;
    }

    public void setCustomerComplainActions(List<CustomerComplainActionDto> customerComplainActions) {
        this.customerComplainActions = customerComplainActions;
    }
}
