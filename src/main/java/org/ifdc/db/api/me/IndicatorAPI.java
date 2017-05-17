package org.ifdc.db.api.me;

import ch.qos.logback.classic.Logger;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import java.util.ArrayList;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.ifdc.db.api.util.JsonUtil;
import org.ifdc.db.api.util.MongoDBHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("me/indicator")
public class IndicatorAPI extends MeDBAPI {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(IndicatorAPI.class);

    @Override
    protected String getCollectionName() {
        return "indicator";
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(
            @QueryParam("skip") @DefaultValue(DEF_SKIP) int skip,
            @QueryParam("limit") @DefaultValue(DEF_LIMIT) int limit) {

        ArrayList<Document> ret = MongoDBHandler.list(getConnection(), skip, limit);

        return JsonUtil.toJsonStr(ret);
    }

    @GET
    @Path("listunit")
    @Produces(MediaType.APPLICATION_JSON)
    public String listName(
            @QueryParam("skip") @DefaultValue("0") int skip,
            @QueryParam("limit") @DefaultValue(Integer.MAX_VALUE + "") int limit) {

        ArrayList<String> ret = MongoDBHandler.distinct(getConnection(), "unit", String.class);

        return JsonUtil.toJsonStr(ret);
    }

    @GET
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public String find(
            @QueryParam("id") @DefaultValue("") String id,
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("title") @DefaultValue("") String title) {

        Document ret = null;
        if (!id.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), id);
        } else if (!name.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), eq("name", name));
        } else if (!title.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), eq("title", title));
        }
        return JsonUtil.toJsonStr(ret);
    }

//    @GET
//    @Path("search")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String search(
//            @QueryParam("keywords") @DefaultValue("") String keywords) {
//        
//        ArrayList<Document> ret = new ArrayList();
//        if (keywords.isEmpty()) {
//            return JsonUtil.toJsonStr(ret);
//        }
//        ret = MongoDBHandler.search(getConnection(), eq("keywords", keywords));
//        return JsonUtil.toJsonStr(ret);
//    }
    @GET
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public String add(
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("title") @DefaultValue("") String title,
            @QueryParam("eg_code1") @DefaultValue("") String egCode1,
            @QueryParam("eg_code2") @DefaultValue("") String egCode2,
            @QueryParam("unit") @DefaultValue("") String unit) {

        if (name.isEmpty() || title.isEmpty()) {
            return JsonUtil.NULL;
        }
        Document record = new Document("name", name).append("title", title);
        if (!unit.isEmpty()) {
            record = record.append("unit", unit);
        }
        if (!egCode1.isEmpty()) {
            record = record.append("eg_code1", egCode1);
        }
        if (!egCode2.isEmpty()) {
            record = record.append("eg_code2", egCode2);
        }
        boolean ret = MongoDBHandler.add(getConnection(), record);
        if (ret) {
            Document doc = MongoDBHandler.find(getConnection(), eq("name", name));
            if (doc != null) {
                return doc.getObjectId("_id").toString();
            } else {
                LOG.warn("Could not retrevie the new inserted record");
                return "-2";
            }
        } else {
            return "-1";
        }
    }

    @GET
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public String update(
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("title") @DefaultValue("") String title,
            @QueryParam("eg_code1") String egCode1,
            @QueryParam("eg_code2") String egCode2,
            @QueryParam("unit") String unit) {

        Bson search;
        if (name.isEmpty() && title.isEmpty()) {
            return JsonUtil.NULL;
        } else if (!name.isEmpty()) {
            search = eq("name", name);
        } else {
            search = eq("title", title);
        }
        ArrayList<Bson> updates = new ArrayList();
        if (unit != null) {
            updates.add(set("unit", unit));
        }
        if (egCode1 != null) {
            updates.add(set("eg_code1", egCode1));
        }
        if (egCode2 != null) {
            updates.add(set("eg_code2", egCode2));
        }
        Document ret = MongoDBHandler.update(getConnection(), search, combine(updates));
        if (ret != null) {
            return ret.getObjectId("_id").toString();
        } else {
            LOG.warn("Could not find the targeted record");
            return "-1";
        }
    }

}
