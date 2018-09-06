/**
 * 
 */
package com.psyb.service.common;

import org.ming.sample.alarm.impl.CommonWorkQueue;

/**
 * 重用CommonWorkQueue异步发送消息
 * 
 * @author chao
 */
public class CommonWorkQueueImpl extends CommonWorkQueue {

	public CommonWorkQueueImpl() {

	}

	@Override
	protected void execute(Object obj) throws Exception {
		super.execute(obj);

	}

}
