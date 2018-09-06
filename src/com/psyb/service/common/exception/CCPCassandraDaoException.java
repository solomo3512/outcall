/**
 * 
 */
package com.psyb.service.common.exception;

import org.ming.sample.core.exception.AbstractException;

/**
 * @author chao
 */
public class CCPCassandraDaoException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param cause
	 */
	public CCPCassandraDaoException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorDesc getErrorDesc(Throwable cause) {
		return null;
	}
}
