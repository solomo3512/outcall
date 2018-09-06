package com.psyb.service.inlet.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psyb.service.common.Constants;
import com.psyb.service.common.QuartzManager;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.dao.CommonJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPRedisException;
import com.psyb.service.common.model.Response;
import com.psyb.service.common.util.Base64;
import com.psyb.service.inlet.controller.ScanCallOutJob;
import com.psyb.service.inlet.dao.InletJdbcDao;
import com.psyb.service.inlet.model.Account;
import com.psyb.service.inlet.model.CallNum;
import com.psyb.service.inlet.model.CallResult;
import com.psyb.service.inlet.model.CallTxt;
import com.psyb.service.inlet.service.InletService;
import com.psyb.service.ytx.YTXService;
import com.psyb.service.task.ThreadDial;

@Service
public class InletServiceImpl implements InletService {

	protected final Logger logger = LogManager.getLogger(getClass().getName());
	@Autowired
	private InletJdbcDao inletJdbcDao;
	
	@Autowired
	private CommonJdbcDao commonJdbcDao;
	
	@Autowired
	private YTXService ytxService;
	
	@Autowired
	private BaseRedisDao baseRedisDao;
	
	@Override
	public Response handleLandCall(String appid, String type, String taskId, String disnum) throws CCPDaoException
	{
		int result = 0;
		if(!StringUtils.isEmpty(type) && type.equals("recall"))
		{
			result=-1;
		}
		
		ThreadDial dthread = new ThreadDial(appid, result, taskId, disnum);
		dthread.start();
		
//		int callnum = 10;
//		Account account = ScriptManager.getScriptManager().getAccountByAppId(appid);
//		if(account != null)
//		{
//			callnum = account.getCallnums();
//		}
//		
//		List<CallNum> nums = inletJdbcDao.getCallNum(appid, result, Integer.parseInt(taskId), callnum, true);
//		
//		logger.info("HandleLandCall list  size = "+nums.size() + ", taskid = " + taskId);
//		if(nums.size() == 0)
//		{
//			return new Response(Constants.SUCC, Constants.SUCC_DESC); 
//		}
//		
//		String landTxt="";
//		
//		CallTxt txt = inletJdbcDao.getLandCallTxt(appid, taskId);
//		if(txt != null)
//		{
//			landTxt = txt.getMsg();
//		}
//		
//				
//		if(StringUtils.isEmpty(landTxt))
//		{
//			return new Response(Constants.ERR_NOTEMPID, Constants.SUCC_NOTEMPID_DESC);
//		}
//		
//		List<CallNum> disNums = new ArrayList<CallNum>();
//		if(StringUtils.isEmpty(disnum))
//		{
//			List<CallNum> numlist = inletJdbcDao.getServiceNum(appid);
//			if(numlist.size() > 0)
//			{
//				disNums.addAll(numlist);
//			}
//			else
//			{
//				return new Response(Constants.ERR_UNDISNUM, Constants.SUCC_UNDISNUM_DESC);
//			}
//			
//		}
//		else
//		{
//			CallNum num = new CallNum();
//			num.setNumber(disnum);
//			disNums.add(num);
//		}
//		
//		logger.info("HandleLandCall list  disnum = "+disnum+", type = "+type+", result="+result +","
//				+ " taskId="+taskId +", size="+nums.size() +", landTxt="+landTxt + ", CallNums = " + disNums.size());
//
//		
//		JSONObject json = new JSONObject();
//		json.element("appid", appid);
//		json.element("taskid", taskId);
//		
////		String sBase = Base64.encodeToString(json.toString());
//		
////		logger.info("HandleLandCall list  sBase = "+sBase+", userdata = "+json.toString()+", mediaName="+txt.getMediaName());
//		
//		Iterator<CallNum> it1 = nums.iterator();
//		int indexnum = 0;
//        while(it1.hasNext())
//        {
//        	CallNum num = (CallNum)it1.next();
////        	String landTxt2 = landTxt.replace("$name", num.getUsername());
//        	
//        	if(num.getDatas().length() > 0)
//        	{ 
//                //将jsonArray字符串转化为JSONArray  
//                JSONArray jsonArray = JSONArray.fromObject(num.getDatas());  
//                for(int i=0; i < jsonArray.size(); i++)
//                {
//                	String index = "{" + (i+1) +"}";
//                	landTxt = landTxt.replace(index, (String)jsonArray.get(i));
//                }
//        	}
//            String cdisnum = disNums.get(indexnum%disNums.size()).getNumber();
//            json.element("disnum", cdisnum);
//    		
//    		String sBase = Base64.encodeToString(json.toString());
//            indexnum ++;
//            logger.info("HandleLandCall list  indexnum = "+indexnum+", cdisnum = "+cdisnum);
//
//            HashMap<String, Object> hashmap=ytxService.sendIVRDial(num.getAccountSid(), num.getAuthToken(),appid, cdisnum, num.getNumber(), sBase);
//            String statuscode = (String)hashmap.get("statusCode");
//            String statusMsg = (String)hashmap.get("statusMsg");
//            
//            if(!StringUtils.isEmpty(statuscode) && Integer.parseInt(statuscode) ==0)
//            {
//            	logger.info("HandleLandCall num="+num.getNumber() + ", statuscode =" + statuscode +", incode="+Integer.parseInt(statuscode));
//            	if(hashmap.containsKey("LandingCall"))
//            	{
//            		@SuppressWarnings("unchecked")
//					HashMap<String, Object> hashmap2 = (HashMap<String, Object>)hashmap.get("LandingCall");
//            		if(hashmap2.containsKey("callSid"))
//            		{
//            			String callsid = (String)hashmap2.get("callSid");
//            			String orderid = (String)hashmap2.get("orderId");
//            			inletJdbcDao.updateCallDate(appid, callsid, num.getId());
//            			String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
//            			json.element("orderid", orderid);
//            			json.element("caller", cdisnum);
//            			json.element("called", num.getNumber());
//            			json.element("taskid", Integer.parseInt(taskId));
//            			json.element("billtype", Constants.CT_LANDCALL);
//            			try {
//							baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
//						} catch (CCPRedisException e) {
//							e.printStackTrace();
//						}
//            		}
//            	}
//            	else if(hashmap.containsKey("data"))
//            	{
//            		@SuppressWarnings("unchecked")
//					HashMap<String, Object> hashmap2 = (HashMap<String, Object>)hashmap.get("data");
//            		if(hashmap2.containsKey("callSid"))
//            		{
//            			String callsid = (String)hashmap2.get("callSid");
//            			String orderid = (String)hashmap2.get("orderId");
//            			inletJdbcDao.updateCallDate(appid, callsid, num.getId());
//                		String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
//            			json.element("orderid", orderid);
//            			json.element("caller", cdisnum);
//            			json.element("called", num.getNumber());
//            			json.element("taskid", Integer.parseInt(taskId));
//            			json.element("billtype", Constants.CT_LANDCALL);
//            			json.element("medianame", txt.getMediaName());
//            			json.element("mediatxt", landTxt);
//            			json.element("loop", txt.getLoop());
//            			json.element("dtmf", num.getDtmf());
//            			
//            			try {
//    						baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
//    					} catch (CCPRedisException e) {
//    						e.printStackTrace();
//    					}
//            		}
//            	}
//            }
//            else
//            {
//            	return new Response(statuscode, statusMsg);
//            }
//            logger.info("HandleLandCall num="+num.getNumber() + ", it1 =" + it1.hasNext());
//        }
//        
//        ScanCallOutJob job = new ScanCallOutJob();
//        String JobID = appid +"+" + taskId;
//        JobDetail jobDetail = new JobDetail(JobID, QuartzManager.TRIGGER_GROUP, job.getClass());
//		jobDetail.getJobDataMap().put("appid", appid);
//		jobDetail.getJobDataMap().put("txt", txt);
//		jobDetail.getJobDataMap().put("JobID", JobID);
//		jobDetail.getJobDataMap().put("indexnum", indexnum);
//		jobDetail.getJobDataMap().put("result", result);
//		jobDetail.getJobDataMap().put("disNums", disNums);
//		jobDetail.getJobDataMap().put("callnum", callnum);
//		jobDetail.getJobDataMap().put("taskid", Integer.parseInt(taskId));
//		jobDetail.getJobDataMap().put("inletJdbcDao", inletJdbcDao);
//		jobDetail.getJobDataMap().put("baseRedisDao", baseRedisDao);
//		jobDetail.getJobDataMap().put("ytxService", ytxService);
//        QuartzManager.addSimpleJob(JobID, job, jobDetail);
        
        return new Response(Constants.SUCC, Constants.SUCC_DESC);
	}
	
	@Override
	public void updateCallResult(CallResult callresult) throws CCPDaoException {
		int result = -1;
		if(callresult.getState().equals("0"))
		{
			result = 1;
		}
		int asrcause = 0;
		if(callresult.getCallstate() != null)
		{
			asrcause = Integer.parseInt(callresult.getCallstate());
		}
		
		inletJdbcDao.updateCallResult(callresult.getCallSid(), callresult.getStarttime(), callresult.getEndtime(), callresult.getDuration(), result, asrcause);
//		JSONObject jsonObject = JSONObject.fromObject(Base64.decodeToString(callresult.getUserData()));
		
		String key = BaseRedisDao.REDIS_CDR_DATA+callresult.getCallSid();
		try {
			String value = baseRedisDao.getRedisValue(key);
			if(!StringUtils.isEmpty(value))
			{
				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
				JSONObject json = JSONObject.fromObject(value);
				logger.info("updateCallResult userdata="+Base64.decodeToString(callresult.getUserData()) +",  Redis json="+json);
				commonJdbcDao.InsertCallCdr(uuid, json.optInt("billtype", Constants.CT_LANDCALL), 
						json.getString("appid"), json.getString("caller"), json.getString("called"), callresult.getCallSid(), 
						callresult.getStarttime(), callresult.getEndtime(), Integer.parseInt(callresult.getDuration()), "", "", asrcause, json.optInt("taskid",0));
			}
			baseRedisDao.deleteRedisValue(key);
		} catch (CCPRedisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Response handleLandCall2(String appid, String type, String tempid, String disnum) throws CCPDaoException
	{
		int result = 0;
		if(!StringUtils.isEmpty(type) && type.equals("recall"))
		{
			result=-1;
		}
		List<CallNum> nums = inletJdbcDao.getCallNum(appid, result,1, 30, true);
		
		String landTxt="";
		String tempid2="1";
		
		if(!StringUtils.isEmpty(tempid))
		{
			tempid2 = tempid;
		}
		
		if(nums.size() > 0)
		{
			
			CallTxt txt = inletJdbcDao.getLandCallTxt(appid, tempid2);
			if(txt != null)
			{
				landTxt = txt.getMsg();
			}
		}
		
		logger.info("HandleLandCall list  disnum = "+disnum+", type = "+type+", result="+result +", tempid="+tempid2 +", size="+nums.size() +", landTxt="+landTxt);
		ScanCallOutJob job = new ScanCallOutJob();
        
        
		
		Iterator<CallNum> it1 = nums.iterator();
        while(it1.hasNext())
        {
        	CallNum num = (CallNum)it1.next();
        	if(!StringUtils.isEmpty(num.getUsername()))
        	{
        		landTxt = landTxt.replace("$name", num.getUsername());
        	}
        	
        	if(!StringUtils.isEmpty(num.getCash()))
        	{
        		landTxt = landTxt.replace("$cash", num.getCash());
        	}
            
//            HashMap<String, Object> hashmap=ytxService.sendVoiceNotice(disnum, num.getNumber(), landTxt);
//            String statuscode = (String)hashmap.get("statusCode");
//            String statusMsg = (String)hashmap.get("statusMsg");
//            logger.info("HandleLandCall num="+num.getNumber() + ", statuscode =" + statuscode +", incode="+Integer.parseInt(statuscode));
//            if(Integer.parseInt(statuscode) ==0)
//            {
//            	if(hashmap.containsKey("LandingCall"))
//            	{
//            		@SuppressWarnings("unchecked")
//					HashMap<String, Object> hashmap2 = (HashMap<String, Object>)hashmap.get("LandingCall");
//            		if(hashmap2.containsKey("callSid"))
//            		{
//            			String callsid = (String)hashmap2.get("callSid");
//            			String orderid = (String)hashmap2.get("orderid");
//            			inletJdbcDao.updateCallDate(appid, callsid, result, num.getNumber());
//            		}
//            	}
//            }
//            else
//            {
//            	QuartzManager.removeJob("InletServiceImpl");
//            	return new Response(statuscode, statusMsg);
//            }
            logger.info("HandleLandCall num="+num.getNumber() + ", it1 =" + it1.hasNext());
        }
        
        JobDetail jobDetail = new JobDetail("InletServiceImpl", "InletServiceImplgroup", job.getClass());
		jobDetail.getJobDataMap().put("appid", appid);
		jobDetail.getJobDataMap().put("disnum", disnum);
		jobDetail.getJobDataMap().put("result", result);
		jobDetail.getJobDataMap().put("tempid", tempid2);
		jobDetail.getJobDataMap().put("inletJdbcDao", inletJdbcDao);
		jobDetail.getJobDataMap().put("ytxService", ytxService);
        QuartzManager.addSimpleJob("InletServiceImpl", job, jobDetail);
        
        
        return new Response(Constants.SUCC, Constants.SUCC_DESC);
	}

	@Override
	public Response handleCreateSubID(String appid, String fname)
			throws CCPDaoException {
		logger.info("handleCreateSubID appid =" + appid );
		if(StringUtils.isEmpty(appid))
		{
			return new Response(Constants.ERR_ACCOUNT, Constants.ERR_ACCOUNT_DESC);
		}
		
		Account account = inletJdbcDao.getAccount(appid, 0);
		logger.info("handleCreateSubID appid =" + appid + ", account = " + account);
		if(account != null)
		{
			HashMap<String, Object> hashmap=ytxService.createSubAccounts(account.getAccountSid(), account.getAuthToken(),appid, fname);
            String statuscode = (String)hashmap.get("statusCode");
            String statusMsg = (String)hashmap.get("statusMsg");
            logger.info("handleCreateSubID statuscode =" + statuscode);
            if(Integer.parseInt(statuscode) ==0)
            {
            	if(hashmap.containsKey("data"))
            	{
            		@SuppressWarnings("unchecked")
					HashMap<String, Object> hashmap2 = (HashMap<String, Object>) hashmap.get("data");
            		if(hashmap2.containsKey("SubAccount"))
                	{
                		@SuppressWarnings("unchecked")
                		ArrayList<HashMap<String, Object>> arrayList = (ArrayList<HashMap<String, Object>>)hashmap2.get("SubAccount");
                		for(Iterator<HashMap<String, Object>> it = arrayList.iterator();it.hasNext();)    {   

                			HashMap<String, Object> submap=it.next();
                			if(submap.containsKey("subAccountSid"))
                    		{
                    			String subAccountSid = (String)submap.get("subAccountSid");
                    			String subToken = (String)submap.get("subToken");
                    			String voipAccount = (String)submap.get("voipAccount");
                    			String voipPwd = (String)submap.get("voipPwd");
                    			inletJdbcDao.InsertAccountSub(appid, subAccountSid, subToken, voipAccount, voipPwd);
                    		}
                		}
                	}
            	}
            	return new Response(Constants.SUCC, Constants.SUCC_DESC);
            }
            return new Response(statuscode, statusMsg);
		}
		return new Response(Constants.ERR_ACCOUNT, Constants.ERR_ACCOUNT_DESC);
	}

	@Override
	public Response handleCallBack(String subaccount, String from, String fdisnum,
			String to, String tDisnum) throws CCPDaoException {
		
		Account account = inletJdbcDao.getSubAccount(subaccount);
		if(account != null)
		{
			ytxService.sendCallBack(account.getSubaccountsid(), account.getSubpwd(), account.getAppId(), from, to, fdisnum, tDisnum);
		}
		
		return new Response(Constants.SUCC, Constants.SUCC_DESC);
	}
}
