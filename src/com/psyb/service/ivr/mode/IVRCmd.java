package com.psyb.service.ivr.mode;

import java.io.Serializable;

public class IVRCmd implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_GET_PBX = 3;   //绑定分机外呼
	public static final int TYPE_GET_EAR = 4;   //区域选择外呼号码

	private String appId;
	private int type;   //1 Play  2 Get 3 Consulation
	private String number;
	private int loop;
	private String playfile;
	private int timeout;
	private int numdigits;
	private String finishkey;
	private String firstendkey;
	private String redirectid;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getLoop() {
		return loop;
	}
	public void setLoop(int loop) {
		this.loop = loop;
	}
	public String getPlayfile() {
		return playfile;
	}
	public void setPlayfile(String playfile) {
		this.playfile = playfile;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getNumdigits() {
		return numdigits;
	}
	public void setNumdigits(int numdigits) {
		this.numdigits = numdigits;
	}
	public String getFinishkey() {
		return finishkey;
	}
	public void setFinishkey(String finishkey) {
		this.finishkey = finishkey;
	}
	public String getFirstendkey() {
		return firstendkey;
	}
	public void setFirstendkey(String firstendkey) {
		this.firstendkey = firstendkey;
	}
	public String getRedirectid() {
		return redirectid;
	}
	public void setRedirectid(String redirectid) {
		this.redirectid = redirectid;
	}
}
