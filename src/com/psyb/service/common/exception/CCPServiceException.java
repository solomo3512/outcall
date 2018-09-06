/**
 * 
 */
package com.psyb.service.common.exception;

import org.ming.sample.core.exception.AbstractException;

/**
 * 业务异常（消息应该是具体的错误描述）
 * 
 * @version 0.1
 */
public class CCPServiceException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5661579194124038369L;

	/**
	 * 格式：message = {"errorCode":"16000",
	 * "errorMsg":"未知错误","alarmLevel":"IMPORTANT","customMsg":"应用不存在"}
	 * 
	 * @param message
	 */
	public CCPServiceException(String message) {
		super(message);
	}

	@Override
	public ErrorDesc getErrorDesc(Throwable cause) {
		return null;
	}
}
