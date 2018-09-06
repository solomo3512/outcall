package com.psyb.service.set.service;

import com.psyb.service.common.exception.CCPDaoException;
import com.psyb.service.set.model.XNumSet;

public interface SetService {

	public String handleSetTransferNum(XNumSet set) throws CCPDaoException;
	
	
	
}
