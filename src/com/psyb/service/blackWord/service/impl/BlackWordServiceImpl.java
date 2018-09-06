package com.psyb.service.blackWord.service.impl;

import java.util.Collection;
import java.util.HashMap;

import org.ahocorasick.trie.Emit;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.ming.sample.alarm.impl.AbstractMessage.AlarmLevel;
import org.ming.sample.util.ProtocolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psyb.service.blackWord.dao.IBlackWordDao;
import com.psyb.service.blackWord.form.BlackReq;
import com.psyb.service.blackWord.form.BlackResp;
import com.psyb.service.blackWord.service.IBlackWordService;
import com.psyb.service.common.BlackWordHelper;
import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPServiceException;
import com.psyb.service.common.util.StringUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
/**
 * 黑词管理
 * @Title：BlackWordServiceImpl 
 * @author:maliang
 * @date:2017年2月23日下午2:10:14
 */
@Service
public class BlackWordServiceImpl implements IBlackWordService {

	private Logger logger = LogManager.getLogger(BlackWordServiceImpl.class);

	@Autowired
	private IBlackWordDao blackWordDao;
	@Autowired
	private BlackWordHelper loadBlackSerice;

	/**
	 * 黑白词管理
	 */
	@Override
	public BlackResp manageBlackWord(BlackReq req) throws CCPServiceException,
	CCPDaoException {
		String id=req.getId();
		String cmdType=req.getCmdType();
		String blackWords=req.getWords();
		String accountSid=req.getAccountSid();
		String wordType=req.getWordType();

		logger.info("基本参数校验.");

		if(StringUtils.isEmpty(wordType)){
			throw new CCPServiceException(ScriptManager.buildError("113506"));
		}

		if(StringUtils.isEmpty(cmdType)){
			throw new CCPServiceException(ScriptManager.buildError("113500"));
		}
		//处理黑词
		if(Constants.BALCK_WORDS_TYPE.equals(wordType)){
			logger.info("start deal balck words.");
			try{
				//添加黑词
				if(Constants.BALCK_WORDS_ADD.equals(cmdType)){
					logger.info("start deal add balck words.");
					if(StringUtils.isEmpty(blackWords)){
						throw new CCPServiceException(ScriptManager.buildError("113502"));
					}

					if(StringUtils.length(blackWords) >100 ){
						throw new CCPServiceException(ScriptManager.buildError("113505"));
					}
					if(!blackWordDao.checkBlackWordsByWord(blackWords)){
						try{
							blackWordDao.addBlackWord(blackWords,wordType);
						}catch(Exception e){
							//容错处理，有可能数据已经存在
							boolean flag=blackWordDao.checkBlackWordsByWord(blackWords);
							if(flag){
								logger.info("the blackWord is have in db:{}",blackWords);
							}else{
								throw new CCPServiceException(ScriptManager.buildError("113509"));
							}
						}
					}else{
						logger.info("the blackWord is have in db:{}",blackWords);
					}
				}else if(Constants.BALCK_WORDS_MODIFY.equals(cmdType)){//修改黑词
					logger.info("start deal modify balck words.");

					if(StringUtils.isEmpty(id)){
						throw new CCPServiceException(ScriptManager.buildError("113503"));
					}

					if(!StringUtils.isNumeric(id)){
						throw new CCPServiceException(ScriptManager.buildError("113504"));
					}

					if(StringUtils.isEmpty(blackWords)){
						throw new CCPServiceException(ScriptManager.buildError("113502"));
					}

					if(StringUtils.length(blackWords) >100 ){
						throw new CCPServiceException(ScriptManager.buildError("113505"));
					}

					blackWordDao.updateBlackWord(Integer.parseInt(id), blackWords,wordType);

				}else if(Constants.BALCK_WORDS_DELETE.equals(cmdType)){//删除黑词
					logger.info("start deal delete balck words.");

					if(StringUtils.isEmpty(id)){
						throw new CCPServiceException(ScriptManager.buildError("113503"));
					}

					if(!StringUtils.isNumeric(id)){
						throw new CCPServiceException(ScriptManager.buildError("113504"));
					}

					blackWordDao.deleteBlackWord(Integer.parseInt(id));
				}else{
					logger.info("cmdType :{} error.",cmdType);
					throw new CCPServiceException(ScriptManager.buildError("113501"));
				}
			}finally{
				//刷新黑词缓存
				loadBlackSerice.reloadBlackWord();
			}

		}else if(Constants.WHITE_WORDS_TYPE.equals(wordType)){//处理白词
			logger.info("start deal white words.");
			if(Constants.BALCK_WORDS_ADD.equals(cmdType)){//添加白词
				logger.info("start deal add white words.");
				if(StringUtils.isEmpty(blackWords)){
					throw new CCPServiceException(ScriptManager.buildError("113502"));
				}

				if(StringUtils.length(blackWords) >100 ){
					throw new CCPServiceException(ScriptManager.buildError("113505"));
				}

				if(StringUtils.isEmpty(accountSid)){
					throw new CCPServiceException(ScriptManager.buildError("113508"));
				}
				if(!blackWordDao.checkBlackWordsByWord(blackWords, accountSid)){
					try{
						blackWordDao.addWhiteWord(blackWords,wordType,accountSid);
					}catch(Exception e){
						boolean flag=blackWordDao.checkBlackWordsByWord(blackWords, accountSid);
						if(flag){
							logger.info("the whiteWord is have in db:{}",blackWords);
						}else{
							throw new CCPServiceException(ScriptManager.buildError("113509"));
						}
					}
				}else{
					logger.info("the whiteWord is have in db:{}",blackWords);
				}

			}else if(Constants.BALCK_WORDS_MODIFY.equals(cmdType)){//修改白词
				logger.info("start deal modify white words.");

				if(StringUtils.isEmpty(id)){
					throw new CCPServiceException(ScriptManager.buildError("113503"));
				}

				if(!StringUtils.isNumeric(id)){
					throw new CCPServiceException(ScriptManager.buildError("113504"));
				}

				if(StringUtils.isEmpty(blackWords)){
					throw new CCPServiceException(ScriptManager.buildError("113502"));
				}

				if(StringUtils.length(blackWords) >100 ){
					throw new CCPServiceException(ScriptManager.buildError("113505"));
				}

				if(StringUtils.isEmpty(accountSid)){
					throw new CCPServiceException(ScriptManager.buildError("113508"));
				}

				blackWordDao.updateWhiteWord(Integer.parseInt(id), blackWords,wordType,accountSid);

			}else if(Constants.BALCK_WORDS_DELETE.equals(cmdType)){//删除白词
				logger.info("start deal delete white words.");

				if(StringUtils.isEmpty(id)){
					throw new CCPServiceException(ScriptManager.buildError("113503"));
				}

				if(!StringUtils.isNumeric(id)){
					throw new CCPServiceException(ScriptManager.buildError("113504"));
				}

				blackWordDao.deleteBlackWord(Integer.parseInt(id));
			}else{
				logger.info("cmdType :{} error.",cmdType);
				throw new CCPServiceException(ScriptManager.buildError("113501"));
			}
		}else{
			logger.info("wordType :{} error.",wordType);
			throw new CCPServiceException(ScriptManager.buildError("113507"));
		}

		return new BlackResp(Constants.SUCC,Constants.SUCC_DESC);
	}

	@Override
	public BlackResp CheckBlackWord(BlackReq req) throws CCPServiceException,
	CCPDaoException {
		String id=req.getId();
		String cmdType=req.getCmdType();
		String blackWords=req.getWords();
		String accountSid=req.getAccountSid();
		String wordType=req.getWordType();

		logger.info("基本参数校验.");
//
//		if(StringUtils.isEmpty(wordType)){
//			throw new CCPServiceException(ScriptManager.buildError("113506"));
//		}
//
//		if(StringUtils.isEmpty(cmdType)){
//			throw new CCPServiceException(ScriptManager.buildError("113500"));
//		}

		checkBlackWord(req.getText(), accountSid);

		return new BlackResp(Constants.SUCC,Constants.SUCC_DESC);
	}

	/**
	 * 黑词过滤
	 * 
	 * @param mediaTxt
	 * @throws CCPServiceException
	 * @throws CCPDaoException
	 */
	private void checkBlackWord(String mediaTxt, String accountSid)
			throws CCPServiceException, CCPDaoException {
		// TTS文本黑字典过滤
		logger.info("开启黑词校验功能,准备校验黑词...");
		Collection<Emit> collection = loadBlackSerice.checkBlackWord(mediaTxt);
		if (collection != null && collection.size() > 0) {
			logger.info("Black words：{}", collection.toString());
			String blackWords[] = collection.toString()
					.replace("[", "").replace("]", "").split(",");
			if (blackWords != null && blackWords.length > 0) {
				logger.info("start check white words.");
				StringBuffer returnWords = new StringBuffer();
				for (int i = 0; i < blackWords.length; i++) {
					if (!loadBlackSerice.checkWhiteWord(accountSid,
							blackWords[i].trim())) {
						returnWords.append(blackWords[i].trim())
						.append(",");
					}
				}
				if (returnWords.toString() != null
						&& returnWords.toString().length() > 0) {
					String blackWord = "";
					if (returnWords.toString().endsWith(",")) {
						blackWord = returnWords.toString().substring(0,
								returnWords.toString().length() - 1);
					} else {
						blackWord = returnWords.toString();
					}
					throw new CCPServiceException(
							ScriptManager.buildError("111352",
									"mediaTxt内容中存在敏感词：[" + blackWord
									+ "]",
									AlarmLevel.IMPORTANT, "BlackWords "
											+ returnWords.toString()
											+ " was be found by TTS."));
				}
			}
		}
	}

}
