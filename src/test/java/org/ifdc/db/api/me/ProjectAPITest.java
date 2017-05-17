package org.ifdc.db.api.me;

import ch.qos.logback.classic.Logger;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class ProjectAPITest extends JerseyTest {
    
    private final static Logger LOG = (Logger) LoggerFactory.getLogger(ProjectAPITest.class);
    private final static String BASE = "me/project";

    @Override
    protected Application configure() {
//        ((Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(Level.INFO);
        ResourceConfig config = new ResourceConfig(ProjectAPI.class);
        config.register(MultiPartFeature.class);
        return config;
    }

    @Test
    public void testList() {
        final String responseMsg = target().path(BASE).path("list").queryParam("skip", "1").queryParam("limit", "10").request().get(String.class);
        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg);
        final String responseMsg2 = target().path(BASE).path("list").request().get(String.class);
        LOG.info(responseMsg2);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"},{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg2);
    }

    @Test
    public void testListname() {
        final String responseMsg = target().path(BASE).path("listname").queryParam("skip", "1").queryParam("limit", "10").request().get(String.class);
        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg);
        final String responseMsg2 = target().path(BASE).path("listname").request().get(String.class);
        LOG.info(responseMsg2);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"},{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg2);
    }

    @Test
    public void testFind() {
        final String responseMsg = target().path(BASE).path("find").queryParam("name", "GHANA VEG").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}", responseMsg);
        final String responseMsg2 = target().path(BASE).path("find").queryParam("id", "59168ad2c2d0ed4374b1a060").request().get(String.class);
//        LOG.info(responseMsg2);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"}", responseMsg2);
    }

    @Test
    public void testSearch() {
        final String responseMsg = target().path(BASE).path("search").queryParam("name", "GHANA VEG").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg);
    }

    @Test
    public void testAdd() {
        final String responseMsg = target().path(BASE).path("add").queryParam("name", "GHANA VEG2").request().get(String.class);
        LOG.info(responseMsg);
        assertNotEquals(null, responseMsg);
        final String responseMsg2 = target().path(BASE).path("add").queryParam("name", "GHANA VEG2").request().get(String.class);
        LOG.info(responseMsg2);
        assertEquals("-1", responseMsg2);
    }
}
