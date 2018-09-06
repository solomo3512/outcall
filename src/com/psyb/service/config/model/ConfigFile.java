package com.psyb.service.config.model;

public class ConfigFile {

	private int version;
	private String filePath;
	
	public ConfigFile() {
		
	}
	
	public ConfigFile(int version, String filePath) {
		this.version = version;
		this.filePath = filePath;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
