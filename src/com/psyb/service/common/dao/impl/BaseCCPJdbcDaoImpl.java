/*
 * @(#)BaseJdbcDao.java Copyright (c) 2013 by hisunsray. All rights reserved.
 */
package com.psyb.service.common.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ming.sample.sql.SqlSetupSupport;
import org.ming.sample.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.psyb.service.common.Constants;
import com.psyb.service.common.dao.BaseCCPJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;

/**
 * Base data access API
 */
@Repository
public class BaseCCPJdbcDaoImpl extends JdbcDaoSupport implements BaseCCPJdbcDao {

	@Autowired
	public void setJdbcTemplates(JdbcTemplate ccpJdbcTemplate) {
		super.setJdbcTemplate(ccpJdbcTemplate);
	}

	public void executeSql(final String sql) throws CCPDaoException {
		try {
			getJdbcTemplate().execute(sql);
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	@Override
	public int update(String sql, Object[] array) throws CCPDaoException {
		int count = 0;
		try {
			if (array == null) {
				count = getJdbcTemplate().update(sql);
			} else {
				count = getJdbcTemplate().update(sql, array);
			}
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
		return count;
	}

	@Override
	public int[] batchUpdate(String[] sql) throws CCPDaoException {
		try {
			return getJdbcTemplate().batchUpdate(sql);
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	@Override
	public int[] batchUpdate(String sql, BatchPreparedStatementSetter bpss) throws CCPDaoException {
		try {
			return getJdbcTemplate().batchUpdate(sql, bpss);
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}
	
	@Override
	public List<?> queryForList(String sql, Object[] array, Class<?> c) throws CCPDaoException {
		try {
			List<?> list = (array == null ? getJdbcTemplate().queryForList(sql) : getJdbcTemplate().queryForList(sql,
					array));
			if (list != null && list.size() > 0) {
				return SqlSetupSupport.populateResult(list, c);
			}
			return list;
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	@Override
	public List<?> queryForList(String sql, Class<?> c) throws CCPDaoException {
		return queryForList(sql, null, c);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object[] array) throws CCPDaoException {
		List<Map<String, Object>> list = null;
		try {
			if (array == null) {
				list = getJdbcTemplate().queryForList(sql);
			} else {
				list = getJdbcTemplate().queryForList(sql, array);
			}
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
		return list;
	}

	@Override
	public Object queryForObject(final String sql, Object[] array, final Class<?> c) throws CCPDaoException {
		try {
			List<?> list = array == null ? getJdbcTemplate().queryForList(sql) : getJdbcTemplate().queryForList(sql,
					array);
			if (list != null && list.size() > 0) {
				return SqlSetupSupport.populateResult(list, c).get(0);
			}
			return null;
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	@Override
	public Object queryForObject(String sql, Class<?> c) throws CCPDaoException {
		return queryForObject(sql, null, c);
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Object[] obj) throws CCPDaoException {
		Map<String, Object> map = null;
		List<Map<String, Object>> list = null;
		try {
			if (obj == null) {
				list = getJdbcTemplate().queryForList(sql);
			} else {
				list = getJdbcTemplate().queryForList(sql, obj);
			}
			if (list != null && !list.isEmpty()) {
				map = list.get(0);
			}
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
		return map;
	}

	@Override
	public String queryForJson(String sql, Object[] obj) throws CCPDaoException {
		String json = null;
		List<Map<String, Object>> list = null;
		try {
			if (obj == null) {
				list = getJdbcTemplate().queryForList(sql);
			} else {
				list = getJdbcTemplate().queryForList(sql, obj);
			}
			if (list != null && !list.isEmpty()) {
				json = JSONUtil.list2json(list);
			}
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
		return json;
	}

	public String queryForString(String sql) throws CCPDaoException {
		try {
			return (String) getJdbcTemplate().queryForObject(sql, String.class);
		} catch (IncorrectResultSizeDataAccessException irsae) {
			return null;
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	public int queryForInt(String sql) throws CCPDaoException {
		try {
			return getJdbcTemplate().queryForObject(sql, Integer.class);
		} catch (IncorrectResultSizeDataAccessException irsae) {
			return Constants.DB_EMPTY;
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	public Date queryForDate(String sql) throws CCPDaoException {
		try {
			return getJdbcTemplate().queryForObject(sql, Date.class);
		} catch (IncorrectResultSizeDataAccessException irsae) {
			return null;
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}

	public int queryForCount(String sql) throws CCPDaoException {
		try {
			return getJdbcTemplate().queryForObject(sql, Integer.class);
		} catch (IncorrectResultSizeDataAccessException irsae) {
			return Constants.DB_COUNT_EMPTY;
		} catch (Exception e) {
			throw new CCPDaoException(e);
		}
	}
	
}