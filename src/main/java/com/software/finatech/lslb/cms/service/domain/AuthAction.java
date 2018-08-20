package com.software.finatech.lslb.cms.service.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 */
@SuppressWarnings("serial")
@Document(collection = "AuthActions")
public class AuthAction extends AbstractFact {
	private String name;
	private String code;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getFactName() {
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
