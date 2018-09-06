package com.psyb.service.auth.model;

import java.io.Serializable;

public class CallInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accountSid;
	private String appId;
	private String disNumber;
	private int record;
	private int recordPoint;
	
	
	public String getAccountSid() {
		return accountSid;
	}
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getDisNumber() {
		return disNumber;
	}
	public void setDisNumber(String disNumber) {
		this.disNumber = disNumber;
	}
	public int getRecord() {
		return record;
	}
	public void setRecord(int record) {
		this.record = record;
	}
	public int getRecordPoint() {
		return recordPoint;
	}
	public void setRecordPoint(int recordPoint) {
		this.recordPoint = recordPoint;
	}
	
	
}
