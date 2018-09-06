package com.psyb.service.inlet.service;

import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.model.Response;
import com.psyb.service.inlet.model.CallResult;

public interface InletService {

	public Response handleLandCall(String appid, String type, String tempid, String disnum) throws CCPDaoException;
	
	public Response handleCreateSubID(String appid, String fname) throws CCPDaoException;
	
	public Response handleCallBack(String subaccount, String from, String fdisnum, String to, String tDisnum) throws CCPDaoException;
	
	
	public void updateCallResult(CallResult callresult) throws CCPDaoException;
	
	public Response handleLandCall2(String appid, String type, String tempid, String disnum) throws CCPDaoException;
}
