/**
 * 
 */
package com.psyb.service.common.dao.impl;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.exception.CCPRedisException;

/**
 * Base redis access api.
 */
@Repository
public class BaseRedisDaoImpl implements BaseRedisDao {

	private static final Logger log = LogManager.getLogger(BaseRedisDaoImpl.class.getName());

	@Autowired
	private RedisTemplate<Serializable, Serializable> redisTemplate1;

	/**
	 * 根据key查询redis上的value
	 * 
	 * @param key
	 *            key值
	 * @return
	 */
	public String getRedisValue(final String key) throws CCPRedisException {
		try {
			return redisTemplate1.execute(new RedisCallback<String>() {

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					if (connection.exists(bkey)) {
						byte[] bvalue = connection.get(bkey);
						String value = redisTemplate1.getStringSerializer().deserialize(bvalue);
						log.debug("getRedisValue key: {} value: {}", key, value);
						return value;
					}
					return null;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	/**
	 * 保存redis数据
	 * 
	 * @param key
	 *            key值
	 * @param value
	 *            value值
	 * @param expire
	 *            过期时间，单位秒
	 */
	public void saveRedisValue(final String key, final String value, final long expire) throws CCPRedisException {
		try {
			redisTemplate1.execute(new RedisCallback<Object>() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					byte[] bvalue = redisTemplate1.getStringSerializer().serialize(value);
					connection.set(bkey, bvalue);
					connection.expire(bkey, expire);
					// log.debug("saveRedisValue key: " + key + " value: " +
					// value + " expire: " + expire);
					log.debug("saveRedisValue key: {} value: {} expire: {}", key, value, expire);
					return null;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	/**
	 * 保存redis数据
	 * 
	 * @param key
	 *            key值
	 * @param value
	 *            value值
	 */
	public void saveRedisValue(final String key, final String value) throws CCPRedisException {
		try {
			redisTemplate1.execute(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					byte[] bvalue = redisTemplate1.getStringSerializer().serialize(value);
					connection.set(bkey, bvalue);
					// log.debug("saveRedisValue key: " + key + " value: " +
					// value);
					log.debug("saveRedisValue key: {} value: {}", key, value);
					return null;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	/**
	 * redis数据原子+1操作
	 * 
	 * @param key
	 *            key值
	 */
	public Long incrRedis(final String key) throws CCPRedisException {

		try {
			return (Long) redisTemplate1.execute(new RedisCallback<Object>() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					Long incr = connection.incr(bkey);
					// log.debug("incrRedis key: " + key + "value: " + incr);
					log.debug("incrRedis key: {} value: {}", key, incr);
					return incr;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	public void deleteRedisValue(final String key) throws CCPRedisException {
		try {
			redisTemplate1.execute(new RedisCallback<Object>() {
				public Object doInRedis(RedisConnection connection) {
					Long del = connection.del(redisTemplate1.getStringSerializer().serialize(key));
					// log.debug("deleteRedisValue key: " + key + " value: " +
					// del);
					log.debug("deleteRedisValue key: {} value: {}", key, del);
					return del;
				}
			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	public Long getRedisTTL(final String key) throws CCPRedisException {
		try {
			return (Long) redisTemplate1.execute(new RedisCallback<Object>() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					Long ttl = connection.ttl(bkey);
					// log.debug("getRedisTTL key: " + key + " TTL: " + ttl);
					log.debug("getRedisTTL key: {} TTL: {}", key, ttl);
					return ttl;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	@Override
	public String popRedisValue(final String key) throws CCPRedisException {
		try {
			return redisTemplate1.execute(new RedisCallback<String>() {

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					if (connection.exists(bkey)) {
						byte[] bvalue = connection.lPop(bkey);
						String value = redisTemplate1.getStringSerializer().deserialize(bvalue);
						log.debug("popRedisValue key: {} value: {}", key, value);
						return value;
					}
					return null;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

	@Override
	public void pushRedisValue(final String key,final String value) throws CCPRedisException {
		try {
			redisTemplate1.execute(new RedisCallback<Object>() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					byte[] bvalue = redisTemplate1.getStringSerializer().serialize(value);
					connection.lPush(bkey, bvalue);
					// log.debug("saveRedisValue key: " + key + " value: " +
					// value + " expire: " + expire);
					log.debug("pushRedisValue key: {} value: {}", key, value);
					return null;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
		
	}

	@Override
	public void clearRedisList(final String key) throws CCPRedisException {
		// TODO Auto-generated method stub
		try {
			redisTemplate1.execute(new RedisCallback<Object>() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] bkey = redisTemplate1.getStringSerializer().serialize(key);
					connection.lTrim(bkey, 1, 0);
					log.debug("clearRedisList key: {}", key);
					return null;
				}

			});
		} catch (Exception e) {
			throw new CCPRedisException(e);
		}
	}

}
