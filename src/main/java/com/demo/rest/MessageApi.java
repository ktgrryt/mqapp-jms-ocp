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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("ローカルキューへの送信に失敗しました: " + e.getMessage())
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("リモートキューへの送信に失敗しました: " + e.getMessage())
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error: " + e.getMessage())
                           .build(); // HTTP 500 Internal Server Error を返す
        }
    }
    
}