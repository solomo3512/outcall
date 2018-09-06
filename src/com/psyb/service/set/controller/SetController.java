package com.psyb.service.set.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.ZooKeeper;
import org.ming.sample.util.JSONUtil;
import org.ming.sample.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.model.Response;
import com.psyb.service.set.model.XNumSet;
import com.psyb.service.set.service.SetService;

@Controller("SetController")
@RequestMapping(Constants.SET_PATH)
public class SetController extends AbstractController{

	
	private static final String KEY_TRANSFER_NUM_SET = "lv_callresult";
	
	protected Object parser(DATAGRAM_TYPE type, String key, String body) throws Exception {
		if (KEY_TRANSFER_NUM_SET.equals(key)) {
			if (type == DATAGRAM_TYPE.XML) {
				ObjectUtil binder = new ObjectUtil(XNumSet.class);
				return binder.fromXml(body);
			} else {
				return JSONUtil.jsonToObj(body, XNumSet.class);
			}
		}
		return null;
	}
	
	@Autowired
	private SetService setService;
	
	//"%s?appid=%s&callid=%s&from=%s&to=%s&direction=%d&userdata=&origcalled=fromattr=serviceno=&digits=transfernum="
	@RequestMapping(method = RequestMethod.POST, value = "/set")
	public ModelAndView startService(
			@RequestBody String body,  
			HttpServletRequest request, HttpServletResponse response) throws CCPDaoException, CCPServiceException {
		printStartTag("xset");
		printStartTag("body=" + body);
		printHttpPacket(request, null);
		XNumSet xset = (XNumSet) postRequest(request, KEY_TRANSFER_NUM_SET, body);
		String cmd = setService.handleSetTransferNum(xset);
		logger.info("startservice: response=\n"+ cmd);
		
		printEndTag("xset");
		Response resp = new Response(Constants.SUCC, null);
		logger.info("startservice: response=\n"+ JSONUtil.objToMap(resp));
		
		return postResponse(request, resp);
	}
	
	
	@Override
	protected String getClassName() {
		return SetController.class.getName();
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
		
		ZooKeeper zk;
	}
}
