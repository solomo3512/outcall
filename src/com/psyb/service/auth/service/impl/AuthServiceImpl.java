package com.psyb.service.auth.service.impl;

import java.util.Date;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psyb.service.auth.dao.AuthJdbcDao;
import com.psyb.service.auth.model.CallAuth;
import com.psyb.service.auth.model.CallAuthResp;
import com.psyb.service.auth.model.CallInfo;
import com.psyb.service.auth.model.Hangup;
import com.psyb.service.auth.service.AuthService;
import com.psyb.service.common.Constants;
import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.dao.CommonJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPRedisException;
import com.psyb.service.common.util.DateUtil;

@Service
public class AuthServiceImpl implements AuthService {

//	private Logger logger = LogManager.getLogger(AuthServiceImpl.class);
	@Autowired
	private AuthJdbcDao authJdbcDao;
	
	@Autowired
	private CommonJdbcDao commJdbcDao;
	
	@Autowired
	private BaseRedisDao baseRedisDao;

	@Override
	public CallAuthResp HandleCallAuthSubacc(CallAuth callauth)
			throws CCPDaoException {
		
		 
		CallAuthResp resp = new CallAuthResp();
		if(callauth.getType().equals(Constants.TYPE_DCALL)
				|| callauth.getType().equals(Constants.TYPE_P2P_M)
				|| callauth.getType().equals(Constants.TYPE_P2P_V))
		{
			
			CallInfo callinfo = authJdbcDao.GetVoipCallDisNum(callauth.getCaller());
			if(callinfo != null)
			{
				resp.setStatuscode(Constants.SUCC);
				resp.setRecord(callinfo.getRecord());
				resp.setRecordPoint(callinfo.getRecordPoint());
				resp.setDisNumber(callinfo.getDisNumber());
				
				JSONObject json = new JSONObject();
				json.element("accountid", callinfo.getAccountSid());
				json.element("appid", callinfo.getAppId());
				try {
					String key = BaseRedisDao.REDIS_CDR_DATA+callauth.getCallSid();
					baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
				} catch (CCPRedisException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				commJdbcDao.InsertCallBind(callinfo.getAppId(), callauth.getCalled(), callauth.getCaller());
				return resp;
			}
		}else if(callauth.getType().equals(Constants.TYPE_CALLBACK)){
			resp.setStatuscode(Constants.SUCC);
			JSONObject json = new JSONObject();
			json.element("appid", callauth.getAppId());
			try {
				String key = BaseRedisDao.REDIS_CDR_DATA+callauth.getCallSid();
				baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
			} catch (CCPRedisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resp;
		}
		resp.setStatuscode(Constants.ERR_ACCOUNT);
		return resp;
	}

	@Override
	public String HandleHangup(Hangup hangup) throws CCPDaoException {
		
		String key = BaseRedisDao.REDIS_CDR_DATA+hangup.getCallSid();
		try {
			
			int type = Constants.CT_DCALL;
			if(hangup.getType().equals(Constants.TYPE_P2P_M))
			{
				type = Constants.CT_P2P;
			}
			else if(hangup.getType().equals(Constants.TYPE_P2P_V))
			{
				type = Constants.CT_P2P_V;
			}
			else if(hangup.getType().equals(Constants.TYPE_CALLBACK))
			{
				type = Constants.CT_CALLBACK;
			}
			String date=DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE);
			String value = baseRedisDao.getRedisValue(key);
			JSONObject json = JSONObject.fromObject(value);
			commJdbcDao.InsertCallCdr(hangup.getOrderid(), type, 
					json.getString("appid"), hangup.getCaller(), hangup.getCalled(), hangup.getCallSid(), 
					hangup.getStarttime(), hangup.getStarttime(), Integer.parseInt(hangup.getTalkDuration()), hangup.getRecordurl(), "", 0,0);
			baseRedisDao.deleteRedisValue(key);
		} catch (CCPRedisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
