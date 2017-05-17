package org.ifdc.db.api.me;

import ch.qos.logback.classic.Logger;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;
import java.util.ArrayList;
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
@Path("me/report")
public class ReportAPI extends MeDBAPI {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ReportAPI.class);

    @Override
    public String getCollectionName() {
        return "report";
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
            @QueryParam("crop") @DefaultValue("") String crop,
            @QueryParam("year") @DefaultValue("") String year) {

        Document ret = null;
        if (!id.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), id);
        } else if (!projName.isEmpty() && !tech.isEmpty() && !crop.isEmpty() && !year.isEmpty()) {
            ret = MongoDBHandler.find(getConnection(), and(
                    eq("project_name", projName),
                    eq("tech", tech),
                    eq("crop", crop),
                    eq("year", year)));
        }
        return JsonUtil.toJsonStr(ret);
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(
            @QueryParam("project_name") @DefaultValue("") String projName,
            @QueryParam("tech") @DefaultValue("") String tech,
            @QueryParam("crop") @DefaultValue("") String crop,
            @QueryParam("year") @DefaultValue("") String year,
            @QueryParam("skip") @DefaultValue(DEF_SKIP) int skip,
            @QueryParam("limit") @DefaultValue(DEF_LIMIT) int limit) {

        ArrayList<Document> ret = new ArrayList();

        if (projName.isEmpty() && tech.isEmpty() && crop.isEmpty() && year.isEmpty()) {
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
            if (!year.isEmpty()) {
                search.add(eq("year", year));
            }
            ret = MongoDBHandler.search(getConnection(), and(search), skip, limit);
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
            @QueryParam("year") @DefaultValue("") String year,
            @QueryParam("activity_id") @DefaultValue("") String activityId,
            @QueryParam("indi_name") @DefaultValue("") String indiName,
            @QueryParam("indi_value") @DefaultValue("") String indiValue) {

        if (indiName.isEmpty()) {
            return JsonUtil.NULL;
        }

        ObjectId activityOId;
        if (activityId.isEmpty()) {
            if (projectName.isEmpty() || crop.isEmpty() || tech.isEmpty()) {
                return JsonUtil.NULL;
            }
            Document act = MongoDBHandler.find(getConnection("activity"),
                    and(
                            eq("project_name", projectName),
                            eq("crop", crop),
                            eq("tech", tech)
                    ), include());
            if (act == null) {
                LOG.warn("Could not retrevie the activity record");
                return "-3";
            } else {
                activityOId = act.getObjectId("_id");
            }
        } else {
            activityOId = new ObjectId(activityId);
        }
        Document record = new Document("project_name", projectName)
                .append("crop", crop)
                .append("tech", tech)
                .append("year", year)
                .append("activity_id", activityOId);
        MongoDBHandler.add(getConnection(), record);
        Bson search = and(eq("project_name", projectName),eq("crop", crop),eq("tech", tech),eq("year", year));
        Document doc = MongoDBHandler.find(getConnection(), search, include());
        if (doc != null) {
            ObjectId reportOId = doc.getObjectId("_id");
            Bson update;
            if (indiValue.isEmpty()) {
                update = unset("indicators." + indiName);
            
            } else {
                update = set("indicators." + indiName, indiValue);
            }
            boolean ret = MongoDBHandler.addSub(getConnection(),
                    and(eq("activity_id", activityOId), eq("year", year)),
                    update);
            if (!ret) {
                return "-1";
            }
            return reportOId.toString();
        } else {
            LOG.warn("Could not retrevie the target record");
            return "-2";
        }

    }

}
