package com.psyb.service.common.exception;

import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.model.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ming.sample.util.JSONUtil;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FileServer Exception union handler, also it can send alarm message.
 * 
 * @author chao
 * @see ExceptionHandler
 */
public class ExceptionHandler implements HandlerExceptionResolver {

	public static final Logger logger = LogManager.getLogger(ExceptionHandler.class.getName());

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object obj,
			Exception e) {
		logger.info("@Catch Exception: [" + e.getClass().getName() + "]");
		handleStackTraceElement(e);

		ModelAndView mv = null;
		if (e instanceof CCPServiceException) {

			// 业务异常和错误, 直接给用户提示
			CCPServiceException se = (CCPServiceException) e;
			logger.info("Service Error: " + se.getErrorCode() + "|" + se.getErrorMsg());
			logger.info("Service Custom: " + se.getCustomMsg());

			mv = getModelAndView(request, new Response(se.getErrorCode(), se.getErrorMsg()));

		}  else if (e instanceof CCPException) {

			// 内部模块或者向外部发送请求的网络错误, 这种错误可以给用户明确提示或者包装后提示
			CCPException ccp = (CCPException) e;
			logger.info("CCPE Error: " + ccp.getErrorCode() + "|" + ccp.getErrorMsg());
			logger.info("CCPE Custom: " + ccp.getCustomMsg());
			
			mv = getModelAndView(request, new Response(ccp.getErrorCode(), ccp.getErrorMsg()));

		} else {
			logger.error("ExceptionHandler: ", e);
			// 未知错误
			mv = getModelAndView(request, handleUnknownResponse(e));
		}

		// At last
		handleTag(obj, e);
		return mv;
	}

	private Response handleUnknownResponse(Exception e) {
		String errorMsg = e.getMessage();
		if (e instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
			return new Response("560001", String.format(ScriptManager.getErrorDesc("560001")));
		} else if (errorMsg != null && errorMsg.length() == 5 && errorMsg.startsWith("560")) {
			return new Response(errorMsg, ScriptManager.getErrorDesc(errorMsg));
		}
		return new Response("560000", ScriptManager.getErrorDesc("560000"));
	}

	private ModelAndView getModelAndView(final HttpServletRequest request, final Object obj) {
		String contentType = request.getContentType();
		if(contentType != null && contentType.toUpperCase().indexOf("XML") != -1){
			return new ModelAndView(Constants.XML_VIEW_NAME, "Response", obj);
		}else{
			return new ModelAndView(Constants.JSON_VIEW_NAME, JSONUtil.objToMap(obj));
		}
	}

	private void handleTag(Object obj, Exception ex) {
		StackTraceElement se = ex.getStackTrace()[0];
		if (obj instanceof AbstractController) {
			((AbstractController) obj).printEndTag(se.getMethodName(), AbstractController.MARGIN_1);
		}
	}

	private void handleStackTraceElement(Exception ex) {
		StackTraceElement[] ste = ex.getStackTrace();
		if (ste != null) {
			for (int i = 0; i < ste.length; i++) {
				StackTraceElement cause = ste[i];
				if (i == 0) {
					logger.info("Exception Location [ClassName: " + cause.getClassName() + ", MethodName: "
							+ cause.getMethodName() + ", LineNumber: " + cause.getLineNumber() + "]");
					break;
				}
			}
		}
	}
}
