package com.psyb.service.common.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class InnerServer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String server_name;
	private String scheme;
	private String host;
	private int port;
	private String action;
	private int conn_time_out;
	private int so_time_out;
	public String getServer_name() {
		return server_name;
	}
	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getConn_time_out() {
		return conn_time_out;
	}
	public void setConn_time_out(int conn_time_out) {
		this.conn_time_out = conn_time_out;
	}
	public int getSo_time_out() {
		return so_time_out;
	}
	public void setSo_time_out(int so_time_out) {
		this.so_time_out = so_time_out;
	}
	public StringBuffer getBaseUrl() {
		StringBuffer sb = new StringBuffer();
		if(!StringUtils.isEmpty(scheme))
			sb.append(scheme);
		sb.append("://");
		if(!StringUtils.isEmpty(host))
			sb.append(host).append(":").append(port);
		if(!StringUtils.isEmpty(action))
			sb.append(action);
		return sb;
	}
	
}
