package com.software.finatech.lslb.cms.service.service.contracts;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.VerificationToken;
import com.software.finatech.lslb.cms.service.dto.*;
import com.software.finatech.lslb.cms.service.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOPasswordResetModel;
import com.software.finatech.lslb.cms.service.dto.sso.SSOUser;
import com.software.finatech.lslb.cms.service.dto.sso.SSOUserDetailInfo;
import com.software.finatech.lslb.cms.service.exception.ApprovalRequestProcessException;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public interface AuthInfoService {
    Mono<ResponseEntity> createAuthInfo(AuthInfoCreateDto authInfoCreateDto, String appUrl, HttpServletRequest request) throws IOException, ApprovalRequestProcessException;

    AuthInfo createApplicantAuthInfo(CreateApplicantAuthInfoDto createApplicantAuthInfoDto, String appUrl, HttpServletRequest request);

    Mono<String> updateAuthInfo();

    String resetPasswordToken(String email, HttpServletRequest request);

    Mono<ResponseEntity> resetPassword(SSOPasswordResetModel ssoPasswordResetModel, HttpServletRequest request ,AuthInfo authInfo);

    Mono<ResponseEntity> changePassword(String token, SSOChangePasswordModel model, HttpServletRequest request);

    Mono<String> deactivateAuthInfo();

    Mono<String> getToken();

    Mono<ResponseEntity> loginToken(String userName, String password, AuthInfo authInfo, HttpServletRequest request);

    Mono<ResponseEntity> completeRegistration(VerificationToken verificationToken, AuthInfoCompleteDto authInfoCompleteDto, AuthInfo authInfo);

    ArrayList<AuthInfo> getAllActiveGamingOperatorUsersForInstitution(String institutionId);

    AuthInfo getUserById(String userId);

    ArrayList<AuthInfo> findAllLSLBMembersThatHasPermission(String authPermissionId);

    ArrayList<AuthInfo> getUsersFromUserIds(Collection<String> userIds);

    Mono<ResponseEntity> addPermissionsToUser(UserAuthPermissionDto userAuthPermissionDto, HttpServletRequest request);

    Mono<ResponseEntity> removePermissionFromUser(UserAuthPermissionDto userAuthPermissionDto, HttpServletRequest request);

    Mono<ResponseEntity> updateUserRole(UserRoleUpdateDto userRoleUpdateDto, HttpServletRequest request);

    ArrayList<AuthInfo> findAllActiveVGGAdminAndUsers();

    ArrayList<AuthInfo> findAllOtherActiveUsersForApproval(AuthInfo initiator);

    Mono<ResponseEntity> getUserFullDetail(String userId);

    AuthInfo findActiveUserWithEmail(String emailAddres);

    void updateInstitutionMembersToGamingOperatorRole(String institutionId);

    ArrayList<AuthInfo> findAllEnabledUsersForInstitution(String institutionId);

    SSOUserDetailInfo getSSOUserDetailInfoByEmail(String email);

    Mono<ResponseEntity> completeResourceOwnerCreatedUserRegistration(AuthInfo authInfo, HttpServletRequest httpServletRequest, AuthInfoCompleteDto authInfoCompleteDto);
}
