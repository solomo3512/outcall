package com.psyb.service.config.model;

public class UploadAttr {
	
	private String appId;
	private String localFileName;
	
	public UploadAttr() {
		
	}
	
	public UploadAttr(String appId, String localFileName) {
		this.appId = appId;
		this.localFileName = localFileName;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the localFileName
	 */
	public String getLocalFileName() {
		return localFileName;
	}

	/**
	 * @param localFileName
	 *            the localFileName to set
	 */
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}
}
