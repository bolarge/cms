package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.util.PersistenceModification;

import java.io.Serializable;

public interface FactObject extends Serializable, Cloneable {

	String getFactName();
	PersistenceModification getPersistenceModification();
	void setPersistenceModification(PersistenceModification persistenceMods);
	Long getVersion();

	void setVersion(Long version);

	void setId(String id);

	String getId();

	void setTenantId(String tenantId);

	String getTenantId();

	Object clone() throws CloneNotSupportedException;

	java.util.Date getCreated();

	void setCreated(java.util.Date created);

	java.util.Date getModified();

	void setModified(java.util.Date modified);

	String getLocale();

	void setLocale(String locale);

	String getTimeZone();
	void setTimeZone(String timeZone);

	String getLastModifiedBy();
	void setLastModifiedBy(String lastModifiedBy);
	String getCreatedBy();
	void setCreatedBy(String createdBy);


}
