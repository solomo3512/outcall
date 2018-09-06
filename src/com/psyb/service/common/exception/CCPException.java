/**
 * 
 */
package com.psyb.service.common.exception;

import org.ming.sample.alarm.impl.AbstractMessage.AlarmLevel;
import org.ming.sample.core.exception.AbstractException;

import com.psyb.service.common.ScriptManager;

/**
 * 网络异常
 * 
 * @author chao
 */
public class CCPException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 160401346168282408L;

	/**
	 * 比较明确的自定义异常
	 * 
	 * @param message
	 */
	public CCPException(String message) {
		super(message);
	}

	/**
	 * 包装虚拟机抛出的底层异常
	 * 
	 * @param cause
	 */
	public CCPException(Throwable cause) {
		super(cause);
	}

	public ErrorDesc getErrorDesc(Throwable cause) {
		String errorCode = "560000";
		String errorMsg = ScriptManager.getErrorDesc(errorCode);
		String customMsg = cause.getMessage();
		AlarmLevel level = AlarmLevel.NORMAL;
		
		return new ErrorDesc(errorCode, errorMsg, customMsg, level);
	}

}
