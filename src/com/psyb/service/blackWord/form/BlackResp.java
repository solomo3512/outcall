package com.psyb.service.blackWord.form;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 黑词响应
 * 
 * @Title：BlackResp
 * @author:maliang
 * @date:2017年2月24日下午5:04:30
 */
@XmlRootElement(name = "Response")
public class BlackResp {

	private String statusCode;
	private String statusMsg;

	public BlackResp(){}
	
	public BlackResp(String statusCode,String statusMsg){
		this.statusCode=statusCode;
		this.statusMsg=statusMsg;
	}
	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

}
