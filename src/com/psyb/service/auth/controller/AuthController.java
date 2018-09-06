package com.psyb.service.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ming.sample.util.JSONUtil;
import org.ming.sample.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.psyb.service.auth.model.CallAuth;
import com.psyb.service.auth.model.CallAuthResp;
import com.psyb.service.auth.model.Hangup;
import com.psyb.service.auth.service.AuthService;
import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.model.Response;

@Controller("AuthController")
@RequestMapping(Constants.AUTH_PATH)
public class AuthController extends AbstractController{

	public static String KEY_CALLAUTH = "KEY_CALLAUTH";
	public static String KEY_CALLESTABLISH = "KEY_CALLESTABLISH";
	public static String KEY_HANGUP = "KEY_HANGUP";
	
	public static String CDR_HANGUP_URL = "http://192.168.178.200:8080/server/auth/hangupcdr";
	
	@Autowired
	private AuthService authService;
	
	protected Object parser(DATAGRAM_TYPE type, String key, String body) throws Exception {
//		if (KEY_DOWNCONFIGFILE.equals(key) || KEY_GENERATESERVERADDRS.equals(key)) {
//			return JSONUtil.jsonToObj(body, ConfigFileForm.class);
//		} 
		if (KEY_CALLAUTH.equals(key)) {
			if (type == DATAGRAM_TYPE.XML) {
				ObjectUtil binder = new ObjectUtil(CallAuth.class);
				return binder.fromXml(body);
			} else {
				return JSONUtil.jsonToObj(body, CallAuth.class);
			}
		}
		else if (KEY_HANGUP.equals(key)) {
			if (type == DATAGRAM_TYPE.XML) {
				ObjectUtil binder = new ObjectUtil(Hangup.class);
				return binder.fromXml(body);
			} else {
				return JSONUtil.jsonToObj(body, Hangup.class);
			}
		}
		return null;
	}
	
	/**
	 * 呼叫鉴权
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/CallAuth")
	public ModelAndView CallAuth(@RequestBody
			String body, HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		
		
		printStartTag("CallAuth");
		printHttpPacket(request, body);
		
		CallAuth callauth = (CallAuth) postRequest(request, KEY_CALLAUTH, body);
		CallAuthResp resp=null;
		try {
			resp = authService.HandleCallAuthSubacc(callauth);
		} catch (CCPDaoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return postResponse2(request, resp);
	}
	
	
	/**
	 * 呼叫摘机
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/CallEstablish")
	public ModelAndView CallEstablish(@RequestBody
			String body, HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		
		printStartTag("CallEstablish");
		
		printEndTag("CallEstablish");
		return postResponse2(request, new Response(Constants.SUCC, null));
	}
	
	/**
	 * 挂机消息
	 * 
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/Hangup")
	public ModelAndView Hangup(@RequestBody
			String body, HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException {
		
		printStartTag("Hangup");
		printHttpPacket(request, body);
		
		Hangup hangup = (Hangup) postRequest(request, KEY_HANGUP, body);
		try {
			authService.HandleHangup(hangup);
		} catch (CCPDaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printEndTag("Hangup");
		return postResponse2(request, new Response(Constants.SUCC, null));
	}
	
	@Override
	protected String getClassName() {
		// TODO Auto-generated method stub
		return AuthController.class.getName();
	}

	
	

//	String disnum = "";
//	if(callauth.getType() != null && callauth.getType().equals("0"))
//	{
//		disnum = "02022953148";
//	}
//	
//	logger.info("CallAuth action= "+callauth.getAction() + ", callsid= " + callauth.getCallSid());
//	printEndTag("CallAuth");
//	CallAuthResp resp = new CallAuthResp(Constants.SUCC, 6000, 1, disnum, "");
//	resp.setRecordPoint(1);
}
