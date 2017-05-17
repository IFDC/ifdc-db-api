package org.ifdc.db.api.me;

import ch.qos.logback.classic.Logger;
import static com.mongodb.client.model.Filters.and;
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
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.ifdc.db.api.util.JsonUtil;
import org.ifdc.db.api.util.MongoDBHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("me/activity")
public class ActivityAPI extends MeDBAPI {
    
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ActivityAPI.class);

    @Override
    public String getCollectionName() {
        return "activity";
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
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public String find(
            @QueryParam("id") @DefaultValue("") String id,
            @QueryParam("project_name") @DefaultValue("") String projName,
            @QueryParam("tech") @DefaultValue("") String tech,
            @QueryParam("crop") @DefaultValue("") String crop) {
        
        Document ret = null;
        if (!id.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), id);
        } else if (!projName.isEmpty() && !tech.isEmpty() && !crop.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), and(eq("project_name", projName), eq("tech", tech), eq("crop", crop)));
        }
        return JsonUtil.toJsonStr(ret);
    }
    
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(
            @QueryParam("project_name") @DefaultValue("") String projName,
            @QueryParam("tech") @DefaultValue("") String tech,
            @QueryParam("crop") @DefaultValue("") String crop) {
        
        ArrayList<Document> ret = new ArrayList();
        
        if (projName.isEmpty() && tech.isEmpty() && crop.isEmpty()) {
            return JsonUtil.toJsonStr(ret);
        } else {
            ArrayList<Bson> search = new ArrayList();
            if (!projName.isEmpty()) {
                search.add(eq("project_name", projName));
            }
            if (!tech.isEmpty()) {
                search.add(eq("tech", tech));
            }
            if (!crop.isEmpty()) {
                search.add(eq("crop", crop));
            }
            ret = MongoDBHandler.search(getConnection(), and(search));
            return JsonUtil.toJsonStr(ret);
        }
    }
    
    @GET
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public String add(
            @QueryParam("project_name") @DefaultValue("") String projectName,
            @QueryParam("crop") @DefaultValue("") String crop,
            @QueryParam("tech") @DefaultValue("") String tech,
            @QueryParam("project_id") @DefaultValue("") String projectId) {
        
        if (projectName.isEmpty() || crop.isEmpty() || tech.isEmpty()) {
            return JsonUtil.NULL;
        }
        
        ObjectId projectOId;
        if (projectId.isEmpty()) {
            Document proj = MongoDBHandler.find(getConnection("project"), eq("name", projectName), include());
            if (proj == null) {
                LOG.warn("Could not retrevie the project record");
                return "-3";
            } else {
                projectOId = proj.getObjectId("_id");
            }
        } else {
            projectOId = new ObjectId(projectId);
        }
        Document record = new Document("project_name", projectName)
                .append("crop", crop)
                .append("tech", tech)
                .append("project_id", projectOId);
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
    
    @GET
    @Path("listcrop")
    @Produces(MediaType.APPLICATION_JSON)
    public String listCrop() {
        
        ArrayList<String> ret = MongoDBHandler.distinct(getConnection(), "crop", String.class);
        return JsonUtil.toJsonStr(ret);
    }
    
    @GET
    @Path("listtech")
    @Produces(MediaType.APPLICATION_JSON)
    public String listTech() {
        
        ArrayList<String> ret = MongoDBHandler.distinct(getConnection(), "tech", String.class);
        return JsonUtil.toJsonStr(ret);
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
