package com.psyb.service.ivr.mode;

import java.io.Serializable;

public class Schedule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String appId;
	private String offworktime;
	private String offworkdate;
	private String offweekday;
	private String offworkprompt;
	private String redirectid;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getOffworktime() {
		return offworktime;
	}
	public void setOffworktime(String offworktime) {
		this.offworktime = offworktime;
	}
	public String getOffworkdate() {
		return offworkdate;
	}
	public void setOffworkdate(String offworkdate) {
		this.offworkdate = offworkdate;
	}
	public String getOffweekday() {
		return offweekday;
	}
	public void setOffweekday(String offweekday) {
		this.offweekday = offweekday;
	}
	public String getOffworkprompt() {
		return offworkprompt;
	}
	public void setOffworkprompt(String offworkprompt) {
		this.offworkprompt = offworkprompt;
	}
	public String getRedirectid() {
		return redirectid;
	}
	public void setRedirectid(String redirectid) {
		this.redirectid = redirectid;
	}
	
	
}
