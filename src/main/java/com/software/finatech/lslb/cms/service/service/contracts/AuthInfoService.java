package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.VerificationToken;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCompleteDto;
import com.software.finatech.lslb.cms.service.dto.AuthInfoCreateDto;
import com.software.finatech.lslb.cms.service.dto.CreateApplicantAuthInfoDto;
import com.software.finatech.lslb.cms.service.dto.UserAuthPermissionDto;
import com.software.finatech.lslb.cms.service.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOPasswordResetModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public interface AuthInfoService {
    Mono<ResponseEntity> createAuthInfo(AuthInfoCreateDto authInfoCreateDto, String appUrl);

    AuthInfo createApplicantAuthInfo(CreateApplicantAuthInfoDto createApplicantAuthInfoDto, String appUrl);

    Mono<String> updateAuthInfo();

    String resetPasswordToken(String email);

    Mono<ResponseEntity> resetPassword(SSOPasswordResetModel ssoPasswordResetModel);

    Mono<ResponseEntity> changePassword(String token, SSOChangePasswordModel model);

    Mono<String> deactivateAuthInfo();

    Mono<String> getToken();

    Mono<ResponseEntity> loginToken(String userName, String password, AuthInfo authInfo, HttpServletRequest request);

    Mono<ResponseEntity> completeRegistration(VerificationToken verificationToken, AuthInfoCompleteDto authInfoCompleteDto, AuthInfo authInfo);

    ArrayList<AuthInfo> getAllActiveGamingOperatorAdminsAndUsersForInstitution(String institutionId);

    ArrayList<AuthInfo> getAllActiveGamingOperatorAdminsForInstitution(String institutionId);

    AuthInfo getUserById(String userId);

    ArrayList<AuthInfo> findAllLSLBMembersThatCanReceiveCustomerComplainNotification();

    ArrayList<AuthInfo> findAllLSLBMembersThatCanReceiveApplicationSubmissionNotification();

    ArrayList<AuthInfo> findAllLSLBMembersThatCanReceivePaymentNotification();

    ArrayList<AuthInfo> findAllLSLBMembersThatCanApproveAgentApprovals();

    Mono<ResponseEntity> addPermissionsToUser(UserAuthPermissionDto userAuthPermissionDto);

    ArrayList<AuthInfo> findAllLSLBMembersThatCanReceiveNewCaseNotification();
}
