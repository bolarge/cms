package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.CustomerComplain;
import com.software.finatech.lslb.cms.service.domain.CustomerComplainAction;
import com.software.finatech.lslb.cms.service.domain.CustomerComplainStatus;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.CustomerComplainService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.NumberUtil;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.CustomerComplaintEmailSenderAsync;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service
public class CustomerComplainServiceImpl implements CustomerComplainService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerComplainServiceImpl.class);

    private static final int MAX_DAYS_BEFORE_COMPLAIN_REMINDER = 7;
    private static final String customerComplainAuditActionId = AuditActionReferenceData.CUSTOMER_COMPLAIN;

    private MongoRepositoryReactiveImpl mongoRepositoryReactive;
    private CustomerComplaintEmailSenderAsync customerComplaintEmailSenderAsync;
    private AuditLogHelper auditLogHelper;
    private SpringSecurityAuditorAware springSecurityAuditorAware;

    @Autowired
    public CustomerComplainServiceImpl(MongoRepositoryReactiveImpl mongoRepositoryReactive,
                                       CustomerComplaintEmailSenderAsync customerComplaintEmailSenderAsync,
                                       AuditLogHelper auditLogHelper,
                                       SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.mongoRepositoryReactive = mongoRepositoryReactive;
        this.customerComplaintEmailSenderAsync = customerComplaintEmailSenderAsync;
        this.auditLogHelper = auditLogHelper;
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }

    @Override
    public Mono<ResponseEntity> findAllCustomerComplains(int page,
                                                         int pageSize,
                                                         String sortDirection,
                                                         String sortProperty,
                                                         String customerEmail,
                                                         String customerPhone,
                                                         String customerComplainStatusId,
                                                         String startDate,
                                                         String endDate,
                                                         String categoryId,
                                                         String typeId,
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
            if (!StringUtils.isEmpty(categoryId)) {
                query.addCriteria(Criteria.where("caseAndComplainCategoryId").is(categoryId));
            }
            if (!StringUtils.isEmpty(typeId)) {
                query.addCriteria(Criteria.where("caseAndComplainTypeId").is(typeId));
            }
            if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                LocalDate fromDate = new LocalDate(startDate);
                LocalDate toDate = new LocalDate(endDate).plusDays(1);
                query.addCriteria(Criteria.where("timeReported").gte(fromDate).lte(toDate));
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
        } catch (IllegalArgumentException e) {
            return Mono.just(new ResponseEntity<>("Invalid Date format , please use yyyy-MM-dd", HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            String errorMsg = "An error occurred while finding customer complaints";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public Mono<ResponseEntity> createCustomerComplain(CustomerComplainCreateDto customerComplainCreateDto, HttpServletRequest request) {
        try {
            CustomerComplain customerComplain = fromCreateCustomerComplain(customerComplainCreateDto);
            saveCustomerComplain(customerComplain);

            customerComplaintEmailSenderAsync.sendInitialNotificationsForCustomerComplain(customerComplain);

            String verbiage = String.format("Created customer complain -> Ticket Id : %s ", customerComplain.getTicketId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(customerComplainAuditActionId,
                    customerComplain.getCustomerFullName(), customerComplain.getCustomerEmailAddress(),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return Mono.just(new ResponseEntity<>(customerComplain.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while creating customer complain", e);
        }
    }

    @Override
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

    @Override
    public CustomerComplain findCustomerComplainById(String customerComplainId) {
        if (StringUtils.isEmpty(customerComplainId)) {
            return null;
        }
        return (CustomerComplain) mongoRepositoryReactive.findById(customerComplainId, CustomerComplain.class).block();
    }

    @Override
    public Mono<ResponseEntity> closeCustomerComplain(String customerComplainId, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(customerComplainId)) {
                return Mono.just(new ResponseEntity<>("Customer complain id  should not be empty", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            if (!canResolveCustomerComplain(user)) {
                return Mono.just(new ResponseEntity<>("User does not have permission to update customer complaints", HttpStatus.BAD_REQUEST));
            }
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
            customerComplainAction.setUserId(user.getId());
            existingCustomerComplain.setCustomerComplainStatusId(CustomerComplainStatusReferenceData.CLOSED_ID);
            existingCustomerComplain.getCustomerComplainActionList().add(customerComplainAction);
            mongoRepositoryReactive.saveOrUpdate(existingCustomerComplain);
            customerComplaintEmailSenderAsync.sendClosedCustomerComplaintToCustomer(existingCustomerComplain);

            String verbiage = String.format("Closed customer complain -> Ticket Id: %s ", existingCustomerComplain.getTicketId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(customerComplainAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));


            return Mono.just(new ResponseEntity<>(existingCustomerComplain.convertToFullDetailDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, String.format("An error occurred while resolving customer complain %s", customerComplainId), e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateCustomerComplainStatus(CustomerComplainUpdateDto customerComplainUpdateDto, HttpServletRequest request) {
        try {
            String customerComplainId = customerComplainUpdateDto.getCustomerComplainId();
            String customerComplainStatusId = customerComplainUpdateDto.getCustomerComplainStatusId();
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            if (!canResolveCustomerComplain(user)) {
                return Mono.just(new ResponseEntity<>("User does not have permission to update customer complaints", HttpStatus.BAD_REQUEST));
            }
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
            customerComplainAction.setComplainStatusId(customerComplainStatusId);
            customerComplainAction.setUserId(user.getId());
            existingCustomerComplain.setCustomerComplainStatusId(customerComplainStatusId);
            existingCustomerComplain.getCustomerComplainActionList().add(customerComplainAction);
            mongoRepositoryReactive.saveOrUpdate(existingCustomerComplain);
            if (customerComplainUpdateDto.isClosedUpdate()) {
                customerComplaintEmailSenderAsync.sendClosedCustomerComplaintToCustomer(existingCustomerComplain);
            }

            String verbiage = String.format("Updated Customer complain status -> Ticket Id: ->  %s ", existingCustomerComplain.getTicketId());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(customerComplainAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return Mono.just(new ResponseEntity<>(existingCustomerComplain.convertToFullDetailDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while changing complain status", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getAllCustomerComplainStatus() {
        return ReferenceDataUtil.getAllEnumeratedEntity("CustomerComplainStatus", CustomerComplainStatus.class);
    }

    @Override
    public Mono<ResponseEntity> beginCustomerComplainReview(CustomerComplainReviewRequest reviewRequest, HttpServletRequest request) {
        try {
            String complaintId = reviewRequest.getCustomerComplainId();
            CustomerComplain customerComplain = findCustomerComplainById(complaintId);
            if (customerComplain == null) {
                return Mono.just(new ResponseEntity<>(String.format("Customer complaint with id %s does not exist", complaintId), HttpStatus.BAD_REQUEST));
            }
            if (!customerComplain.isPending()) {
                return Mono.just(new ResponseEntity<>("Customer complaint is not pending", HttpStatus.BAD_REQUEST));
            }
            if ((!StringUtils.equals(CaseAndComplainTypeReferenceData.OTHERS_ID, reviewRequest.getTypeId())
                    && !StringUtils.isEmpty(reviewRequest.getOtherTypeName()))
                    ||
                    (!StringUtils.equals(CaseAndComplainCategoryReferenceData.OTHERS_ID, reviewRequest.getCategoryId())
                            && !StringUtils.isEmpty(reviewRequest.getOtherCategoryName()))) {
                return Mono.just(new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST));
            }
            customerComplain.setCustomerComplainStatusId(CustomerComplainStatusReferenceData.IN_REVIEW_ID);
            customerComplain.setCaseAndComplainCategoryId(reviewRequest.getCategoryId());
            customerComplain.setCaseAndComplainTypeId(reviewRequest.getTypeId());
            customerComplain.setOtherCategoryName(reviewRequest.getOtherCategoryName());
            customerComplain.setOtherTypeName(reviewRequest.getOtherTypeName());
            mongoRepositoryReactive.saveOrUpdate(customerComplain);

            String verbiage = String.format("Started Customer complain Review -> Ticket Id: ->  %s, Category -> %s , Type -> %s ", customerComplain.getTicketId(), customerComplain.getCaseAndComplainCategory(), customerComplain.getCaseAndComplainType());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(customerComplainAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return Mono.just(new ResponseEntity<>(customerComplain.convertToDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while starting  customer complain review", e);
        }
    }

    @Override
    public Mono<ResponseEntity> addCustomerComplaintComment(CustomerComplainCommentCreateDto customerComplainCommentCreateDto, HttpServletRequest request) {
        try {
            String complaintId = customerComplainCommentCreateDto.getComplaintId();
            CustomerComplain customerComplain = findCustomerComplainById(complaintId);
            if (customerComplain == null) {
                return Mono.just(new ResponseEntity<>(String.format("Customer complaint with is %s not found", complaintId), HttpStatus.BAD_REQUEST));
            }
            if (customerComplain.isClosed()) {
                return Mono.just(new ResponseEntity<>("The Customer complaint is already closed", HttpStatus.BAD_REQUEST));
            }
            AuthInfo user = springSecurityAuditorAware.getLoggedInUser();
            if (user == null) {
                return Mono.just(new ResponseEntity<>("Cannot find logged in user", HttpStatus.BAD_REQUEST));
            }
            if (!canResolveCustomerComplain(user)) {
                return Mono.just(new ResponseEntity<>("User cannot add comment to customer complaint,please check user role and user status", HttpStatus.BAD_REQUEST));
            }
            customerComplain.getComments().add(CommentDetail.fromCommentAndUser(customerComplainCommentCreateDto.getComment(), user.getFullName()));
            mongoRepositoryReactive.saveOrUpdate(customerComplain);

            String verbiage = String.format("Added Comment to Customer complaints, Ticket id: -> %s ,Comment -> \"%s\"",
                    customerComplain.getTicketId(), customerComplainCommentCreateDto.getComment());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(customerComplainAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), customerComplain.getCustomerEmailAddress(),
                    true,RequestAddressUtil.getClientIpAddr(request), verbiage));
            return Mono.just(new ResponseEntity<>(customerComplain.convertToFullDetailDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding comment to customer compalin", e);
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
        customerComplain.setTimeReported(LocalDateTime.now());
        customerComplain.setNextNotificationDateTime(LocalDateTime.now().plusDays(MAX_DAYS_BEFORE_COMPLAIN_REMINDER));
        customerComplain.setTimeOfIncident(customerComplainCreateDto.getTimeOfIncident());
        customerComplain.setDateOfIncident(customerComplainCreateDto.getDateOfIncident());
        customerComplain.setAddress(customerComplainCreateDto.getAddress());
        customerComplain.setStateOfResidence(customerComplainCreateDto.getStateOfResidence());
        customerComplain.setNameOfOperator(customerComplainCreateDto.getNameOfOperator());
        return customerComplain;
    }

    private void saveCustomerComplain(CustomerComplain customerComplain) {
        mongoRepositoryReactive.saveOrUpdate(customerComplain);
    }

    private String generateTicketId() {
        int randomInt = NumberUtil.getRandomNumberInRange(100, 3000);
        LocalDateTime dateTime = LocalDateTime.now();
        return String.format("LS-CMTK-%s%s%s%s", randomInt, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), dateTime.getSecondOfMinute());
    }

    private boolean canResolveCustomerComplain(AuthInfo user) {
        if (user == null) {
            return false;
        }
        return user.getEnabled() && getValidRoleIds().contains(user.getAuthRoleId());
    }

    private List<String> getValidRoleIds() {
        List<String> validRolesIds = LSLBAuthRoleReferenceData.getLslbRoles();
        validRolesIds.add(AuthRoleReferenceData.SUPER_ADMIN_ID);
        return validRolesIds;
    }
}