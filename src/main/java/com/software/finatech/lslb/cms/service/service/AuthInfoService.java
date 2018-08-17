package com.software.finatech.lslb.cms.userservice.service;

import com.software.finatech.lslb.cms.userservice.domain.AuthInfo;
import com.software.finatech.lslb.cms.userservice.domain.VerificationToken;
import com.software.finatech.lslb.cms.userservice.dto.AuthInfoCompleteDto;
import com.software.finatech.lslb.cms.userservice.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.userservice.dto.CreateGameOperatorAuthInfoDto;
import com.software.finatech.lslb.cms.userservice.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.userservice.dto.sso.SSOPasswordResetModel;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public interface AuthInfoService {
    AuthInfo createAuthInfo(AuthInfoCreateDto authInfoCreateDto, String appUrl);
    AuthInfo createGameOperatorAuthInfo(CreateGameOperatorAuthInfoDto createGameOperatorAuthInfoDto, String appUrl);
    Mono<String> updateAuthInfo();
    String resetPasswordToken(String email);
    Mono<ResponseEntity> resetPassword(SSOPasswordResetModel ssoPasswordResetModel);
    Mono<ResponseEntity> changePassword(String token, SSOChangePasswordModel model);
    Mono<String> deactivateAuthInfo();
    Mono<String> getToken();
    Mono<ResponseEntity> loginToken(String userName, String password,AuthInfo authInfo);
    Mono<ResponseEntity> completeRegistration(VerificationToken verificationToken, AuthInfoCompleteDto authInfoCompleteDto, AuthInfo authInfo);
}
