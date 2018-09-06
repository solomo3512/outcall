package com.psyb.service.common.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PropertiesListener implements ServletContextListener {

	//	private String LOG4J_PATH = this.getClass().getClassLoader().getResource("").getPath();

	public void contextInitialized(ServletContextEvent event) {
		String status = "Properties listener start...";
		event.getServletContext().log(status);

		//        System.setProperty("log4j.configurationFile", LOG4J_PATH);
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		//        event.getServletContext().log("log4j.configurationFile = " + System.getProperty("log4j.configurationFile"));
		event.getServletContext().log("Log4jContextSelector = " + System.getProperty("Log4jContextSelector"));
	}

	public void contextDestroyed(ServletContextEvent event) {
		String status = "Properties listener stop";
		event.getServletContext().log(status);
	}

}