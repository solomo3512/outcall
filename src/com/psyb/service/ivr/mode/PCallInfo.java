package com.psyb.service.ivr.mode;

import java.io.Serializable;

public class PCallInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String from;
	private String to;
	private int type;  //记录呼叫业务模式，区分流程
	
	public PCallInfo()
	{
		
	}
	
	public PCallInfo(String from, String to, int type)
	{
		this.from=from;
		this.to = to;
		this.type = type;
	}
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
