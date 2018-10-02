package com.software.finatech.lslb.cms.service.controller;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.VerificationToken;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOPasswordResetModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOToken;
import com.software.finatech.lslb.cms.service.dto.sso.SSOUserConfirmResetPasswordRequest;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.service.AuthInfoServiceImpl;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Api(value = "AuthInfo", description = "", tags = "")
@RestController
@RequestMapping("/api/v1/authInfo")
public class AuthInfoController extends BaseController {
    @Autowired
    private AuthInfoServiceImpl authInfoService;
    @Autowired
    private MailContentBuilderService mailContentBuilderService;

    private static Logger logger = LoggerFactory.getLogger(AuthInfoController.class);

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

            String token = authInfoService.resetPasswordToken(emailAddress);

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
            return authInfoService.resetPassword(model);
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

            return authInfoService.changePassword(token, ssoChangePasswordModel);
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
                return Mono.just(new ResponseEntity("Invalid Username/Password", HttpStatus.UNAUTHORIZED));
            }

            try {
                authInfo.setAssociatedProperties();
            } catch (FactNotFoundException e) {
                e.printStackTrace();
            }

            if (authInfo.getEnabled() != true) {
                return Mono.just(new ResponseEntity("User Deactivated", HttpStatus.UNAUTHORIZED));
            }

            return authInfoService.loginToken(loginDto.getUserName(), loginDto.getPassword(), authInfo);

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
                return Mono.just(new ResponseEntity("Email already registered", HttpStatus.BAD_REQUEST));
            }
            /*String appUrl =
                    "http://" + request.getServerName() +
                            ":" + request.getServerPort() +
                            request.getContextPath();*/
            String appUrl = appHostPort + request.getContextPath();
            return authInfoService.createAuthInfo(authInfoCreateDto, appUrl);
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

            AuthInfo authInfo = authInfoService.createApplicantAuthInfo(createGameOperatorAuthInfoDto, appUrl);
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
                return Mono.just(new ResponseEntity("Invalid EmailAddress", HttpStatus.BAD_REQUEST));
            }

            authInfo.setEnabled(false);
            mongoRepositoryReactive.saveOrUpdate(authInfo);

            return Mono.just(new ResponseEntity("Success", HttpStatus.OK));

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
                return Mono.just(new ResponseEntity("Invalid EmailAddress", HttpStatus.BAD_REQUEST));
            }

            authInfo.setEnabled(true);
            mongoRepositoryReactive.saveOrUpdate(authInfo);

            return Mono.just(new ResponseEntity("Success", HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return All users full information
     */
    @RequestMapping(method = RequestMethod.GET, value = "/all", params = {"institutionId", "page", "size", "sorting", "sortProperty"})
    @ApiOperation(value = "Get Authinfos", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> authInfos(@Param("institutionId") String institutionId, @Param("page") @NotNull int page, @Param("size") @NotNull int size, @Param("sortProperty") String sortProperty, @Param("sorting") String sorting) {
        try {
            //@TODO validate reqquest params
            Query query = new Query();
            if (institutionId != null && !institutionId.isEmpty()) {
                query.addCriteria(Criteria.where("institutionId").is(institutionId));
            }

            if (sorting != null && !sorting.isEmpty() && sortProperty != null && !sortProperty.isEmpty()) {
                query.with(new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortProperty));
            }

            query.with(new Pageable() {
                @Override
                public int getPageNumber() {
                    return page;
                }

                @Override
                public int getPageSize() {
                    return size;
                }

                @Override
                public long getOffset() {
                    return 0;
                }

                @Override
                public Sort getSort() {
                    if (sorting != null && !sorting.isEmpty() && sortProperty != null && !sortProperty.isEmpty()) {
                        return new Sort((sorting.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortProperty);
                    } else {
                        return new Sort(Sort.Direction.ASC, "id");
                    }
                }

                @Override
                public Pageable next() {
                    return null;
                }

                @Override
                public Pageable previousOrFirst() {
                    return null;
                }

                @Override
                public Pageable first() {
                    return null;
                }

                @Override
                public boolean hasPrevious() {
                    return false;
                }
            });


            ArrayList<AuthInfo> authInfos = (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());

            ArrayList<AuthInfoDto> authInfoDto = new ArrayList<>();
            authInfos.forEach(entry -> {
                try {
                    entry.setAssociatedProperties();
                } catch (FactNotFoundException e) {
                    e.printStackTrace();
                }
                authInfoDto.add(entry.convertToDto());
            });

            if (authInfoDto.size() == 0) {
                return Mono.just(new ResponseEntity("No record found", HttpStatus.NOT_FOUND));
            }

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

            List<String> gamingOperatorRoles = authInfoService.getAllGamingOperatorAdminAndUserRoles();
            if (!StringUtils.equals(authInfoUpdateDto.getAuthRoleId(), authInfo.getAuthRoleId())
                    && !StringUtils.isEmpty(authInfo.getInstitutionId())
                    && gamingOperatorRoles.contains(authInfoUpdateDto.getAuthRoleId())) {
                int maxNumberOfGamingOperatorUsers = 3;
                List<AuthInfo> authInfoListWithGamingOperatorLimitedRoles = authInfoService.getAllActiveGamingOperatorAdminsAndUsersForInstitution(authInfo.getInstitutionId());
                if (authInfoListWithGamingOperatorLimitedRoles.size() >= maxNumberOfGamingOperatorUsers) {
                    return Mono.just(new ResponseEntity<>("Number of users for gaming operator exceeded", HttpStatus.BAD_REQUEST));
                }
            }

            if (authInfoUpdateDto.getAuthRoleId() != null && !authInfoUpdateDto.getAuthRoleId().isEmpty()) {
                authInfo.setAuthRoleId(authInfoUpdateDto.getAuthRoleId());
            } else if (authInfoUpdateDto.getFirstName() != null && !authInfoUpdateDto.getFirstName().isEmpty()) {
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


    @RequestMapping(method = RequestMethod.GET, value = "/add-permission-to-user")
    @ApiOperation(value = "Add permissions to user", response = AuthInfoDto.class, consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "You are not authorized access the resource"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not Found")
    }
    )
    public Mono<ResponseEntity> addPermissionsToUser(@RequestBody UserAuthPermissionDto userAuthPermissionDto){
        return authInfoService.addPermissionsToUser(userAuthPermissionDto);
    }

    public String getAppHostPort() {
        return this.appHostPort;
    }
}
