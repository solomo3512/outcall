package com.psyb.service.set.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psyb.service.common.Constants;
import com.psyb.service.common.dao.BaseRedisDao;
import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.common.exception.CCPRedisException;
import com.psyb.service.set.model.XNumSet;
import com.psyb.service.set.service.SetService;

@Service
public class SetServiceImpl implements SetService {

	protected final Logger logger = LogManager.getLogger(getClass().getName());
	
	private static final String IVR_HANGUP = "<Hangup/>";
	
	@Autowired
	private BaseRedisDao baseRedisDao;
	
	@Override
	public String handleSetTransferNum(XNumSet set)
			throws CCPDaoException {
		
		String key = BaseRedisDao.REDIS_X_NUM+set.getFrom()+"#"+set.getDisnumber();
		try {
			logger.info("handleSetTransferNum saveRedisKey =" + key + ", transferNum =" + set.getTo());
			baseRedisDao.saveRedisValue(key, set.getTo(), Constants.XNUM_DISABLED_TIME);
		} catch (CCPRedisException e) {
			e.printStackTrace();
		}
		return IVR_HANGUP;
	}
	

	
}
