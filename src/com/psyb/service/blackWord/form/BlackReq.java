package com.psyb.service.blackWord.form;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 黑词请求
 * 
 * @Title：BlackReq
 * @author:maliang
 * @date:2017年2月24日下午2:29:03
 */
@XmlRootElement(name = "Request")
public class BlackReq {

	private String id;// 黑词主键，修改、删除黑词时非空
	private String words;
	private String cmdType;// 命令类型 0:新增黑词 1：修改黑词 2:删除黑词
	private String accountSid;
	private String wordType;//0黑词 1白词
	private String text;
	
	public BlackReq() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public String getCmdType() {
		return cmdType;
	}

	public void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}

	public String getAccountSid() {
		return accountSid;
	}

	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	public String getWordType() {
		return wordType;
	}

	public void setWordType(String wordType) {
		this.wordType = wordType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
