/**
 * 
 */
package com.psyb.service.common.dao;

import com.psyb.service.common.exception.CCPRedisException;

/**
 * @author chao
 */
public interface BaseRedisDao {

	
	//中间环节数据
	public static final String REDIS_CDR_DATA = "KCDR";
	
	public static final String REDIS_X_NUM = "KXNUM";
	
	public static final String REDIS_SERVICE_NUM = "KSERVICENUM";
	
	public static final String REDIS_TASK = "KTASK";
	
	// 多渠道网关模块编号
//	public static final String REDIS_MCM_MODULR_NO = "k026";
//
//	//im使用redis队列
//	public static final String REDIS_KEY_CORPORATION = "ytx002|";
//	public static final String REDIS_KEY_ATTACH = "ytx005|";
//	public static final String REDIS_KEY_APP = "ytx009|";
//	public static final String REDIS_KEY_SERVER_ADDR = "ytx012|addrs";
//	public static final String REDIS_KEY_AMQ_QUEUE = "ytx007|";
//	public static final String REDIS_KEY_GROUP_MEMBER = "ytx026|";

	/**
	 * 同步
	 * 
	 */
	String getRedisValue(String key) throws CCPRedisException;

	/**
	 * 同步
	 */
	void saveRedisValue(String key, String value, long expire) throws CCPRedisException;

	/**
	 * 同步
	 * 
	 */
	void saveRedisValue(String key, String value) throws CCPRedisException;

	/**
	 * 同步 redis数据原子+1
	 * 
	 */
	Long incrRedis(String key) throws CCPRedisException;

	/**
	 * 同步 删除redis 数据
	 * 
	 * @param key
	 * @throws CCPRedisException
	 */
	void deleteRedisValue(final String key) throws CCPRedisException;

	/**
	 * 同步 获取key过期时间
	 * 
	 * @param key
	 * @throws CCPRedisException
	 */
	Long getRedisTTL(final String key) throws CCPRedisException;
	
	
	/**
	 * 同步
	 * 
	 */
	String popRedisValue(final String key) throws CCPRedisException;

	/**
	 * 同步
	 */
	void pushRedisValue(final String key, final String value) throws CCPRedisException;
	
	/**
	 * 同步
	 */
	void clearRedisList(final String key) throws CCPRedisException;
}
