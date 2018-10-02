package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.dto.AuthInfoDto;
import com.software.finatech.lslb.cms.service.dto.AuthPermissionDto;
import com.software.finatech.lslb.cms.service.exception.FactNotFoundException;
import com.software.finatech.lslb.cms.service.util.Mapstore;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Document(collection = "AuthInfo")
public class AuthInfo extends AbstractFact {
	protected String passwordResetToken;
	protected String attachmentId;
	protected String institutionId;
	protected String firstName ;
	protected String lastName ;
	protected String phoneNumber ;
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
    //UI application level field settings
    protected Set<String> authViews = new java.util.HashSet<>();
    protected String gameTypeId;
    protected Set<String> authPermissionIds = new HashSet<>();
    @Transient
    protected AuthRole authRole;
    @Transient
    protected String gameTypeName;

	public String getGameTypeName() {
		GameType gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
		if(gameType != null){
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

	public AuthInfoDto convertToDto(){
		AuthInfoDto authInfoDto = new AuthInfoDto();
		authInfoDto.setEnabled(getEnabled());
		authInfoDto.setAuthRoleId(getAuthRoleId());
		authInfoDto.setAccountLocked(getAccountLocked());
		authInfoDto.setEmailAddress(getEmailAddress());
		authInfoDto.setId(getId());
		authInfoDto.setAttachmentId(getAttachmentId());
		authInfoDto.setPhoneNumber(getPhoneNumber());
		authInfoDto.setFirstName(getFirstName());
		authInfoDto.setLastName(getLastName());
		authInfoDto.setFullName(getFullName());
		authInfoDto.setInstitutionId(getInstitutionId());
		authInfoDto.setSsoUserId(getSsoUserId());
		authInfoDto.setGameTypeId(getGameTypeId());
		authInfoDto.setGameTypeName(getGameTypeName());
		authInfoDto.setAuthRole(getAuthRole()==null?null:getAuthRole().convertToDto());
		Institution userInstitution= getInstitution();
		if (userInstitution != null){
			authInfoDto.setInstitutionName(userInstitution.getInstitutionName());
		}
		authInfoDto.setAgentId(getAgentId());
		return authInfoDto;
	}

    public void setAssociatedProperties() throws FactNotFoundException {
        if (authRoleId != null) {
            AuthRole authRole = (AuthRole) Mapstore.STORE.get("AuthRole").get(authRoleId);
            if (authRole == null) {
                authRole = (AuthRole) mongoRepositoryReactive.findById(authRoleId, AuthRole.class).block();
                if (authRole == null) {
                    throw new FactNotFoundException("AuthRole", authRoleId);
                } else {
                    Mapstore.STORE.get("AuthRole").put(authRole.getId(), authRole);
                }
            }
            setAuthRole(authRole);
        }

        if (gameTypeId != null) {
            GameType gameType = (GameType) mongoRepositoryReactive.findById(gameTypeId, GameType.class).block();
            if (gameType != null) {
                gameTypeName = gameType.name;
            }
        }
    }

    public AuthRole getAuthRole() {
        if (StringUtils.isEmpty(this.authRoleId)) {
            return null;
        }

        AuthRole authRole = (AuthRole) Mapstore.STORE.get("AuthRole").get(authRoleId);
        if (authRole == null) {
            authRole = (AuthRole) mongoRepositoryReactive.findById(authRoleId, AuthRole.class).block();
            if (authRole != null) {
                Mapstore.STORE.get("AuthRole").put(authRole.getId(), authRole);
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

    private Set<AuthPermissionDto> getAllPermissionsForUser() {
        Set<String> authPermissionIds = getAllUserPermissionIdsForUser();
        if (authPermissionIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<AuthPermissionDto> authPermissionDtos = new HashSet<>();
        for (String authPermissionId : authPermissionIds) {
            AuthPermission authPermission = getAuthPermission(authPermissionId);
            if (authPermission != null) {
                authPermissionDtos.add(authPermission.convertToDto());
            }
        }
        return authPermissionDtos;
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
        AuthPermission authPermission = (AuthPermission) Mapstore.STORE.get("AuthPermission").get(authPermissionId);
        if (authPermission == null) {
            authPermission = (AuthPermission) mongoRepositoryReactive.findById(authPermissionId, AuthPermission.class).block();
            if (authPermission != null) {
                Mapstore.STORE.get("AuthPermission").put(authPermission.getId(), authPermission);
            }
        }
        return authPermission;
    }
}
