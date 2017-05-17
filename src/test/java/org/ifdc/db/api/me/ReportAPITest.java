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
public class ReportAPITest extends JerseyTest {
    
    private final static Logger LOG = (Logger) LoggerFactory.getLogger(ReportAPITest.class);
    private final static String BASE = "me/report";

    @Override
    protected Application configure() {
//        ((Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(Level.INFO);
        ResourceConfig config = new ResourceConfig(ReportAPI.class);
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
    public void testFind() {
        LOG.info("testFind");
        final String responseMsg = target().path(BASE).path("find").queryParam("project_name", "MALI-FDP/MD").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("null", responseMsg);
        final String responseMsg2 = target().path(BASE).path("find")
                .queryParam("project_name", "MALI-FDP/MD")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .queryParam("year", "2014")
                .request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad4c2d0ed4374b1a065\"},\"project_name\":\"MALI-FDP/MD\",\"crop\":\"Rice\",\"tech\":\"FDP\",\"year\":\"2014\",\"activity_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a062\"},\"indicators\":{\"AUIT\":\"273\",\"NJC\":\"0\"}}", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("find").queryParam("id", "59168ad4c2d0ed4374b1a065").request().get(String.class);
//        LOG.info(responseMsg2);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad4c2d0ed4374b1a065\"},\"project_name\":\"MALI-FDP/MD\",\"crop\":\"Rice\",\"tech\":\"FDP\",\"year\":\"2014\",\"activity_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a062\"},\"indicators\":{\"AUIT\":\"273\",\"NJC\":\"0\"}}", responseMsg3);
    }

    @Test
    public void testSearch() {
        LOG.info("testSearch");
        final String responseMsg = target().path(BASE).path("search").queryParam("year", "2014").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("[{\"_id\":{\"$oid\":\"59168ad4c2d0ed4374b1a065\"},\"project_name\":\"MALI-FDP/MD\",\"crop\":\"Rice\",\"tech\":\"FDP\",\"year\":\"2014\",\"activity_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a062\"},\"indicators\":{\"AUIT\":\"273\",\"NJC\":\"0\"}}]", responseMsg);
    }

    @Test
    public void testAdd() {
        LOG.info("testAdd");
        final String responseMsg = target().path(BASE).path("add")
                .queryParam("project_name", "MALI-FDP/MD")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .queryParam("year", "2016")
                .queryParam("indi_name", "AUIT")
                .queryParam("indi_value", "19429")
                .request().get(String.class);
        LOG.info(responseMsg);
        assertNotEquals(null, responseMsg);
        final String responseMsg2 = target().path(BASE).path("add")
                .queryParam("project_name", "MALI-FDP/MD")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .request().get(String.class);
        LOG.info(responseMsg2);
        assertEquals("null", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("add")
                .queryParam("project_name", "MALI-FDP/MD99")
                .queryParam("crop", "Rice")
                .queryParam("tech", "FDP")
                .queryParam("year", "2016")
                .queryParam("indi_name", "AUIT")
                .queryParam("indi_value", "19429")
                .request().get(String.class);
        LOG.info(responseMsg3);
        assertEquals("-3", responseMsg3);
    }
}
