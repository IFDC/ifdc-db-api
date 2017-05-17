package org.ifdc.db.api.me;

import ch.qos.logback.classic.Logger;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bson.Document;
import org.ifdc.db.api.util.JsonUtil;
import org.ifdc.db.api.util.MongoDBHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("me/project")
public class ProjectAPI extends MeDBAPI {
    
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ProjectAPI.class);

    @Override
    protected String getCollectionName() {
        return "project";
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
    @Path("listname")
    @Produces(MediaType.APPLICATION_JSON)
    public String listName(
            @QueryParam("skip") @DefaultValue("0") int skip,
            @QueryParam("limit") @DefaultValue(Integer.MAX_VALUE + "") int limit) {
        
        ArrayList<Document> result = MongoDBHandler.list(getConnection(), skip, limit, fields(include("name") ,excludeId()));
        ArrayList<String> ret = new ArrayList();
        for (Document d : result) {
            ret.add(d.getString("name"));
        }
        
        return JsonUtil.toJsonStr(ret);
    }
    
    @GET
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public String find(
            @QueryParam("id") @DefaultValue("") String id,
            @QueryParam("name") @DefaultValue("") String name) {
        
        Document ret = null;
        if (!id.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), id);
        } else if (!name.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), eq("name", name));
        }
        return JsonUtil.toJsonStr(ret);
    }
    
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(
            @QueryParam("name") @DefaultValue("") String name) {
        
        ArrayList<Document> ret = new ArrayList();
        if (name.isEmpty()) {
            return JsonUtil.toJsonStr(ret);
        }
        ret = MongoDBHandler.search(getConnection(), eq("name", name));
        return JsonUtil.toJsonStr(ret);
    }
    
    @GET
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public String add(
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("description") @DefaultValue("") String description) {
        
        if (name.isEmpty()) {
            return JsonUtil.NULL;
        }
        Document record = new Document("name", name);
        if (!description.isEmpty()) {
            record = record.append("description", description);
        }
        boolean ret = MongoDBHandler.add(getConnection(), record);
        if (ret) {
            Document doc = MongoDBHandler.find(getConnection(), record);
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
    
//    @POST
//    @Path("list")
//    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
//    public String listForSample(
//            @FormParam("test") @DefaultValue("") String test) {
//        ArrayList<HashMap> ret = new ArrayList();
//        System.out.println(test);
//        HashMap tmp = new HashMap();
//        tmp.put("test", test);
//        ret.add(tmp);
//        return ret.toString();
//    }
    
}
