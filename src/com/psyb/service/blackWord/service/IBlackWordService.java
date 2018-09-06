package com.psyb.service.blackWord.service;

import com.psyb.service.blackWord.form.BlackReq;
import com.psyb.service.blackWord.form.BlackResp;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;

/**
 * 黑词管理
 * @Title：IBlackWordService 
 * @author:maliang
 * @date:2017年2月23日下午2:08:35
 */
public interface IBlackWordService {

	/**
	 * 黑词管理
	 * @param req
	 * @throws CCPServiceException
	 * @throws CCPDaoException
	 */
	public BlackResp manageBlackWord(BlackReq req) throws CCPServiceException,CCPDaoException;

	public BlackResp CheckBlackWord(BlackReq req) throws CCPServiceException,CCPDaoException;
	
}
