package com.psyb.service.common.util;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class ConverterUtil {

	@SuppressWarnings("unchecked")
	public static Object populateResult(Map map, Class clazz) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
			BeanUtils.populate(obj, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
