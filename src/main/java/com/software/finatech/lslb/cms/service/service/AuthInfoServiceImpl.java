package com.software.finatech.lslb.cms.service.service;

import com.software.finatech.jjwt.JwtHeaderTokenExtractor;
import com.software.finatech.lslb.cms.service.config.SpringSecurityAuditorAware;
import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.AuthRole;
import com.software.finatech.lslb.cms.service.domain.UserApprovalRequest;
import com.software.finatech.lslb.cms.service.domain.VerificationToken;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.dto.sso.*;
import com.software.finatech.lslb.cms.service.exception.ApprovalRequestProcessException;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.referencedata.*;
import com.software.finatech.lslb.cms.service.service.contracts.AuthInfoService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthPermissionService;
import com.software.finatech.lslb.cms.service.service.contracts.AuthRoleService;
import com.software.finatech.lslb.cms.service.util.AuditTrailUtil;
import com.software.finatech.lslb.cms.service.util.ErrorResponseUtil;
import com.software.finatech.lslb.cms.service.util.FrontEndPropertyHelper;
import com.software.finatech.lslb.cms.service.util.RequestAddressUtil;
import com.software.finatech.lslb.cms.service.util.async_helpers.AuditLogHelper;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.ApprovalRequestNotifierAsync;
import com.software.finatech.lslb.cms.service.util.async_helpers.mail_senders.NewUserEmailNotifierAsync;
import io.advantageous.boon.json.JsonFactory;
import io.advantageous.boon.json.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.software.finatech.lslb.cms.service.util.ErrorResponseUtil.logAndReturnError;

@Service("authInfoService")
public class AuthInfoServiceImpl implements AuthInfoService {

    @Value("${sso.baseIdentityURL}")
    private String baseIdentityURL;
    @Value("${sso.baseAPIURL}")
    private String baseAPIURL;
    @Value("${sso.apiUsername}")
    private String apiUsername;
    @Value("${sso.apiPassword}")
    private String apiPassword;
    @Value("${sso.clientId}")
    private String clientId;
    @Value("${sso.clientSecret}")
    private String clientSecret;
    @Value("${sso.baseLogoutURL}")
    private String baseLogoutURL;


    private static final String DEFAULT_PASSWORD = "Password@12";

    @Autowired
    private MailContentBuilderService mailContentBuilderService;
    @Autowired
    private NewUserEmailNotifierAsync newUserEmailNotifierAsync;

    @Autowired
    private JwtHeaderTokenExtractor tokenExtractor;

    @Autowired
    private AuthPermissionService authPermissionService;
    @Autowired
    private AuditLogHelper auditLogHelper;
    @Autowired
    private SpringSecurityAuditorAware springSecurityAuditorAware;
    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private ApprovalRequestNotifierAsync approvalRequestNotifierAsync;

    @Autowired
    private EmailService emailService;
    @Autowired
    private FrontEndPropertyHelper frontEndPropertyHelper;
    private static Logger logger = LoggerFactory.getLogger(AuthInfoServiceImpl.class);
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
    protected ObjectMapper mapper;

    private static final String userAuditActionId = AuditActionReferenceData.USER_ID;

    /**
     * Initialize class
     */
    @PostConstruct
    public void initialize() {
        mapper = JsonFactory.createUseAnnotations(true);
    }

    @Override
    public Mono<ResponseEntity> createAuthInfo(AuthInfoCreateDto authInfoCreateDto, String appUrl, HttpServletRequest request) throws ApprovalRequestProcessException {
        String requestIpAddress = RequestAddressUtil.getClientIpAddr(request);

        if (!StringUtils.isEmpty(authInfoCreateDto.getInstitutionId())) {
            Mono<ResponseEntity> validateCreateAuthInfo = validateCreateGamingOperatorAuthInfo(authInfoCreateDto);
            if (validateCreateAuthInfo != null) {
                return validateCreateAuthInfo;
            }
        }

        AuthInfo authInfo = new AuthInfo();
        authInfo.setId(UUID.randomUUID().toString());
        authInfo.setEnabled(false);
        authInfo.setAuthRoleId(authInfoCreateDto.getAuthRoleId());
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress(authInfoCreateDto.getEmailAddress());
        authInfo.setPhoneNumber(authInfoCreateDto.getPhoneNumber());
        authInfo.setFirstName(authInfoCreateDto.getFirstName());
        authInfo.setLastName(authInfoCreateDto.getLastName());
        authInfo.setTitle(authInfoCreateDto.getTitle());
        authInfo.setFullName(authInfoCreateDto.getFirstName() + " " + authInfoCreateDto.getLastName());
        authInfo.setInstitutionId(authInfoCreateDto.getInstitutionId());
        authInfo.setAgentId(authInfoCreateDto.getAgentId());
        mongoRepositoryReactive.saveOrUpdate(authInfo);

        // First we check if User exists
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            HttpGet httpGet = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;
            boolean userExists = false;
            SSOUserDetailInfo userDetail = null;
            HashMap<String, Object> model = new HashMap<>();

            String apiToken = tokenExtractor.extract(request.getHeader("Authorization"));
            url = baseAPIURL + "/account/getuserbyemail?email=" + authInfo.getEmailAddress();
            httpGet = new HttpGet(url);
            httpGet.addHeader("Authorization", "Bearer " + apiToken);
            httpGet.addHeader("client-id", clientId);
            response = httpclient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            SSOUserDetail SSOUserDetail = null;

            if (responseCode == 200) {
                // everything is fine, handle the response
                String stringResponse = EntityUtils.toString(response.getEntity());
                SSOUserDetail = mapper.readValue(stringResponse, SSOUserDetail.class);
                if (SSOUserDetail.getData().size() > 0) {
                    userExists = true;
                }
            } else {
                throw new ApprovalRequestProcessException("Unable to check if user exist on SSO");
            }

            // user exist so we add claims
            if (userExists) {
                return createExistingSSOUser(SSOUserDetail, authInfo, apiToken, requestIpAddress);
            } else {
                createNewUserOnSSO(authInfo, request);
                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setId(UUID.randomUUID().toString());
                verificationToken.setActivated(false);
                // @TODO encode to base64
                verificationToken.setConfirmationToken(UUID.randomUUID().toString());
                verificationToken.setExpired(false);
                verificationToken.setAuthInfoId(authInfo.getId());
                verificationToken.setExpiryDate(DateTime.now().plusHours(24));
                verificationToken.setForResourceOwnerUserCreation(true);
                mongoRepositoryReactive.saveOrUpdate(verificationToken);

                // HashMap<String, Object> model = new HashMap<>();
                model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
                model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
                url = appUrl + "/authInfo/confirm?token=" + verificationToken.getConfirmationToken();
                model.put("CallbackUrl", url);
                model.put("isApplicant", false);
                model.put("isAgent", authInfo.isAgent());
                String content = mailContentBuilderService.build(model, "continueRegistration-new");
                content = content.replaceAll("CallbackUrl", url);
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());

                String verbiage = String.format("Create user  -> Name : %s ", authInfo.getFullName());
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                        springSecurityAuditorAware.getCurrentAuditorNotNull(), authInfo.getFullName(),
                        LocalDateTime.now(), LocalDate.now(), true, requestIpAddress, verbiage));
                return Mono.just(new ResponseEntity<>(toCreateAuthInfoResponse(authInfo, verificationToken), HttpStatus.OK));
            }
        } catch (Exception e) {
            if (e instanceof ApprovalRequestProcessException) {
                throw new ApprovalRequestProcessException(e.getMessage());
            }
            String errorMsg = "An error occurred when trying to create user";
            return logAndReturnError(logger, errorMsg, e);
        }
    }


    @Override
    public AuthInfo createApplicantAuthInfo(CreateApplicantAuthInfoDto createApplicantAuthInfoDto, String appUrl, HttpServletRequest request) {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setId(UUID.randomUUID().toString());
        authInfo.setEnabled(true);
        authInfo.setAuthRoleId(LSLBAuthRoleReferenceData.APPLICANT_ROLE_ID);
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress(createApplicantAuthInfoDto.getEmailAddress());
        authInfo.setPhoneNumber(createApplicantAuthInfoDto.getPhoneNumber());
        authInfo.setFirstName(createApplicantAuthInfoDto.getFirstName());
        authInfo.setLastName(createApplicantAuthInfoDto.getLastName());
        authInfo.setTitle(createApplicantAuthInfoDto.getTitle());
        authInfo.setFullName(createApplicantAuthInfoDto.getFirstName() + " " + createApplicantAuthInfoDto.getLastName());

        // First we check if User exists
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            HttpGet httpGet = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;
            boolean userExists = false;
            SSOUserDetailInfo userDetail = null;
            HashMap<String, Object> model = new HashMap<>();

            String apiToken = getAPIToken();
            url = baseAPIURL + "/account/getuserbyemail?email=" + authInfo.getEmailAddress();
            httpGet = new HttpGet(url);
            httpGet.addHeader("Authorization", "Bearer " + apiToken);
            httpGet.addHeader("client-id", clientId);
            response = httpclient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            SSOUserDetail SSOUserDetail = null;
            switch (responseCode) {
                case 200: {
                    // everything is fine, handle the response
                    String stringResponse = EntityUtils.toString(response.getEntity());
                    SSOUserDetail = mapper.readValue(stringResponse, SSOUserDetail.class);
                    if (SSOUserDetail.getData().size() > 0) {
                        userExists = true;
                    }
                }
            }

            // user exist so we add claims
            if (userExists) {
                String content;
                SSOUserDetailInfo ssoUserDetailInfo = SSOUserDetail.getData().get(0);

                AuthRole authRole = authRoleService.findRoleById(authInfo.getAuthRoleId());
                if (authRole == null) {
                    return null;
                }
                userDetail = SSOUserDetail.getData().get(0);
                authInfo.setSsoUserId(userDetail.getId());

                SSOClaim applicationClaim = new SSOClaim();
                applicationClaim.setType("application");
                applicationClaim.setValue("lslb-cms");

                SSOClaim roleClaim1 = new SSOClaim();
                roleClaim1.setType("role");
                roleClaim1.setValue(authRole.getSsoRoleMapping());

                SSOClaim roleClaim2 = new SSOClaim();
                roleClaim2.setType("role");
                roleClaim2.setValue(authRole.getName());
                SSOUserAddClaim ssoUserAddClaim = new SSOUserAddClaim();
                ssoUserAddClaim.setUserId(userDetail.getId());
                ssoUserAddClaim.getClaims().add(applicationClaim);
                ssoUserAddClaim.getClaims().add(roleClaim1);
                ssoUserAddClaim.getClaims().add(roleClaim2);

                //Add claims
                url = baseAPIURL + "/account/addclaims";
                httpPost = new HttpPost(url);
                //final com.fasterxml.jackson.databind.ObjectMapper mapperJson = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.toJson(ssoUserAddClaim);
                httpPost.setEntity(new StringEntity(json));
                httpPost.addHeader("Authorization", "Bearer " + apiToken);
                httpPost.addHeader("client-id", clientId);
                httpPost.addHeader("Content-Type", "application/json");

                response = httpclient.execute(httpPost);
                responseCode = response.getStatusLine().getStatusCode();
                String stringResponse = EntityUtils.toString(response.getEntity());

                if (responseCode != 200) {
                    return null;
                }

                model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
                model.put("frontEndUrl", frontEndPropertyHelper.getFrontEndUrl());
                content = mailContentBuilderService.build(model, "ExistingUserRegistrationEmail");
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());
                authInfo.setEnabled(true);
                if (ssoUserDetailInfo != null) {
                    authInfo.setSsoUserId(ssoUserDetailInfo.getId());
                }
                mongoRepositoryReactive.saveOrUpdate(authInfo);

                String verbiage = String.format("Create user  -> Name : %s , Id -> %s ", authInfo.getFullName(), authInfo.getId());
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                        springSecurityAuditorAware.getCurrentAuditorNotNull(), authInfo.getFullName(),
                        LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));
                return authInfo;
            } else {

                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setId(UUID.randomUUID().toString());
                verificationToken.setActivated(false);
                // @TODO encode to base64
                verificationToken.setConfirmationToken(UUID.randomUUID().toString());
                verificationToken.setExpired(false);
                verificationToken.setAuthInfoId(authInfo.getId());
                verificationToken.setExpiryDate(DateTime.now().plusHours(24));
                mongoRepositoryReactive.saveOrUpdate(verificationToken);

                // HashMap<String, Object> model = new HashMap<>();
                model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
                model.put("date", LocalDate.now().toString("dd-MM-YYYY"));
                url = appUrl + "/authInfo/confirm?token=" + verificationToken.getConfirmationToken();
                model.put("CallbackUrl", url);
                model.put("isApplicant", true);
                String content = mailContentBuilderService.build(model, "continueRegistration-new");
                content = content.replaceAll("CallbackUrl", url);
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());
                mongoRepositoryReactive.saveOrUpdate(authInfo);
            }

            String verbiage = String.format("Create user  -> Name : %s ", authInfo.getFullName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), authInfo.getFullName(),
                    LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return authInfo;
        } catch (Exception e) {
            logger.error("An error occurred when trying to confirm if user exists", e);
            return null;
        }
    }


    @Override
    public Mono<String> updateAuthInfo() {
        return null;
    }

    @Override
    public String resetPasswordToken(String emailAddress, HttpServletRequest request) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;

            String apiToken = getAPIToken();
            url = baseAPIURL + "/account/forgotpassword?email=" + emailAddress;
            httpGet = new HttpGet(url);

            httpGet.addHeader("Authorization", "Bearer " + apiToken);
            httpGet.addHeader("client-id", clientId);

            response = httpclient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            SSOPasswordReset SSOPasswordReset = null;
            if (responseCode == 200) {
                String stringResponse = EntityUtils.toString(response.getEntity());
                SSOPasswordReset = mapper.readValue(stringResponse, SSOPasswordReset.class);
            } else {
                return null;
            }

            String verbiage = String.format("Reset password for user  -> Email : %s ", emailAddress);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), springSecurityAuditorAware.getCurrentAuditorNotNull(),
                    LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

            return SSOPasswordReset.getResponse().getPasswordToken();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Mono<ResponseEntity> resetPassword(SSOPasswordResetModel ssoPasswordResetModel, HttpServletRequest request, AuthInfo authInfo) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;

            String apiToken = getAPIToken();

            // Create User
            url = baseAPIURL + "/account/resetpassword";
            httpPost = new HttpPost(url);

            // final com.fasterxml.jackson.databind.ObjectMapper mapperJson = new
            // com.fasterxml.jackson.databind.ObjectMapper();
            // String model = mapperJson.writeValueAsString(ssoPasswordResetModel);
            String model = mapper.toJson(ssoPasswordResetModel);
            httpPost.setEntity(new StringEntity(model));
            httpPost.addHeader("Authorization", "Bearer " + apiToken);
            httpPost.addHeader("client-id", clientId);
            httpPost.addHeader("Content-Type", "application/json");

            response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();

            String stringResponse = EntityUtils.toString(response.getEntity());

            String userFullName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            if (responseCode == 200) {
                String verbiage = String.format("Successful password reset  -> User : %s ", userFullName);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                        userFullName, userFullName, LocalDateTime.now(),
                        LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

                return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
            } else {

                String verbiage = String.format("Unsuccessful password reset  -> User : %s ", userFullName);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                        userFullName, userFullName, LocalDateTime.now(),
                        LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

                if (authInfo.isInactive() == true) {
                    authInfo.setInactive(false);
                    authInfo.setInactiveReason(null);
                    authInfo.setLastInactiveDate(LocalDate.now());
                    mongoRepositoryReactive.saveOrUpdate(authInfo);
                }
                return Mono.just(new ResponseEntity<>(stringResponse, HttpStatus.valueOf(responseCode)));
            }
        } catch (Throwable e) {
            String errorMsg = "An error occurred while resetting user password";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> changePassword(String token, SSOChangePasswordModel model, HttpServletRequest request) {

        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;

            // Create User
            url = baseAPIURL + "/account/changepassword";
            httpPost = new HttpPost(url);
            // final com.fasterxml.jackson.databind.ObjectMapper mapperJson = new
            // com.fasterxml.jackson.databind.ObjectMapper();
            String modelString = mapper.toJson(model);
            httpPost.setEntity(new StringEntity(modelString));
            httpPost.addHeader("Authorization", "Bearer " + token);
            httpPost.addHeader("client-id", clientId);
            httpPost.addHeader("Content-Type", "application/json");

            response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            String userFullName = springSecurityAuditorAware.getCurrentAuditorNotNull();
            if (responseCode == 200) {
                String verbiage = String.format("Successful password change  -> User : %s ", userFullName);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                        userFullName, userFullName, LocalDateTime.now(),
                        LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));


                return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
            } else {
                String verbiage = String.format("Unsuccessful password change  -> User : %s ", userFullName);
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                        userFullName, userFullName, LocalDateTime.now(),
                        LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), verbiage));

                return Mono.just(new ResponseEntity<>(EntityUtils.toString(response.getEntity()), HttpStatus.valueOf(responseCode)));
            }
        } catch (Throwable e) {
            String errorMsg = "An error occurred while changing user password";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<String> deactivateAuthInfo() {
        return null;
    }

    @Override
    public Mono<String> getToken() {
        return null;
    }

    @Override
    public Mono<ResponseEntity> completeRegistration(VerificationToken verificationToken, AuthInfoCompleteDto authInfoCompleteDto,
                                                     AuthInfo authInfo) {
        verificationToken.setActivated(true);
        verificationToken.setExpired(true);

        AuthRole authRole = (AuthRole) mongoRepositoryReactive.findById(authInfo.getAuthRoleId(), AuthRole.class)
                .block();

        // Create User on SSO at this point
        SSOClaim applicationClaim = new SSOClaim();
        applicationClaim.setType("application");
        applicationClaim.setValue("lslb-cms");

        SSOClaim roleClaim1 = new SSOClaim();
        roleClaim1.setType("role");
        roleClaim1.setValue(authRole.getSsoRoleMapping());

        SSOClaim roleClaim2 = new SSOClaim();
        roleClaim2.setType("role");
        roleClaim2.setValue(authRole.getName());

        SSOUser ssoUser = new SSOUser();
        ssoUser.getClaims().add(applicationClaim);
        ssoUser.getClaims().add(roleClaim1);
        ssoUser.getClaims().add(roleClaim2);
        ssoUser.setConfirmEmail(true);
        ssoUser.setEmail(authInfo.getEmailAddress());
        String firstName = authInfo.getFirstName();
        String[] names = firstName.split(" ");
        ssoUser.setFirstName(names[0]);
        //   ssoUser.setFirstName(authInfo.getFirstName());
        ssoUser.setLastName(authInfo.getLastName());
        ssoUser.setPassword(authInfoCompleteDto.getPassword());
        ssoUser.setPhoneNumber(authInfo.getPhoneNumber());
        ssoUser.setUserName(authInfo.getEmailAddress());

        try {
            boolean userExists = false;
            UserRegisterResponse userRegisterResponse = null;
            SSOUserDetailInfo userDetail = null;

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            HttpGet httpGet = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;

            // First we check if User exists
            String apiToken = getAPIToken();
            url = baseAPIURL + "/account/getuserbyemail?email=" + authInfo.getEmailAddress();
            httpGet = new HttpGet(url);

            httpGet.addHeader("Authorization", "Bearer " + apiToken);
            httpGet.addHeader("client-id", clientId);

            response = httpclient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            SSOUserDetail SSOUserDetail = null;
            switch (responseCode) {
                case 200: {
                    // everything is fine, handle the response
                    String stringResponse = EntityUtils.toString(response.getEntity());
                    SSOUserDetail = mapper.readValue(stringResponse, SSOUserDetail.class);
                    if (SSOUserDetail.getData().size() > 0) {
                        userExists = true;
                    }
                }
            }

            // user exist so we add claims
            if (userExists) {
                userDetail = SSOUserDetail.getData().get(0);

                SSOUserAddClaim ssoUserAddClaim = new SSOUserAddClaim();
                ssoUserAddClaim.setUserId(userDetail.getId());
                ssoUserAddClaim.getClaims().add(applicationClaim);
                ssoUserAddClaim.getClaims().add(roleClaim1);
                ssoUserAddClaim.getClaims().add(roleClaim2);

                // Add claims
                url = baseAPIURL + "/account/addclaims";
                httpPost = new HttpPost(url);
                // final com.fasterxml.jackson.databind.ObjectMapper mapperJson = new
                // com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.toJson(ssoUserAddClaim);
                httpPost.setEntity(new StringEntity(json));
                httpPost.addHeader("Authorization", "Bearer " + apiToken);
                httpPost.addHeader("client-id", clientId);
                httpPost.addHeader("Content-Type", "application/json");

                response = httpclient.execute(httpPost);
                responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {

                    return Mono.just(new ResponseEntity<>(EntityUtils.toString(response.getEntity()), HttpStatus.valueOf(responseCode)));
                }
            } else {
                // Create User
                url = baseAPIURL + "/account/register";
                httpPost = new HttpPost(url);
                String json = mapper.toJson(ssoUser);
                httpPost.setEntity(new StringEntity(json));
                httpPost.addHeader("Authorization", "Bearer " + apiToken);
                httpPost.addHeader("client-id", clientId);
                httpPost.addHeader("Content-Type", "application/json");

                response = httpclient.execute(httpPost);
                responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 201) {
                    return Mono.just(new ResponseEntity<>(EntityUtils.toString(response.getEntity()), HttpStatus.valueOf(responseCode)));
                }

                String stringResponse = EntityUtils.toString(response.getEntity());
                userRegisterResponse = mapper.readValue(stringResponse, UserRegisterResponse.class);
            }

            // GetUserId set
            String UserId = null;
            if (userExists == false) {
                UserId = userRegisterResponse.getUserId();
            } else {
                UserId = userDetail.getId();
            }

            authInfo.setSsoUserId(UserId);
            authInfo.setEnabled(true);
            mongoRepositoryReactive.saveOrUpdate(authInfo);

            mongoRepositoryReactive.saveOrUpdate(verificationToken);

            if (authRole.isSSOClientAdmin()) {
                newUserEmailNotifierAsync.sendNewSSOClientAdminNotificationToVGGAdmins(authInfo);
                return Mono.just(new ResponseEntity<>("User created successfully,\n please contact your admin for SSO clearance", HttpStatus.OK));
            }
            return Mono.just(new ResponseEntity<>("User created successfully", HttpStatus.OK));

        } catch (Throwable e) {
            String errorMsg = "An error occurred while completing user creation";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    /**
     * @param userName
     * @param password
     * @return
     */
    @Override
    public Mono<ResponseEntity> loginToken(String userName, String password, AuthInfo authInfo, HttpServletRequest request) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            // URI urlObject = new URI(baseIdentityURL+"/connect/token");
            String url = baseIdentityURL + "/connect/token";
            HttpPost httpPost = new HttpPost(url);
            // List<NameValuePair> formparams = URLEncodedUtils.parse(urlObject,"UTF-8");
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("grant_type", "password"));
            urlParameters.add(new BasicNameValuePair("scope", "openid profile identity-server-api"));
            urlParameters.add(new BasicNameValuePair("username", userName));
            urlParameters.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            httpPost.addHeader("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());

            if (responseCode == 400 && StringUtils.equalsIgnoreCase("{\"error\":\"invalid_grant\"}", stringResponse)) {
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.LOGIN_ID, authInfo.getFullName(), null, LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), "Unsuccessful Login Attempt -> Response From SSO : \n" + stringResponse));
                return Mono.just(new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED));
            }

            if (responseCode == 200) {
                // everything is fine, handle the response
                SSOToken token = mapper.readValue(stringResponse, SSOToken.class);
                token.setAuthInfo(authInfo.convertToLoginDto());
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.LOGIN_ID, authInfo.getFullName(), null, LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), "Successful Login Attempt"));

                return Mono.just(new ResponseEntity<>((token), HttpStatus.OK));
            } else {
                auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.LOGIN_ID, authInfo.getFullName(), null, LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), "Unsuccessful Login Attempt -> Response From SSO : \n" + stringResponse));
                return Mono.just(new ResponseEntity<>(stringResponse, HttpStatus.valueOf(responseCode)));
            }
        } catch (Throwable e) {
            String errorMsg = "An error occurred while logging in";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    public String getAPIToken() {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            // URI urlObject = new URI(baseIdentityURL+"/connect/token");
            String url = baseIdentityURL + "/connect/token";
            HttpPost httpPost = new HttpPost(url);
            // List<NameValuePair> formparams = URLEncodedUtils.parse(urlObject,"UTF-8");
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
            urlParameters.add(new BasicNameValuePair("scope", "identity-server-api"));
            urlParameters.add(new BasicNameValuePair("client_id", apiUsername));
            urlParameters.add(new BasicNameValuePair("client_secret", apiPassword));

            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            switch (responseCode) {
                case 200: {
                    // everything is fine, handle the response
                    String stringResponse = EntityUtils.toString(response.getEntity());
                    SSOToken token = mapper.readValue(stringResponse, SSOToken.class);
                    return token.getAccess_token();
                }
                case 500: {
                    // server problems ?
                    return null;
                }
                case 403: {
                    // you have no authorization to access that resource
                    return null;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    private Mono<ResponseEntity> validateCreateGamingOperatorAuthInfo(AuthInfoCreateDto authInfoCreateDto) {
        int maxNumberOfGamingOperatorUsers = 3;
        String authRoleId = authInfoCreateDto.getAuthRoleId();
        if (StringUtils.equals(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, authRoleId)) {
            ArrayList<AuthInfo> authInfoListWithGamingOperatorLimitedRoles = getAllActiveGamingOperatorUsersForInstitution(authInfoCreateDto.getInstitutionId());
            if (authInfoListWithGamingOperatorLimitedRoles.size() >= maxNumberOfGamingOperatorUsers) {
                return Mono.just(new ResponseEntity<>("Number of users for gaming operator exceeded", HttpStatus.BAD_REQUEST));
            }
        } else {
            return Mono.just(new ResponseEntity<>("Role specified cannot be used to create an institution user", HttpStatus.BAD_REQUEST));
        }
        return null;
    }

    @Override
    public ArrayList<AuthInfo> getAllActiveGamingOperatorUsersForInstitution(String institutionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("authRoleId").in(Arrays.asList(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, LSLBAuthRoleReferenceData.APPLICANT_ROLE_ID)));
        query.addCriteria(Criteria.where("enabled").is(true));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }

    @Override
    public AuthInfo getUserById(String userId) {
        return (AuthInfo) mongoRepositoryReactive.findById(userId, AuthInfo.class).block();
    }

    @Override
    public ArrayList<AuthInfo> findAllLSLBMembersThatHasPermission(String authPermissionId) {
        ArrayList<AuthInfo> validMembers = new ArrayList<>();
        for (AuthInfo lslbMember : getAllEnabledLSLBMembers()) {
            Set<String> userPermissions = lslbMember.getAllUserPermissionIdsForUser();
            if (userPermissions.contains(authPermissionId)) {
                validMembers.add(lslbMember);
            }
        }
        return validMembers;
    }

    @Override
    public ArrayList<AuthInfo> findAllActiveVGGAdminAndUsers() {
        Query query = new Query();
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("authRoleId").in(Arrays.asList(AuthRoleReferenceData.VGG_ADMIN_ID, AuthRoleReferenceData.VGG_USER_ID)));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }

    @Override
    public ArrayList<AuthInfo> findAllOtherActiveUsersForApproval(AuthInfo initiator) {
        Query query = new Query();
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("authRoleId").is(initiator.getAuthRoleId()));
        query.addCriteria(Criteria.where("id").ne(initiator.getId()));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());

    }

    @Override
    public ArrayList<AuthInfo> getUsersFromUserIds(Collection<String> userIds) {
        ArrayList<AuthInfo> users = new ArrayList<>();
        for (String userId : userIds) {
            AuthInfo user = getUserById(userId);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }


    public ArrayList<AuthInfo> getOperatorUsers(String institutionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        query.addCriteria(Criteria.where("authRoleId").in(Arrays.asList(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, LSLBAuthRoleReferenceData.APPLICANT_ROLE_ID)));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }

    private ArrayList<AuthInfo> getAllEnabledLSLBMembers() {
        Query query = new Query();
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("authRoleId").in(LSLBAuthRoleReferenceData.getLslbRoles()));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }

    @Override
    public Mono<ResponseEntity> addPermissionsToUser(UserAuthPermissionDto userAuthPermissionDto, HttpServletRequest request) {
        try {
            String subjectUserId = userAuthPermissionDto.getUserId();
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.BAD_REQUEST));
            }
            AuthInfo subjectUser = getUserById(subjectUserId);
            String loggedInUserId = loggedInUser.getId();
            if (subjectUser == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s does not exist", subjectUserId), HttpStatus.BAD_REQUEST));
            }

            UserApprovalRequest userApprovalRequest = new UserApprovalRequest();
            userApprovalRequest.setId(UUID.randomUUID().toString());
            userApprovalRequest.setInitiatorId(loggedInUserId);
            userApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            userApprovalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.ADD_PERMISSION_TO_USER_ID);
            userApprovalRequest.setNewPermissionIds(userAuthPermissionDto.getAuthPermissionIds());
            userApprovalRequest.setAuthInfoId(subjectUserId);
            mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
            approvalRequestNotifierAsync.sendNewUserApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, userApprovalRequest);

            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), subjectUser.getFullName(), LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), String.format("Created user approval request to add permissions to user %s", subjectUser.getFullName())));
            return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));

        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while adding permissions to user ", e);
        }
    }

    @Override
    public Mono<ResponseEntity> removePermissionFromUser(UserAuthPermissionDto userAuthPermissionDto, HttpServletRequest request) {
        try {
            String subjectUserId = userAuthPermissionDto.getUserId();
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.BAD_REQUEST));
            }
            AuthInfo subjectUser = getUserById(subjectUserId);
            if (subjectUser == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s does not exist", subjectUserId), HttpStatus.BAD_REQUEST));
            }

            UserApprovalRequest userApprovalRequest = new UserApprovalRequest();
            userApprovalRequest.setId(UUID.randomUUID().toString());
            userApprovalRequest.setInitiatorId(loggedInUser.getId());
            userApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            userApprovalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.REMOVE_PERMISSION_FROM_USER_ID);
            userApprovalRequest.setRemovedPermissionIds(userAuthPermissionDto.getAuthPermissionIds());
            userApprovalRequest.setAuthInfoId(subjectUserId);
            mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);
            approvalRequestNotifierAsync.sendNewUserApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, userApprovalRequest);
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), subjectUser.getFullName(), LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request), String.format("Created user approval request to remove permissions from user %s", subjectUser.getFullName())));
            return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return ErrorResponseUtil.logAndReturnError(logger, "An error occurred while removing permission from user", e);
        }
    }

    @Override
    public Mono<ResponseEntity> updateUserRole(UserRoleUpdateDto userRoleUpdateDto, HttpServletRequest request) {
        try {
            String subjectUserId = userRoleUpdateDto.getUserId();
            String newRoleId = userRoleUpdateDto.getNewRoleId();
            AuthInfo loggedInUser = springSecurityAuditorAware.getLoggedInUser();
            if (loggedInUser == null) {
                return Mono.just(new ResponseEntity<>("Could not find logged in user", HttpStatus.BAD_REQUEST));
            }
            AuthInfo subjectUser = getUserById(subjectUserId);
            if (subjectUser == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s does not exist", subjectUserId), HttpStatus.BAD_REQUEST));
            }
            AuthRole oldRole = subjectUser.getAuthRole();
            AuthRole newRole = authRoleService.findRoleById(newRoleId);
            if (newRole == null) {
                return Mono.just(new ResponseEntity<>(String.format("Role with id %s not found", newRoleId), HttpStatus.BAD_REQUEST));
            }

            if (StringUtils.equals(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, oldRole.getId())
                    || StringUtils.equals(LSLBAuthRoleReferenceData.AGENT_ROLE_ID, oldRole.getId())
                    || StringUtils.equals(LSLBAuthRoleReferenceData.APPLICANT_ROLE_ID, oldRole.getId())) {
                return Mono.just(new ResponseEntity<>("User role cannot be changed", HttpStatus.BAD_REQUEST));
            }

            if ((StringUtils.equals(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, newRole.getId()))) {
                return Mono.just(new ResponseEntity<>("User role cannot be changed to Gaming operator Role", HttpStatus.BAD_REQUEST));
            }

            UserApprovalRequest userApprovalRequest = new UserApprovalRequest();
            userApprovalRequest.setId(UUID.randomUUID().toString());
            userApprovalRequest.setAuthInfoId(subjectUserId);
            userApprovalRequest.setInitiatorAuthRoleId(loggedInUser.getAuthRoleId());
            userApprovalRequest.setInitiatorId(loggedInUser.getId());
            userApprovalRequest.setUserApprovalRequestTypeId(UserApprovalRequestTypeReferenceData.CHANGE_USER_ROLE_ID);
            userApprovalRequest.setApprovalRequestStatusId(ApprovalRequestStatusReferenceData.PENDING_ID);
            userApprovalRequest.setNewAuthRoleId(newRoleId);
            approvalRequestNotifierAsync.sendNewUserApprovalRequestEmailToAllOtherUsersInRole(loggedInUser, userApprovalRequest);
            mongoRepositoryReactive.saveOrUpdate(userApprovalRequest);

            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(AuditActionReferenceData.USER_ID, loggedInUser.getFullName(), subjectUser.getFullName(), LocalDateTime.now(), LocalDate.now(), true, RequestAddressUtil.getClientIpAddr(request),
                    String.format("Created user approval request to change role, old role -> %s, New Role -> %s", oldRole, newRole)));
            return Mono.just(new ResponseEntity<>(userApprovalRequest.convertToHalfDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while updating user role", e);
        }
    }

    @Override
    public Mono<ResponseEntity> getUserFullDetail(String userId) {
        try {
            AuthInfo user = getUserById(userId);
            if (user == null) {
                return Mono.just(new ResponseEntity<>(String.format("User with id %s not found ", userId), HttpStatus.BAD_REQUEST));
            }
            return Mono.just(new ResponseEntity<>(user.convertToFullDto(), HttpStatus.OK));
        } catch (Exception e) {
            return logAndReturnError(logger, "An error occurred while getting user full detail", e);
        }
    }

    @Override
    public AuthInfo findActiveUserWithEmail(String emailAddres) {
        Query query = new Query();
        query.addCriteria(Criteria.where("emailAddress").is(emailAddres));
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("accountLocked").is(false));
        return (AuthInfo) mongoRepositoryReactive.find(query, AuthInfo.class).block();
    }

    @Override
    public void updateInstitutionMembersToGamingOperatorRole(String institutionId) {
        ArrayList<AuthInfo> operatorUsers = getOperatorUsers(institutionId);
        for (AuthInfo operatorUser : operatorUsers) {
            try {
                operatorUser.setAuthRoleId(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID);
                mongoRepositoryReactive.saveOrUpdate(operatorUser);
            } catch (Exception e) {
                logger.error("An error occurred while getting updating user status");
            }
        }
    }

    private CreateAuthInfoResponse toCreateAuthInfoResponse(AuthInfo authInfo, VerificationToken verificationToken) {
        CreateAuthInfoResponse createAuthInfoResponse = new CreateAuthInfoResponse();
        createAuthInfoResponse.setEnabled(authInfo.getEnabled());
        createAuthInfoResponse.setAuthRoleId(authInfo.getAuthRoleId());
        createAuthInfoResponse.setAccountLocked(authInfo.getAccountLocked());
        createAuthInfoResponse.setEmailAddress(authInfo.getEmailAddress());
        createAuthInfoResponse.setId(authInfo.getId());
        createAuthInfoResponse.setAttachmentId(authInfo.getAttachmentId());
        createAuthInfoResponse.setPhoneNumber(authInfo.getPhoneNumber());
        createAuthInfoResponse.setFirstName(authInfo.getFirstName());
        createAuthInfoResponse.setLastName(authInfo.getLastName());
        createAuthInfoResponse.setFullName(authInfo.getFullName());
        createAuthInfoResponse.setAgentId(authInfo.getAgentId());
        createAuthInfoResponse.setInstitutionId(authInfo.getInstitutionId());
        createAuthInfoResponse.setSsoUserId(authInfo.getSsoUserId());
        createAuthInfoResponse.setTitle(authInfo.getTitle());
        createAuthInfoResponse.setAuthRole(authInfo.getAuthRole() == null ? null : authInfo.getAuthRole().convertToDto());
        createAuthInfoResponse.setExpiryDate(verificationToken.getExpiryDate().toString("dd/MM/yyy HH:mm:ss"));
        createAuthInfoResponse.setVerificationToken(verificationToken.getConfirmationToken());
        return createAuthInfoResponse;
    }

    @Override
    public ArrayList<AuthInfo> findAllEnabledUsersForInstitution(String institutionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("enabled").is(true));
        query.addCriteria(Criteria.where("institutionId").is(institutionId));
        return (ArrayList<AuthInfo>) mongoRepositoryReactive.findAll(query, AuthInfo.class).toStream().collect(Collectors.toList());
    }

    @Override
    public SSOUserDetailInfo getSSOUserDetailInfoByEmail(String email) {

        HttpClient httpclient = HttpClientBuilder.create().build();

        try {

            String token = this.getAPIToken();

            String url = baseAPIURL + "/account/getuserbyemail?email=" + email;
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Authorization", "Bearer " + token);
            httpGet.addHeader("client-id", clientId);
            HttpResponse response = httpclient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() != 200) {

                logger.error("get user by email returns status code " + response.getStatusLine().getStatusCode());
                logger.error(response.getStatusLine().getReasonPhrase());
                logger.error(EntityUtils.toString(response.getEntity()));
                return null;

            }

            String stringResponse = EntityUtils.toString(response.getEntity());
            logger.info("string response is " + stringResponse);
            SSOUserDetail userDetail = mapper.readValue(stringResponse, SSOUserDetail.class);

            return userDetail.getData().get(0);

        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }

    }


    private Mono<ResponseEntity> createExistingSSOUser(SSOUserDetail SSOUserDetail,
                                                       AuthInfo authInfo,
                                                       String apiToken,
                                                       String requestIpAddress) throws ApprovalRequestProcessException {
        try {
            SSOUserDetailInfo ssoUserDetailInfo = SSOUserDetail.getData().get(0);

            HashMap<String, Object> model = new HashMap<>();
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            HttpGet httpGet = null;
            String url = null;
            HttpResponse response = null;
            int responseCode;
            boolean userExists = false;
            SSOUserDetailInfo userDetail = null;
            AuthRole authRole = authRoleService.findRoleById(authInfo.getAuthRoleId());
            if (authRole == null) {
                return Mono.just(new ResponseEntity<>(String.format("Role with id %s not found", authInfo.getAuthRoleId()), HttpStatus.BAD_REQUEST));
            }
            userDetail = SSOUserDetail.getData().get(0);
            authInfo.setSsoUserId(userDetail.getId());

            SSOClaim applicationClaim = new SSOClaim();
            applicationClaim.setType("application");
            applicationClaim.setValue("lslb-cms");

            SSOClaim roleClaim1 = new SSOClaim();
            roleClaim1.setType("role");
            roleClaim1.setValue(authRole.getSsoRoleMapping());

            SSOClaim roleClaim2 = new SSOClaim();
            roleClaim2.setType("role");
            roleClaim2.setValue(authRole.getName());
            SSOUserAddClaim ssoUserAddClaim = new SSOUserAddClaim();
            ssoUserAddClaim.setUserId(userDetail.getId());
            ssoUserAddClaim.getClaims().add(applicationClaim);
            ssoUserAddClaim.getClaims().add(roleClaim1);
            ssoUserAddClaim.getClaims().add(roleClaim2);

            //Add claims
            url = baseAPIURL + "/account/addclaims";
            httpPost = new HttpPost(url);
            //final com.fasterxml.jackson.databind.ObjectMapper mapperJson = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.toJson(ssoUserAddClaim);
            httpPost.setEntity(new StringEntity(json));

            httpPost.addHeader("Authorization", "Bearer " + apiToken);
            httpPost.addHeader("client-id", clientId);
            httpPost.addHeader("Content-Type", "application/json");

            response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());

            if (responseCode != 200) {
                return Mono.just(new ResponseEntity<>(stringResponse, HttpStatus.valueOf(responseCode)));
            }

            model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
            model.put("frontEndUrl", frontEndPropertyHelper.getFrontEndUrl());
            String content = mailContentBuilderService.build(model, "ExistingUserRegistrationEmail");
            emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());
            authInfo.setEnabled(true);
            if (ssoUserDetailInfo != null) {
                authInfo.setSsoUserId(ssoUserDetailInfo.getId());
            }
            mongoRepositoryReactive.saveOrUpdate(authInfo);

            String verbiage = String.format("Create user  -> Name : %s ", authInfo.getFullName());
            auditLogHelper.auditFact(AuditTrailUtil.createAuditTrail(userAuditActionId,
                    springSecurityAuditorAware.getCurrentAuditorNotNull(), authInfo.getFullName(),
                    LocalDateTime.now(), LocalDate.now(), true, requestIpAddress, verbiage));
            return Mono.just(new ResponseEntity<>(authInfo.convertToDto(), HttpStatus.OK));
        } catch (IOException e) {
            throw new ApprovalRequestProcessException(e.getMessage());
        }
    }

    private void createNewUserOnSSO(AuthInfo authInfo, HttpServletRequest httpServletRequest) throws ApprovalRequestProcessException {
        try {
            authInfo.setInitialPassword(DEFAULT_PASSWORD);
            String apiToken = tokenExtractor.extract(httpServletRequest.getHeader("Authorization"));
            if (StringUtils.isEmpty(apiToken)) {
                throw new ApprovalRequestProcessException("Could not get authentication from request");
            }

            AuthRole authRole = authInfo.getAuthRole();

            SSOClaim applicationClaim = new SSOClaim();
            applicationClaim.setType("application");
            applicationClaim.setValue("lslb-cms");

            SSOClaim roleClaim1 = new SSOClaim();
            roleClaim1.setType("role");
            roleClaim1.setValue(authRole.getSsoRoleMapping());

            SSOClaim roleClaim2 = new SSOClaim();
            roleClaim2.setType("role");
            roleClaim2.setValue(authRole.getName());

            SSOUser ssoUser = new SSOUser();
            ssoUser.getClaims().add(applicationClaim);
            ssoUser.getClaims().add(roleClaim1);
            ssoUser.getClaims().add(roleClaim2);
            ssoUser.setEmail(authInfo.getEmailAddress());
            String firstName = authInfo.getFirstName();
            String[] names = firstName.split(" ");
            ssoUser.setFirstName(names[0]);
            ssoUser.setLastName(authInfo.getLastName());
            ssoUser.setPassword(authInfo.getInitialPassword());
            ssoUser.setPhoneNumber(authInfo.getPhoneNumber());
            ssoUser.setUserName(authInfo.getEmailAddress());
            ssoUser.setConfirmEmail(true);

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httpPost = null;
            HttpGet httpGet = null;
            String url = null;
            UserRegisterResponse userRegisterResponse = null;
            HttpResponse response = null;
            int responseCode;
            url = baseAPIURL + "/account/register";
            httpPost = new HttpPost(url);
            String json = mapper.toJson(ssoUser);
            httpPost.setEntity(new StringEntity(json));
            httpPost.addHeader("Authorization", "Bearer " + apiToken);
            httpPost.addHeader("client-id", clientId);
            httpPost.addHeader("Content-Type", "application/json");

            response = httpclient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 201) {
                throw new ApprovalRequestProcessException(EntityUtils.toString(response.getEntity()));
            }

            String stringResponse = EntityUtils.toString(response.getEntity());
            userRegisterResponse = mapper.readValue(stringResponse, UserRegisterResponse.class);
            // GetUserId set
            String UserId = userRegisterResponse.getUserId();

            authInfo.setSsoUserId(UserId);
            authInfo.setEnabled(true);
            //Set UnActive to true so User will not be to login on  CMS
            authInfo.setInactive(true);
            authInfo.setInactiveReason("Kindly Set Password");
            mongoRepositoryReactive.saveOrUpdate(authInfo);
        } catch (IOException e) {
            throw new ApprovalRequestProcessException(e.getMessage());
        }
    }

    @Override
    public Mono<ResponseEntity> completeResourceOwnerCreatedUserRegistration(AuthInfo authInfo, HttpServletRequest httpServletRequest, AuthInfoCompleteDto authInfoCompleteDto) {
        SSOChangePasswordModel ssoChangePasswordModel = new SSOChangePasswordModel();
        ssoChangePasswordModel.setCurrentPassword(authInfo.getInitialPassword());
        ssoChangePasswordModel.setNewPassword(authInfoCompleteDto.getPassword());
        ssoChangePasswordModel.setUserId(authInfo.getSsoUserId());
        Mono<ResponseEntity> responseEntityMono = loginToken(authInfo.getEmailAddress(), authInfo.getInitialPassword(), authInfo, httpServletRequest);
        ResponseEntity<SSOToken> responseEntity = responseEntityMono.block();
        if (responseEntity == null
                || responseEntity.getBody() == null
                || responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntityMono;
        }

        String token = responseEntity.getBody().getAccess_token();
        responseEntityMono = changePassword(token, ssoChangePasswordModel, httpServletRequest);
        responseEntity = responseEntityMono.block();
        if (responseEntity == null
                || responseEntity.getBody() == null
                || responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntityMono;
        }
        authInfo.setInactiveReason(null);
        authInfo.setInactive(false);
        mongoRepositoryReactive.saveOrUpdate(authInfo);
        return responseEntityMono;
    }
}
