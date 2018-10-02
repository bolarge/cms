package com.software.finatech.lslb.cms.service.dto;

import com.software.finatech.lslb.cms.service.domain.CustomerComplainAction;

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
