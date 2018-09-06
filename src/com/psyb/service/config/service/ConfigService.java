package com.psyb.service.config.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psyb.service.common.exception.CCPCassandraDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.config.form.ConfigFileForm;
import com.psyb.service.config.form.UploadFileResp;

public interface ConfigService {

	/**
	 * 下载配置文件
	 * @param downConfigFileForm
	 * @param response
	 * @throws CCPServiceException
	 */
	public void downConfigFile(ConfigFileForm downConfigFileForm, HttpServletRequest request, 
			HttpServletResponse response) throws CCPServiceException, CCPCassandraDaoException;
	
	/**
	 * 上传文件
	 * @param request
	 * @param fileName
	 * @return
	 * @throws CCPServiceException
	 */
	public UploadFileResp uploadFile(String token, HttpServletRequest request) 
			throws CCPServiceException, CCPCassandraDaoException;
}
