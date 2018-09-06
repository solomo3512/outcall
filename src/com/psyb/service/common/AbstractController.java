/**
 * 
 */
package com.psyb.service.common;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.json.JSONObject;
import org.ming.sample.util.JSONUtil;
import org.ming.sample.util.ProtocolUtil;
import org.springframework.web.servlet.ModelAndView;

import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.model.Response;

/**
 * All of Controller parents class.
 * 
 * @version 1.0
 */
public abstract class AbstractController {
	protected final Logger logger = LogManager.getLogger(getClassName());

	public AbstractController() {
	}

	/**
	 * Sub class must be Override
	 * 
	 * @return
	 */
	protected abstract String getClassName();

	public static enum DATAGRAM_TYPE {
		XML {
			public String getValue() {
				return "XML";
			}
		},
		JSON {
			public String getValue() {
				return "JSON";
			}
		};
		public abstract String getValue();
	}

	/**
	 * print start tag margin 1 with up.
	 * 
	 * @param name
	 * @param space
	 * @return
	 */
	public void printStartTag(String tagName) {
		logger.info("@Controller");
		printStartTag(tagName, MARGIN_0);
	}

	/**
	 * print start tag
	 * 
	 * @param name
	 * @param space
	 * @return
	 */
	public void printStartTag(String tagName, int space) {
		StringBuffer sb = new StringBuffer();
		switch (space) {
		case MARGIN_0:
			break;
		case MARGIN_1:
			sb.append("\r\n");
			break;
		case MARGIN_2:
			sb.append("\r\n\r\n");
			break;
		default:
			sb.append("\r\n");
			break;
		}
		sb.append("----------------------------[").append(tagName).append(" Start").append(
				"]-------------------------------");
		logger.info(sb.toString());
	}

	/**
	 * print end tag
	 * 
	 * @param name
	 * @param space
	 * @return
	 */
	public final void printEndTag(String tagName) {
		printEndTag(tagName, MARGIN_1);
		ThreadContext.removeStack();
	}

	/**
	 * distance of up or down
	 */
	public static final int MARGIN_0 = 0;
	public static final int MARGIN_1 = 1;
	public static final int MARGIN_2 = 2;

	/**
	 * print end tag
	 * 
	 * @param name
	 * @param space
	 * @return
	 */
	public final void printEndTag(String tagName, int space) {
		StringBuffer sb = new StringBuffer("----------------------------[").append(tagName).append(" End").append(
				"]-------------------------------");
		switch (space) {
		case MARGIN_0:
			break;
		case MARGIN_1:
			sb.append("\r\n");
			break;
		case MARGIN_2:
			sb.append("\r\n\r\n");
			break;
		default:
			sb.append("\r\n");
			break;
		}
		logger.info(sb.toString());
	}

	/**
	 * print http packet
	 * 
	 * @param log
	 * @param request
	 * @param body
	 */
	protected final void printHttpPacket(HttpServletRequest request, String body) {
		logger.info(ProtocolUtil.getHttpRequestPacket(request, body));
	}

	/**
	 * request parameter transfer object by parse json or xml.
	 * 
	 * @param type
	 * @param key
	 * @param body
	 * @return
	 * @throws Exception
	 */
	protected Object parser(DATAGRAM_TYPE type, String key, String body) throws Exception {
		return null;
	}

	/**
	 * @param request
	 * @param key
	 * @param body
	 * @return
	 * @throws CCPControllerException
	 */
	protected final Object postRequest(HttpServletRequest request, String key, String body)
			throws CCPServiceException {
		try {
			DATAGRAM_TYPE dt = doCheckAccept(request);
			logger.info("@Start parse body... [" + key + "]"+", @Response Accept: " + dt.getValue());
			Object obj = parser(dt, key, body);
			logger.info("@End parse body [" + (obj == null ? "null" : obj.getClass().getName()) + "]");
			return obj;
		} catch (Exception e) {
			logger.error(e);
			throw new CCPServiceException(ScriptManager.buildError("16008"));
		}
	}

	public DATAGRAM_TYPE doCheckAccept(HttpServletRequest request) {
		return ProtocolUtil.doCheckAccept(request) == 1 ? DATAGRAM_TYPE.JSON : DATAGRAM_TYPE.XML;
	}

	/**
	 * @param request
	 * @param obj
	 * @param isPrintEnd
	 * @return
	 */
	protected final ModelAndView postResponse(HttpServletRequest request, Object obj) {
		DATAGRAM_TYPE dt = doCheckAccept(request);
		logger.info("@Response Accept: {}", dt.getValue());
		if (dt == DATAGRAM_TYPE.JSON) {
			return new ModelAndView(Constants.JSON_VIEW_NAME, JSONUtil.objToMap(obj));
		}
		return new ModelAndView(Constants.XML_VIEW_NAME, "Response", obj);
//		return new ModelAndView(Constants.JSON_VIEW_NAME, JSONUtil.objToMap(obj));
	}
	
	/**
	 * @param request
	 * @param obj
	 * @param isPrintEnd
	 * @return
	 */
	protected final ModelAndView postResponse2(HttpServletRequest request, Object obj) {
		DATAGRAM_TYPE dt = doCheckAccept(request);
		logger.info("@Response Accept: " + dt.getValue());
		if (dt == DATAGRAM_TYPE.JSON) {
//			return new ModelAndView(Constants.JSON_VIEW_NAME, JSONUtil.objToMap4Obj(obj));
			return null;
		}
		return new ModelAndView(Constants.XML_VIEW_NAME, "Response", obj);
	}
	
	protected final void postResponse(HttpServletRequest request, HttpServletResponse response, String body) {
		DATAGRAM_TYPE dt = doCheckAccept(request);
		logger.info("@Response Accept: " + dt.getValue());
		if (dt == DATAGRAM_TYPE.JSON) {
//			return new ModelAndView(Constants.JSON_VIEW_NAME, JSONUtil.objToMap4Obj(obj));
//			return;
		}
		
		String sendBody = "";
		if(!StringUtils.isEmpty(body))
		{
			if(body.contains("<Response>"))
			{
				sendBody = body;
			}
			else
			{
				sendBody = "<Response>\n"+body+"\n</Response>\n";
			}
		}
		
		try {
			response.getWriter().write(sendBody);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected final void postResponse(HttpServletRequest request, HttpServletResponse response, Response resp) {
		DATAGRAM_TYPE dt = doCheckAccept(request);
		logger.info("@Response Accept2: " + dt.getValue());
		try {
			String sendBody = "";
			if (dt == DATAGRAM_TYPE.JSON) {
//				return new ModelAndView(Constants.JSON_VIEW_NAME, JSONUtil.objToMap4Obj(obj));
				JSONObject ob = new JSONObject();
				ob.put("Response", resp.getJsonBody());
				sendBody = ob.toString();
			}
			else
			{
				String body = resp.getBody();
				if(!StringUtils.isEmpty(body))
				{
					if(body.contains("<Response>"))
					{
						sendBody = body;
					}
					else
					{
						sendBody = "<Response>\n"+body+"\n</Response>\n";
					}
				}
				
			}
			logger.info("Response xml body: \n" + sendBody);
			response.getWriter().write(sendBody);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
