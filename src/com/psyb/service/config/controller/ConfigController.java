/**
 * 
 */
package com.psyb.service.config.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.NDC;
import org.ming.sample.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.exception.CCPCassandraDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.util.EncryptUtil;
import com.psyb.service.common.util.StringUtil;
import com.psyb.service.config.dao.ConfigDao;
import com.psyb.service.config.form.ConfigFileForm;
import com.psyb.service.config.form.UploadFileResp;
import com.psyb.service.config.service.ConfigService;

/**
 * @author chao
 *
 */
//@Controller("api2015.ConfigController")
//@RequestMapping(Constants.ApiVersion2015)
public class ConfigController extends AbstractController {
	
	public static String KEY_DOWNCONFIGFILE = "KEY_DOWNCONFIGFILE";
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private ConfigDao configDao;

	protected Object parser(DATAGRAM_TYPE type, String key, String body) throws Exception {
		if (KEY_DOWNCONFIGFILE.equals(key)) {
			return JSONUtil.jsonToObj(body, ConfigFileForm.class);
		}
		return null;
	}
	
	/**
	 * 下载配置文件
	 * @param body
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/Corp/yuntongxun/Download/Attach")
	public ModelAndView DownConfigFile(@RequestBody
			String body, HttpServletRequest request, HttpServletResponse response) 
			throws CCPServiceException, CCPCassandraDaoException {
		printStartTag("DownConfigFile");
		printHttpPacket(request, body);
		// 鉴权
		auth(request);
		ConfigFileForm downConfigFileForm = (ConfigFileForm) postRequest(request, KEY_DOWNCONFIGFILE, body);
		configService.downConfigFile(downConfigFileForm, request, response);
		printEndTag("DownConfigFile");
		return null;
	}
	
	/**
	 * 上传文件
	 * @param request
	 * @return
	 * @throws CCPServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/Corp/yuntongxun/Upload/Attach")
	public ModelAndView UploadFile(HttpServletRequest request) 
			throws CCPServiceException, CCPCassandraDaoException {
		NDC.push(StringUtil.getUUID4MD5());
		printStartTag("UploadFile");
		printHttpPacket(request, null);
		String token = request.getParameter("token");
		UploadFileResp uploadFileResp = configService.uploadFile(token, request);
		printEndTag("UploadFile");
		return postResponse(request, uploadFileResp);
	}
	
	public void auth(HttpServletRequest request) 
			throws CCPServiceException, CCPCassandraDaoException {
		NDC.push(StringUtil.getUUID4MD5());
		logger.info(" ---------------------------------[preHandle Start]--------------------------------- ");
		String realIP = request.getHeader(Constants.X_REAL_IP);
		realIP = (realIP == null ? request.getRemoteAddr() : realIP);
		logger.info("Remote Host: " + realIP + ":" + request.getRemotePort());
		String accessUrl = request.getRequestURL() + "?" + request.getQueryString();
		logger.info("URL: " + accessUrl);
		HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		logger.info("@Check HTTP Header:");
		if (httpMethod == HttpMethod.POST) {
			String contentType = request.getContentType();
			logger.info("HTTP ContentType: " + contentType);

			String chunked = request.getHeader("Transfer-Encoding");
			logger.info("HTTP Transfer-Encoding: " + chunked);

			int contentLength = request.getContentLength();
			logger.info("HTTP ContentLength: " + contentLength);

			if (chunked != null && chunked.indexOf("chunked") > -1) {
				throw new CCPServiceException(ScriptManager.buildError("16005"));
			}
			if (contentType == null || contentType.length() == 0) {
				throw new CCPServiceException(ScriptManager.buildError("16006"));
			}
			if (contentLength == -1 && chunked == null) {
				throw new CCPServiceException(ScriptManager.buildError("16007"));
			}
		}
		String sig = request.getParameter("sig");
		if (sig == null || "".equals(sig)) {
			throw new CCPServiceException(ScriptManager.buildError("16002"));
		}
		// 目前corp写为yuntongxun可先不做判断
//		String uri = request.getRequestURI();
//		String[] urlArray = ProtocolUtil.getURLPart(uri);
//		if (urlArray == null || urlArray.length != 2) {
//			throw new CCPServiceException(ScriptManager.buildError("111100"));
//		}
//		String corp = urlArray[1];
		// 根据公司ID查密码
		String pwd = configDao.GetPwdByCorp(/*Constants.CORP*/"");
		if (pwd == null || "".equals(pwd)) {
			throw new CCPServiceException(ScriptManager.buildError("16004"));
		}
		logger.info("password:" + pwd);
		String serverSig = EncryptUtil.md5(/*Constants.CORP*/"" + pwd);
		logger.info("serverSig:" + serverSig);
		if (!sig.equals(serverSig)) {
			throw new CCPServiceException(ScriptManager.buildError("16003"));
		}
		logger.info(" ---------------------------------[preHandle End]--------------------------------- ");
	}
	
	@Override
	protected String getClassName() {
		return ConfigController.class.getName();
	}

}
