/**
 * 
 */
package com.psyb.service.common.dao.impl;

import org.springframework.stereotype.Repository;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.psyb.service.common.dao.BaseCassandraDao;
import com.psyb.service.common.exception.CCPCassandraDaoException;

/**
 * @author chao
 */
@Repository
public class BaseCassandraDaoImpl implements BaseCassandraDao {

	private Session session;

	private String keyspace;

	private String seedNode;

	public BaseCassandraDaoImpl() {

	}

	public void init() {
		PoolingOptions poolingOptions = new PoolingOptions();
		poolingOptions.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.LOCAL, 32);
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 2);
		poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL, 4);

		Cluster cluster = Cluster.builder().addContactPoint(seedNode).withPoolingOptions(poolingOptions).build();
		session = cluster.connect(keyspace);
	}

	@Override
	public ResultSet execute(String cql) throws CCPCassandraDaoException {
		try {
			return this.session.execute(cql);
		} catch (Exception e) {
			throw new CCPCassandraDaoException(e);
		}
	}

	@Override
	public ResultSet execute(String cql, Object... params) throws CCPCassandraDaoException {
		try {
			return this.session.execute(cql, params);
		} catch (Exception e) {
			throw new CCPCassandraDaoException(e);
		}
	}

	@Override
	public ResultSet execute(Statement statement) throws CCPCassandraDaoException {
		try {
			return this.session.execute(statement);
		} catch (Exception e) {
			throw new CCPCassandraDaoException(e);
		}
	}

	@Override
	public ResultSetFuture executeAsync(Statement statement) throws CCPCassandraDaoException {
		try {
			return this.session.executeAsync(statement);
		} catch (Exception e) {
			throw new CCPCassandraDaoException(e);
		}
	}

	@Override
	public ResultSetFuture executeAsync(String cql) throws CCPCassandraDaoException {
		try {
			return this.session.executeAsync(cql);
		} catch (Exception e) {
			throw new CCPCassandraDaoException(e);
		}
	}

	@Override
	public ResultSetFuture executeAsync(String cql, Object... params) throws CCPCassandraDaoException {
		try {
			return this.session.executeAsync(cql, params);
		} catch (Exception e) {
			throw new CCPCassandraDaoException(e);
		}
	}

	public void destroy() {
		System.out.println("BaseCassandraDaoImpl destroy");
	}

	/**
	 * @return the keyspace
	 */
	public String getKeyspace() {
		return keyspace;
	}

	/**
	 * @param keyspace
	 *            the keyspace to set
	 */
	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	/**
	 * @return the seedNode
	 */
	public String getSeedNode() {
		return seedNode;
	}

	/**
	 * @param seedNode
	 *            the seedNode to set
	 */
	public void setSeedNode(String seedNode) {
		this.seedNode = seedNode;
	}
}
