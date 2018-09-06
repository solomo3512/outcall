package com.psyb.service.common.load;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.NDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.psyb.service.common.AbstractController;
import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.model.Response;

@Controller("api2013.reloadCacheController")
@RequestMapping(Constants.ApiVersion2015)
public class ReloadCacheController extends AbstractController {

	@Override
	protected String getClassName() {
		return ReloadCacheController.class.getName();
	}
	
	/**
	 * http://112.124.0.43:9881/2013-12-26/inner/reloadCache?type=script&name=BillServer
	 * 本地配置加载
	 * 
	 * @param sig
	 * @param param
	 * @param request
	 * @return
	 * @throws CCPDaoException
	 * @throws CCPServiceException
	 * @throws IOException
	 * @throws CCPDaoException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/inner/reloadCache")
	public ModelAndView reloadCache(@RequestParam("type")
	String type, @RequestParam("name")
	String fileName, HttpServletRequest request) throws CCPServiceException, IOException, CCPDaoException {
		printStartTag("reloadCache");
		printHttpPacket(request, null);
		if ("local".equals(type)) {
			String localDir = ScriptManager.getScriptManager().getLocalAbsolutePath();
			String filePath = localDir + File.separator + fileName + ".xml";
			reload(fileName, filePath);
		} else {
			throw new CCPServiceException(ScriptManager.buildError("111050"));
		}

		printEndTag("reloadCache");
		return postResponse(request, new Response(Constants.SUCC, "[" + NDC.get() + "] " + fileName + " reload success!"));
	}

	private void reload(String fileName, String filePath) throws IOException, CCPServiceException {
		logger.info("---------------------------- Found [" + fileName
				+ "] has changed, then reload Start] ----------------------------");
		if (Constants.GENERAL_FILE_NAME.equals(fileName)) {
			ScriptManager.getScriptManager().loadGeneralConfig(filePath);
		} else if (Constants.ERROR_FILE_NAME.equals(fileName)) {
			ScriptManager.getScriptManager().loadErrorCode(filePath);
		} else if (Constants.UNCHECKRIGHT_NAME.equals(fileName)) {
			ScriptManager.getScriptManager().loadRight(filePath);
		} else if (Constants.MDN_BLACK_LIST_NAME.equals(fileName)) {
			ScriptManager.getScriptManager().loadNumberBlackList(filePath);
		} else {
			throw new CCPServiceException(ScriptManager.buildError("111051"));
		}
		logger.info("---------------------------- Reload [" + fileName
				+ "] File End] ----------------------------\r\n\r\n");
	}
}
