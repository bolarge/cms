package com.software.finatech.lslb.cms.service.domain;


import com.software.finatech.lslb.cms.service.dto.CustomerComplainActionDto;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainDto;
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
    private List<CustomerComplainAction> customerComplainActionList;
    private LocalDateTime nextNotificationDateTime;


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
        dto.setTicketId(getTicketId());
        LocalDateTime timeReported= getTimeReported();
        if(timeReported != null){
            dto.setTimeReported(timeReported.toString("dd-MM-yyyy HH:mm:ss"));
        }
        CustomerComplainStatus customerComplainStatus = getCustomerComplainStatus(getCustomerComplainStatusId());
        if (customerComplainStatus != null) {
            dto.setCustomerComplainStatusId(getCustomerComplainStatusId());
            dto.setCustomerComplainStatusName(customerComplainStatus.getName());
        }
        return dto;
    }

    public CustomerComplainDto convertToFullDetailDto() {
        CustomerComplainDto dto = convertToDto();
        dto.setComplainDetails(getComplainDetails());
        dto.setCustomerComplainActions(getCustomerComplainActions());
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

    private AuthInfo getUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    @Override
    public String getFactName() {
        return "CustomerComplains";
    }
}
