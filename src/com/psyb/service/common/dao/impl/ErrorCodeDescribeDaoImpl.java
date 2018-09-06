package com.psyb.service.common.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.psyb.service.common.dao.BaseCCPJdbcDao;
import com.psyb.service.common.dao.IErrorCodeDescribeDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.model.ErrorCodeDescribePo;

@Repository(value = "errorCodeDescribeDao")
public class ErrorCodeDescribeDaoImpl implements IErrorCodeDescribeDao{

	@Autowired
	private BaseCCPJdbcDao baseCCPJdbcDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ErrorCodeDescribePo> getErrorcodeDescribeList() throws CCPDaoException {
		String sql = "SELECT error_code, error_des, error_type FROM ccp_errorcode_des";
		return (List<ErrorCodeDescribePo>)baseCCPJdbcDao.queryForList(sql, ErrorCodeDescribePo.class);
	}

}
