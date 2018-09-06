package com.psyb.service.auth.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
public class CallAuthResp {

	private String statuscode;
	private int sessiontime;
	private String countDownPrompt;
	private int countDownTime;
	private int record;
	private int recordTrackType;
	private String disNumber;
	private String callerDisNumber;
	private String destNumber;
	private String callStateUrl;
	private int callStatePush;
	private int recordPoint;
	private String hangupCdrUrl;
	
	
	public CallAuthResp(String statuscode, int sessiontime, int record, String disNumber, String callStateUrl)
	{
		this.statuscode=statuscode;
		this.sessiontime = sessiontime;
		this.record = record;
		this.disNumber = disNumber;
		this.callStateUrl = callStateUrl;
	}
	
	public CallAuthResp()
	{
		
	}


	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public int getSessiontime() {
		return sessiontime;
	}


	public void setSessiontime(int sessiontime) {
		this.sessiontime = sessiontime;
	}


	public String getCountDownPrompt() {
		return countDownPrompt;
	}


	public void setCountDownPrompt(String countDownPrompt) {
		this.countDownPrompt = countDownPrompt;
	}


	public int getCountDownTime() {
		return countDownTime;
	}


	public void setCountDownTime(int countDownTime) {
		this.countDownTime = countDownTime;
	}


	public int getRecord() {
		return record;
	}


	public void setRecord(int record) {
		this.record = record;
	}


	public int getRecordTrackType() {
		return recordTrackType;
	}


	public void setRecordTrackType(int recordTrackType) {
		this.recordTrackType = recordTrackType;
	}


	public String getDisNumber() {
		return disNumber;
	}


	public void setDisNumber(String disNumber) {
		this.disNumber = disNumber;
	}


	public String getCallerDisNumber() {
		return callerDisNumber;
	}


	public void setCallerDisNumber(String callerDisNumber) {
		this.callerDisNumber = callerDisNumber;
	}


	public String getDestNumber() {
		return destNumber;
	}


	public void setDestNumber(String destNumber) {
		this.destNumber = destNumber;
	}


	public String getCallStateUrl() {
		return callStateUrl;
	}


	public void setCallStateUrl(String callStateUrl) {
		this.callStateUrl = callStateUrl;
	}


	public int getCallStatePush() {
		return callStatePush;
	}


	public void setCallStatePush(int callStatePush) {
		this.callStatePush = callStatePush;
	}


	public int getRecordPoint() {
		return recordPoint;
	}


	public void setRecordPoint(int recordPoint) {
		this.recordPoint = recordPoint;
	}

	public String getHangupCdrUrl() {
		return hangupCdrUrl;
	}

	public void setHangupCdrUrl(String hangupCdrUrl) {
		this.hangupCdrUrl = hangupCdrUrl;
	}
	
	
}
