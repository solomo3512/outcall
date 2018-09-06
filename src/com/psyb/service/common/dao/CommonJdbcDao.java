package com.psyb.service.common.dao;

import java.util.List;

import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.model.InnerServer;
import com.psyb.service.inlet.model.Account;
import com.psyb.service.ivr.mode.NumInfo;

public interface CommonJdbcDao {

	public String InsertCallCdr(String orderid, int calltype, String appid, String caller, String called, String callsid, 
			String starttime, String endtime, int duration, String recordurl,String relationcallid, int ringcause, int taskid) throws CCPDaoException;
	
	public String InsertCallCdr(int calltype, String appid, String callid, String caller, String called, String callsid, String direct) throws CCPDaoException;
	
	public String InsertCallCdr(String orderid, int calltype, String appid, String callsid, String caller, String called, String starttime, 
			String endtime, String relationcallid, int duration, String recordurl, int asrresult) throws CCPDaoException;
	
	public String UpdateCallCdr(String callsid, String starttime, String endtime, String relationcallid, int duration, int errorcode) throws CCPDaoException;
	
	public void InsertCallBind(String appid, String userNum, String number) throws CCPDaoException;
	
	public NumInfo GetUserBindSerNum(String appid, String userNum) throws CCPDaoException;
	
	public List<Account> queryAccounts() throws CCPDaoException;
	
	public List<InnerServer> queryInnerServer() throws CCPDaoException;

}
