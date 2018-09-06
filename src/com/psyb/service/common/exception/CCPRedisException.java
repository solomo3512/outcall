/**
 * 
 */
package com.psyb.service.common.exception;

import org.ming.sample.alarm.impl.AbstractMessage.AlarmLevel;
import org.ming.sample.core.exception.AbstractException;

import com.psyb.service.common.ScriptManager;

/**
 * @author chao
 */
public class CCPRedisException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CCPRedisException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorDesc getErrorDesc(Throwable cause) {
		String errorCode = "111000";
		String errorMsg = ScriptManager.getErrorDesc(errorCode);
		String customMsg = cause.getMessage();
		AlarmLevel level = AlarmLevel.NORMAL;
		
		if (cause instanceof org.springframework.dao.DataAccessException) {
			errorCode = "111007";
			errorMsg = ScriptManager.getErrorDesc(errorCode);
			customMsg = cause.toString();
			level = AlarmLevel.IMPORTANT;
		}
		
		return new ErrorDesc(errorCode, errorMsg, customMsg, level);
	}
}
