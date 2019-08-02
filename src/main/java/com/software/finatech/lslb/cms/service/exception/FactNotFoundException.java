package com.software.finatech.lslb.cms.service.exception;

@SuppressWarnings("serial")
public class FactNotFoundException extends Exception {
	
	private String regionName;
	
	private String id;
	
	public FactNotFoundException(String factName, String id) {
		this.regionName = factName;
		this.id = id;
	}
	
	public String getFactName() {
		return regionName;
	}
	
	public String getPropertyName() {
		return id;
	}
	
	@Override
	public String getMessage() {
		return "No such record with id " + id + " on region " + regionName;
	}
}
