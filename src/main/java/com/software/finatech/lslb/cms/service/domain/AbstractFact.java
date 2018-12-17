package com.software.finatech.lslb.cms.service.domain;

import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.GlobalApplicationContext;
import com.software.finatech.lslb.cms.service.util.PersistenceModification;
import io.advantageous.boon.json.annotations.JsonIgnore;
import org.joda.time.DateTime;
import org.springframework.data.annotation.*;

import javax.validation.constraints.NotNull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@SuppressWarnings("serial")
public abstract class AbstractFact implements PropertyChangeListener, FactObject {

	@Transient
	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	protected MongoRepositoryReactiveImpl mongoRepositoryReactive = GlobalApplicationContext.ctx.getBean(MongoRepositoryReactiveImpl.class);

	@Transient
	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	protected PersistenceModification persistenceModification;
	@Transient
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public AbstractFact() {
		pcs.addPropertyChangeListener(this);
	}

	/**
	 * This method gets called when a bound property is changed.
	 *
	 * @param evt A PropertyChangeEvent object describing the event source
	 *            and the property that has changed.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("Name      = " + evt.getPropertyName());
		System.out.println("Old Value = " + evt.getOldValue());
		System.out.println("New Value = " + evt.getNewValue());
	}

	@Id
	@NotNull
	protected String id;
	protected String tenantId;
	@Version
	protected Long version;
	@CreatedDate
	protected DateTime createdAt;
	@LastModifiedDate
	protected DateTime lastModified;
	@CreatedBy
	protected String createdBy;
	@LastModifiedBy
	protected String lastModifiedBy;
	protected String idref;

	protected String filePath;

	protected java.util.Date created;
	protected java.util.Date modified;
	protected String locale;
	protected String timeZone;


	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public PersistenceModification getPersistenceModification() {
		return persistenceModification;
	}

	public void setPersistenceModification(PersistenceModification persistenceModification) {
		this.persistenceModification = persistenceModification;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public java.util.Date getCreated() {
		return created;
	}

	public void setCreated(java.util.Date created) {
		this.created = created;
	}

	public java.util.Date getModified() {
		return modified;
	}

	public void setModified(java.util.Date modified) {
		this.modified = modified;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
