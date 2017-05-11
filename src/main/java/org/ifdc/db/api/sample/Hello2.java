package org.ifdc.db.api.sample;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mike
 */
@Path("hello2")
public class Hello2 {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test(@DefaultValue("unknow user") @QueryParam("user") String user) {
        return "hello world from " + user + "!";
    }
}
