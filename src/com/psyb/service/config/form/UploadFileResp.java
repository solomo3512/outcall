package com.psyb.service.config.form;

public class UploadFileResp {

	private String statusCode;
	private long offset;

	public UploadFileResp() {

	}

	public UploadFileResp(String statusCode, long offset) {
		this.statusCode = statusCode;
		this.offset = offset;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
}
