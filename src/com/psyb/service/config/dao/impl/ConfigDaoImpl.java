package com.psyb.service.config.dao.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.psyb.service.common.dao.BaseCassandraDao;
import com.psyb.service.common.exception.CCPCassandraDaoException;
import com.psyb.service.config.dao.ConfigDao;
import com.psyb.service.config.model.ConfigFile;
import com.psyb.service.config.model.UploadAttr;

//@Repository
public class ConfigDaoImpl implements ConfigDao {
	
	public static final Logger logger = Logger.getLogger(ConfigDaoImpl.class);
	
	@Autowired
	BaseCassandraDao baseCassandraDao;

	@Override
	public ConfigFile CheckConfigFile(String type, String version)
			throws CCPCassandraDaoException {
//		ConfigFile configFile = null;
//		String cql = "";
//		logger.info("cql:" + cql);
//		ResultSet resultSet = baseCassandraDao.execute(cql);
//		if (resultSet != null) {
//			Row row = resultSet.one();
//			if (row != null) {
//				configFile = new ConfigFile(row.getString(0), row.getString(1));
//			}
//		}
//		return configFile;
//		return new ConfigFile(2, "D:\\test\\test2\\ServerAddr_1.xml");
		return new ConfigFile(2, "/app/fileserver_6001/saved_files/ServerAddr_1.xml");
	}

	@Override
	public UploadAttr CheckUploadToken(String token)
			throws CCPCassandraDaoException {
		String cql = "select attachinfo from ytx_attach where attachtoken = '" + token + "'";
		logger.info("cql:" + cql);
		UploadAttr ua = null;
		ResultSet resultSet = baseCassandraDao.execute(cql);
		if (resultSet != null) {
			Row row = resultSet.one();
			if (row != null) {
				Map<String, String> map = row.getMap(0, String.class, String.class);
				ua = new UploadAttr(map.get("appId"), map.get("localFileName"));
			}
		}
		return ua;
	}

	@Override
	public String GetPwdByCorp(String corp) throws CCPCassandraDaoException {
		String cql = "select corpinfo from ytx_corporation where corpname = '" + corp + "'";
		logger.info("cql:" + cql);
		String pwd = "";
		ResultSet resultSet = baseCassandraDao.execute(cql);
		if (resultSet != null) {
			Row row = resultSet.one();
			if (row != null) {
				Map<String, String> map = row.getMap(0, String.class, String.class);
				pwd = map.get("pwd");
			}
		}
		return pwd;
	}

}
