/**
 * 
 */
package com.psyb.service.common.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Statement;
import com.psyb.service.common.exception.CCPCassandraDaoException;

/**
 * @author chao
 *
 */
public interface BaseCassandraDao {

	public ResultSet execute(Statement statement) throws CCPCassandraDaoException;

	public ResultSet execute(String cql) throws CCPCassandraDaoException;

	public ResultSet execute(String cql, Object... params) throws CCPCassandraDaoException;

	public ResultSetFuture executeAsync(Statement statement) throws CCPCassandraDaoException;

	public ResultSetFuture executeAsync(String cql) throws CCPCassandraDaoException;

	public ResultSetFuture executeAsync(String cql, Object... params) throws CCPCassandraDaoException;
}
