package com.psyb.service.inlet.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ming.sample.util.JSONUtil;
import org.ming.sample.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.model.Response;
import com.psyb.service.inlet.model.CallResult;
import com.psyb.service.inlet.service.InletService;
import com.psyb.service.ytx.YTXService;

@Controller("InletController")
@RequestMapping(Constants.INLET_PATH)
public class InletController extends AbstractController {

	
	private static final String KEY_CALLRESULT = "lv_callresult";
	
	@Autowired
	private YTXService ytxService;
	@Autowired
	private InletService inletService;
	
	protected Object parser(DATAGRAM_TYPE type, String key, String body) throws Exception {
		if (KEY_CALLRESULT.equals(key)) {
			if (type == DATAGRAM_TYPE.XML) {
				ObjectUtil binder = new ObjectUtil(CallResult.class);
				return binder.fromXml(body);
			} else {
				return JSONUtil.jsonToObj(body, CallResult.class);
			}
		}
		return null;
	}
	
	/**
	 * 回拨请求
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
//	@RequestMapping(method = RequestMethod.POST, value = "/CallBack")
	@RequestMapping(value = "/SubAccount/{subaccount}/CallBack")
	public void sendCallBack(
			@PathVariable("subaccount") String subaccount,
			@RequestParam("from") String from, 
			@RequestParam("to") String to,
			@RequestParam(value = "fdisnum", required = false) String fdisnum, 
			@RequestParam(value = "tdisnum", required = false) String tdisnum, 
			HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		printStartTag("sendCallBack");
		
		logger.info("sendCallBack subaccount =" + subaccount  +", from="+from + ", to = " + to + ", fdisnum = " + fdisnum + ", tdisnum = " + tdisnum);
	            
		try {
			inletService.handleCallBack(subaccount, from, fdisnum, to , tdisnum);
		} catch (CCPDaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printEndTag("sendCallBack");
		postResponse(request, new Response(Constants.SUCC, null));
	}
	
	
	/**
	 * 处理外呼通知，语音验证码状态通知
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/CallResult")
	public void handleCallResult(@RequestBody
			String body,  
			HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		printStartTag("handleCallResult");
		printHttpPacket(request, body);
		CallResult callresult = (CallResult) postRequest(request, KEY_CALLRESULT, body);
		try {
			inletService.updateCallResult(callresult);
		} catch (CCPDaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printEndTag("handleCallResult");
		postResponse(request, new Response(Constants.SUCC, null));
	}
	
	/**
	 * 回拨请求
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(value = "/AppId/{appid}/SubAccount")
	public void sendCreateSubAcc(
			@PathVariable("appid") String appId,
			@RequestParam("fname") String name,
			HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		printStartTag("sendCreateSubAcc");
		
		try {
			inletService.handleCreateSubID(appId, name);
		} catch (CCPDaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
		
		printEndTag("sendCreateSubAcc");
		postResponse(request, new Response(Constants.SUCC, null));
	}
	
	/**
	 * 发起外呼通知，根据应用选择
	 * @param appid
	 * @param username
	 * @param cash
	 * @param type
	 * @param disnum
	 * @param tempid
	 * @param request
	 * @param response
	 * @throws CCPServiceException
	 */
	@RequestMapping(value = "/AppId/{appid}/LandCall")
	public void startLandCall(
			@PathVariable("appid") String appid,
			@RequestParam(value = "type", required = false) String type, 
			@RequestParam(value = "disnum", required = false) String disnum,
			@RequestParam("taskid") String taskid,
			HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		printStartTag("startLandCall Appid="+appid);
		Response resp=null;
		try {
			resp = inletService.handleLandCall(appid, type, taskid, disnum);
		} catch (CCPDaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		postResponse(request, response, resp);
		printEndTag("startLandCall");
	}
	
	
	@Override
	protected String getClassName() {
		// TODO Auto-generated method stub
		return InletController.class.getName();
	}

	
	//国家电网
	private static final String GDTXTMSG ="$name，您好！您6月的电费账单为$cash元，请您在6月30号前交清，谢谢您的配合";
	//广州邮政
	private static final String GYTXTMSG ="广州邮政速递今日将向您投递标快$cash，揽投员$name，关注微信“广州邮政”可获取邮件最新状态，客服电话11185-6。";
		
		
	/**
	 * 电费通知
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
//	@RequestMapping(method = RequestMethod.POST, value = "/Landcall")
	@RequestMapping(value = "/Landcall")
	public void sendChargeCall(
			@RequestParam(value = "username", required = false) String username, 
			@RequestParam(value = "cash", required = false) String cash, 
			@RequestParam(value = "type", required = false) String type, 
			@RequestParam(value = "disnum", required = false) String disnum,
			@RequestParam(value = "tempid", required = false) String tempid,
			HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		printStartTag("sendChargeCall");
		
		
		String appid = "ff8080813f84717a013f847540ef0000";
		
		try {
			inletService.handleLandCall2(appid, type, tempid, disnum);
		} catch (CCPDaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printEndTag("sendChargeCall");
		postResponse(request, new Response(Constants.SUCC, null));
	}
	
}
