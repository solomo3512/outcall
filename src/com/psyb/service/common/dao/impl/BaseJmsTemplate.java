/**
 * 
 */
package com.psyb.service.common.dao.impl;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.springframework.jms.core.JmsTemplate;

/**
 * @author chao
 */
public class BaseJmsTemplate extends JmsTemplate {

	public BaseJmsTemplate() {
		super();
	}

	public BaseJmsTemplate(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	public void doSend(MessageProducer producer, Message message) throws JMSException {
		producer.send(message, getDeliveryMode(), getPriority(), getTimeToLive());
	}
}
