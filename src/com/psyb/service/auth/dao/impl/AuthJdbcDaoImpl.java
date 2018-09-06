package com.psyb.service.auth.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.psyb.service.auth.dao.AuthJdbcDao;
import com.psyb.service.auth.model.CallInfo;
import com.psyb.service.common.dao.BaseJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;

@Repository
public class AuthJdbcDaoImpl implements AuthJdbcDao {

	private Logger logger = LogManager.getLogger(AuthJdbcDaoImpl.class);
	@Autowired
	private BaseJdbcDao baseJdbcDao;
	
	
	@Override
	public void InsertCallBind(String caller, String bindnum) throws CCPDaoException {
//		（1）创建索引：CREATE UNIQUE INDEX indexName ON tableName(tableColumns(length))
//		（2）修改表结构：ALTER tableName ADD UNIQUE [indexName] ON (tableColumns(length))
//		REPLACE的运行与INSERT很相似。只有一点例外，假如表中的一个旧记录与一个用于PRIMARY
//
//		KEY或一个UNIQUE索引的新记录具有相同的值，则在新记录被插入之前，旧记录被删除。 
//		注意，除非表有一个PRIMARY KEY或UNIQUE索引，否则，使用一个REPLACE语句没有意义。该
//
//		语句会与INSERT相同，因为没有索引被用于确定是否新行复制了其它的行

		String sql="REPLACE INTO x_pbx_bind_num (userNum, number) VALUES( '"+caller+"','"+bindnum+"')";
		logger.info("InsertCallBind sql="+sql);
		baseJdbcDao.executeSql(sql);
	}
	

	@Override
	public CallInfo GetVoipCallDisNum(String voipid) throws CCPDaoException {
		//查询分机外呼，显号，是否录音，通话时长等
		String sql = "SELECT a.accountSid, v.appId, v.disNumber, a.record, a.recordPoint FROM"
				+ " a_voip_info v, a_application_info a WHERE v.appId=a.appId AND a.status=2";
		sql += " AND v.voipaccount='" +voipid+ "'";
		logger.info("GetVoipCallDisNum sql="+sql);
		
		return (CallInfo) baseJdbcDao.queryForObject(sql, CallInfo.class);
	}
}
