package com.psyb.service.ivr.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psyb.service.common.Constants;
import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.dao.CommonJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPRedisException;
import com.psyb.service.common.util.Base64;
import com.psyb.service.common.util.DateUtil;
import com.psyb.service.common.util.StringUtil;
import com.psyb.service.inlet.dao.InletJdbcDao;
import com.psyb.service.ivr.dao.IVRJdbcDao;
import com.psyb.service.ivr.mode.IVRCmd;
import com.psyb.service.ivr.mode.NumInfo;
import com.psyb.service.ivr.mode.Schedule;
import com.psyb.service.ivr.service.IVRService;
import com.psyb.service.ytx.YTXService;

@Service
public class IVRServiceImpl implements IVRService {

	protected final Logger logger = LogManager.getLogger(getClass().getName());
	@Autowired
	private IVRJdbcDao ivrJdbcDao;
	
	private static final String IVR_HANGUP = "<Hangup/>";
	
	@Autowired
	private InletJdbcDao inletJdbcDao;
	
	@Autowired
	private CommonJdbcDao commonJdbcDao;

	@Autowired
	private BaseRedisDao baseRedisDao;
	
	@Autowired
	private YTXService ytxService;
	
	@Override
	public String handleInComingCall(int calltype, String appid, String callid,
			String caller, String called, String direct)
			throws CCPDaoException {
		
		String sernum = caller;
		int billtype = Constants.CT_IVROUT;
		if(Integer.parseInt(direct) == 0)
		{//显号
			sernum = called;
			billtype = Constants.CT_IVRIN;
		}
		
		//AXB呼叫
		String key = BaseRedisDao.REDIS_X_NUM+caller+"#"+called;
		try {
			String transnum = baseRedisDao.getRedisValue(key);
			logger.info("handleInComingCall getRedisKey =" + key + ", transferNum =" + transnum);
			if(!StringUtils.isEmpty(transnum))
			{
				String consu= "<ConsultationCall number='"+transnum+"' disnumber='"+called+"'>\n";
				consu += "<Play loop='"+-1+"'>cailing.wav</Play>\n";
				consu += "</ConsultationCall>\n";
				return consu;
			}
		} catch (CCPRedisException e) {
			e.printStackTrace();
			logger.info("handleInComingCall AXB getRedisKey =" + key + ", e="+e.toString());
		}
		
		//外呼通知
		key = BaseRedisDao.REDIS_CDR_DATA+callid;
		try {
			String value = baseRedisDao.getRedisValue(key);
			if(!StringUtils.isEmpty(value))
			{
				JSONObject json = JSONObject.fromObject(value);
				int btype = json.optInt("billtype", -1);
				if(btype == Constants.CT_LANDCALL){
					int loop = json.optInt("loop", 1);
					String mname = json.optString("medianame", "");
					String mtxt = json.optString("mediatxt", "");
					int dtmf = json.optInt("dtmf", -1);
					String landcmd="";
					if(dtmf > 0){
						landcmd= "<Get action='dtmfreport' numdigits='"+dtmf+"'>\n";
						if(!StringUtils.isEmpty(mname)){
							landcmd += "<Play loop='"+loop+"'>"+mname+"</Play>\n";
						}else if(!StringUtils.isEmpty(mtxt)){
							landcmd += "<PlayTTS loop='"+loop+"'>"+mtxt+"</PlayTTS>\n";
						}
						landcmd += "</Get>\n";
					}else{
						if(!StringUtils.isEmpty(mname)){
							landcmd += "<Play loop='"+loop+"'>"+mname+"</Play>\n";
						}else if(!StringUtils.isEmpty(mtxt)){
							landcmd += "<PlayTTS loop='"+loop+"'>"+mtxt+"</PlayTTS>\n";
						}
					}
					logger.info("handleInComingCall LandCall Cmd =" + landcmd);
					if(!StringUtils.isEmpty(landcmd)){
						return landcmd;
					}
				}
			}else{ //记录话单信息
				JSONObject json = new JSONObject();
				json.element("appid", appid);
    			json.element("caller", caller);
    			json.element("called", called);
    			json.element("billtype", billtype);
    			try {
					baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
				} catch (CCPRedisException e) {
					e.printStackTrace();
				}
			}
			
		} catch (CCPRedisException e) {
			e.printStackTrace();
			logger.info("handleInComingCall LandCall getRedisKey =" + key + ", e="+e.toString());
		}
		
		//接入号工作时间  日程管理
		List<Schedule> schedule = ivrJdbcDao.getWorkSchedule(appid, called);
		if(schedule.size() > 0)
		{
			boolean boffwork=false;
			Iterator<Schedule> it = schedule.iterator();
			Date date = new Date();
			String shm = DateUtil.dateToStr(date, DateUtil.HM);
			String sdate = DateUtil.dateToStr(date, DateUtil.DATE);
			String sweek = DateUtil.getWeekOfDate(date);
	        while(it.hasNext())
	        {
	        	Schedule sche = (Schedule)it.next();
	        	
//	        	"Sun#Mon"
	        	if(!StringUtils.isEmpty(sche.getOffweekday()))
	        	{
	        		if(sche.getOffweekday().contains(sweek))
	        		{
	        			if(!StringUtils.isEmpty(sche.getOffworkprompt()))
	        			{//播放非工作时间3遍
	        				return "<Play loop='3'>"+sche.getOffworkprompt()+"</Play>";
	        			}
	        			boffwork=true;
	        		}
	        	}
	        	
//	        	"1001-1003"
	        	if(!StringUtils.isEmpty(sche.getOffworkdate()))
	        	{
	        		String[] workdates =sche.getOffworkdate().split("-");
	        		if(workdates.length == 2)
	        		{
	        			if(sdate.compareTo(workdates[0]) >= 0 && sdate.compareTo(workdates[1]) <= 0)
	        			{
	        				if(!StringUtils.isEmpty(sche.getOffworkprompt()))
	        				{//播放非工作时间3遍
	        					return "<Play loop='3'>"+sche.getOffworkprompt()+"</Play>";
	        				}
	        				boffwork=true;
	        			}
	        		}
	        	}
	        	
//	        	"09:30-10:30"
	        	if(!StringUtils.isEmpty(sche.getOffworktime()))
	        	{
	        		String[] worktimes =sche.getOffworktime().split("-");
	        		if(worktimes.length == 2)
	        		{
	        			if(shm.compareTo(worktimes[0]) >= 0 && shm.compareTo(worktimes[1]) <= 0)
	        			{
	        				if(!StringUtils.isEmpty(sche.getOffworkprompt()))
	        				{//播放非工作时间3遍
	        					return "<Play loop='3'>"+sche.getOffworkprompt()+"</Play>";
	        				}
	        				boffwork=true;
	        			}
	        		}
	        	}
	        	
	        	if(boffwork)
	        	{
	        		logger.info("handleInComingCall offWork Schedule: Offworkdate =" + sche.getOffworkdate() + ", Offworktime =" + sche.getOffworktime() + ", Offweekday =" + sche.getOffweekday() + ", Offworkprompt =" + sche.getOffworkprompt());
	        	}
	        }
		}
		
		NumInfo serviceNum = ivrJdbcDao.getNumInfoByTo(appid, called);
		if(serviceNum!= null)
		{//1 直线总机；2 总机流程； 3 优先外呼绑定关联分机； 4 直接转号码
			if(serviceNum.getType() == 1)
			{//直线呼叫
				if(!StringUtils.isEmpty(serviceNum.getMemberId()))
				{
					NumInfo numinfo2 = ivrJdbcDao.getCallOutNum(appid, serviceNum.getMemberId());
					if(numinfo2 != null)
					{
						return combinConsulCmd(appid, numinfo2.getVoipaccount(), serviceNum.getPlayId(), called);
					}
					logger.error("handleInComingCall Service Num: type =" + serviceNum.getType() + ", unmatch direct line");
				}
			}
			else if(serviceNum.getType() == 2)
			{//总机呼叫
//				if(!StringUtils.isEmpty(serviceNum.getMemberId()))
//				{
//					NumInfo num = commonJdbcDao.GetUserBindSerNum(appid, caller);
//					if(num != null)
//					{//配置4规则，用户呼入外呼内线  显示用户号码
//						return combinConsulCmd(appid, num.getNumber(), serviceNum.getPlayId(), caller);
//					}
//					logger.warn("handleInComingCall Service Num: type =" + serviceNum.getType() + ", Unmatch ContusmNum");
//				}
			}
			else if(serviceNum.getType() == 3)
			{//总机呼叫，优先上次关联呼叫
				if(!StringUtils.isEmpty(serviceNum.getMemberId()))
				{
					NumInfo num = commonJdbcDao.GetUserBindSerNum(appid, caller);
					if(num != null)
					{//配置4规则，用户呼入外呼内线  显示用户号码
						return combinConsulCmd(appid, num.getNumber(), serviceNum.getPlayId(), called);
					}
					logger.warn("handleInComingCall Service Num: type =" + serviceNum.getType() + ", Unmatch ContusmNum");
				}
			}
			if(!StringUtils.isEmpty(serviceNum.getMenuId()))
			{
				IVRCmd ivrcmd = ivrJdbcDao.getMenuInfo(appid, serviceNum.getMenuId());
				
				if(ivrcmd != null)
				{
					if(ivrcmd.getType() == 1)
					{
						String play = "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>\n";
						if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
						{
							play += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>";
						}
						else 
						{
							play += "<Hangup/>";
						}
						return play;
					}
					else if(ivrcmd.getType() == 2)
					{
						String consu= "<ConsultationCall number='"+ivrcmd.getNumber()+"' disnumber='"+sernum+"'>\n";
						if(!StringUtils.isEmpty(ivrcmd.getPlayfile()))
						{
							consu += "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>\n";
						}
						consu += "</ConsultationCall>\n";
						if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
						{
							consu += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>";
						}
						
						return consu;
					}
					else if(ivrcmd.getType() == 3
							|| ivrcmd.getType() == 4)
					{
						String get = "<Get ";
						String action = "dtmfreport?type="+ivrcmd.getType();
						if(!StringUtils.isEmpty(serviceNum.getPlayId()))
						{
							action +="&playid=" + serviceNum.getPlayId();
						}
						if(!StringUtils.isEmpty(ivrcmd.getFinishkey()))
						{
							get += " finishkey='" +ivrcmd.getFinishkey()+ "'";
							try {
								action +="&fink=" + URLEncoder.encode(ivrcmd.getFinishkey(), "utf-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(!StringUtils.isEmpty(ivrcmd.getFirstendkey()))
						{
							get += " firstendkey='" +ivrcmd.getFirstendkey()+ "'";
							try {
								action +="&firk=" + URLEncoder.encode(ivrcmd.getFirstendkey(), "utf-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(!StringUtils.isEmpty(serviceNum.getMemberId()))
						{
							action +="&mid=" + serviceNum.getMemberId();
						}
						action +="&dnum=" + sernum;
						
						get += " action='" +action+ "'";
						
						if(ivrcmd.getNumdigits() > 0)
						{
							get += " numdigits='" +ivrcmd.getNumdigits()+ "'";
						}
						if(ivrcmd.getTimeout() > 0)
						{
							get += " timeout='" +ivrcmd.getTimeout()+ "'";
						}
						get += ">\n";
						
						if(!StringUtils.isEmpty(ivrcmd.getPlayfile()))
						{
							get += "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>\n";
						}
						get += "</Get>\n";
						if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
						{
							get += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>";
						}
						return get;
					}
					
				}
			}
		}
		return IVR_HANGUP;
	}
	
	@Override
	public void handleEndComingCall(String appid, String callsid, String starttime, String endtime, String relationcallid, int duration, String recordurl, int errorcode) throws CCPDaoException{
		String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
		try {
			String value = baseRedisDao.getRedisValue(key);
			if(!StringUtils.isEmpty(value))
			{
				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
				JSONObject json = JSONObject.fromObject(value);
				int bill = json.optInt("billtype", Constants.CT_IVRIN);
				
				int asr = 0;
				if(bill == Constants.CT_LANDCALL){
					int result = -1;
					if(duration > 0)
					{
						result = 1;
					}
					
					if(errorcode/1000 == 107)
					{
						asr = errorcode - 107000;
					}
					inletJdbcDao.updateCallResult(callsid, starttime, endtime, String.valueOf(duration), result, asr);
				}
				commonJdbcDao.InsertCallCdr(uuid, bill, 
						json.getString("appid"), callsid, json.getString("caller"), json.getString("called"), 
						starttime, endtime, relationcallid, duration, recordurl, asr);
				baseRedisDao.deleteRedisValue(key);
				
				if(bill == Constants.CT_LANDCALL){
					
	        		String taskkey = BaseRedisDao.REDIS_TASK+appid+json.getInt("taskid");
	        		do {
						String numinfo = baseRedisDao.popRedisValue(taskkey);
						if(!StringUtils.isEmpty(numinfo))
		        		{
							JSONObject jsonnum = JSONObject.fromObject(numinfo);
							HashMap<String, Object> hashmap=ytxService.sendIVRDial(jsonnum.getString("accountid"), jsonnum.getString("authtoken"),appid, 
									jsonnum.getString("caller"), jsonnum.getString("called"), "");
		                    String statuscode = (String)hashmap.get("statusCode");
	//	                    String statusMsg = (String)hashmap.get("statusMsg");
		                    
		                    if(!StringUtils.isEmpty(statuscode) && Integer.parseInt(statuscode) ==0)
		                    {
		                    	logger.info("HandleLandCall num="+jsonnum.get("called") + ", statuscode =" + statuscode +", incode="+Integer.parseInt(statuscode));
		                    	if(hashmap.containsKey("data"))
		                    	{
		                    		@SuppressWarnings("unchecked")
		        					HashMap<String, Object> hashmap2 = (HashMap<String, Object>)hashmap.get("data");
		                    		if(hashmap2.containsKey("callSid"))
		                    		{
		                    			String sid = (String)hashmap2.get("callSid");
		                    			String orderid = (String)hashmap2.get("orderId");
		                    			inletJdbcDao.updateCallDate(appid, sid, jsonnum.getLong("id"));
		                        		String key2 = BaseRedisDao.REDIS_CDR_DATA+callsid;
		                    			json.element("orderid", orderid);
		            					baseRedisDao.saveRedisValue(key2, json.toString(), Constants.CDR_DISABLED_TIME);
		            					break;
		                    		}
		                    	}
		                    }
		        		}else {
		        			break;
		        		}
						
	        		}while(true);
				}
			}
			
		} catch (CCPRedisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String HandleDtmfReport(String appid, String callid, String type, String result,
			String fink, String firk, String digits, String playid, String dnum, String mid)
			throws CCPDaoException, UnsupportedEncodingException {
		
		String cmd = "<Hangup/>";
		
		logger.info("HandleDtmfReport fink= "+ fink + ", firk = " + firk + ", digits = " + digits + ", dnum = " + dnum + ", mid = " + mid);
//		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		//外呼通知
		String key = BaseRedisDao.REDIS_CDR_DATA+callid;
		try {
			String value = baseRedisDao.getRedisValue(key);
			if(!StringUtils.isEmpty(value))
			{
				JSONObject json = JSONObject.fromObject(value);
				int btype = json.optInt("billtype", -1);
				if(btype == Constants.CT_LANDCALL){
					ivrJdbcDao.updateCallDtmf(callid, digits);
					return cmd;
				}
			}
		} catch (CCPRedisException e) {
			e.printStackTrace();
			logger.info("HandleDtmfReport CDRData getRedisKey =" + key + ", e="+e.toString());
		}
		
		if(!StringUtils.isEmpty(firk))
		{
			String sfirk = URLDecoder.decode(firk, "utf-8");
			if(sfirk.equals(digits))
			{
				if(!StringUtils.isEmpty(mid))
				{
					NumInfo num = commonJdbcDao.GetUserBindSerNum(appid, dnum);
					if(num != null)
					{//配置4规则，用户呼入外呼内线  显示用户号码
						return combinConsulCmd(appid, num.getNumber(), playid, dnum);
					}
				} 
			}
		}
		
		if(!StringUtils.isEmpty(fink))
		{
			String sfink = URLDecoder.decode(fink, "utf-8");
			if(digits.endsWith(sfink))
			{
				digits = digits.substring(0, digits.length()-sfink.length());
			}
			
		}
		
		if(!StringUtils.isEmpty(type) && !StringUtils.isEmpty(result))
		{
			if(Integer.parseInt(result) == 0)
			{
				int nType = Integer.parseInt(type);
				if(nType == IVRCmd.TYPE_GET_PBX)
				{
					cmd = HandlePbxExtenByDtmf(appid, digits, playid, dnum);
				}
				else if(nType == IVRCmd.TYPE_GET_EAR)
				{
					cmd = getTransferNumByDtmf(appid, digits, playid,dnum);
				}
			}
			
		}
		return cmd;
	}
	
	@Override
	public String getTransferNumByDtmf(String appid, String dtmf, String playid, String dnum)
			throws CCPDaoException {
		// TODO Auto-generated method stub
		NumInfo num= ivrJdbcDao.getTransferByDtmf(appid, dtmf);
		
		if(num != null)
		{
			if(num.getNumber().length() > 0)
			{
				return combinConsulCmd(appid, num.getNumber(), playid,dnum);
			}
		}
		return IVR_HANGUP;
	}

	@Override
	public String HandlePbxExtenByDtmf(String appid, String dtmf, String playid, String dnum)
			throws CCPDaoException {
		NumInfo num= ivrJdbcDao.getPbxExtenByDtmf(appid, dtmf);
		if(num != null)
		{
			if(!StringUtils.isEmpty(num.getVoipaccount()))
			{
				return combinConsulCmd(appid, num.getVoipaccount(), playid,dnum);
			}
		}
		return IVR_HANGUP;
	}

	@Override
	public String handleRedirect(String appid, String tag)
			throws CCPDaoException {
		if(!StringUtils.isEmpty(tag))
		{
			IVRCmd ivrcmd = ivrJdbcDao.getMenuInfo(appid, tag);
			
			if(ivrcmd != null)
			{
				if(ivrcmd.getType() == 1)
				{
					String play = "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>\n";
					if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
					{
						play += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>";
					}
					else 
					{
						play += "<Hangup/>";
					}
					return play;
				}
				else if(ivrcmd.getType() == 2)
				{
					String consu= "<ConsultationCall number='"+ivrcmd.getNumber()+"'>\n";
					
					if(!StringUtils.isEmpty(ivrcmd.getPlayfile()))
					{
						consu += "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>";
					}
					consu += "</ConsultationCall>\n";
					if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
					{
						consu += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>";
					}
					
					return consu;
				}
				else if(ivrcmd.getType() == 3
						|| ivrcmd.getType() == 4)
				{
					String get = "<Get action='dtmfreport?type="+ivrcmd.getType()+"'";
					if(ivrcmd.getNumdigits() > 0)
					{
						get += " numdigits='" +ivrcmd.getNumdigits()+ "'";
					}
					if(!StringUtils.isEmpty(ivrcmd.getFinishkey()))
					{
						get += " finishkey='" +ivrcmd.getFinishkey()+ "'";
					}
					if(ivrcmd.getTimeout() > 0)
					{
						get += " timeout='" +ivrcmd.getTimeout()+ "'";
					}
					get += ">\n";
					
					if(!StringUtils.isEmpty(ivrcmd.getPlayfile()))
					{
						get += "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>\n";
					}
					get += "</Get>\n";
					if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
					{
						get += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>";
					}
					return get;
				}
				
			}
		}
		return IVR_HANGUP;
	}

	private String combinConsulCmd(String appid, String number, String playid, String dnum) throws CCPDaoException
	{
		String consu= "<ConsultationCall record='true' number='"+number+"'";
		
		if(!StringUtils.isEmpty(dnum))
		{
			consu += " disnumber='"+dnum+"'";
		}
		
		consu += ">\n";
		
		if(!StringUtils.isEmpty(playid))
		{
			IVRCmd ivrcmd = ivrJdbcDao.getPlayInfo(appid, playid);
			if(ivrcmd != null)
			{
				consu += "<Play loop='"+ivrcmd.getLoop()+"'>"+ivrcmd.getPlayfile()+"</Play>\n";
				consu += "</ConsultationCall>\n";
				if(!StringUtils.isEmpty(ivrcmd.getRedirectid()))
				{
					consu += "<Redirect tag='"+ivrcmd.getRedirectid()+"'>redirect</Redirect>\n";
				}
				else
				{
					consu += "<Hangup/>\n";
				}
			}
			else
			{
				consu += "</ConsultationCall>\n";
				consu += "<Hangup/>\n";
			}
		}
		else
		{
			consu += "<Play loop='-1'>ccp_transprompt.wav</Play>\n";
			consu += "</ConsultationCall>\n<Hangup/>\n";
		}
		return consu;
	}

	@Override
	public void handleCdr(String appid, String callsid, String caller,
			String called, int direct, String starttime, String endtime,
			String relationcallid, int duration, String recordurl, int errorcode)
			throws CCPDaoException {
//		appid=ff8080813f84717a013f847540ef0000&caller=01051616199&called=80037400000003&direction=1&
//		callid=17092716390803280001000400011fab&relatecallid=17092716390720720001000400011fa9&maincall=0&
//				callresult=1&callstate=200&callstarttime=20170927163908&sipcause=200&ringtime=20170927163908&
//				starttime=20170927163910&endtime=20170927163911&duration=1&timestamp=20170927163911 HTTP/1.1
		String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
		try {
			String value = baseRedisDao.getRedisValue(key);
			if(!StringUtils.isEmpty(value))
			{
				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
				JSONObject json = JSONObject.fromObject(value);
				int bill = json.optInt("billtype", Constants.CT_IVRIN);
				commonJdbcDao.InsertCallCdr(uuid, bill, 
						appid, callsid, json.optString("caller", caller), json.optString("called", called), 
						starttime, endtime, relationcallid, duration, recordurl, 0);
				if(bill == Constants.CT_LANDCALL){
					int result = -1;
					if(duration > 0)
					{
						result = 1;
					}
					inletJdbcDao.updateCallResult(callsid, starttime, endtime, String.valueOf(duration), result, 0);
				}
			}
			baseRedisDao.deleteRedisValue(key);
		} catch (CCPRedisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		int type = Constants.CT_IVRIN;
//		if(direct == 1)
//		{
//			type = Constants.CT_IVROUT;
//		}
//		String orderid = "AS"+System.currentTimeMillis()+StringUtil.generateString(11);
//		commonJdbcDao.InsertCallCdr(orderid, type, appid, caller, called, callsid, 
//				starttime, endtime, duration, recordurl, relationcallid, errorcode, 0);
		
	}

	
}
