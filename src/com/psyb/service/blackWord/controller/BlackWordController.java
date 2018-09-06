package com.psyb.service.blackWord.controller;

import javax.servlet.http.HttpServletRequest;

import org.ming.sample.util.JSONUtil;
import org.ming.sample.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.psyb.service.blackWord.form.BlackReq;
import com.psyb.service.blackWord.form.BlackResp;
import com.psyb.service.blackWord.service.IBlackWordService;
import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
/**
 * 黑词管理
 * @Title：BlackController 
 * @author:maliang
 * @date:2017年2月23日上午11:50:42
 */
@Controller("BlackWordController")
@RequestMapping(Constants.INLET_PATH)
public class BlackWordController extends AbstractController {

	
	public static final String KEY_BLACKWORDS = "KEY_BLACKWORDS";
	
	@Autowired
	private IBlackWordService blackWordService;
	
	protected Object parser(DATAGRAM_TYPE type, String key, String body) throws Exception {
		if (KEY_BLACKWORDS.equals(key)) {
			if (type == DATAGRAM_TYPE.JSON) {
				return JSONUtil.jsonToObj(body, BlackReq.class);
			} else {
				ObjectUtil binder = new ObjectUtil(BlackReq.class);
				return binder.fromXml(body);
			}
		}
		return null;
	}
	/**
	 * 黑词管理
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 * @throws CCPDaoException
	 */
	@RequestMapping(method = RequestMethod.POST, value = {"/inner/black"})
	public ModelAndView addBlack(@RequestBody
	String body, HttpServletRequest request) throws CCPServiceException, CCPDaoException {
		printStartTag("black/white words");
		printHttpPacket(request, body);
		BlackReq blackReq = (BlackReq) postRequest(request, KEY_BLACKWORDS, body);
		BlackResp resp=blackWordService.manageBlackWord(blackReq);
		printEndTag("black/white words");
		return postResponse(request, resp);
	}
	
	/**
	 * 黑词检测
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 * @throws CCPDaoException
	 */
	@RequestMapping(method = RequestMethod.POST, value = {"/inner/black/check"})
	public ModelAndView checkBlack(@RequestBody
	String body, HttpServletRequest request) throws CCPServiceException, CCPDaoException {
		printStartTag("check black/white words");
		printHttpPacket(request, body);
		BlackReq blackReq = (BlackReq) postRequest(request, KEY_BLACKWORDS, body);
		BlackResp resp=blackWordService.CheckBlackWord(blackReq);
		printEndTag("check black/white words");
		return postResponse(request, resp);
	}
	
	
	
	
	@Override
	protected String getClassName() {
		return BlackWordController.class.getName();
	}

}
