package com.psyb.service.inlet.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class CallResult {

	
	private String action;
	private String callSid;
	private String number;
	private String state;
	private String starttime;
	private String endtime;
	private String duration;
	private String userData;
	private String callstate;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCallSid() {
		return callSid;
	}
	public void setCallSid(String callSid) {
		this.callSid = callSid;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getUserData() {
		return userData;
	}
	public void setUserData(String userData) {
		this.userData = userData;
	}
	public String getCallstate() {
		return callstate;
	}
	public void setCallstate(String callstate) {
		this.callstate = callstate;
	}
	
	
}
