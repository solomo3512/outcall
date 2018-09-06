package com.psyb.service.common.model;

public class ErrorCodeDescribePo {

	private int error_code;
	private int error_type; 
	private String error_des; 

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public int getError_type() {
		return error_type;
	}

	public void setError_type(int error_type) {
		this.error_type = error_type;
	}

	public String getError_des() {
		return error_des;
	}

	public void setError_des(String error_des) {
		this.error_des = error_des;
	}

}
