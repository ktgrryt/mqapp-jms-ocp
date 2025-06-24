package com.demo.messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Stateless
public class MQProducer {

    @Inject
    @JMSConnectionFactory("jms/wmqCF")
    JMSContext context;

    @Resource(lookup = "jms/queue1")
    Queue queue;

    @Resource(lookup = "jms/remote1")
    Queue remoteQueue;

    // ローカルキューにメッセージを送信するメソッド
    public String sendLocalMessage(String message) throws Exception {
        try {
            TextMessage textMessage = context.createTextMessage();
            textMessage.setText(message);
            context.createProducer().send(queue, textMessage);

            // JMS ヘッダー情報を取得
            JsonObject headers = Json.createObjectBuilder()
                    .add("JMSMessageID", textMessage.getJMSMessageID())
                    .add("JMSTimestamp", textMessage.getJMSTimestamp())
                    .add("JMSCorrelationID", textMessage.getJMSCorrelationID() != null ? textMessage.getJMSCorrelationID() : "")
                    .add("JMSDestination", textMessage.getJMSDestination() != null ? textMessage.getJMSDestination().toString() : "")
                    .add("JMSDeliveryMode", textMessage.getJMSDeliveryMode())
                    .add("JMSExpiration", textMessage.getJMSExpiration())
                    .add("JMSPriority", textMessage.getJMSPriority())
                    .add("JMSReplyTo", textMessage.getJMSReplyTo() != null ? textMessage.getJMSReplyTo().toString() : "")
                    .add("JMSType", textMessage.getJMSType() != null ? textMessage.getJMSType() : "")
                    .build();

            // メッセージとヘッダー情報をJSON形式で返す
            return Json.createObjectBuilder()
                    .add("message", message)
                    .add("headers", headers)
                    .build()
                    .toString();
        } catch (Exception e) {
            throw new Exception("ローカルキューへの送信に失敗しました  " + e.getMessage(), e);
        }
    }

    // リモートキューにメッセージを送信するメソッド
    public String sendRemoteMessage(String message) throws Exception {
        try {
            TextMessage textMessage = context.createTextMessage();
            textMessage.setText(message);
            context.createProducer().send(remoteQueue, textMessage);

            // JMS ヘッダー情報を取得
            JsonObject headers = Json.createObjectBuilder()
                    .add("JMSMessageID", textMessage.getJMSMessageID())
                    .add("JMSTimestamp", textMessage.getJMSTimestamp())
                    .add("JMSCorrelationID", textMessage.getJMSCorrelationID() != null ? textMessage.getJMSCorrelationID() : "")
                    .add("JMSDestination", textMessage.getJMSDestination() != null ? textMessage.getJMSDestination().toString() : "")
                    .add("JMSDeliveryMode", textMessage.getJMSDeliveryMode())
                    .add("JMSExpiration", textMessage.getJMSExpiration())
                    .add("JMSPriority", textMessage.getJMSPriority())
                    .add("JMSReplyTo", textMessage.getJMSReplyTo() != null ? textMessage.getJMSReplyTo().toString() : "")
                    .add("JMSType", textMessage.getJMSType() != null ? textMessage.getJMSType() : "")
                    .build();

            // メッセージとヘッダー情報をJSON形式で返す
            return Json.createObjectBuilder()
                    .add("message", message)
                    .add("headers", headers)
                    .build()
                    .toString();
        } catch (Exception e) {
            throw new Exception("リモートキューへの送信に失敗しました  " + e.getMessage(), e);
        }
    }
}
