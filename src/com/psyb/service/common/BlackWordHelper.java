/**
 * 
 */
package com.psyb.service.common;

import java.util.Collection;
import java.util.List;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.TrieConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psyb.service.blackWord.dao.IBlackWordDao;
import com.psyb.service.blackWord.po.BlackWordsInfo;
import com.psyb.service.common.exception.CCPDaoException;

/**
 * 过滤黑词
 */
@Service("loadBlackSerice")
public class BlackWordHelper {
	
	private static Logger logger = LogManager.getLogger(BlackWordHelper.class);
	
	private static Trie trie = null;
	
	@Autowired
	private IBlackWordDao blackWordDao;
	
	/**
	 * 加载数据库中黑词数据
	 */
	public void loadBlackWord(){
		logger.info("start load black from db.");
		try {
			List<BlackWordsInfo> list=blackWordDao.getBlackWordsList();
			trie = new Trie(new TrieConfig(true, false));
			if(null !=list && list.size() >0){
				for(BlackWordsInfo words:list){
					trie.addKeyword(words.getWords());
				}
			}
			logger.info("load black word finish.");
		} catch (CCPDaoException e) {
			logger.info("query black list from db error.{}",e);
		}
	}
	
	/**
	 * 刷新黑词缓存
	 */
	public void reloadBlackWord(){
		logger.info("start reload black from db.");
		try {
			List<BlackWordsInfo> list=blackWordDao.getBlackWordsList();
			trie = new Trie(new TrieConfig(true, false));
			if(null !=list && list.size() >0){
				for(BlackWordsInfo words:list){
					trie.addKeyword(words.getWords());
				}
			}
		} catch (CCPDaoException e) {
			logger.info("query black list from db error.{}",e);
		}
	}
	
	/**
	 * 检查黑词
	 * @param text
	 * @return
	 */
	public Collection<Emit> checkBlackWord(String text) {
		try {
			if(trie==null){
				logger.info("no have black word.");
			}else{
				return trie.parseText(text);
			}
		} catch (Exception e) {
			logger.error("Check blackword exception:", e);
		}
		return null;
	}
	
	/**
	 * 根据主账号校验白词
	 * @param accountSid
	 * @param text
	 * @return
	 * @throws CCPDaoException
	 */
	public boolean checkWhiteWord(String accountSid,String text) throws CCPDaoException{
		List<BlackWordsInfo> list=blackWordDao.getWhiteWordsByAccountSid(accountSid);
		if(list != null && list.size() > 0){
			Trie trie = new Trie(new TrieConfig(true, false));
			for(BlackWordsInfo words:list){
				trie.addKeyword(words.getWords());
			}
			long textLen=text.length();
			Collection<Emit> coll=trie.parseText(text);
			if(coll == null || coll.size() ==0){
				logger.info("the text no have one in white words");
				return false;
			}else{
				if(textLen == coll.size()){
					logger.info("the text all in white words");
					return true;
				}else{
					Emit emit=coll.iterator().next();
					String whiteWrod=emit.getKeyword();
					if(whiteWrod.equals(text)){
						logger.info("the text all in white words");
						return true;
					}else{
						logger.info("white word have a part of the in white words");
						return false;
					}
				}
			}
		}else{
			logger.info("accountSid :{} no white word.",accountSid);
			return false;
		}
	}
}
