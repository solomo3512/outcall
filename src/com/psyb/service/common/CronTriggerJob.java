package com.psyb.service.common;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.psyb.service.common.dao.CommonJdbcDao;

public class CronTriggerJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		CommonJdbcDao commonDao = (CommonJdbcDao)arg0.getJobDetail().getJobDataMap().get("CommonJdbcDao");
		ScriptManager.getScriptManager().loadInnerServers(commonDao);
		ScriptManager.getScriptManager().loadAccount(commonDao);
		System.out.println("CronTriggerJob = " + System.currentTimeMillis());
	}

}
