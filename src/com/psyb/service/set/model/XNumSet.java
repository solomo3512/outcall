package com.psyb.service.set.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class XNumSet {

	
	private String from;
	private String to;
	private String disnumber;
	private String appId;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getDisnumber() {
		return disnumber;
	}
	public void setDisnumber(String disnumber) {
		this.disnumber = disnumber;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	
	
}
