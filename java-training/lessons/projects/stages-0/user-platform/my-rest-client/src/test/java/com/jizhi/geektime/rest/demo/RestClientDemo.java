package com.jizhi.geektime.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        /*Response response = client
                .target("http://127.0.0.1:8080/user-web/rest/get")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response
        String content = response.readEntity(String.class);
        System.out.println(content);*/


        Entity<String> post = Entity.json("243242");
        Response postResponse = client
                .target("http://127.0.0.1:8080/user-web/rest/post")      // WebTarget
                .request() // Invocation.Builder
                .post(post);
        String postContent = postResponse.readEntity(String.class);
        System.out.println(postContent);


    }
}
