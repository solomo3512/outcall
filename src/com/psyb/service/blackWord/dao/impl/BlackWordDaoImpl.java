package com.psyb.service.blackWord.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.psyb.service.blackWord.dao.IBlackWordDao;
import com.psyb.service.blackWord.po.BlackWordsInfo;
import com.psyb.service.common.dao.BaseJdbcDao;
import com.psyb.service.common.exception.CCPDaoException;
/**
 * 黑词管理
 * @Title：BlackWordDaoImpl 
 * @author:maliang
 * @date:2017年2月23日下午2:05:42
 */
@Repository
public class BlackWordDaoImpl implements IBlackWordDao {

	@Autowired
	private BaseJdbcDao baseJdbcDao;

	@Override
	public void addBlackWord(String blackWords,String wordType) throws CCPDaoException {
		String sql="INSERT INTO sys_black_words (words,addTime,wordType) VALUES (?,NOW(),?)";
		baseJdbcDao.update(sql, new Object[]{blackWords,wordType});
	}

	@Override
	public void updateBlackWord(int id, String blackWords,String wordType)
			throws CCPDaoException {
		String sql="UPDATE sys_black_words SET words=?,update_time=NOW(),wordType=? WHERE id=?";
		baseJdbcDao.update(sql, new Object[]{blackWords,wordType,id});
	}

	@Override
	public void deleteBlackWord(int id) throws CCPDaoException {
		String sql="DELETE FROM ytx_black_words WHERE id=?";
		baseJdbcDao.update(sql, new Object[]{id});
	}

	/**
	 * 获取黑词列表
	 * @return
	 * @throws CCPDaoException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlackWordsInfo> getBlackWordsList() throws CCPDaoException {
		String sql="SELECT words FROM sys_black_words WHERE wordType='0' ";
		return (List<BlackWordsInfo>) baseJdbcDao.queryForList(sql, null, BlackWordsInfo.class);
	}

	/**
	 * 增加白词
	 * @param blackWords
	 * @param wordType
	 * @param accountSid
	 * @throws CCPDaoException
	 */
	@Override
	public void addWhiteWord(String blackWords, String wordType,
			String accountSid) throws CCPDaoException {
		String sql="INSERT INTO sys_black_words (words,addTime,wordType,accountSid) VALUES (?,NOW(),?,?)";
		baseJdbcDao.update(sql, new Object[]{blackWords,wordType,accountSid});
	}

	/**
	 * 更新白词
	 * @param id
	 * @param blackWords
	 * @param wordType
	 * @param accountSid
	 * @throws CCPDaoException
	 */
	@Override
	public void updateWhiteWord(int id, String blackWords, String wordType,
			String accountSid) throws CCPDaoException {
		String sql="UPDATE sys_black_words SET words=?,update_time=NOW(),wordType=?,accountSid=? WHERE id=?";
		baseJdbcDao.update(sql, new Object[]{blackWords,wordType,accountSid,id});
	}

	/**
	 * 查询主账号白词列表
	 * @param accountSid
	 * @return
	 * @throws CCPDaoException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BlackWordsInfo> getWhiteWordsByAccountSid(String accountSid)
			throws CCPDaoException {
		String sql="SELECT  words  FROM sys_black_words WHERE accountSid =? AND wordType='1' ";
		return (List<BlackWordsInfo>) baseJdbcDao.queryForList(sql, new Object[]{accountSid}, BlackWordsInfo.class);
	}

	/**
	 * 检查黑词是否已存在
	 * @param blackWords
	 * @param wordType
	 * @return
	 * @throws CCPDaoException 
	 */
	@Override
	public boolean checkBlackWordsByWord(String blackWords) throws CCPDaoException {
		String sql="SELECT  words  FROM sys_black_words WHERE words =? AND wordType='0' ";
		List<BlackWordsInfo> list=(List<BlackWordsInfo>) baseJdbcDao.queryForList(sql, new Object[]{blackWords}, BlackWordsInfo.class);
		if(list !=null && list.size()>0){
			return true;
		}
		return false;
	}

	/**
	 * 校验主账号下是否已存在该白词
	 * @param blackWords
	 * @param accountSid
	 * @return
	 * @throws CCPDaoException
	 */
	@Override
	public boolean checkBlackWordsByWord(String blackWords, String accountSid)
			throws CCPDaoException {
		String sql="SELECT  words  FROM sys_black_words WHERE words =? AND accountSid=? AND wordType='1' ";
		List<BlackWordsInfo> list=(List<BlackWordsInfo>) baseJdbcDao.queryForList(sql, new Object[]{blackWords,accountSid}, BlackWordsInfo.class);
		if(list !=null && list.size()>0){
			return true;
		}
		return false;
	}
	
	
	

}
