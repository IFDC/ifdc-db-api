package org.ifdc.db.api;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
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
import org.ifdc.db.util.DBUtil;
import org.ifdc.db.util.MappingHelper;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("mapping/icasa")
public class MappingIcasaAPI {

    private final static Logger LOG = (Logger) LoggerFactory.getLogger(MappingIcasaAPI.class);

    @GET
    @Path("sync")
    public boolean syncIcasa() {
        HashMap<String, String> mappingJsons = MappingHelper.readIcasaToJsonMap(true);

        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {

            MongoDatabase database = mongoClient.getDatabase("ifdc_db_mapping");
            MongoCollection<Document> collection = database.getCollection("icasa");

            // Clear the mapping records
            collection.drop();

            // Create Indexes
            collection.createIndex(new Document("code_display", 1), new IndexOptions().unique(true));

            // Insert updated records
            mappingJsons.keySet().stream().map((key) -> {
//                System.out.print(key);
//                System.out.print("  :  ");
                LOG.debug("Add mapping: {}", mappingJsons.get(key));
                return key;
            }).forEach((key) -> {
                collection.insertOne(Document.parse(mappingJsons.get(key)));
//                collection.findOneAndReplace(new Document("code_display", key), Document.parse(mappingJsons.get(key)));
            });

            return true;
        } catch (MongoWriteException ex) {
            LOG.warn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @POST
    @Path("find")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIcasaMappingByPost(
            @FormDataParam("variable_names") @DefaultValue("") String varNames,
            @FormDataParam("keywords") String keywords) {
        return getIcasaMapping(varNames, keywords);
    }

    @GET
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public String getIcasaMappingByGet(
            @QueryParam("variable_names") @DefaultValue("") String varNames,
            @QueryParam("keywords") String keywords) {
        return getIcasaMapping(varNames, keywords);
    }

    public String getIcasaMapping(String varNames, String keywords) {
        
        JsonFactory jsonfactory = new JsonFactory();
        try (
                MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JsonGenerator generator = jsonfactory.createGenerator(baos);) {

            MongoDatabase database = mongoClient.getDatabase("ifdc_db_mapping");
            MongoCollection<Document> collection = database.getCollection("icasa");
            String[] varNameArr = varNames.toUpperCase().split(",");
//            String[] keywordsArr = keywords.split("|");
            generator.writeStartObject();
            for (String varName : varNameArr) {
                Document ret = collection.find(new Document("code_display", varName)).first();
                generator.writeFieldName(varName);
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
