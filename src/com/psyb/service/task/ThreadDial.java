package com.psyb.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPRedisException;
import com.psyb.service.common.util.Base64;
import com.psyb.service.inlet.dao.InletJdbcDao;
import com.psyb.service.inlet.model.Account;
import com.psyb.service.inlet.model.CallNum;
import com.psyb.service.inlet.model.CallTxt;
import com.psyb.service.ytx.YTXService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ThreadDial extends Thread {

	protected final Logger logger = LogManager.getLogger(getClass().getName());
	
	private InletJdbcDao inletJdbcDao;
	private BaseRedisDao baseRedisDao;
	private YTXService ytxService;
	
	private String m_sAppid;
	private int m_nResult;
	private String m_sTaskId;
	private String m_sDisnum;
	
	private boolean m_bStartCall; //是否发送请求
	private int m_nTurn;  //计数
	public ThreadDial(String appid, int result, String task, String disnum)
	{
		this.m_sAppid = appid;
		this.m_nResult = result;
		this.m_sTaskId = task;
		this.m_sDisnum = disnum;
		m_bStartCall = true;
		m_nTurn = 0;
		baseRedisDao = (BaseRedisDao) ScriptManager.getScriptManager().getBean("baseRedisDaoImpl");
		inletJdbcDao = (InletJdbcDao) ScriptManager.getScriptManager().getBean("inletJdbcDaoImpl");
		ytxService = (YTXService) ScriptManager.getScriptManager().getBean("yTXService");
	}
	
	@Override
    public void run() {
		logger.info("线程"+Thread.currentThread().getName() + "开始处理");
		int callnum = 10;
		Account account = ScriptManager.getScriptManager().getAccountByAppId(m_sAppid);
		if(account != null)
		{
			callnum = account.getCallnums();
		}
		boolean update = false;
        while(true)
        {
        	try {
        		
        		Thread.sleep(200); 
        		
        		List<CallNum> nums = inletJdbcDao.getCallNum(m_sAppid, m_nResult, Integer.parseInt(m_sTaskId), callnum, true);
        		
        		logger.info("HandleLandCall list  size = "+nums.size() + ", taskid = " + m_sTaskId);
        		if(nums.size() == 0)
        		{
        			break;
        		}
        		
        		String landTxt="";
        		
        		CallTxt txt = inletJdbcDao.getLandCallTxt(m_sAppid, m_sTaskId);
        		if(txt != null)
        		{
        			landTxt = txt.getMsg();
        		}
        		
        				
        		if(StringUtils.isEmpty(landTxt))
        		{
        			break;
        		}
        		
//        		List<CallNum> disNums = new ArrayList<CallNum>();
//        		if(StringUtils.isEmpty(m_sDisnum))
//        		{
//        			List<CallNum> numlist = inletJdbcDao.getServiceNum(m_sAppid); //获取显号列表
//        			if(numlist.size() > 0)
//        			{
//        				disNums.addAll(numlist);
//        			}
//        			else
//        			{
//        				break;
//        			}
//        			
//        		}
//        		else
//        		{
//        			CallNum num = new CallNum();
//        			num.setNumber(m_sDisnum);
//        			disNums.add(num);
//        		}
        		if(StringUtils.isEmpty(landTxt))
        		{
        			return;
        		}
        		
        		logger.info("HandleLandCall list  disnum = "+m_sDisnum+", result="+m_nResult +","
        				+ " taskId="+m_sTaskId +", size="+nums.size() +", landTxt="+landTxt);
        		String servicekey = BaseRedisDao.REDIS_SERVICE_NUM+m_sAppid;
        		String taskkey = BaseRedisDao.REDIS_TASK+m_sAppid+m_sTaskId;
        		Iterator<CallNum> itr = nums.iterator();
                while(itr.hasNext())
                {
                	CallNum num = (CallNum)itr.next();
                	JSONObject json = new JSONObject();
            		json.element("appid", m_sAppid);
            		json.element("taskid", Integer.parseInt(m_sTaskId));
            		json.element("account", num.getAccountSid());
            		json.element("authtoken", num.getAuthToken());
            		json.element("called", num.getNumber());
            		json.element("billtype", Constants.CT_LANDCALL);
        			json.element("medianame", txt.getMediaName());
        			json.element("mediatxt", landTxt);
        			json.element("loop", txt.getLoop());
        			json.element("dtmf", num.getDtmf());
        			json.element("id", num.getId());
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
                	json.element("landtxt", landTxt);
                	String disnum = m_sDisnum;
                	if(StringUtils.isEmpty(disnum)){
                		disnum = baseRedisDao.popRedisValue(servicekey);
                		if(!StringUtils.isEmpty(disnum)){
                    		baseRedisDao.pushRedisValue(servicekey, disnum);
                    	}
                	}
                	if(!StringUtils.isEmpty(disnum)){
                		json.element("caller", disnum);
                	}
                	update = false;
            		if(!m_bStartCall){
            			baseRedisDao.pushRedisValue(taskkey, json.toString());
            		}else {
            			
                        HashMap<String, Object> hashmap=ytxService.sendIVRDial(num.getAccountSid(), num.getAuthToken(),m_sAppid, disnum, num.getNumber(), "");
                        String statuscode = (String)hashmap.get("statusCode");
//                        String statusMsg = (String)hashmap.get("statusMsg");
                        
                        if(!StringUtils.isEmpty(statuscode) && Integer.parseInt(statuscode) ==0)
                        {
                        	logger.info("HandleLandCall num="+num.getNumber() + ", statuscode =" + statuscode +", incode="+Integer.parseInt(statuscode));
                        	if(hashmap.containsKey("data"))
                        	{
                        		@SuppressWarnings("unchecked")
            					HashMap<String, Object> hashmap2 = (HashMap<String, Object>)hashmap.get("data");
                        		if(hashmap2.containsKey("callSid"))
                        		{
                        			String callsid = (String)hashmap2.get("callSid");
                        			String orderid = (String)hashmap2.get("orderId");
                        			inletJdbcDao.updateCallDate(m_sAppid, callsid, num.getId());
                        			update = true;
                        			m_nTurn++;
                            		String key = BaseRedisDao.REDIS_CDR_DATA+callsid;
                        			json.element("orderid", orderid);
                					baseRedisDao.saveRedisValue(key, json.toString(), Constants.CDR_DISABLED_TIME);
                        		}
                        	}
                        }
                        if(m_nTurn >= callnum) {
            				m_bStartCall = false;
            			}
            		}
            		if(!update)
            			inletJdbcDao.updateCallDate(m_sAppid, "", num.getId());
                }
    		} catch (CCPDaoException e) {
    			e.printStackTrace();
    		} catch (CCPRedisException e) {
    			e.printStackTrace();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        }
        
        logger.info("线程"+Thread.currentThread().getName()+"执行完毕");
    }

}
