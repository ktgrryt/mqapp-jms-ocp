package com.demo.rest;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import com.demo.messaging.MQConsumer;
import com.demo.messaging.MQProducer;
import java.io.StringWriter;
import java.io.PrintWriter;

@Stateless
@Path("/")
public class MessageApi {

    @Inject
    MQProducer producer;

    @Inject
    MQConsumer consumer;
    
    @POST
    @Path("/sendlocal")
    public Response sendLocal(@FormParam("msg") String message) {
        try {
            String result = producer.sendLocalMessage(message);
            return Response.ok(result).build(); // HTTP 200 OK を返す
        } catch (Exception e) {
            // スタックトレースを文字列として取得
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("ローカルキューへの送信に失敗しました: " + e.getMessage() + stackTrace)
                           .build(); // HTTP 500 Internal Server Error を返す
        }
    }
    
    @POST
    @Path("/sendremote")
    public Response sendRemote(@FormParam("msg") String message) {
        try {
            String result = producer.sendRemoteMessage(message);
            return Response.ok(result).build(); // HTTP 200 OK を返す
        } catch (Exception e) {
            // スタックトレースを文字列として取得
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("リモートキューへの送信に失敗しました: " + e.getMessage() + stackTrace)
                           .build(); // HTTP 500 Internal Server Error を返す
        }
    }
    
    @GET
    @Path("/recv")
    public Response recv() {
        try {
            String result = consumer.recvMessage();
            return Response.ok(result).build(); // HTTP 200 OK を返す
        } catch (Exception e) {
            // スタックトレースを文字列として取得
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error: " + e.getMessage() + stackTrace)
                           .build(); // HTTP 500 Internal Server Error を返す
        }
    }
}
