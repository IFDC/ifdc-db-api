package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mike
 */

@Path("hello/{username}")
public class Hello {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test(@PathParam("username") String userName) {
        return "Hello, Heroku from " + userName + "!";
    }
}
