/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.messaging.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.carewebframework.api.messaging.IPublisherInfo;
import org.carewebframework.api.spring.SpringUtil;
import org.springframework.jms.core.JmsTemplate;

/**
 * JMS utility methods.
 */
public final class JMSUtil {
    
    /**
     * Enforce static class.
     */
    private JMSUtil() {
    }
    
    /**
     * Message property. For use in message selectors.
     */
    public static final String MESSAGE_RECIPIENTS_PROPERTY = "Recipients";
    
    /**
     * Message property. For use in message selectors.
     */
    public static final String MESSAGE_SENDER_PROPERTY = "Sender";
    
    private static final String JMS_TOPIC_TEMPLATE = "jmsTopicTemplate";
    
    private static final String JMS_QUEUE_TEMPLATE = "jmsQueueTemplate";
    
    /**
     * @return JmsTemplate
     */
    public static JmsTemplate getJmsTopicTemplate() {
        return SpringUtil.getBean(JMS_TOPIC_TEMPLATE, JmsTemplate.class);
    }
    
    /**
     * @return JmsTemplate
     */
    public static JmsTemplate getJmsQueueTemplate() {
        return SpringUtil.getBean(JMS_QUEUE_TEMPLATE, JmsTemplate.class);
    }
    
    /**
     * Returns the client id from the connection.
     * 
     * @param connection JMS connection (may be null).
     * @return The client id (may be null).
     */
    public static String getClientId(Connection connection) {
        String clientId = null;
        
        try {
            clientId = connection == null ? null : connection.getClientID();
        } catch (JMSException e) {}
        
        return clientId;
    }
    
    /**
     * Creates a message selector which considers JMSType and recipients properties.
     * 
     * @param eventName The event name (i.e. DESKTOP.LOCK).
     * @param publisherInfo Info on the publisher. If null, then no recipients properties are added.
     * @return The message selector.
     */
    public static String getMessageSelector(String eventName, IPublisherInfo publisherInfo) {
        StringBuilder sb = new StringBuilder(
                "(JMSType='" + eventName + "' OR JMSType LIKE '" + eventName + ".%') AND (Recipients IS NULL");
        
        if (publisherInfo != null) {
            for (String selector : publisherInfo.getAttributes().values()) {
                addRecipientSelector(selector, sb);
            }
        }
        
        sb.append(')');
        return sb.toString();
    }
    
    /**
     * Add a recipient selector for the given value.
     * 
     * @param value Recipient value.
     * @param sb String builder to receive value.
     */
    private static void addRecipientSelector(String value, StringBuilder sb) {
        if (value != null) {
            sb.append(" OR Recipients LIKE '%,").append(value).append(",%'");
        }
    }
    
}
