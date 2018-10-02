package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.CustomerComplain;
import com.software.finatech.lslb.cms.service.domain.CustomerComplainAction;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainCreateDto;
import com.software.finatech.lslb.cms.service.dto.CustomerComplainDto;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.CustomerComplainStatusReferenceData;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.CustomerComplainService;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.CustomerComplaintEmailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class CustomerComplainServiceImpl implements CustomerComplainService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerComplainServiceImpl.class);

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private AuthInfoService authInfoService;
    private CustomerComplaintEmailSenderAsync customerComplaintEmailSenderAsync;

    @Autowired
    public void setCustomerComplaintEmailSenderAsync(CustomerComplaintEmailSenderAsync customerComplaintEmailSenderAsync) {
        this.customerComplaintEmailSenderAsync = customerComplaintEmailSenderAsync;
    }

    @Autowired
    public void setAuthInfoService(AuthInfoService authInfoService) {
        this.authInfoService = authInfoService;
    }

    @Autowired
    public void setMongoRepositoryReactive(MongoRepositoryReactiveImpl mongoRepositoryReactive) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
    }

    // @Override
    public Mono<ResponseEntity> findAllApplicationForm(int page,
                                                       int pageSize,
                                                       String sortDirection,
                                                       String sortProperty,
                                                       String customerEmail,
                                                       String customerPhone,
                                                       String customerComplainStatusId,
                                                       HttpServletResponse httpServletResponse) {

        try {
            Query query = new Query();
            if (!StringUtils.isEmpty(customerEmail)) {
                query.addCriteria(Criteria.where("customerEmailAddress").regex("^" + customerEmail));
            }
            if (!StringUtils.isEmpty(customerPhone)) {
                query.addCriteria(Criteria.where("customerPhoneNumber").is(customerPhone));
            }
            if (!StringUtils.isEmpty(customerComplainStatusId)) {
                query.addCriteria(Criteria.where("customerComplainStatusId").is(customerComplainStatusId));
            }
            if (page == 0) {
                Long count = mongoRepositoryReactive.count(query, CustomerComplain.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

            Sort sort;
            if (!StringUtils.isEmpty(sortDirection) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, pageSize, sort));
            query.with(sort);
            ArrayList<CustomerComplain> customerComplainArrayList = (ArrayList<CustomerComplain>) mongoRepositoryReactive.findAll(query, CustomerComplain.class).toStream().collect(Collectors.toList());
            if (customerComplainArrayList == null || customerComplainArrayList.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<CustomerComplainDto> customerComplainDtos = new ArrayList<>();

            customerComplainArrayList.forEach(customerComplain -> {
                customerComplainDtos.add(customerComplain.convertToDto());
            });

            return Mono.just(new ResponseEntity<>(customerComplainDtos, HttpStatus.OK));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding customer complains";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    public Mono<ResponseEntity> getCustomerComplainFullDetail(String customerComplainId) {
        try {
            if (StringUtils.isEmpty(customerComplainId)) {
                return Mono.just(new ResponseEntity<>("Customer complain id should not be empty", HttpStatus.BAD_REQUEST));
            }

            CustomerComplain customerComplain = findCustomerComplainById(customerComplainId);
            if (customerComplain == null) {
                return Mono.just(new ResponseEntity<>(String.format("No customer complain with id %s found", customerComplainId), HttpStatus.NOT_FOUND));
            }
            return Mono.just(new ResponseEntity<>(customerComplain.convertToFullDetailDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while getting customer complain with id %s dull detail", customerComplainId), e);
        }
    }


    public Mono<ResponseEntity> resolveCustomerComplain(String userId, String customerComplainId) {
        try {
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(customerComplainId)) {
                return Mono.just(new ResponseEntity<>("User id and customer complain id  should not be empty", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = authInfoService.getUserById(userId);
            if (user == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s does not exist", userId), HttpStatus.BAD_REQUEST));
            }
            //TODO: VALIDATE USER ROLE
            CustomerComplain existingCustomerComplain = findCustomerComplainById(customerComplainId);
            if (existingCustomerComplain == null) {
                return Mono.just(new ResponseEntity<>(String.format("Customer complain with id %s not found", customerComplainId), HttpStatus.BAD_REQUEST));
            }
            String existingCustomerComplainStatusId = existingCustomerComplain.getCustomerComplainStatusId();
            if (StringUtils.equals(CustomerComplainStatusReferenceData.RESOLVED_ID, existingCustomerComplainStatusId)) {
                return Mono.just(new ResponseEntity<>("The customer complain is already resolved by another user", HttpStatus.BAD_REQUEST));
            }
            if (StringUtils.equals(CustomerComplainStatusReferenceData.CLOSED_ID, existingCustomerComplainStatusId)) {
                return Mono.just(new ResponseEntity<>("The customer complain is already closed", HttpStatus.BAD_REQUEST));
            }
            CustomerComplainAction customerComplainAction = new CustomerComplainAction();
            customerComplainAction.setActionTime(LocalDateTime.now());
            customerComplainAction.setComplainStatusId(CustomerComplainStatusReferenceData.RESOLVED_ID);
            customerComplainAction.setUserId(userId);
            existingCustomerComplain.setCustomerComplainStatusId(CustomerComplainStatusReferenceData.RESOLVED_ID);
            List<CustomerComplainAction> existingCustomerComplainActions = existingCustomerComplain.getCustomerComplainActionList();
            existingCustomerComplainActions.add(customerComplainAction);
            existingCustomerComplain.setCustomerComplainActionList(existingCustomerComplainActions);
            mongoRepositoryReactive.saveOrUpdate(existingCustomerComplain);
            return Mono.just(new ResponseEntity<>(existingCustomerComplain.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while resolving customer complain %s with user id %s", customerComplainId, userId), e);
        }
    }


    public Mono<ResponseEntity> closeCustomerComplain(String userId, String customerComplainId) {
        try {
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(customerComplainId)) {
                return Mono.just(new ResponseEntity<>("User id and customer complain id  should not be empty", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = authInfoService.getUserById(userId);
            if (user == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s does not exist", userId), HttpStatus.BAD_REQUEST));
            }
            //TODO: VALIDATE USER ROLE
            CustomerComplain existingCustomerComplain = findCustomerComplainById(customerComplainId);
            if (existingCustomerComplain == null) {
                return Mono.just(new ResponseEntity<>(String.format("Customer complain with id %s not found", customerComplainId), HttpStatus.BAD_REQUEST));
            }
            String existingCustomerComplainStatusId = existingCustomerComplain.getCustomerComplainStatusId();
            if (StringUtils.equals(CustomerComplainStatusReferenceData.CLOSED_ID, existingCustomerComplainStatusId)) {
                return Mono.just(new ResponseEntity<>("The customer complain is already closed", HttpStatus.BAD_REQUEST));
            }
            CustomerComplainAction customerComplainAction = new CustomerComplainAction();
            customerComplainAction.setActionTime(LocalDateTime.now());
            customerComplainAction.setComplainStatusId(CustomerComplainStatusReferenceData.CLOSED_ID);
            customerComplainAction.setUserId(userId);
            existingCustomerComplain.setCustomerComplainStatusId(CustomerComplainStatusReferenceData.CLOSED_ID);
            List<CustomerComplainAction> existingCustomerComplainActions = existingCustomerComplain.getCustomerComplainActionList();
            existingCustomerComplainActions.add(customerComplainAction);
            existingCustomerComplain.setCustomerComplainActionList(existingCustomerComplainActions);
            mongoRepositoryReactive.saveOrUpdate(existingCustomerComplain);
            return Mono.just(new ResponseEntity<>(existingCustomerComplain.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while resolving customer complain %s with user id %s", customerComplainId, userId), e);
        }
    }


    public Mono<ResponseEntity> createCustomerComplain(CustomerComplainCreateDto customerComplainCreateDto) {
        try {
            CustomerComplain customerComplain = fromCreateCustomerComplain(customerComplainCreateDto);
            saveCustomerComplain(customerComplain);

            return Mono.just(new ResponseEntity<>(customerComplain.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating customer complain", e);
        }
    }

    private CustomerComplain fromCreateCustomerComplain(CustomerComplainCreateDto customerComplainCreateDto) {
        CustomerComplain customerComplain = new CustomerComplain();
        customerComplain.setId(UUID.randomUUID().toString());
        customerComplain.setCustomerComplainStatusId(CustomerComplainStatusReferenceData.PENDING_ID);
        customerComplain.setComplainDetails(customerComplainCreateDto.getComplainDetail());
        customerComplain.setCustomerEmailAddress(customerComplainCreateDto.getEmailAddress());
        customerComplain.setComplainSubject(customerComplainCreateDto.getComplainSubject());
        customerComplain.setCustomerFullName(customerComplainCreateDto.getFullName());
        customerComplain.setCustomerPhoneNumber(customerComplainCreateDto.getPhoneNumber());
        customerComplain.setTicketId(generateTicketId());
        return customerComplain;
    }

    public void saveCustomerComplain(CustomerComplain customerComplain) {
        mongoRepositoryReactive.saveOrUpdate(customerComplain);
    }


    public String generateTicketId() {
        int randomInt = NumberUtil.getRandomNumberInRange(3000, 300000000);
        return String.format("LSLB-CM-%s", randomInt);
    }

    public CustomerComplain findCustomerComplainById(String customerComplainId) {
        if (StringUtils.isEmpty(customerComplainId)) {
            return null;
        }
        return (CustomerComplain) mongoRepositoryReactive.findById(customerComplainId, CustomerComplain.class).block();
    }
}