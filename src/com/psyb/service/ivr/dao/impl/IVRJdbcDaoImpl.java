package com.psyb.service.ivr.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.psyb.service.common.dao.BaseJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.util.DateUtil;
import com.psyb.service.ivr.dao.IVRJdbcDao;
import com.psyb.service.ivr.mode.IVRCmd;
import com.psyb.service.ivr.mode.NumInfo;
import com.psyb.service.ivr.mode.Schedule;

@Repository
public class IVRJdbcDaoImpl implements IVRJdbcDao {

	protected final Logger logger = LogManager.getLogger(getClass().getName());
	
	@Autowired
	private BaseJdbcDao baseJdbcDao;
	
	@Override
	public NumInfo getNumInfoByTo(String appid, String sernum) throws CCPDaoException
	{//通过接入号查询服务类型
		String sql = "SELECT n.appId,n.number,n.type, n.memberId, n.menuId, n.playId FROM x_service_num n"
				+ " WHERE status=1 AND number='"+sernum+"' AND appId='"+appid+"'" ;
		NumInfo num = (NumInfo) baseJdbcDao.queryForObject(sql, NumInfo.class);
		logger.info("getNumInfoByTo sql="+sql);
		return num;
	}

	@Override
	public NumInfo getTransferByDtmf(String appid, String dtmf)
			throws CCPDaoException {
		//通过按键选择排队的号码
		String sql = "SELECT appId, number FROM x_transfer_num WHERE isuse=0 AND status=1 AND "
				+ "dtmf='"+dtmf+"' AND appId='"+appid+"' ORDER BY update_time asc LIMIT 1";
		logger.info("getTransferByDtmf sql="+sql);
		NumInfo num = (NumInfo) baseJdbcDao.queryForObject(sql, NumInfo.class);
		return num;
	}
	
	@Override
	public NumInfo getPbxExtenByDtmf(String appid, String dtmf)
			throws CCPDaoException {
		String sql = "SELECT v.appId, v.voipaccount FROM c_member_info m, a_voip_info v WHERE m.voip_id=v.id AND m.status=1 AND "
				+ "m.exten='"+dtmf+"' AND v.appId='"+appid+"'";
		logger.info("getPbxExtenByDtmf sql="+sql);
		NumInfo num = (NumInfo) baseJdbcDao.queryForObject(sql, NumInfo.class);
		return num;
	}

	@Override
	public List<Schedule> getWorkSchedule(String appid, String servnum)
			throws CCPDaoException {
		String sql = "SELECT s.appId,s.offworktime,s.offworkdate,s.offweekday,s.offworkprompt,s.redirectid "
				+ "FROM p_pbx_work_schedule s,  x_service_num n WHERE s.status=1 AND n.id=s.serId AND n.number='"+servnum+"' "
				+ "AND n.appId='"+appid+"' ORDER BY s.priority" ;
		logger.info("getWorkSchedule sql="+sql);
		@SuppressWarnings("unchecked")
		List<Schedule> schedules = (List<Schedule>) baseJdbcDao.queryForList(sql, Schedule.class);
		return schedules;
	}

	@Override
	public NumInfo getCallOutNum(String appid, String memberid)
			throws CCPDaoException {
		String sql = "SELECT v.appId,v.voipaccount FROM c_member_info m, a_voip_info v"
				+ " WHERE m.voip_id=v.id AND m.id="+memberid+" AND appId='"+appid+"'" ;
		logger.info("getCallOutNum sql="+sql);
		NumInfo num = (NumInfo) baseJdbcDao.queryForObject(sql, NumInfo.class);
		return num;
	}

	@Override
	public IVRCmd getMenuInfo(String appid, String menuid)
			throws CCPDaoException {
		String sql = "SELECT c.appId, c.type, c.loop, c.playfile, c.numdigits, c.timeout, c.finishkey, c.firstendkey, c.number, c.redirectid"
				+ " FROM p_pbx_cmdlist c"
				+ " WHERE c.id="+menuid+" AND c.appId='"+appid+"'" ;
		logger.info("getMenuInfo sql="+sql);
		IVRCmd ivrcmd = (IVRCmd) baseJdbcDao.queryForObject(sql, IVRCmd.class);
		return ivrcmd;
	}
	
	@Override
	public IVRCmd getPlayInfo(String appid, String playid)
			throws CCPDaoException {
		String sql = "SELECT c.appId, c.loop, c.playfile, c.redirectid"
				+ " FROM p_pbx_cmdlist c"
				+ " WHERE c.id="+playid+" AND c.appId='"+appid+"'" ;
		logger.info("getPlayInfo sql="+sql);
		IVRCmd ivrcmd = (IVRCmd) baseJdbcDao.queryForObject(sql, IVRCmd.class);
		return ivrcmd;
	}
	
	
	@Override
	public void updateCallDtmf(String callsid, String dtmf)
			throws CCPDaoException {
		String sql="UPDATE x_callout_list SET dtmf='"+dtmf+"' WHERE callSid='"+callsid+"'";
		logger.info("updateCallDtmf sql="+sql);
		baseJdbcDao.executeSql(sql);
		
	}

}
