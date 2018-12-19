package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.*;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOPasswordResetModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOToken;
import com.software.finatech.lslb.cms.service.dto.sso.SSOUserConfirmResetPasswordRequest;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.AuthInfoServiceImpl;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Api(value = "AuthInfo", description = "", tags = "AuthInfo Controller")
@RestController
@RequestMapping("/api/v1/authInfo")
public class AuthInfoController extends BaseController {
    @Autowired
    private AuthInfoServiceImpl authInfoService;
    @Autowired
    private MailContentBuilderService mailContentBuilderService;
    @Autowired
    private AuditLogHelper auditLogHelper;
    private static Logger logger = LoggerFactory.getLogger(AuthInfoController.class);

    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    @Autowired
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;

    private static final String LOGIN = AuditActionReferenceData.LOGIN_ID;

    /**
     * @param id AuthInfo id
     * @return AuthInfo full information
     */
    @ApiOperation(value = "Get AuthInfo By Id", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Mono<ResponseEntity> getById(@PathVariable String id) {
        AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(id, AuthInfo.class).block();
        if (authInfo == null) {
            return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
        }

        return Mono.just(new ResponseEntity(authInfo.convertToDto(), HttpStatus.OK));
    }

    /**
     * @param token
     * @param userId
     * @return
     */
    @RequestMapping(value = "/confirmPasswordReset", method = RequestMethod.GET, produces = "application/json", params = {"token", "userId"})
    @ResponseBody
    @ApiOperation(value = "Get token from SSO", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> confirmPasswordReset(@Param("token") @NotEmpty String token, @Param("userId") @NotEmpty String userId) {
        try {
            SSOUserConfirmResetPasswordRequest ssoUserConfirmRequest = new SSOUserConfirmResetPasswordRequest();
            ssoUserConfirmRequest.setToken(token);
            ssoUserConfirmRequest.setUserId(userId);

            return Mono.just(new ResponseEntity(ssoUserConfirmRequest, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * @param emailAddress
     * @param request
     * @return
     */
    @RequestMapping(value = "/resetpasswordvalidation", method = RequestMethod.GET, produces = "application/json", params = {"emailAddress"})
    @ResponseBody
    @ApiOperation(value = "Reset token from SSO", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> resetPasswordToken(@Param("emailAddress") String emailAddress, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("emailAddress").is(emailAddress)), AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity("Invalid EmailAddress", HttpStatus.BAD_REQUEST));
            }

            if (authInfo.getSsoUserId() == null || authInfo.getSsoUserId().isEmpty()) {
                return Mono.just(new ResponseEntity("User has not been created on SSO", HttpStatus.BAD_REQUEST));
            }

            String token = authInfoService.resetPasswordToken(emailAddress, request);

            if (token == null) {
                return Mono.just(new ResponseEntity("Invalid EmailAddress", HttpStatus.BAD_REQUEST));
            } else {
                authInfo.setPasswordResetToken(token);
                /*String appUrl =
                        "http://" + request.getServerName() +
                                ":" + request.getServerPort() +
                                request.getContextPath();*/
                String appUrl = appHostPort + request.getContextPath();

                HashMap<String, Object> model = new HashMap<>();
                model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
                model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
                String url = appUrl + "/authInfo/confirmPasswordReset?userId=" + authInfo.getSsoUserId();
                model.put("CallbackUrl", url);
                String content = mailContentBuilderService.build(model, "PasswordResetConfirmation");
                content = content.replaceAll("CallbackUrl", url);
                emailService.sendEmail(content, "Password Reset", authInfo.getEmailAddress());

                mongoRepositoryReactive.saveOrUpdate(authInfo);

                return Mono.just(new ResponseEntity("Success", HttpStatus.OK));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Reset password on SSO", response = String.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> resetPassword(@Valid @RequestBody SSOPasswordResetModel model, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("ssoUserId").is(model.getUserId())), AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Invalid User", HttpStatus.BAD_REQUEST));
            }
            model.setToken(authInfo.getPasswordResetToken());
            return authInfoService.resetPassword(model, request, authInfo);
        } catch (Exception e) {
            String errorMsg = "An error occurred while resetting user password";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    /**
     * @param ssoChangePasswordModel
     * @param request
     * @return
     */
    @RequestMapping(value = "/changepassword", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Change password on SSO", response = SSOChangePasswordModel.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> changePassword(@Valid @RequestBody SSOChangePasswordModel ssoChangePasswordModel, HttpServletRequest request) {
        try {

            String token = tokenExtractor.extract(request.getHeader("Authorization"));
            //@TODO validate if the above is not null.
            if (token == null) {
                return Mono.just(new ResponseEntity("Invalid Username/Password", HttpStatus.BAD_REQUEST));
            }

            return authInfoService.changePassword(token, ssoChangePasswordModel, request);
        } catch (Exception e) {
            String errorMsg = "An error occurred while changing user password";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    /**
     * @param loginDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Login to SSO", response = SSOToken.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> login(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {
        try {


            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("emailAddress").is(loginDto.getUserName())), AuthInfo.class).block();


            if (authInfo == null) {
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(LOGIN, loginDto.getUserName(), null, LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), "Unsuccessful Login Attempt -> User not found"));
                return Mono.just(new ResponseEntity("Invalid Username/Password", HttpStatus.UNAUTHORIZED));
            }
            if (authInfo.isInactive() == true) {
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(LOGIN, authInfo.getFullName(), null, LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), "Unsuccessful Login Attempt -> User Inactive"));
                return Mono.just(new ResponseEntity(authInfo.getInactiveReason(), HttpStatus.UNAUTHORIZED));
            }

            if (authInfo.getEnabled() != true) {
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(LOGIN, authInfo.getFullName(), null, LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), "Unsuccessful Login Attempt -> User Deactivated"));
                return Mono.just(new ResponseEntity("User Deactivated", HttpStatus.UNAUTHORIZED));
            }


            return authInfoService.loginToken(loginDto.getUserName(), loginDto.getPassword(), authInfo, request);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param authInfoCreateDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Creates a new User ", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> createAuthInfo(@Valid @RequestBody AuthInfoCreateDto authInfoCreateDto, HttpServletRequest request) {
        try {
            // Lookup AuthInfo in database by e-mail
            AuthInfo authInfoExists = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("emailAddress").is(authInfoCreateDto.getEmailAddress())), AuthInfo.class).block();
            if (authInfoExists != null) {
                return Mono.just(new ResponseEntity<>("Email already registered", HttpStatus.BAD_REQUEST));
            }

            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.BAD_REQUEST));
            }

            if (loggedInUser.isGamingOperator()) {
                String appUrl = appHostPort + request.getContextPath();
                return authInfoService.createAuthInfo(authInfoCreateDto, appUrl, request);
            /*String appUrl =
                    "http://" + request.getServerName() +
                            ":" + request.getServerPort() +
                            request.getContextPath();*/

            } else {
                if (!StringUtils.isEmpty(authInfoCreateDto.getInstitutionId()) && !authInfoCreateDto.isCreateGamingOperatorUser()) {
                    return Mono.just(new ResponseEntity<>("You can only create a gaming operator user for an operator", HttpStatus.BAD_REQUEST));
                }
                if (StringUtils.isEmpty(authInfoCreateDto.getInstitutionId()) && authInfoCreateDto.isCreateGamingOperatorUser()) {
                    return Mono.just(new ResponseEntity<>("Please provide operator attached to the gaming operator", HttpStatus.BAD_REQUEST));
                }

                PendingAuthInfo pendingAuthInfo = new PendingAuthInfo();
                pendingAuthInfo.setId(UUID.randomUUID().toString());
                pendingAuthInfo.setFirstName(authInfoCreateDto.getFirstName());
                pendingAuthInfo.setLastName(authInfoCreateDto.getLastName());
                pendingAuthInfo.setInstitutionId(authInfoCreateDto.getInstitutionId());
                pendingAuthInfo.setAgentId(authInfoCreateDto.getAgentId());
                pendingAuthInfo.setEmailAddress(authInfoCreateDto.getEmailAddress());
                pendingAuthInfo.setTitle(authInfoCreateDto.getTitle());
                pendingAuthInfo.setPhoneNumber(authInfoCreateDto.getPhoneNumber());
                pendingAuthInfo.setAuthRoleId(authInfoCreateDto.getAuthRoleId());
                mongoRepositoryReactive.saveOrUpdate(pendingAuthInfo);

                UserApprovalRequest userApprovalRequest = new UserApprovalRequest();
                userApprovalRequest.setId(UUID.randomUUID().toString());
                userApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
                userApprovalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.CREATE_USER_ID);
                userApprovalRequest.setInitiatorId(loggedInUser.getId());
                userApprovalRequest.setPendingAuthInfoId(pendingAuthInfo.getId());
                userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
                approvalRequestNotifierAsync.sendNewUserApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, userApprovalRequest);
                mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
                return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping(value = "/new-applicant-user", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Creates a new gaming operator user", response = AuthInfoDto.class, consumes = "application/json",
            notes = "The endpoint is not secured because it is meant to be accessed from outside the login")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> createGamingOperatorAuthInfo(@Valid @RequestBody CreateApplicantAuthInfoDto createGameOperatorAuthInfoDto, HttpServletRequest request) {
        try {
            // Lookup AuthInfo in database by e-mail
            AuthInfo authInfoExists = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("emailAddress").is(createGameOperatorAuthInfoDto.getEmailAddress())), AuthInfo.class).block();
            if (authInfoExists != null) {
                return Mono.just(new ResponseEntity("Email already registered", HttpStatus.BAD_REQUEST));
            }

            /*String appUrl =
                    "http://" + request.getServerName() +
                            ":" + request.getServerPort() +
                            request.getContextPath();*/

            String appUrl = appHostPort + request.getContextPath();

            AuthInfo authInfo = authInfoService.createApplicantAuthInfo(createGameOperatorAuthInfoDto, appUrl, request);
            return Mono.just(new ResponseEntity<>(authInfo.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while creating user", e);
        }
    }

    /**
     * @param token
     * @return
     */
    @RequestMapping(value = "/confirm", method = RequestMethod.GET, produces = "application/json", params = {"token"})
    @ResponseBody
    @ApiOperation(value = "Confirm a user", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> confirm(@RequestParam("token") String token) {

        VerificationToken verificationToken = (VerificationToken) mongoRepositoryReactive.find(new Query(Criteria.where("confirmationToken").is(token)), VerificationToken.class).block();
        if (verificationToken == null) {
            return Mono.just(new ResponseEntity("InvalidToken", HttpStatus.BAD_REQUEST));
        }

        if (verificationToken.getExpiryDate().isBeforeNow()) {
            verificationToken.setActivated(false);
            verificationToken.setExpired(true);
            mongoRepositoryReactive.saveOrUpdate(verificationToken);

            return Mono.just(new ResponseEntity("Expired Token", HttpStatus.MOVED_PERMANENTLY));
        }

        if (verificationToken.getExpired() == true) {
            return Mono.just(new ResponseEntity("Expired Token", HttpStatus.BAD_REQUEST));
        }

        return Mono.just(new ResponseEntity("Confirmed Token", HttpStatus.OK));
    }

    /**
     * @param authInfoCompleteDto
     * @return
     */
    @RequestMapping(value = "/complete", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Complete an authInfo registration", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> completeAuthInfo(@Valid @RequestBody AuthInfoCompleteDto authInfoCompleteDto) {
        try {
            //@TODO please validate if the token has been confirmed
            VerificationToken verificationToken = (VerificationToken) mongoRepositoryReactive.find(new Query(Criteria.where("confirmationToken").is(authInfoCompleteDto.getToken())), VerificationToken.class).block();
            if (verificationToken == null) {
                return Mono.just(new ResponseEntity("InvalidToken", HttpStatus.BAD_REQUEST));
            }

            if (verificationToken.getActivated() == true) {
                return Mono.just(new ResponseEntity("Token already activated", HttpStatus.MOVED_PERMANENTLY));
            }

            if (verificationToken.getExpired() == true) {
                return Mono.just(new ResponseEntity("Expired Token", HttpStatus.BAD_REQUEST));
            }


            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(verificationToken.getAuthInfoId(), AuthInfo.class).block();
            //authInfo.setOldFact(authInfo);
            authInfo.setEnabled(true);

            return authInfoService.completeRegistration(verificationToken, authInfoCompleteDto, authInfo);
        } catch (Exception e) {
            String errorMsg = "An error occurred while completing user registration";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    /**
     * @param emailAddress
     * @param request
     * @return
     */
    @RequestMapping(value = "/deactivate", method = RequestMethod.GET, produces = "application/json", params = {"emailAddress"})
    @ResponseBody
    @ApiOperation(value = "Deactivate a user", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> deactivateUser(@Param("emailAddress") String emailAddress, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("emailAddress").is(emailAddress)), AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Invalid EmailAddress", HttpStatus.BAD_REQUEST));
            }
            if (!authInfo.getEnabled()) {
                return Mono.just(new ResponseEntity<>("User already deactivated", HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            if (loggedInUser.isGamingOperator()) {
                authInfo.setEnabled(false);
                mongoRepositoryReactive.saveOrUpdate(authInfo);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), authInfo.getFullName(), LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), String.format("Deactivated user  %s", authInfo.getFullName())));
                return Mono.just(new ResponseEntity("Success", HttpStatus.OK));
            } else {
                UserApprovalRequest userApprovalRequest = new UserApprovalRequest();
                userApprovalRequest.setId(UUID.randomUUID().toString());
                userApprovalRequest.setAuthInfoId(authInfo.getId());
                userApprovalRequest.setInitiatorId(loggedInUser.getId());
                userApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
                userApprovalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.DEACTIVATE_USER_ID);
                userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
                mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
                approvalRequestNotifierAsync.sendNewUserApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, userApprovalRequest);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), authInfo.getFullName(), LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), String.format("Created user approval request to disable user %s", authInfo.getFullName())));
                return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param emailAddress
     * @param request
     * @return
     */
    @RequestMapping(value = "/activate", method = RequestMethod.GET, produces = "application/json", params = {"emailAddress"})
    @ResponseBody
    @ApiOperation(value = "Activate user", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    //@PreAuthorize("hasAuthority('WRITE')")
    public Mono<ResponseEntity> activateUser(@Param("emailAddress") String emailAddress, HttpServletRequest request) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("emailAddress").is(emailAddress)), AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Invalid EmailAddress", HttpStatus.BAD_REQUEST));
            }
            if (authInfo.getEnabled()) {
                return Mono.just(new ResponseEntity<>("User already activated", HttpStatus.BAD_REQUEST));
            }
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            if (loggedInUser.isGamingOperator()) {
                authInfo.setEnabled(true);
                mongoRepositoryReactive.saveOrUpdate(authInfo);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), authInfo.getFullName(), LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), String.format("Activated user %s", authInfo.getFullName())));
                return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
            } else {
                UserApprovalRequest userApprovalRequest = new UserApprovalRequest();
                userApprovalRequest.setId(UUID.randomUUID().toString());
                userApprovalRequest.setAuthInfoId(authInfo.getId());
                userApprovalRequest.setInitiatorId(loggedInUser.getId());
                userApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
                userApprovalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.ACTIVATE_USER_ID);
                userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
                approvalRequestNotifierAsync.sendNewUserApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, userApprovalRequest);
                mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), authInfo.getFullName(), LocalDateTime.now(), LocalDate.now(), true, request.getRemoteAddr(), String.format("Created user approval request to activate user %s", authInfo.getFullName())));
                return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return All users full information
     */
    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"institutionId", "page", "size", "sorting", "sortProperty", "roleId"})
    @ApiOperation(value = "Get Authinfos", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> authInfos(@Param("institutionId") String institutionId,
                                          @Param("page") @NotNull int page,
                                          @Param("size") @NotNull int size,
                                          @Param("sortProperty") String sortProperty,
                                          @Param("sorting") String sorting,
                                          @Param("roleId") String roleId,
                                          @RequestParam(value = "keyword", required = false) String keyword,
                                          HttpServletResponse httpServletResponse) {
        try {
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            Query query = new Query();
            if (institutionId != null && !institutionId.isEmpty()) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }

            if (StringUtils.isEmpty(roleId)) {
                List<String> notAllowedRoleIds = AuthRoleReferenceData.getNotAllowedRoleIds();
                if (!notAllowedRoleIds.contains(loggedInUser.getAuthRoleId())) {
                    query.addCriteria(Criteria.where("authRoleId").nin(notAllowedRoleIds));
                }
            } else {
                query.addCriteria(Criteria.where("authRoleId").is(roleId));
            }
            if (!StringUtils.isEmpty(keyword)) {
                query.addCriteria(new Criteria().orOperator(Criteria.where("fullName").regex(keyword, "i"),
                        Criteria.where("emailAddress").regex(keyword, "i"),
                        Criteria.where("phoneNumber").regex(keyword, "i")));
            }
            if (page == 0) {
                long count = mongoRepositoryReactive.count(query, AuthInfo.class).block();
                httpServletResponse.setHeader("TotalCount", String.valueOf(count));
            }

//            if (sorting != null && !sorting.isEmpty() && sortProperty != null && !sortProperty.isEmpty()) {
//                query.with(new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortProperty));
//            }
//
//            query.with(new Pageable() {
//                @Override
//                public int getPageNumber() {
//                    return page;
//                }
//
//                @Override
//                public int getPageSize() {
//                    return size;
//                }
//
//                @Override
//                public long getOffset() {
//                    return 0;
//                }
//
//                @Override
//                public Sort getSort() {
//                    if (sorting != null && !sorting.isEmpty() && sortProperty != null && !sortProperty.isEmpty()) {
//                        return new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortProperty);
//                    } else {
//                        return new Sort(Sort.Direction.DESC, "createdAt");
//                    }
//                }
//
//                @Override
//                public Pageable next() {
//                    return null;
//                }
//
//                @Override
//                public Pageable previousOrFirst() {
//                    return null;
//                }
//
//                @Override
//                public Pageable first() {
//                    return null;
//                }
//
//                @Override
//                public boolean hasPrevious() {
//                    return false;
//                }
//            });


            Sort sort;
            if (!StringUtils.isEmpty(sorting) && !StringUtils.isEmpty(sortProperty)) {
                sort = new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC),
                        sortProperty);
            } else {
                sort = new Sort(Sort.Direction.DESC, "createdAt");
            }
            query.with(PageRequest.of(page, size, sort));
            query.with(sort);

            ArrayList<AuthInfo> authInfos = (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
            if (authInfos.size() == 0) {
                return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AuthInfoDto> authInfoDto = new ArrayList<>();
            authInfos.forEach(entry -> {
                authInfoDto.add(entry.convertToDto());
            });

            return Mono.just(new ResponseEntity(authInfoDto, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param authInfoUpdateDto
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Update an authInfo", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> updateAuthInfo(@Valid @RequestBody AuthInfoUpdateDto authInfoUpdateDto) {
        try {
            AuthInfo authInfo = (AuthInfo) mongoRepositoryReactive.findById(authInfoUpdateDto.getId(), AuthInfo.class).block();
            if (authInfo == null) {
                return Mono.just(new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST));
            }

            if (authInfoUpdateDto.getFirstName() != null && !authInfoUpdateDto.getFirstName().isEmpty()) {
                authInfo.setFirstName(authInfoUpdateDto.getFirstName());
            } else if (authInfoUpdateDto.getLastName() != null && !authInfoUpdateDto.getLastName().isEmpty()) {
                authInfo.setLastName(authInfoUpdateDto.getLastName());
            } else if (authInfoUpdateDto.getPhoneNumber() != null && !authInfoUpdateDto.getPhoneNumber().isEmpty()) {
                authInfo.setPhoneNumber(authInfoUpdateDto.getPhoneNumber());
            }
            mongoRepositoryReactive.saveOrUpdate(authInfo);
            return Mono.just(new ResponseEntity(authInfo.convertToDto(), HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @RequestMapping(value = "/update-role", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Update an authInfo role", response = UserApprovalRequestDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> updateUserRole(@Valid @RequestBody UserRoleUpdateDto userRoleUpdateDto, HttpServletRequest request) {
        return authInfoService.updateUserRole(userRoleUpdateDto, request);
    }


    @RequestMapping(value = "/resendToken", method = RequestMethod.GET, produces = "application/json", params = {"token"})
    @ResponseBody
    @ApiOperation(value = "ResendToken for user", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request")
    }
    )
    public Mono<ResponseEntity> resendRegistrationToken(HttpServletRequest request, @RequestParam("token") @NotEmpty String existingToken) {

        VerificationToken expiredVerificationToken = (VerificationToken) mongoRepositoryReactive.find(new Query(Criteria.where("confirmationToken").is(existingToken)), VerificationToken.class).block();
        if (expiredVerificationToken == null) {
            return Mono.just(new ResponseEntity("InvalidToken", HttpStatus.BAD_REQUEST));
        }

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setId(UUID.randomUUID().toString());
        verificationToken.setActivated(false);
        //@TODO encode to base64
        verificationToken.setConfirmationToken(UUID.randomUUID().toString());
        verificationToken.setExpired(false);
        verificationToken.setAuthInfoId(expiredVerificationToken.getAuthInfoId());
        verificationToken.setExpiryDate(DateTime.now().plusHours(24));


        AuthInfo AuthInfo = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("id").is(verificationToken.getAuthInfoId())), AuthInfo.class).block();
        if (AuthInfo.getSsoUserId() != null) {
            return Mono.just(new ResponseEntity("Registration Already Completed", HttpStatus.BAD_REQUEST));
        }
        String appUrl =
                appHostPort +
                        request.getContextPath();

        HashMap<String, Object> model = new HashMap<>();
        model.put("name", AuthInfo.getFirstName() + " " + AuthInfo.getLastName());
        model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
        String url = appUrl + "/authInfo/confirm?token=" + verificationToken.getConfirmationToken();
        model.put("CallbackUrl", url);
        String content = mailContentBuilderService.build(model, "ContinueRegistrationEmail");
        content = content.replaceAll("CallbackUrl", url);
        emailService.sendEmail(content, "Registration Confirmation", AuthInfo.getEmailAddress());


        mongoRepositoryReactive.saveOrUpdate(verificationToken);

        return Mono.just(new ResponseEntity("Token Resent", HttpStatus.CREATED));
    }


    @RequestMapping(method = RequestMethod.POST, value = "/add-permission-to-user")
    @ApiOperation(value = "Add permissions to user", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> addPermissionsToUser(@RequestBody UserAuthPermissionDto userAuthPermissionDto, HttpServletRequest request) {
        return authInfoService.addPermissionsToUser(userAuthPermissionDto, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/remove-permission-from-user")
    @ApiOperation(value = "Remove permissions from user", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> removePermissionsFromUser(@RequestBody UserAuthPermissionDto userAuthPermissionDto, HttpServletRequest request) {
        return authInfoService.removePermissionFromUser(userAuthPermissionDto, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-user-full-detail", params = {"userId"})
    @ApiOperation(value = "Get User full details", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> getUserFullDetails(@RequestParam("userId") String id) {
        return authInfoService.getUserFullDetail(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/new-permissions-for-user")
    @ApiOperation(value = "Get permissions you can add to user", response = AuthPermissionDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAuthPermissionsToAddForUser(@RequestParam("userId") String userId) {
        try {
            AuthInfo loggedInUser = authInfoService.getUserById(userId);
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s not found", userId), HttpStatus.BAD_REQUEST));
            }
            Map<String, FactObject> authPermissionMap = Mapstore.STORE.get("AuthPermission");
            Collection<FactObject> factObjects = authPermissionMap.values();
            ArrayList<AuthPermission> permissions = new ArrayList<>();
            for (FactObject factObject : factObjects) {
                AuthPermission permission = (AuthPermission) factObject;
                if (((StringUtils.equals(loggedInUser.getAuthRoleId(), permission.getAuthRoleId()) || permission.isUsedBySystem())) &&
                        !loggedInUser.getAllUserPermissionIdsForUser().contains(permission.getId())) {
                    permissions.add(permission);
                }
            }
            if (permissions.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No Record Found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AuthPermissionDto> dtos = new ArrayList<>();
            for (AuthPermission permission : permissions) {
                dtos.add(permission.convertToDto());
            }
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while getting permissions for roles", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all-active-lslb-members")
    @ApiOperation(value = "Get permissions you can add to user", response = AuthPermissionDto.class, responseContainer = "List", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")})
    public Mono<ResponseEntity> getAllActiveLSLBMembers() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("authRoleId").in(LSLBAuthRoleReferenceData.getLslbRoles()));
            query.addCriteria(Criteria.where("enabled").is(true));
            ArrayList<AuthInfo> authInfos = (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
            if (authInfos == null || authInfos.isEmpty()) {
                return Mono.just(new ResponseEntity<>("No record found", HttpStatus.NOT_FOUND));
            }
            ArrayList<AuthInfoDto> dtos = new ArrayList<>();
            for (AuthInfo authInfo : authInfos) {
                AuthInfoDto dto = new AuthInfoDto();
                dto.setId(authInfo.getId());
                dto.setFullName(authInfo.getFullName());
                dtos.add(dto);
            }
            return Mono.just(new ResponseEntity<>(dtos, HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while getting permissions for roles", e);
        }
    }
}
