package org.ifdc.db.api;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.ifdc.db.api.util.DBUtil;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("mapping/me")
public class MappingMEAPI {

    private final static Logger LOG = (Logger) LoggerFactory.getLogger(MappingMEAPI.class);

//    @GET
//    @Path("sync")
//    public boolean syncIndicators() {
//        HashMap<String, String> mappingJsons = new HashMap();
//
//        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
//
//            MongoDatabase database = mongoClient.getDatabase("ifdc_db_mapping");
//            MongoCollection<Document> collection = database.getCollection("me_indicators");
//
//            // Clear the mapping records
//            collection.drop();
//
//            // Create Indexes
//            collection.createIndex(new Document("var_code", 1), new IndexOptions().unique(true));
//
//            // Insert updated records
//            mappingJsons.keySet().stream().map((key) -> {
////                System.out.print(key);
////                System.out.print("  :  ");
//                LOG.debug("Add mapping: {}", mappingJsons.get(key));
//                return key;
//            }).forEach((key) -> {
//                collection.insertOne(Document.parse(mappingJsons.get(key)));
////                collection.findOneAndReplace(new Document("code_display", key), Document.parse(mappingJsons.get(key)));
//            });
//
//            return true;
//        } catch (MongoWriteException ex) {
//            LOG.warn(ex.getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    @POST
    @Path("register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean registerMEIndicatorByPost(
            @FormDataParam("title") String title,
            @FormDataParam("code") String code,
            @FormDataParam("unit") String unit) {
        return registerMEIndicator(title, code, unit);
    }

    @GET
    @Path("register")
    public boolean registerMEIndicatorByGet(
            @QueryParam("title") String title,
            @QueryParam("code") String code,
            @QueryParam("unit") String unit) {
        return registerMEIndicator(title, code, unit);
    }

    public boolean registerMEIndicator(String title, String code, String unit) {
        
        if (title == null) {
            return false;
        }
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI())) {

            MongoDatabase database = mongoClient.getDatabase("ifdc_db_mapping");
            MongoCollection<Document> collection = database.getCollection("me_indicators");
            Document indicator = new Document("title", title);
            if (code != null) {
                indicator.append("code", code);
            }
            if (unit != null) {
                indicator.append("unit", unit);
            }
            collection.insertOne(indicator);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @POST
    @Path("find")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMeIndicatorByPost(
            @FormDataParam("titles") @DefaultValue("") String titles,
            @FormDataParam("codes") String codes) {
        return getMeIndicator(titles, codes);
    }

    @GET
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMeIndicatorByGet(
            @QueryParam("titles") @DefaultValue("") String titles,
            @QueryParam("codes") String codes) {
        return getMeIndicator(titles, codes);
    }

    public String getMeIndicator(String titles, String codes) {
        
        JsonFactory jsonfactory = new JsonFactory();
        try (
                MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JsonGenerator generator = jsonfactory.createGenerator(baos);) {

            MongoDatabase database = mongoClient.getDatabase("ifdc_db_mapping");
            MongoCollection<Document> collection = database.getCollection("me_indicators");
            String[] titleArr = titles.split(",");
//            String[] keywordsArr = keywords.split("|");
            generator.writeStartObject();
            for (String title : titleArr) {
                Document ret = collection.find(new Document("title", title)).first();
                generator.writeFieldName(title);
                if (ret != null) {
                    generator.writeRawValue(ret.toJson());
                } else {
                    generator.writeRawValue("{}");
                }
            }
            generator.writeEndObject();
            generator.flush();
            return baos.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
