package com.psyb.service.common.exception;

import com.psyb.service.common.ScriptManager;

import org.ming.sample.alarm.impl.AbstractMessage.AlarmLevel;
import org.ming.sample.core.exception.AbstractException;

/**
 * 数据层的异常
 * 
 * @version 1.0
 */
public class CCPDaoException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5793505615829263043L;

	/**
	 * 包装虚拟机抛出的底层异常
	 * 
	 * @param cause
	 */
	public CCPDaoException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorDesc getErrorDesc(Throwable cause) {
		String errorCode = "560000";
		String errorMsg = ScriptManager.getErrorDesc(errorCode);
		String customMsg = cause.getMessage();
		AlarmLevel level = AlarmLevel.NORMAL;
		
		if (cause instanceof CCPRuntimeException) {
			super.handleExceptionMessage(customMsg);
			return null;
		} else if (cause instanceof java.sql.SQLException) {
			errorCode = "560013";
			errorMsg = ScriptManager.getErrorDesc(errorCode);
			customMsg = cause.toString();
			level = AlarmLevel.IMPORTANT;
		} else if (cause instanceof org.springframework.dao.DataAccessException) {
			errorCode = "560014";
			errorMsg = ScriptManager.getErrorDesc(errorCode);
			customMsg = cause.toString();
			level = AlarmLevel.IMPORTANT;
		}

		return new ErrorDesc(errorCode, errorMsg, customMsg, level);
	}
}
