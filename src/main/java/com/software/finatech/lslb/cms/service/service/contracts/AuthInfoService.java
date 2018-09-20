package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.VerificationToken;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCompleteDto;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.dto.CreateApplicantAuthInfoDto;
import com.software.finatech.lslb.cms.service.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOPasswordResetModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface AuthInfoService {
    Mono<ResponseEntity> createAuthInfo(AuthInfoCreateDto authInfoCreateDto, String appUrl);

    AuthInfo createApplicantAuthInfo(CreateApplicantAuthInfoDto createApplicantAuthInfoDto, String appUrl);

    Mono<String> updateAuthInfo();
    String resetPasswordToken(String email);
    Mono<ResponseEntity> resetPassword(SSOPasswordResetModel ssoPasswordResetModel);
    Mono<ResponseEntity> changePassword(String token, SSOChangePasswordModel model);
    Mono<String> deactivateAuthInfo();
    Mono<String> getToken();
    Mono<ResponseEntity> loginToken(String userName, String password,AuthInfo authInfo);
    Mono<ResponseEntity> completeRegistration(VerificationToken verificationToken, AuthInfoCompleteDto authInfoCompleteDto, AuthInfo authInfo);

    ArrayList<AuthInfo> getAllActiveGamingOperatorAdminsAndUsersForInstitution(String institutionId);

    ArrayList<AuthInfo> getAllActiveGamingOperatorAdminsForInstitution(String institutionId);

    ArrayList<AuthInfo> getAllActiveLSLBFinanceAdmins();

    ArrayList<AuthInfo> getAllActiveLSLBITAdmins();

    ArrayList<AuthInfo> getAllActiveLSLBLegalAdmins();

    ArrayList<AuthInfo> getAllActiveLSLBGeneralManagers();

    AuthInfo getUserById(String userId);

    AuthInfo getUserByAgentId(String agentId);
}
