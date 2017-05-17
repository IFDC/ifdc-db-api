/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ifdc.db.api.util;

import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.ifdc.db.api.me.ProjectAPITest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class JsonUtilTest {
    
    private final static Logger LOG = (Logger) LoggerFactory.getLogger(JsonUtilTest.class);
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws IOException, Exception {
        ArrayList<Document> input = new ArrayList();
        input.add(new Document().append("_id", new ObjectId()).append("name", "KKK"));
        input.add(new Document().append("_id", new ObjectId()).append("name", "YYY"));
        String ret = JsonUtil.toJsonStr(input);
        LOG.info(ret);
        input = JsonUtil.toDocList(ret);
        String ret2 = JsonUtil.toJsonStr(input);
        LOG.info(ret2);
        assertEquals(ret, ret2);
        LOG.info(JsonUtil.toJsonStr(new Document()));
        Document test = null;
        LOG.info(JsonUtil.toJsonStr(test));
        LOG.info("{}", JsonUtil.toObject("null", ArrayList.class) == null);
        assertTrue(JsonUtil.toObject("null", ArrayList.class) == null);
    }
}
