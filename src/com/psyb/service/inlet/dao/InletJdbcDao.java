package com.psyb.service.inlet.dao;

import java.util.List;

import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.inlet.model.Account;
import com.psyb.service.inlet.model.CallNum;
import com.psyb.service.inlet.model.CallTxt;

public interface InletJdbcDao {

	public List<CallNum> getCallNum(String appid, int callresult, int taskid, int count, boolean bfirst) throws CCPDaoException;
	
	public CallTxt getLandCallTxt(String appid, String tempid) throws CCPDaoException;
	
	public Account getAccount(String appid, int type) throws CCPDaoException;
	
	public void updateCallDate(String appid, String callsid,  long id) throws CCPDaoException;
	
	public void updateCallResult(String callsid, String starttime, String endtime, String duration, int result, int asrcause) throws CCPDaoException;
	
	public void InsertAccountSub(String appid, String subaccount, String authtoken, String voipid, String pwd) throws CCPDaoException;
	
	public Account getSubAccount(String subaccount) throws CCPDaoException;
	
	public List<CallNum> getServiceNum(String appid) throws CCPDaoException;
}
