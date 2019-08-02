package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AuthInfoDto;
import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.dto.GameTypeDto;
import com.software.finatech.lslb.cms.service.referencedata.AuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.LSLBAuthRoleReferenceData;
import com.software.finatech.lslb.cms.service.referencedata.ReferenceDataUtil;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
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
    //Updated this day 23.07.2917 Applied Unique constraint to the emailAddress property of AuthInfo
    @Indexed(unique = true)
    protected String emailAddress;
    protected String authRoleId;
    protected String ssoUserId;
    protected String title;
    protected String agentId;
    protected LocalDate lastInactiveDate;
    protected String initialPassword;
    protected String gameTypeId;
    protected Set<String> authPermissionIds = new HashSet<>();
    @Transient
    protected AuthRole authRole;
    @Transient
    protected String gameTypeName;
    private boolean inactive;
    private String inactiveReason;
    //UI application level field settings
    protected Set<String> authViews = new java.util.HashSet<>();

    public AuthInfo() {
        super();
    }

    public String getInitialPassword() {
        return initialPassword;
    }

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }

    public LocalDate getLastInactiveDate() {
        return lastInactiveDate;
    }

    public void setLastInactiveDate(LocalDate lastInactiveDate) {
        this.lastInactiveDate = lastInactiveDate;
    }

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

    // STATE COMMUNICATION; TO BE REIMPLEMENTED AT THE SERVICE LAYER. TOO TIGHTLY COUPLED AS IT IS
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
        dto.setUserPermissions(getPermissionDtos(getAuthPermissionIds()));
        dto.setRolePermissions(getPermissionDtos(authRole.getAuthPermissionIds()));
        return dto;
    }

    public AuthInfoDto convertToLoginDto() {
        AuthInfoDto dto = convertToDto();
        AuthRole authRole = getAuthRole();
        List<AuthPermissionDto> authPermissionDtos = new ArrayList<>();
        authPermissionDtos.addAll(getPermissionDtos(this.authPermissionIds));
        authPermissionDtos.addAll(getPermissionDtos(authRole.getAuthPermissionIds()));
        dto.setAuthPermissions(authPermissionDtos);
        dto.setGameTypeDtos(getGameTypeDtos());
        return dto;
    }

    public AuthRole getAuthRole() {
        if (StringUtils.isEmpty(this.authRoleId)) {
            return null;
        }
        return (AuthRole) mongoRepositoryReactive.findById(this.authRoleId, AuthRole.class).block();
    }

    private Institution getInstitution() {
        return (Institution) mongoRepositoryReactive.findById(institutionId, Institution.class).block();
    }

    public Set<String> getAllUserPermissionIdsForUser() {
        Set<String> authPermissions = getAuthPermissionIds();
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

    public boolean isVGGSuperAdmin() {
        return StringUtils.equals(AuthRoleReferenceData.SUPER_ADMIN_ID, this.authRoleId);
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

    private List<AuthPermissionDto> getPermissionDtos(Set<String> permissionsIds) {
        List<AuthPermissionDto> dtos = new ArrayList<>();
        List<AuthPermission> permissions = new ArrayList<>();
        for (String id : permissionsIds) {
            AuthPermission permission = getAuthPermission(id);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        permissions.sort(ReferenceDataUtil.enumeratedFactComparator);
        for (AuthPermission permission : permissions) {
            dtos.add(permission.convertToDto());
        }
        return dtos;
    }

    private List<GameTypeDto> getGameTypeDtos() {
        if (StringUtils.isEmpty(this.institutionId)) {
            return new ArrayList<>();
        }
        Institution institution = getInstitution();
        if (institution == null) {
            return new ArrayList<>();
        }
        List<GameTypeDto> gameTypeDtos = new ArrayList<>();
        for (String gameTypeId : institution.getGameTypeIds()) {
            GameType gameType = findGameTypeById(gameTypeId);
            if (gameType != null) {
                gameTypeDtos.add(gameType.convertToDto());
            }
        }
        return gameTypeDtos;
    }


    private GameType findGameTypeById(String gameTypeId) {
        if (StringUtils.isEmpty(gameTypeId)) {
            return null;
        }
        return (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
    }

    public boolean isAgent() {
        return StringUtils.equals(LSLBAuthRoleReferenceData.AGENT_ROLE_ID, this.authRoleId);
    }

    public Agent getAgent() {
        if (StringUtils.isEmpty(this.agentId)) {
            return null;
        }
        return (Agent) mongoRepositoryReactive.findById(this.agentId, Agent.class).block();
    }
}
