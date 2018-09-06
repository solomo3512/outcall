package com.psyb.service.inlet.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.psyb.service.common.dao.BaseJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.util.DateUtil;
import com.psyb.service.inlet.dao.InletJdbcDao;
import com.psyb.service.inlet.model.Account;
import com.psyb.service.inlet.model.CallNum;
import com.psyb.service.inlet.model.CallTxt;

@Repository
public class InletJdbcDaoImpl implements InletJdbcDao {

	protected final Logger logger = LogManager.getLogger(getClass().getName());
	@Autowired
	private BaseJdbcDao baseJdbcDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CallNum> getCallNum(String appid, int callresult, int taskid,  int count, boolean bfirst) throws CCPDaoException {
		String where = "c.appId='"+appid+"' AND c.taskId=" + taskid;
		if(callresult == -1)
		{
			where += " AND (c.callresult="+callresult +" OR c.status=0 ) " ;
		}
		else
		{
			where += " AND c.status="+0;
		}
		
		if(bfirst)
		{
			String sql="UPDATE x_callout_list c SET calllimit=0 WHERE " + where;
			logger.info("getCallNum update calllimit sql="+sql);
			baseJdbcDao.executeSql(sql);
		}
		
		String sql = "SELECT c.id, a.accountSid, a.authToken, c.number,c.datas,t.dtmf FROM x_callout_task t, x_callout_list c, a_account_info a, a_application_info p "
				+ " WHERE calllimit=0 AND p.accountSid=a.accountSid AND t.id=c.taskId AND p.appId=c.appId AND " + where;
		
		sql += " LIMIT "+count;
		logger.info("getCallNum sql="+sql);
		List<CallNum> list = (List<CallNum>) baseJdbcDao.queryForList(sql, CallNum.class);
		
		return list;
	}
	
	@Override
	public CallTxt getLandCallTxt(String appid, String taskid) throws CCPDaoException {
		String sql = "SELECT p.id, p.appId, p.msg, p.loop, p.mediaName FROM x_callout_templet p INNER JOIN x_callout_task t "
				+ "ON t.tempId=p.id WHERE p.status=1"
				+ " AND t.id="+taskid;
		logger.info("getLandCallTxt sql="+sql);
		CallTxt num = (CallTxt) baseJdbcDao.queryForObject(sql, CallTxt.class);
		return num;
	}

	@Override
	public void updateCallResult(String callsid, String starttime, String endtime, String duration, int result, int asrcause)
			throws CCPDaoException {
		String sql="UPDATE x_callout_list SET callresult="+result+", asrcause="+asrcause+" WHERE callsid='"+callsid+"'";
//		String sql=" UPDATE x_callout_conf SET callresult="+result+" WHERE callSid='"+callsid+"'";
		baseJdbcDao.executeSql(sql);
		logger.info("updateCallResult sql="+sql);
	}
	
	@Override
	public void updateCallDate(String appid, String callsid, long id)
			throws CCPDaoException {
//		String sql="UPDATE x_callout_conf SET status=1,callTime='"+date+"',callSid='"+callsid+"' WHERE number='"+number+"'";
		String sql="UPDATE x_callout_list SET calllimit=1,status=1,callTime='"+DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE)+"'"
				+ ",callSid='"+callsid+"' WHERE id="+id;
		logger.info("updateCallDate sql="+sql);
		baseJdbcDao.executeSql(sql);
		
	}

	@Override
	public Account getAccount(String ainfo, int type) throws CCPDaoException {
		String sql = "SELECT a.accountSid, a.authToken, p.appId FROM  a_account_info a, a_application_info p"
				+ " WHERE p.accountSid=a.accountSid";
		if(type == 0)
		{
			sql += " AND p.appId='" +ainfo+"'";
		}
		else
		{
			sql += " AND p.sipPrefix='" +ainfo+"'";
		}
		logger.info("getAccount sql="+sql);
		Account account = (Account) baseJdbcDao.queryForObject(sql, Account.class);
		return account;
	}

	@Override
	public void InsertAccountSub(String appid, String subaccount,
			String authtoken, String voipid, String pwd) throws CCPDaoException {
		String sql="INSERT INTO a_voip_info (appId, subaccountsid, subpwd, voipaccount, voippwd) "
				+ "VALUES( '"+appid+"','"+subaccount+"','"+authtoken+"','"+voipid+"','"+pwd+"')";

		logger.info("InsertAccountSub sql="+sql);
		baseJdbcDao.executeSql(sql);
		
		sql = "SELECT * FROM  a_voip_info n "
				+ "WHERE n.appId='"+appid+"';";
		logger.info("InsertAccountSub sql="+sql);
		@SuppressWarnings("unchecked")
		List<Account> list = (List<Account>) baseJdbcDao.queryForList(sql, Account.class);
		
		logger.info("InsertAccountSub get sql size="+list.size());
	}
	
	@Override
	public Account getSubAccount(String subaccount) throws CCPDaoException {
		String sql = "SELECT a.accountSid, a.authToken, p.appId,v.subaccountsid,v.subpwd,v.voipaccount FROM  a_account_info a, a_application_info p, a_voip_info v "
				+ "WHERE v.subaccountsid='"+subaccount+"' AND v.appId=p.appId AND p.accountSid=a.accountSid;";
		logger.info("getSubAccount sql="+sql);
		Account account = (Account) baseJdbcDao.queryForObject(sql, Account.class);
		return account;
	}
	
	@Override
	public List<CallNum> getServiceNum(String appid) throws CCPDaoException {
		String sql = "SELECT n.appId, n.number FROM  x_service_num n "
				+ "WHERE n.appId='"+appid+"';";
		logger.info("getServiceNum sql="+sql);
		@SuppressWarnings("unchecked")
		List<CallNum> list = (List<CallNum>) baseJdbcDao.queryForList(sql, CallNum.class);
		
		return list;
	}
	
}

	
