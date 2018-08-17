package com.software.finatech.lslb.cms.userservice.service;

import com.software.finatech.lslb.cms.userservice.domain.AuthInfo;
import com.software.finatech.lslb.cms.userservice.domain.AuthRole;
import com.software.finatech.lslb.cms.userservice.domain.VerificationToken;
import com.software.finatech.lslb.cms.userservice.dto.AuthInfoCompleteDto;
import com.software.finatech.lslb.cms.userservice.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.userservice.dto.CreateGameOperatorAuthInfoDto;
import com.software.finatech.lslb.cms.userservice.dto.sso.*;
import com.software.finatech.lslb.cms.userservice.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.userservice.util.ErrorResponseUtil;
import io.advantageous.boon.json.JsonFactory;
import io.advantageous.boon.json.ObjectMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.software.finatech.lslb.cms.userservice.util.ErrorResponseUtil.logAndReturnError;

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

    @Autowired
    private MailContentBuilderService mailContentBuilderService;

    @Autowired
    private EmailService emailService;
    private static Logger logger = LoggerFactory.getLogger(AuthInfoServiceImpl.class);
    @Autowired
    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
    protected ObjectMapper mapper;

    /**
     * Initialize class
     */
    @PostConstruct
    public void initialize() {
        mapper = JsonFactory.createUseAnnotations(true);
    }

    @Override
    public AuthInfo createAuthInfo(AuthInfoCreateDto authInfoCreateDto, String appUrl) {

        AuthInfo authInfo = new AuthInfo();
        authInfo.setId(UUID.randomUUID().toString());
        authInfo.setEnabled(false);
        authInfo.setAuthRoleId(authInfoCreateDto.getAuthRoleId());
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress(authInfoCreateDto.getEmailAddress());
        authInfo.setPhoneNumber(authInfoCreateDto.getPhoneNumber());
        authInfo.setFirstName(authInfoCreateDto.getFirstName());
        authInfo.setLastName(authInfoCreateDto.getLastName());
        authInfo.setFullName(authInfoCreateDto.getFirstName() + " " + authInfoCreateDto.getLastName());
        authInfo.setInstitutionId(authInfoCreateDto.getInstitutionId());
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
                model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
                String content = mailContentBuilderService.build(model, "ExistingUserRegistrationEmail");
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());

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
                String content = mailContentBuilderService.build(model, "ContinueRegistrationEmail");
                content = content.replaceAll("CallbackUrl", url);
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());
            }

            return authInfo;
        } catch (Exception e) {
            logger.error("An error occured when trying to confirm if user exists", e);
            return null;
        }
    }

    @Override
    public AuthInfo createGameOperatorAuthInfo(CreateGameOperatorAuthInfoDto createGameOperatorAuthInfoDto, String appUrl) {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setId(UUID.randomUUID().toString());
        authInfo.setEnabled(false);
        authInfo.setAuthRoleId(createGameOperatorAuthInfoDto.getAuthRoleId());
        authInfo.setAccountLocked(false);
        authInfo.setEmailAddress(createGameOperatorAuthInfoDto.getEmailAddress());
        authInfo.setPhoneNumber(createGameOperatorAuthInfoDto.getPhoneNumber());
        authInfo.setFirstName(createGameOperatorAuthInfoDto.getName());
        authInfo.setLastName(createGameOperatorAuthInfoDto.getName());
        authInfo.setFullName(createGameOperatorAuthInfoDto.getName());
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
                model.put("name", authInfo.getFirstName() + " " + authInfo.getLastName());
                String content = mailContentBuilderService.build(model, "ExistingUserRegistrationEmail");
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());

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
                String content = mailContentBuilderService.build(model, "ContinueRegistrationEmail");
                content = content.replaceAll("CallbackUrl", url);
                emailService.sendEmail(content, "Registration Confirmation", authInfo.getEmailAddress());
            }

            return authInfo;
        } catch (Exception e) {
            logger.error("An error occured when trying to confirm if user exists", e);
            return null;
        }
    }


    @Override
    public Mono<String> updateAuthInfo() {
        return null;
    }

    @Override
    public String resetPasswordToken(String emailAddress) {
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

            return SSOPasswordReset.getResponse().getPasswordToken();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Mono<ResponseEntity> resetPassword(SSOPasswordResetModel ssoPasswordResetModel) {
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

            if (responseCode == 200) {
                return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
            } else {
                return Mono.just(new ResponseEntity<>(stringResponse, HttpStatus.valueOf(responseCode)));
            }
        } catch (Throwable e) {
            String errorMsg = "An error occurred while resetting user password";
            return logAndReturnError(logger, errorMsg, e);
        }
    }

    @Override
    public Mono<ResponseEntity> changePassword(String token, SSOChangePasswordModel model) {

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

            if (responseCode == 200) {
                return Mono.just(new ResponseEntity<>("Success", HttpStatus.OK));
            } else {
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
        ssoUser.setFirstName(authInfo.getFirstName());
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

            return Mono.just(new ResponseEntity<>(authInfo.convertToDto(), HttpStatus.OK));

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
    public Mono<ResponseEntity> loginToken(String userName, String password, AuthInfo authInfo) {
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

            if (responseCode == 200) {
                // everything is fine, handle the response
                // String stringResponse = EntityUtils.toString(response.getEntity());
                SSOToken token = mapper.readValue(stringResponse, SSOToken.class);
                token.setAuthInfo(authInfo.convertToDto());

                return Mono.just(new ResponseEntity<>((token), HttpStatus.OK));
            } else {
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

            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            httpPost.addHeader("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString((apiUsername + ":" + apiPassword).getBytes()));
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
}
