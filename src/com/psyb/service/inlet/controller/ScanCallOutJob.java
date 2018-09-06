package com.psyb.service.inlet.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.psyb.service.common.Constants;
import com.psyb.service.common.QuartzManager;
import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPRedisException;
import com.psyb.service.common.util.Base64;
import com.psyb.service.inlet.dao.InletJdbcDao;
import com.psyb.service.inlet.model.CallNum;
import com.psyb.service.inlet.model.CallTxt;
import com.psyb.service.ytx.YTXService;

public class ScanCallOutJob implements Job {
	
	protected final Logger logger = LogManager.getLogger(getClass().getName());
	
	static int num=0;
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		try {
			int result = arg0.getJobDetail().getJobDataMap().getInt("result");
			int taskid = arg0.getJobDetail().getJobDataMap().getInt("taskid");
			String appid = arg0.getJobDetail().getJobDataMap().getString("appid");
			String JobID = arg0.getJobDetail().getJobDataMap().getString("JobID");
			CallTxt txt = (CallTxt)arg0.getJobDetail().getJobDataMap().get("txt");
			int indexnum = arg0.getJobDetail().getJobDataMap().getInt("indexnum");
			int callnum = arg0.getJobDetail().getJobDataMap().getInt("callnum");
			List<CallNum> disNums = (List<CallNum>)arg0.getJobDetail().getJobDataMap().get("disNums");
			InletJdbcDao inletJdbcDao = (InletJdbcDao)arg0.getJobDetail().getJobDataMap().get("inletJdbcDao");
			BaseRedisDao baseRedisDao = (BaseRedisDao)arg0.getJobDetail().getJobDataMap().get("baseRedisDao");
			YTXService ytxService = (YTXService)arg0.getJobDetail().getJobDataMap().get("ytxService");
			
			List<CallNum> nums = inletJdbcDao.getCallNum(appid, result, taskid, callnum, false);
			
			logger.info("HandleLandCall list  size = "+nums.size() + ", taskid = " + taskid);
			if(nums.size() == 0)
			{
				QuartzManager.removeJob(JobID);
				return;
			}
			String landTxt = txt.getMsg();
			logger.info("HandleLandCall list  indexnum = "+indexnum+", result="+result +", landTxt="+txt.getMsg() +", size="+nums.size());
			
			
			
			Iterator<CallNum> it1 = nums.iterator();
			JSONObject json = new JSONObject();
			json.element("appid", appid);
			json.element("taskid", taskid);
			
			String sBase = Base64.encodeToString(json.toString());
	        while(it1.hasNext())
	        {
	        	CallNum num = (CallNum)it1.next();
	        	
	        	if(num.getDatas().length() > 0)
	        	{ 
	                //将jsonArray字符串转化为JSONArray  
	                JSONArray jsonArray = JSONArray.fromObject(num.getDatas());  
	                for(int i=0; i < jsonArray.size(); i++)
	                {
	                	String index = "{" + (i+1) +"}";
	                	landTxt = landTxt.replace(index, (String)jsonArray.get(i));
	                }
	        	}
	        	String cdisnum = disNums.get(indexnum%disNums.size()).getNumber();
	            indexnum ++;
	            logger.info("HandleLandCall list  indexnum = "+indexnum+", cdisnum = "+cdisnum);
	            HashMap<String, Object> hashmap=ytxService.sendIVRDial(num.getAccountSid(), num.getAuthToken(),appid, cdisnum, num.getNumber(), sBase);
//	            HashMap<String, Object> hashmap=ytxService.sendVoiceNotice(num.getAccountSid(), num.getAuthToken(),appid, cdisnum,txt.getLoop(), num.getNumber(), landTxt, txt.getMediaName(), sBase);
	            String statuscode = (String)hashmap.get("statusCode");
	            String statusMsg = (String)hashmap.get("statusMsg");
	            logger.info("HandleLandCall num="+num.getNumber() + ", statuscode =" + statuscode +", incode="+Integer.parseInt(statuscode));
	            if(Integer.parseInt(statuscode) ==0)
	            {
	            	if(hashmap.containsKey("LandingCall"))
	            	{
	            		@SuppressWarnings("unchecked")
						HashMap<String, Object> hashmap2 = (HashMap<String, Object>)hashmap.get("LandingCall");
	            		if(hashmap2.containsKey("callSid"))
	            		{
	            			String callsid = (String)hashmap2.get("callSid");
	            			String orderid = (String)hashmap2.get("orderId");
	            			inletJdbcDao.updateCallDate(appid, callsid, num.getId());
	            			String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
	            			json.element("orderid", orderid);
	            			json.element("caller", cdisnum);
	            			json.element("called", num.getNumber());
	            			json.element("billtype", Constants.CT_LANDCALL);
	            			try {
								baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
							} catch (CCPRedisException e) {
								e.printStackTrace();
							}
	            		}
	            	}
	            	else
	            	{
	            		String callsid = (String)hashmap.get("callSid");
	        			String orderid = (String)hashmap.get("orderId");
	        			inletJdbcDao.updateCallDate(appid, callsid, num.getId());
	            		String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
	        			json.element("orderid", orderid);
	        			json.element("caller", cdisnum);
	        			json.element("called", num.getNumber());
	        			json.element("billtype", Constants.CT_LANDCALL);
	        			json.element("medianame", txt.getMediaName());
	        			json.element("mediatxt", landTxt);
	        			json.element("loop", txt.getLoop());
	        			json.element("dtmf", num.getDtmf());
	        			try {
							baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
						} catch (CCPRedisException e) {
							e.printStackTrace();
						}
	            	}
	            }
	            else
	            {
	            	QuartzManager.removeJob(JobID);
	            }
	            arg0.getJobDetail().getJobDataMap().put("indexnum", indexnum);
	            logger.info("HandleLandCall num="+num.getNumber() + ", it1 =" + it1.hasNext());
	        }
		} catch (CCPDaoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
