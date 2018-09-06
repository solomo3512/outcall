package com.psyb.service.auth.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class Hangup {

	private String action;
	private String type;
	private String subtype;
	private String orderid;
	private String caller;
	private String called;
	private String callSid;
	private String starttime;
	private String endtime;
	private String recordurl;
	private String talkDuration;
	private String byetype;
	private String appId;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getCalled() {
		return called;
	}
	public void setCalled(String called) {
		this.called = called;
	}
	public String getCallSid() {
		return callSid;
	}
	public void setCallSid(String callSid) {
		this.callSid = callSid;
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
	public String getRecordurl() {
		return recordurl;
	}
	public void setRecordurl(String recordurl) {
		this.recordurl = recordurl;
	}
	public String getTalkDuration() {
		return talkDuration;
	}
	public void setTalkDuration(String talkDuration) {
		this.talkDuration = talkDuration;
	}
	public String getByetype() {
		return byetype;
	}
	public void setByetype(String byetype) {
		this.byetype = byetype;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	
}
