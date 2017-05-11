package org.ifdc.db.api.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    @Path("{username: [a-zA-Z][a-zA-Z_0-9]*}")
    public String test(@PathParam("username") String userName) {
        return "Hello, Heroku from " + userName + "!";
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("")
    public String test2() {
        return "Hello, Heroku!";
    }
}
