package com.psyb.service.ivr.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.ivr.mode.IVRCmd;
import com.psyb.service.ivr.mode.PCallInfo;
import com.psyb.service.ivr.service.IVRService;

@Controller("IVRController")
@RequestMapping(Constants.IVR_PATH)
public class IVRController extends AbstractController{

	
	public enum CallType {
		Type_IVR, Type_ZJ, Type_CCS;
	}
	
	@Autowired
	private IVRService ivrService;
	
	//"%s?appid=%s&callid=%s&from=%s&to=%s&direction=%d&userdata=&origcalled=fromattr=serviceno=&digits=transfernum="
	@RequestMapping(method = RequestMethod.POST, value = "/startservice")
	public void startService(
			@RequestParam("appid") String appid, 
			@RequestParam("callid") String callid, 
			@RequestParam("from") String from, 
			@RequestParam("to") String to, 
			@RequestParam("direction") String direction, 
			@RequestParam(value = "userdata", required = false) String userdata, 
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException {
		printStartTag("startService");
		printHttpPacket(request, null);
		logger.info("startservice: appid=" + appid +", callid =" + callid +", from =" + from +", to =" + to +", direction =" + direction);
//		AccountList accountList = (AccountList) accountService.getAccountById(accountSid);
		String cmd = ivrService.handleInComingCall(Constants.CT_IVRIN, appid, callid, from, to, direction);
		logger.info("startservice: response=\n"+ cmd);
		if(cmd != null)
		{
			postResponse(request, response, cmd);
		}
		else
		{
			postResponse(request, response, "<Hangup/>");
		}
		
		
		printEndTag("startService");
		
//		String play="<Play>hdfprompt.wav</Play>";
		
		
	}
	
	//stopservice?appid=%s&callid=%s&relatedcallid=
	//&starttime=&endtime=&callduration=&sstime=&setime=&seccallduration=&recordurl=&recordid=&mediaflow=&videoflow=alertingtime=&confid=&number=&errorcode=
	@RequestMapping(method = RequestMethod.POST, value = "/stopservice")
	public void stopService(
			@RequestParam("appid") String appid, 
			@RequestParam("callid") String callid, 
			@RequestParam(value = "relatedcallid", required = false) String relatedcallid, 
			@RequestParam(value = "starttime", required = false) String starttime, 
			@RequestParam(value = "endtime", required = false) String endtime, 
			@RequestParam(value = "callduration", required = false) String callduration, 
			@RequestParam(value = "sstime", required = false) String sstime, 
			@RequestParam(value = "setime", required = false) String setime, 
			@RequestParam(value = "seccallduration", required = false) String seccallduration, 
			@RequestParam(value = "recordurl", required = false) String recordurl, 
			@RequestParam(value = "errorcode", required = false) String errorcode, 
			@RequestParam(value = "userdata", required = false) String userdata, 
			@RequestParam(value = "timestamp", required = false) String timestamp,
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException {
		printStartTag("stopService");
		printHttpPacket(request, null);
		if(starttime==null || endtime == null)
		{
			starttime = endtime = timestamp;
		}
		ivrService.handleEndComingCall(appid, callid, starttime, endtime, relatedcallid, Integer.parseInt(callduration), recordurl, Integer.parseInt(errorcode));
		printEndTag("stopService");
		postResponse(request, response, "<CmdNone/>");
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/dtmfreport")
	public void demfReport(
			@RequestParam("appid") String appid, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "playid", required = false) String playid,
			@RequestParam(value = "fink", required = false) String fink,
			@RequestParam(value = "firk", required = false) String firk,
			@RequestParam(value = "dnum", required = false) String dnum,
			@RequestParam(value = "mid", required = false) String mid,
			@RequestParam("callid") String callid,
			@RequestParam("digits") String digits,
			@RequestParam("result") String result,
			@RequestParam(value = "userdata", required = false) String userdata, 
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException, UnsupportedEncodingException {
		printStartTag("demfReport");
		printHttpPacket(request, null);
//		String dtmf = request.getParameter("digits");
		String cmd= ivrService.HandleDtmfReport(appid, callid, type, result, fink, firk, digits, playid, dnum, mid);
		printEndTag("demfReport");
		logger.info("dtmfreport: response=\n"+ cmd);
		postResponse(request, response, cmd);
	}
	@RequestMapping(method = RequestMethod.POST, value = "/redirect")
	public void redirect(
			@RequestParam("appid") String appid, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam("callid") String callid,
			@RequestParam(value = "tag", required = false) String tag,
			@RequestParam(value = "result", required = false) String result,
			@RequestParam(value = "userdata", required = false) String userdata, 
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException {
		printStartTag("redirect");
		printHttpPacket(request, null);
		String cmd = ivrService.handleRedirect(appid, tag);
		printEndTag("redirect");
		postResponse(request, response, cmd);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/cdr")
	public void handleCdr(
			@RequestParam("appid") String appid, 
			@RequestParam("callid") String callid, 
			@RequestParam("caller") String caller, 
			@RequestParam("called") String called, 
			@RequestParam("direction") int direction, 
			@RequestParam("callresult") String callresult, 
			@RequestParam(value = "relatecallid", required = false) String relatedcallid, 
			@RequestParam(value = "starttime", required = false) String starttime, 
			@RequestParam(value = "endtime", required = false) String endtime, 
			@RequestParam(value = "duration", required = false) int duration, 
			@RequestParam(value = "sstime", required = false) String sstime, 
			@RequestParam(value = "setime", required = false) String setime, 
			@RequestParam(value = "seccallduration", required = false) String seccallduration, 
			@RequestParam(value = "recordurl", required = false) String recordurl, 
			@RequestParam(value = "sipcause", required = false) String sipcause,
			@RequestParam(value = "userdata", required = false) String userdata, 
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException {
		printStartTag("handleCdr");
		ivrService.handleCdr(appid, callid, caller, called, direction, starttime, endtime, relatedcallid, duration, recordurl, 0);
		printEndTag("handleCdr");
		postResponse(request, response, "<CmdNone/>");
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/{path}")
	public void receiveCustom(
			@PathVariable("path") String path,
			@RequestParam("appid") String appid, 
			@RequestParam("playid") String playid, 
			@RequestParam("callid") String callid,
			@RequestParam(value = "userdata", required = false) String userdata, 
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException {
		printStartTag("receiveCustom");
		printHttpPacket(request, null);
		logger.info("receiveCustom: path = "+path+", appid=" + appid + ", callid = " + callid);
		if(path.equals("dtmfreport"))
		{
				String dtmf = request.getParameter("digits");
				
				String cmd = ivrService.getTransferNumByDtmf(appid, dtmf, playid, "");
				logger.info("receiveCustom: path = "+path+", digits=" + dtmf +", Response: " +cmd);
				if(cmd != null)
				{
					postResponse(request, response, cmd);
					return;
				}
			
		}
		printEndTag("receiveCustom");
		postResponse(request, response, "<CmdNone/>");
	}
	
//	@RequestMapping(method = RequestMethod.GET, value = "/Accounts/{accountSid}/AccountInfo")
//	public ModelAndView getAccountById(@PathVariable("accountSid")
//	String accountSid, HttpServletRequest request) throws CCPDaoException, CCPServiceException {
//		printStartTag("getAccountById");
//		printHttpPacket(request, null);
//		logger.info("accountSid: " + accountSid);
////		AccountList accountList = (AccountList) accountService.getAccountById(accountSid);
//		printEndTag("getAccountById");
////		return postResponse(request, accountList);
//		return null;
//	}
	
	
	@Override
	protected String getClassName() {
		return IVRController.class.getName();
	}

	public static void main(String[] args) {
		
		String digits = "123456#";
		String sfink = "#";
		if(!StringUtils.isEmpty(sfink))
		{
			if(digits.endsWith(sfink))
			{
				digits = digits.substring(0, digits.length()-sfink.length());
			}
			
		}
		System.out.println("digits = " + digits);
	}
}
