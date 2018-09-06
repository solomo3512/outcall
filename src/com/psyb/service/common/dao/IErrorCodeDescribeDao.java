package com.psyb.service.common.dao;

import java.util.List;

import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.model.ErrorCodeDescribePo;

public interface IErrorCodeDescribeDao {

	/**
	 * @Description: 获取所有的平台错误码
	 * @return List<ErrorcodeDesPo>
	 * @throws CCPDaoException
	 */
	public List<ErrorCodeDescribePo> getErrorcodeDescribeList() throws CCPDaoException;
	
}
