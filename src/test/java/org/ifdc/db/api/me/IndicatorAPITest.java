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
public class IndicatorAPITest extends JerseyTest {
    
    private final static Logger LOG = (Logger) LoggerFactory.getLogger(IndicatorAPITest.class);
    private final static String BASE = "me/indicator";

    @Override
    protected Application configure() {
//        ((Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(Level.INFO);
        ResourceConfig config = new ResourceConfig(IndicatorAPI.class);
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
    public void testListunit() {
        final String responseMsg = target().path(BASE).path("listunit").queryParam("skip", "1").queryParam("limit", "10").request().get(String.class);
        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg);
        final String responseMsg2 = target().path(BASE).path("listunit").request().get(String.class);
        LOG.info(responseMsg2);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a060\"},\"name\":\"MALI-FDP/MD\"},{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg2);
    }

    @Test
    public void testFind() {
        final String responseMsg = target().path(BASE).path("find").queryParam("name", "AUIT").request().get(String.class);
//        LOG.info(responseMsg);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a063\"},\"name\":\"AUIT\",\"title\":\"Area under Improved Technology\",\"unit\":\"ha\",\"eg_code1\":\"3.2-18\",\"eg_code2\":\"4.5.2-2\"}", responseMsg);
        final String responseMsg2 = target().path(BASE).path("find").queryParam("id", "59168ad3c2d0ed4374b1a063").request().get(String.class);
//        LOG.info(responseMsg2);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a063\"},\"name\":\"AUIT\",\"title\":\"Area under Improved Technology\",\"unit\":\"ha\",\"eg_code1\":\"3.2-18\",\"eg_code2\":\"4.5.2-2\"}", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("find").queryParam("title", "Area under Improved Technology").request().get(String.class);
//        LOG.info(responseMsg3);
        assertEquals("{\"_id\":{\"$oid\":\"59168ad3c2d0ed4374b1a063\"},\"name\":\"AUIT\",\"title\":\"Area under Improved Technology\",\"unit\":\"ha\",\"eg_code1\":\"3.2-18\",\"eg_code2\":\"4.5.2-2\"}", responseMsg3);
    }

//    @Test
//    public void testSearch() {
//        final String responseMsg = target().path(BASE).path("search").queryParam("name", "GHANA VEG").request().get(String.class);
////        LOG.info(responseMsg);
//        assertEquals("[{\"_id\":{\"$oid\":\"59168ad2c2d0ed4374b1a061\"},\"name\":\"GHANA VEG\"}]", responseMsg);
//    }

    @Test
    public void testAdd() {
        final String responseMsg = target().path(BASE).path("add")
                .queryParam("name", "MFAT")
                .queryParam("title", "Male Farmers Adopting Technology")
                .queryParam("eg_code1", "3.2-17")
                .queryParam("eg_code2", "4.5.2-5")
                .request().get(String.class);
        LOG.info(responseMsg);
        assertNotEquals(null, responseMsg);
        final String responseMsg2 = target().path(BASE).path("add")
                .queryParam("name", "MFAT")
                .queryParam("title", "Male Farmers Adopting Technology")
                .queryParam("eg_code1", "3.2-17")
                .queryParam("eg_code2", "4.5.2-5")
                .request().get(String.class);
        LOG.info(responseMsg2);
        assertEquals("-1", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("add")
                .queryParam("name", "MFAT2")
                .queryParam("eg_code1", "3.2-17")
                .queryParam("eg_code2", "4.5.2-5")
                .request().get(String.class);
        LOG.info(responseMsg3);
        assertEquals("null", responseMsg3);
    }

    @Test
    public void testUpdate() {
        final String responseMsg = target().path(BASE).path("update")
                .queryParam("name", "MFAT")
                .queryParam("unit", "test1")
                .request().get(String.class);
        assertEquals("591a0a6c83c3843be81ef1e9", responseMsg);
        final String responseMsg2 = target().path(BASE).path("update")
                .queryParam("title", "Male Farmers Adopting Technology")
                .queryParam("unit", "test2")
                .request().get(String.class);
        assertEquals("591a0a6c83c3843be81ef1e9", responseMsg2);
        final String responseMsg3 = target().path(BASE).path("update")
                .queryParam("name", "MFAT2")
                .queryParam("unit", "test2")
                .request().get(String.class);
        assertEquals("-1", responseMsg3);
    }
}
