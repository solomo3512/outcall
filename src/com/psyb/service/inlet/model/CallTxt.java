package com.psyb.service.inlet.model;

import java.io.Serializable;

public class CallTxt implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int id;
	String appId;
	int callnums;
	String msg;
	int loop;
	String mediaName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getLoop() {
		return loop;
	}
	public void setLoop(int loop) {
		this.loop = loop;
	}
	public String getMediaName() {
		return mediaName;
	}
	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}
	
	
}
