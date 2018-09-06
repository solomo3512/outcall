package com.psyb.service.auth.service;

import com.psyb.service.auth.model.CallAuth;
import com.psyb.service.auth.model.CallAuthResp;
import com.psyb.service.auth.model.Hangup;
import com.psyb.service.common.exception.CCPDaoException;

public interface AuthService {

	public CallAuthResp HandleCallAuthSubacc(CallAuth callauth) throws CCPDaoException;
	
	public String HandleHangup(Hangup hangup) throws CCPDaoException;
}
