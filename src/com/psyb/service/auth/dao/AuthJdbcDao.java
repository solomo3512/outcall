package com.psyb.service.auth.dao;

import com.psyb.service.auth.model.CallInfo;
import com.psyb.service.common.exception.CCPDaoException;

public interface AuthJdbcDao {

	public CallInfo GetVoipCallDisNum(String voipid) throws CCPDaoException;
	
	void InsertCallBind(String caller, String bindnum) throws CCPDaoException;
}
