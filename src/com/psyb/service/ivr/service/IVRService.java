package com.psyb.service.ivr.service;

import java.io.UnsupportedEncodingException;

import com.psyb.service.common.exception.CCPDaoException;

public interface IVRService {

	public String handleInComingCall(int calltype, String appid, String callid, String caller, String called, String direct) throws CCPDaoException;
	
	public void handleEndComingCall(String appid, String callsid, String starttime, String endtime, String relationcallid, int duration, String recordurl, int errorcode) throws CCPDaoException;
	
	public void handleCdr(String appid, String callsid, String caller, String called, int direct, String starttime, String endtime, String relationcallid, int duration, String recordurl, int errorcode) throws CCPDaoException;
	
	public String HandleDtmfReport(String appid, String callid, String type, String result, String fink, String firk,String dtmf, String playid, String dnum, String mid) throws CCPDaoException, UnsupportedEncodingException;
	
	public String HandlePbxExtenByDtmf(String appid, String dtmf, String playid, String dnum) throws CCPDaoException;
	
	public String getTransferNumByDtmf(String appid, String dtmf, String playid, String dnum) throws CCPDaoException;
	
	public String handleRedirect(String appid, String tag) throws CCPDaoException;
	
}
