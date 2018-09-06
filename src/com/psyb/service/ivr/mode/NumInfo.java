package com.psyb.service.ivr.mode;

import java.io.Serializable;

public class NumInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String appId;
	private String disnumber;
	private String number;
	private String memberId;
	private String menuId;
	private String playId;
	private String voipaccount;
	private int type;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String getDisnumber() {
		return disnumber;
	}
	public void setDisnumber(String disnumber) {
		this.disnumber = disnumber;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getPlayId() {
		return playId;
	}
	public void setPlayId(String playId) {
		this.playId = playId;
	}
	public String getVoipaccount() {
		return voipaccount;
	}
	public void setVoipaccount(String voipaccount) {
		this.voipaccount = voipaccount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
