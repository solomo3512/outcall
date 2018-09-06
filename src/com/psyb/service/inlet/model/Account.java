package com.psyb.service.inlet.model;

import java.io.Serializable;

public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accountSid;
	private String authToken;
	private String appId;
	private int callnums;
	private String sipPrefix;
	private String subaccountsid;
	private String subpwd;
	private String voipaccount;
	
	public String getAccountSid() {
		return accountSid;
	}
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public int getCallnums() {
		return callnums;
	}
	public void setCallnums(int callnums) {
		this.callnums = callnums;
	}
	public String getSipPrefix() {
		return sipPrefix;
	}
	public void setSipPrefix(String sipPrefix) {
		this.sipPrefix = sipPrefix;
	}
	public String getSubaccountsid() {
		return subaccountsid;
	}
	public void setSubaccountsid(String subaccountsid) {
		this.subaccountsid = subaccountsid;
	}
	public String getSubpwd() {
		return subpwd;
	}
	public void setSubpwd(String subpwd) {
		this.subpwd = subpwd;
	}
	public String getVoipaccount() {
		return voipaccount;
	}
	public void setVoipaccount(String voipaccount) {
		this.voipaccount = voipaccount;
	}
	
	
}
