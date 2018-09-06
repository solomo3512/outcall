package com.psyb.service.ivr.dao;

import java.util.List;

import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.ivr.mode.IVRCmd;
import com.psyb.service.ivr.mode.NumInfo;
import com.psyb.service.ivr.mode.Schedule;

public interface IVRJdbcDao {

	public List<Schedule> getWorkSchedule(String appid, String servnum) throws CCPDaoException;
	
	public NumInfo getNumInfoByTo(String appid, String servnum) throws CCPDaoException;
	
	public NumInfo getCallOutNum(String appid, String memberid) throws CCPDaoException;
	
	public NumInfo getPbxExtenByDtmf(String appid, String dtmf) throws CCPDaoException;
	
	public NumInfo getTransferByDtmf(String appid, String dtmf) throws CCPDaoException;
	
	public IVRCmd getMenuInfo(String appid, String menuid) throws CCPDaoException;
	
	public IVRCmd getPlayInfo(String appid, String playid) throws CCPDaoException;
	
	public void updateCallDtmf(String callsid, String dtmf) throws CCPDaoException;
}
