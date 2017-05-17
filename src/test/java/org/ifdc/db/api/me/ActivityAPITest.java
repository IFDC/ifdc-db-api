package org.ifdc.db.api.me;

import ch.qos.logback.classic.Logger;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class ActivityAPITest extends JerseyTest {
    
    private final static Logger LOG = (Logger) LoggerFactory.getLogger(ActivityAPITest.class);
    private final static String BASE = "me/activity";

    @Override
    protected Application configure() {
//        ((Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(Level.INFO);
        ResourceConfig config = new ResourceConfig(ActivityAPI.class);
        config.register(MultiPartFeature.class);
        return config;
    }

    @Test
    public void testList() {
        LOG.info("testList");
        final String responseMsg = target().path(BASE).path("list").queryParam("skip", "1").queryParam("limit", "10").request().get(String.class);
        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg);
        final String responseMsg2 = target().path(BASE).path("list").request().get(String.class);
        LOG.info(responseMsg2);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"},{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg2);
    }

    @Test
    public void testListCrop() {
        LOG.info("testListCrop");
        final String responseMsg = target().path(BASE).path("listcrop").request().get(String.class);
        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"},{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg2);
    }

    @Test
    public void testListTech() {
        LOG.info("testListTech");
        final String responseMsg = target().path(BASE).path("listtech").request().get(String.class);
        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"},{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg2);
    }

    @Test
    public void testFind() {
        LOG.info("testFind");
        final String responseMsg = target().path(BASE).path("find").queryParam("project_name", "MALI-FDP/MD").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("null", responseMsg);
        final String responseMsg2 = target().path(BASE).path("find")
                .queryParam("project_name", "MALI-FDP/MD")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a062\"},\"project_name\":\"MALI-FDP/MD\",\"crop\":\"Rice\",\"tech\":\"FDP\",\"project_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"}}", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("find").queryParam("id", "59168ad3c2d0ed4374b1a062").request().get(String.class);
//        LOG.info(responseMsg2);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a062\"},\"project_name\":\"MALI-FDP/MD\",\"crop\":\"Rice\",\"tech\":\"FDP\",\"project_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"}}", responseMsg3);
    }

    @Test
    public void testSearch() {
        LOG.info("testSearch");
        final String responseMsg = target().path(BASE).path("search").queryParam("project_name", "MALI-FDP/MD").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("[{\"_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a062\"},\"project_name\":\"MALI-FDP/MD\",\"crop\":\"Rice\",\"tech\":\"FDP\",\"project_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"}}]", responseMsg);
    }

    @Test
    public void testAdd() {
        LOG.info("testAdd");
        final String responseMsg = target().path(BASE).path("add")
                .queryParam("project_name", "GHANA VEG2")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .request().get(String.class);
        LOG.info(responseMsg);
        assertNotEquals(null, responseMsg);
        final String responseMsg2 = target().path(BASE).path("add")
                .queryParam("project_name", "GHANA VEG2")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .request().get(String.class);
        LOG.info(responseMsg2);
        assertEquals("-1", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("add")
                .queryParam("project_name", "GHANA VEG99")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .request().get(String.class);
        LOG.info(responseMsg3);
        assertEquals("-3", responseMsg3);
    }
}
