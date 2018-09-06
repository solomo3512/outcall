/*
 * @(#)BaseJdbcDao.java Copyright (c) 2013 by hisunsray. All rights reserved.
 */
package com.psyb.service.common.dao;

import com.psyb.service.common.exception.CCPDaoException;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 存储过程直接用call_存储过程名字来命名整个API
 */
public interface BaseCCPJdbcDao {

	public void executeSql(String sql) throws CCPDaoException;

	public int update(String sql, Object[] array) throws CCPDaoException;

	public int[] batchUpdate(String[] sql) throws CCPDaoException;

	public int[] batchUpdate(String sql, BatchPreparedStatementSetter bpss) throws CCPDaoException;

	public List<?> queryForList(String sql, Class<?> c) throws CCPDaoException;

	public List<?> queryForList(String sql, Object[] array, Class<?> c) throws CCPDaoException;

	public String queryForString(String sql) throws CCPDaoException;

	public int queryForInt(String sql) throws CCPDaoException;

	public Date queryForDate(String sql) throws CCPDaoException;

	public int queryForCount(String sql) throws CCPDaoException;

	public List<Map<String, Object>> queryForList(String sql, Object[] array) throws CCPDaoException;

	public Object queryForObject(String sql, Object[] array, Class<?> c) throws CCPDaoException;

	public Object queryForObject(String sql, Class<?> c) throws CCPDaoException;

	public Map<String, Object> queryForMap(String sql, Object[] obj) throws CCPDaoException;

	public String queryForJson(String sql, Object[] obj) throws CCPDaoException;

}
