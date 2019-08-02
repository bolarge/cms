package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.CommentDetail;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainActionDto;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainDto;
import com.software.finatech.lslb.cms.service.referencedata.CustomerComplainStatusReferenceData;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.StringCapitalizer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Document(collection = "CustomerComplains")
public class CustomerComplain extends AbstractFact {
    private String customerFullName;
    private String customerPhoneNumber;
    private String customerEmailAddress;
    private String complainSubject;
    private String complainDetails;
    private String customerComplainStatusId;
    private String ticketId;
    private LocalDateTime timeReported;
    private String nameOfOperator;
    private String dateOfIncident;
    private String address;
    private String timeOfIncident;
    private String stateOfResidence;
    private List<CustomerComplainAction> customerComplainActionList = new ArrayList<>();
    private LocalDateTime nextNotificationDateTime;
    private String caseAndComplainCategoryId;
    private String caseAndComplainTypeId;
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

    public String getOtherCategoryName() {
        return otherCategoryName;
    }

    public List<CommentDetail> getComments() {
        return comments;
    }

    public void setComments(List<CommentDetail> comments) {
        this.comments = comments;
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

    public String getCaseAndComplainCategoryId() {
        return caseAndComplainCategoryId;
    }

    public void setCaseAndComplainCategoryId(String caseAndComplainCategoryId) {
        this.caseAndComplainCategoryId = caseAndComplainCategoryId;
    }

    public String getCaseAndComplainTypeId() {
        return caseAndComplainTypeId;
    }

    public void setCaseAndComplainTypeId(String caseAndComplainTypeId) {
        this.caseAndComplainTypeId = caseAndComplainTypeId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimeOfIncident() {
        return timeOfIncident;
    }

    public void setTimeOfIncident(String timeOfIncident) {
        this.timeOfIncident = timeOfIncident;
    }

    public String getStateOfResidence() {
        return stateOfResidence;
    }

    public void setStateOfResidence(String stateOfResidence) {
        this.stateOfResidence = stateOfResidence;
    }

    public LocalDateTime getTimeReported() {
        return timeReported;
    }

    public void setTimeReported(LocalDateTime timeReported) {
        this.timeReported = timeReported;
    }

    public LocalDateTime getNextNotificationDateTime() {
        return nextNotificationDateTime;
    }

    public void setNextNotificationDateTime(LocalDateTime nextNotificationDateTime) {
        this.nextNotificationDateTime = nextNotificationDateTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
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

    public List<CustomerComplainAction> getCustomerComplainActionList() {
        return customerComplainActionList;
    }

    public void setCustomerComplainActionList(List<CustomerComplainAction> customerComplainActionList) {
        this.customerComplainActionList = customerComplainActionList;
    }

    public CustomerComplainStatus getCustomerComplainStatus(String customerComplainStatusId) {
        if (StringUtils.isEmpty(customerComplainStatusId)) {
            return null;
        }
        Map customerComplainStatusMap = Mapstore.STORE.get("CustomerComplainStatus");
        CustomerComplainStatus customerComplainStatus = null;
        if (customerComplainStatusMap != null) {
            customerComplainStatus = (CustomerComplainStatus) customerComplainStatusMap.get(customerComplainStatusId);
        }
        if (customerComplainStatus == null) {
            customerComplainStatus = (CustomerComplainStatus) mongoRepositoryReactive.findById(customerComplainStatusId, CustomerComplainStatus.class).block();
            if (customerComplainStatus != null && customerComplainStatusMap != null) {
                customerComplainStatusMap.put(customerComplainStatusId, customerComplainStatus);
            }
        }
        return customerComplainStatus;
    }

    public CustomerComplainDto convertToDto() {
        CustomerComplainDto dto = new CustomerComplainDto();
        dto.setId(getId());
        dto.setCustomerFullName(getCustomerFullName());
        dto.setCustomerEmail(getCustomerEmailAddress());
        dto.setCustomerPhone(getCustomerPhoneNumber());
        dto.setComplainSubject(getComplainSubject());
        dto.setNameOfOperator(getNameOfOperator());
        dto.setTicketId(getTicketId());
        LocalDateTime timeReported = getTimeReported();
        if (timeReported != null) {
            dto.setTimeReported(timeReported.toString("dd-MM-yyyy HH:mm a"));
        }
        CustomerComplainStatus customerComplainStatus = getCustomerComplainStatus(getCustomerComplainStatusId());
        if (customerComplainStatus != null) {
            dto.setCustomerComplainStatusId(getCustomerComplainStatusId());
            dto.setCustomerComplainStatusName(customerComplainStatus.getName());
        }
        CaseAndComplainType type = getCaseAndComplainType();
        if (type != null) {
            dto.setType(String.valueOf(type));
            dto.setTypeId(type.getId());
        }
        CaseAndComplainCategory category = getCaseAndComplainCategory();
        if (category != null) {
            dto.setCategory(String.valueOf(category));
            dto.setCategoryId(category.getId());
        }
        dto.setOtherCategoryName(getOtherCategoryName());
        dto.setOtherTypeName(getOtherTypeName());
        return dto;
    }

    public CustomerComplainDto convertToFullDetailDto() {
        CustomerComplainDto dto = convertToDto();
        dto.setComplainDetails(getComplainDetails());
        dto.setCustomerComplainActions(getCustomerComplainActions());
        dto.setStateOfResidence(getStateOfResidence());
        dto.setTimeOfIncident(getTimeOfIncident());
        dto.setDateOfIncident(getDateOfIncident());
        dto.setStateOfResidence(getStateOfResidence());
        dto.setAddress(getAddress());
        dto.setComments(getComments());
        dto.setLoggedCaseId(getLoggedCaseId());
        return dto;
    }


    private List<CustomerComplainActionDto> getCustomerComplainActions() {
        List<CustomerComplainActionDto> complainActions = new ArrayList<>();
        for (CustomerComplainAction customerComplainAction : getCustomerComplainActionList()) {
            AuthInfo user = getUser(customerComplainAction.getUserId());
            CustomerComplainStatus customerComplainStatus = getCustomerComplainStatus(customerComplainAction.getComplainStatusId());
            LocalDateTime actionDateTime = customerComplainAction.getActionTime();
            if (user != null && customerComplainStatus != null && actionDateTime != null) {
                String actionString = String.format("%s moved this to %s at %s", user.getFullName(),
                        customerComplainStatus.getName(), actionDateTime.toString("dd-MM-yyyy HH:mm:ss"));
                actionString = StringCapitalizer.convertToTitleCaseIteratingChars(actionString);
                CustomerComplainActionDto actionDto = new CustomerComplainActionDto();
                actionDto.setActionString(actionString);
                actionDto.setUserFullName(user.getFullName());
                actionDto.setComplainStatus(customerComplainStatus.getName());
                actionDto.setDatePerformed(actionDateTime.toString("dd-MM-yyyy HH:mm:ss"));
                complainActions.add(actionDto);
            }
        }
        return complainActions;
    }

    public CaseAndComplainCategory getCaseAndComplainCategory() {
        if (StringUtils.isEmpty(this.caseAndComplainCategoryId)) {
            return null;
        }
        CaseAndComplainCategory category = null;
        Map<String, FactObject> categoryMap = Mapstore.STORE.get("CaseAndComplainCategory");
        if (categoryMap != null) {
            category = (CaseAndComplainCategory) categoryMap.get(this.caseAndComplainCategoryId);
        }
        if (category == null) {
            category = (CaseAndComplainCategory) mongoRepositoryReactive.findById(this.caseAndComplainCategoryId, CaseAndComplainCategory.class).block();
            if (category != null && categoryMap != null) {
                categoryMap.put(this.caseAndComplainCategoryId, category);
            }
        }
        return category;
    }

    public CaseAndComplainType getCaseAndComplainType() {
        if (StringUtils.isEmpty(this.caseAndComplainTypeId)) {
            return null;
        }
        CaseAndComplainType type = null;
        Map<String, FactObject> typeMap = Mapstore.STORE.get("CaseAndComplainType");
        if (typeMap != null) {
            type = (CaseAndComplainType) typeMap.get(this.caseAndComplainTypeId);
        }
        if (type == null) {
            type = (CaseAndComplainType) mongoRepositoryReactive.findById(this.caseAndComplainTypeId, CaseAndComplainType.class).block();
            if (type != null && typeMap != null) {
                typeMap.put(this.caseAndComplainTypeId, type);
            }
        }
        return type;
    }

    private AuthInfo getUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    public boolean isPending() {
        return StringUtils.equals(CustomerComplainStatusReferenceData.PENDING_ID, this.customerComplainStatusId);
    }

    public boolean isClosed() {
        return StringUtils.equals(CustomerComplainStatusReferenceData.CLOSED_ID, this.customerComplainStatusId);
    }

    @Override
    public String getFactName() {
        return "CustomerComplains";
    }
}
