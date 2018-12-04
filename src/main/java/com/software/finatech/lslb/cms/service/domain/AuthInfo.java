package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AuthInfoDto;
import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.referencedata.AuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@SuppressWarnings("serial")
@Document(collection = "AuthInfo")
public class AuthInfo extends AbstractFact {
    protected String passwordResetToken;
    protected String attachmentId;
    protected String institutionId;
    protected String firstName;
    protected String lastName;
    protected String phoneNumber;
    protected String verificationTokenId;
    protected String fullName;
    protected boolean enabled;
    protected boolean accountLocked;
    protected DateTime accountExpirationTime;
    protected DateTime credentialsExpirationTime;
    protected String emailAddress;
    protected String authRoleId;
    protected String ssoUserId;
    protected String title;
    protected String agentId;
    protected LocalDate lastInactiveDate;

    public LocalDate getLastInactiveDate() {
        return lastInactiveDate;
    }

    public void setLastInactiveDate(LocalDate lastInactiveDate) {
        this.lastInactiveDate = lastInactiveDate;
    }

    //UI application level field settings
    protected Set<String> authViews = new java.util.HashSet<>();
    protected String gameTypeId;
    protected Set<String> authPermissionIds = new HashSet<>();
    @Transient
    protected AuthRole authRole;
    @Transient
    protected String gameTypeName;

    private boolean inactive;
    private String inactiveReason;

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public String getInactiveReason() {
        return inactiveReason;
    }

    public void setInactiveReason(String inactiveReason) {
        this.inactiveReason = inactiveReason;
    }

    public String getGameTypeName() {
        GameType gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
        if (gameType != null) {
            this.gameTypeName = gameType.name;
        }
        return this.gameTypeName;
    }

    public Set<String> getAuthPermissionIds() {
        return authPermissionIds;
    }

    public void setAuthPermissionIds(Set<String> authPermissionIds) {
        this.authPermissionIds = authPermissionIds;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(String gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }


    public void setAuthRole(AuthRole authRole) {
        this.authRole = authRole;
    }

    public String getSsoUserId() {
        return ssoUserId;
    }

    public void setSsoUserId(String ssoUserId) {
        this.ssoUserId = ssoUserId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getVerificationTokenId() {
        return verificationTokenId;
    }

    public void setVerificationTokenId(String verificationTokenId) {
        this.verificationTokenId = verificationTokenId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public DateTime getAccountExpirationTime() {
        return accountExpirationTime;
    }

    public void setAccountExpirationTime(DateTime accountExpirationTime) {
        this.accountExpirationTime = accountExpirationTime;
    }

    public DateTime getCredentialsExpirationTime() {
        return credentialsExpirationTime;
    }

    public void setCredentialsExpirationTime(DateTime credentialsExpirationTime) {
        this.credentialsExpirationTime = credentialsExpirationTime;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAuthRoleId() {
        return authRoleId;
    }

    public void setAuthRoleId(String authRoleId) {
        this.authRoleId = authRoleId;
    }

    public Set<String> getAuthViews() {
        return authViews;
    }

    public void setAuthViews(Set<String> authViews) {
        this.authViews = authViews;
    }

    @Override
    public String getFactName() {
        return "AuthInfo";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public AuthInfoDto convertToDto() {
        AuthInfoDto authInfoDto = new AuthInfoDto();
        authInfoDto.setEnabled(getEnabled());
        authInfoDto.setAuthRoleId(getAuthRoleId());
        authInfoDto.setAccountLocked(getAccountLocked());
        authInfoDto.setEmailAddress(getEmailAddress());
        authInfoDto.setId(getId());
        authInfoDto.setPhoneNumber(getPhoneNumber());
        authInfoDto.setFirstName(getFirstName());
        authInfoDto.setLastName(getLastName());
        authInfoDto.setFullName(getFullName());
        authInfoDto.setInstitutionId(getInstitutionId());
        authInfoDto.setSsoUserId(getSsoUserId());
        authInfoDto.setGameTypeId(getGameTypeId());
        authInfoDto.setGameTypeName(getGameTypeName());
        authInfoDto.setAuthRole(getAuthRole() == null ? null : getAuthRole().convertToHalfDto());
        Institution userInstitution = getInstitution();
        if (userInstitution != null) {
            authInfoDto.setInstitutionName(userInstitution.getInstitutionName());
        }
        authInfoDto.setAgentId(getAgentId());
        return authInfoDto;
    }

    public AuthInfoDto convertToFullDto() {
        AuthInfoDto dto = convertToDto();
        AuthRole authRole = getAuthRole();
        dto.setUserPermissions(getPermissionDtos(this.authPermissionIds));
        dto.setRolePermissions(getPermissionDtos(authRole.authPermissionIds));
        return dto;
    }

    public AuthInfoDto convertToLoginDto() {
        AuthInfoDto dto = convertToDto();
        AuthRole authRole = getAuthRole();
        Set<AuthPermissionDto> authPermissionDtos = new HashSet<>();
        authPermissionDtos.addAll(getPermissionDtos(this.authPermissionIds));
        authPermissionDtos.addAll(getPermissionDtos(authRole.authPermissionIds));
        dto.setAuthPermissions(authPermissionDtos);
        return dto;
    }

    public AuthRole getAuthRole() {
        if (StringUtils.isEmpty(this.authRoleId)) {
            return null;
        }
        Map authRoleMap = Mapstore.STORE.get("AuthRole");
        AuthRole authRole = null;
//        if (authRoleMap != null) {
//            authRole = (AuthRole) authRoleMap.get(this.authRoleId);
//        }
        if (authRole == null) {
            authRole = (AuthRole) mongoRepositoryReactive.findById(this.authRoleId, AuthRole.class).block();
            if (authRole != null && authRoleMap != null) {
                authRoleMap.put(authRole.getId(), authRole);
            }
        }
        return authRole;
    }

    private Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AuthInfo == false) {
            return false;
        }

        if (this == obj) {
            return true;
        }
        AuthInfo that = (AuthInfo) obj;
        Object thisObject = this.getId();
        Object thatObject = that.getId();

        if ((thisObject != null) && (thatObject != null)) {
            return thisObject.equals(thatObject);
        } else {
            return false;
        }
    }

    public Set<String> getAllUserPermissionIdsForUser() {
        Set<String> authPermissions = this.authPermissionIds;
        AuthRole userRole = getAuthRole();
        if (userRole != null) {
            authPermissions.addAll(userRole.getAuthPermissionIds());
        }
        return authPermissions;
    }

    private AuthPermission getAuthPermission(String authPermissionId) {
        if (StringUtils.isEmpty(authPermissionId)) {
            return null;
        }
        AuthPermission authPermission = null;
        Map authPermissionMap = Mapstore.STORE.get("AuthPermission");

        if (authPermissionMap != null) {
            authPermission = (AuthPermission) authPermissionMap.get(authPermissionId);
        }

        if (authPermission == null) {
            authPermission = (AuthPermission) mongoRepositoryReactive.findById(authPermissionId, AuthPermission.class).block();
            if (authPermission != null && authPermissionMap != null) {
                authPermissionMap.put(authPermission.getId(), authPermission);
            }
        }
        return authPermission;
    }

    public boolean isSuperAdmin() {
        return StringUtils.equals(AuthRoleReferenceData.SUPER_ADMIN_ID, this.authRoleId);
    }

    public boolean isVGGAdmin() {
        return StringUtils.equals(AuthRoleReferenceData.VGG_ADMIN_ID, this.authRoleId);
    }

    public boolean isVGGUser() {
        return StringUtils.equals(AuthRoleReferenceData.VGG_USER_ID, this.authRoleId);
    }

    public boolean isLSLBAdmin() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.LSLB_ADMIN_ID, this.authRoleId);
    }

    public String getInstitutionName() {
        Institution institution = getInstitution();
        if (institution != null) {
            return institution.getInstitutionName();
        }
        return null;
    }

    public boolean isLSLBUser() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.LSLB_USER_ID, this.authRoleId);
    }

    public boolean isLSLBMember() {
        return isLSLBAdmin() || isLSLBUser();
    }

    public boolean isGamingOperator() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.GAMING_OPERATOR_ROLE_ID, this.authRoleId);
    }

    private Set<AuthPermissionDto> getPermissionDtos(Set<String> permissionsIds) {
        List<AuthPermissionDto> dtos = new ArrayList<>();
        for (String id : permissionsIds) {
            AuthPermission permission = getAuthPermission(id);
            if (permission != null) {
                dtos.add(permission.convertToDto());
            }
        }
        dtos.sort(ReferenceDataUtil.objectComparator);
        return new HashSet<>(dtos);
    }

    public boolean isAgent() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.AGENT_ROLE_ID, this.authRoleId);
    }
}
