package com.psyb.service.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yuntongxun.tools.cache.EhCacheManager;

@Component
public class PwdCache {

	@Autowired
	private EhCacheManager cacheManager; 
	
	/**
	 * 添加缓存信息。
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized void put(String key, String value) {
		cacheManager.putDataInCache("strCache", key, value);
	}
	
	/**
	 * 获取缓存中的信息。
	 * @param key
	 * @return
	 */
	public String get(String key) {
		Object temp = cacheManager.getCacheData("strCache", key);
		if(temp != null){
			return (String)temp;
		}
		return null;
	}
}
