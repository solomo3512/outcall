package com.psyb.service.blackWord.po;

import java.util.Date;

/**
 * 黑词
 * 
 * @Title：BlackWordsInfo
 * @author:maliang
 * @date:2017年2月24日下午2:47:17
 */
public class BlackWordsInfo {

	private int id;
	private String words;
	private Date addTime;
	private Date updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
