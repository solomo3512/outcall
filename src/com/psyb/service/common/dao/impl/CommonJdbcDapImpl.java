package com.psyb.service.common.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.psyb.service.common.Constants;
import com.psyb.service.common.dao.BaseJdbcDao;
import com.psyb.service.common.dao.CommonJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.model.InnerServer;
import com.psyb.service.common.util.DateUtil;
import com.psyb.service.inlet.model.Account;
import com.psyb.service.ivr.mode.NumInfo;

@Repository
public class CommonJdbcDapImpl implements CommonJdbcDao {

	private Logger logger = LogManager.getLogger(CommonJdbcDapImpl.class);
	@Autowired
	private BaseJdbcDao baseJdbcDao;
	
	@Override
	public String InsertCallCdr(int calltype, String appid, String callid, String caller,
			String called, String callsid, String direct) throws CCPDaoException {
		String sql=" INSERT INTO d_call_bill_cdr (bill_id, calltype, appid, caller, called, callSid, call_starttime, createdate) "
				+ "VALUES('"+System.currentTimeMillis()+"',"+calltype+",'"+appid+"','"+caller+"','"+called+"','"+callsid+"','"+DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE) +"','"+DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE) +"')";
		logger.info("InsertCallCdr2 sql="+sql);
		baseJdbcDao.executeSql(sql);
		return "";
	}

	@Override
	public String InsertCallCdr(String orderid, int calltype, String appid, String callsid, String caller,
			String called, String starttime, String endtime,
			String relationcallid, int duration, String recordurl, int asrresult) throws CCPDaoException {
		int callresult = Constants.CR_FAIL;
		if(duration > 0)
		{
			callresult = Constants.CR_SUCC;
		}
		if(starttime==null)
		{
			starttime="";
		}
		if(endtime==null)
		{
			endtime="";
		}
		
		String sql=" INSERT INTO d_call_bill_cdr (bill_id, calltype, callresult, appid, caller, called, callSid, starttime, endtime, duration, relationcallid, recordurl, asrcause) "
				+ "VALUES('"+orderid+"',"+calltype+","+callresult+",'"+appid+"','"+caller+"','"+called+"','"+callsid+"'"
						+ ",'"+starttime+"','"+endtime+"',"+duration+",'"+relationcallid+"','"+recordurl+"',"+asrresult+" )";
		logger.info("InsertCallCdr3 sql="+sql);
		baseJdbcDao.executeSql(sql);
		return "";
	}

	@Override
	public String UpdateCallCdr(String callsid, String starttime,
			String endtime, String relationcallid, int duration, int errorcode)
			throws CCPDaoException {
		int callresult = Constants.CR_FAIL;
		if(duration > 0)
		{
			callresult = Constants.CR_SUCC;
		}
		String sql=" UPDATE d_call_bill_cdr SET callresult="+callresult;
		if(!StringUtils.isEmpty(starttime))
		{
			sql += ", starttime='"+starttime+"',endtime='"+endtime+"'";
		}
		sql += ", relationcallid='"+relationcallid+"',duration="+duration+",errorcode="+errorcode+" WHERE callsid='"+callsid+"'";
		logger.info("UpdateCallCdr sql="+sql);
		baseJdbcDao.executeSql(sql);
		return "";
	}

	@Override
	public void InsertCallBind(String appid, String userNum, String number)
			throws CCPDaoException {
		String sql="REPLACE INTO x_pbx_bind_num (appId, userNum, number) VALUES('"+appid+"','"+userNum+"','"+number+"')";
		logger.info("InsertCallBind sql="+sql);
		baseJdbcDao.executeSql(sql);
	}

	@Override
	public NumInfo GetUserBindSerNum(String appid, String userNum)
			throws CCPDaoException {
		String sql="SELECT p.appId, p.number FROM x_pbx_bind_num p WHERE p.appId='"+appid+"' AND p.userNum='"+userNum+"'";
		logger.info("GetUserBindSerNum sql="+sql);
		NumInfo num = (NumInfo) baseJdbcDao.queryForObject(sql, NumInfo.class);
		return num;
	}

	@Override
	public List<Account> queryAccounts() throws CCPDaoException {
		String sql = "SELECT a.accountSid, a.authToken, p.appId, p.sipPrefix, p.callnums FROM a_application_info p INNER JOIN a_account_info a"
				+ " ON p.accountSid=a.accountSid";
		
		logger.info("queryAccount sql="+sql);
		@SuppressWarnings("unchecked")
		List<Account> list = (List<Account>) baseJdbcDao.queryForList(sql, Account.class);
		return list;
	}

	@Override
	public String InsertCallCdr(String orderid, int calltype, String appid,
			String caller, String called, String callsid, String starttime,
			String endtime, int duration, String recordurl, String relationcallid, int ringcause, int taskid)
			throws CCPDaoException {
		int callresult = Constants.CR_FAIL;
		if(duration > 0)
		{
			callresult = Constants.CR_SUCC;
		}
		if(StringUtils.isEmpty(starttime))
		{
			String date=DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE);
			starttime = endtime = date;
		}
		String sql=" INSERT INTO d_call_bill_cdr (bill_id, calltype, callresult, appid, caller, called, callSid, "
				+ "starttime, endtime, duration, recordurl, relationcallid,asrcause, taskId) "
				+ "VALUES('"+orderid+"',"+calltype+","+callresult+",'"+appid+"','"+caller+"','"+called+"','"+callsid+"',"
						+ "'"+starttime +"','"+endtime +"',"+duration+",'"+recordurl+"','"+relationcallid+"',"+ringcause+","+taskid+")";
		logger.info("InsertCallCdr sql="+sql);
		baseJdbcDao.executeSql(sql);
		return null;
	}

	@Override
	public List<InnerServer> queryInnerServer() throws CCPDaoException {
		String sql = "SELECT * FROM sys_inner_server_info";
		
		logger.info("queryInnerServer sql="+sql);
		@SuppressWarnings("unchecked")
		List<InnerServer> list = (List<InnerServer>) baseJdbcDao.queryForList(sql, InnerServer.class);
		return list;
	}

	
}
