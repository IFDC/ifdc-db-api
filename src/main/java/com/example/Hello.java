package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mike
 */

@Path("hello")
public class Hello {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Hello, Heroku from mike!";
    }
}
