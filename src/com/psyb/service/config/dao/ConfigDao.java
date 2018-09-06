package com.psyb.service.config.dao;

import com.psyb.service.common.exception.CCPCassandraDaoException;
import com.psyb.service.config.model.ConfigFile;
import com.psyb.service.config.model.UploadAttr;

public interface ConfigDao {

	/**
	 * 检查配置文件版本
	 * @param typez
	 * @return
	 * @throws CCPCassandraDaoException
	 */
	public ConfigFile CheckConfigFile(String type, String version) throws CCPCassandraDaoException;
	
	/**
	 * 检查token是否有效
	 * @param token
	 * @return
	 * @throws CCPCassandraDaoException
	 */
	public UploadAttr CheckUploadToken(String token) throws CCPCassandraDaoException;
	
	/**
	 * 根据公司ID查密码
	 * @param corp
	 * @return
	 * @throws CCPCassandraDaoException
	 */
	public String GetPwdByCorp(String corp) throws CCPCassandraDaoException;
	
	
}
