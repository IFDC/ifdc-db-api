package org.ifdc.db.api;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.combine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.ifdc.db.util.DBUtil;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("data/me")
public class DataMEAPI {

    private final static Logger LOG = (Logger) LoggerFactory.getLogger(DataMEAPI.class);
    
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean uploadMeDataByPost(
            @FormDataParam("data") @DefaultValue("{}") String data) {
        return uploadMeData(data);
    }
    
    @GET
    @Path("upload")
    public boolean uploadMeDataByGet(
            @QueryParam("data") @DefaultValue("{}") String data) {
        return uploadMeData(data);
    }
    
    private boolean uploadMeData(String jsonData) {
        
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<HashMap> dataArr;
        try {
            dataArr = mapper.readValue(jsonData, ArrayList.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            for (HashMap data : dataArr) {
                collection.insertOne(new Document(data));
            }
            
            return true;
        } catch (MongoWriteException ex) {
            LOG.warn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    @POST
    @Path("replace2")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean replaceMeDataByPost(
            @FormDataParam("id") String id,
            @FormDataParam("indicatorData") @DefaultValue("{}") String indicatorData) {
        return replaceMeData(id, indicatorData);
    }
    
    @GET
    @Path("replace2")
    public boolean replaceMeDataByGet(
            @QueryParam("id") String id,
            @QueryParam("indicatorData") @DefaultValue("{}") String indicatorData) {
        return replaceMeData(id, indicatorData);
    }
    
    private boolean replaceMeData(String id, String indicatorData) {
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            collection.findOneAndReplace(new Document("_id", new ObjectId(id)), Document.parse(indicatorData));
            
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
    public String replaceMeDataByPost(
            @FormDataParam("project") String project,
            @FormDataParam("year") String year,
            @FormDataParam("crop") String crop,
            @FormDataParam("technology") String technology) {
        return findMeData(project, year, crop, technology);
    }
    
    @GET
    @Path("find")
    public String replaceMeDataByGet(
            @QueryParam("project") String project,
            @QueryParam("year") String year,
            @QueryParam("crop") String crop,
            @QueryParam("technology") String technology) {
        return findMeData(project, year, crop, technology);
    }
    
    private String findMeData(String project, String year, String crop, String technology) {
        
        if (project == null || crop == null || technology == null) {
            return "{}";
        }
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI())) {
     
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            Document search = new Document("project", project)
                    .append("crop", crop)
                    .append("technology", technology);
            if (year != null) {
                search = search.append("year", year);
            }
//            if (crop != null) {
//                search = search.append("crop", crop);
//            }
//            if (technology != null) {
//                search = search.append("technology", technology);
//            }
            HashMap<String, HashMap<String, String>> records = new HashMap();
            collection.find(search)
                    .projection(fields(include("year", "indicators"), excludeId()))
                    .forEach(new Consumer<Document>() {
                @Override
                public void accept(Document t) {
                    HashMap<String, String> record = new HashMap();
                    Document indicators = t.get("indicators", Document.class);
                    if (indicators != null) {
                        indicators.keySet().stream().forEach((key) -> {
                            record.put(key, indicators.getString(key));
                        });
                    }
                    records.put(t.getString("year"), record);
                }
            });
            
            return new ObjectMapper().writeValueAsString(records);
        } catch (MongoWriteException ex) {
            LOG.warn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "{}";
    }
    
    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean updateMeDataByPost(
            @FormDataParam("project") String project,
            @FormDataParam("year") String year,
            @FormDataParam("crop") String crop,
            @FormDataParam("technology") String technology,
            @FormDataParam("indicatorData") @DefaultValue("{}") String indicatorData) {
        return updateMeData(project, year, crop, technology, indicatorData);
    }
    
    @GET
    @Path("update")
    public boolean updateMeDataByGet(
            @QueryParam("project") String project,
            @QueryParam("year") String year,
            @QueryParam("crop") String crop,
            @QueryParam("technology") String technology,
            @QueryParam("indicatorData") @DefaultValue("{}") String indicatorData) {
        return updateMeData(project, year, crop, technology, indicatorData);
    }
    
    private boolean updateMeData(String project, String year, String crop, String technology, String indicatorData) {
        
        if (project == null || crop == null || technology == null || year == null) {
            return false;
        }
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            Document search = new Document("project", project)
                    .append("year", year)
                    .append("crop", crop)
                    .append("technology", technology);
            Document indicatorBson = Document.parse(indicatorData);
            Document newRecord = new Document(search).append("indicators", indicatorBson);
            
            try {
               collection.insertOne(newRecord);
            }  catch (MongoWriteException e) {
                if(e.getMessage().contains("duplicate key")) {
                    ArrayList<Bson> sets = new ArrayList();
                    for (String key : indicatorBson.keySet()) {
                        sets.add(set("indicators." + key, indicatorBson.getString(key)));
                    }
                    
                    Document ret = collection.findOneAndUpdate(search, combine(sets));
                    return ret != null;
                } else {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } catch (MongoWriteException ex) {
            LOG.warn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    @POST
    @Path("update2")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean updateMeDataByPost(
            @FormDataParam("project") String project,
            @FormDataParam("year") String year,
            @FormDataParam("crop") String crop,
            @FormDataParam("technology") String technology,
            @FormDataParam("indicator") String indicator,
            @FormDataParam("value") String value) {
        return updateMeData(project, year, crop, technology, indicator, value);
    }
    
    @GET
    @Path("update2")
    public boolean updateMeDataByGet(
            @QueryParam("project") String project,
            @QueryParam("year") String year,
            @QueryParam("crop") String crop,
            @QueryParam("technology") String technology,
            @QueryParam("indicator") String indicator,
            @QueryParam("value") String value) {
        return updateMeData(project, year, crop, technology, indicator, value);
    }
    
    private boolean updateMeData(String project, String year, String crop, String technology, String indicator, String value) {
        
        if (project == null || crop == null || technology == null || year == null || indicator == null || value == null) {
            return false;
        }
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            Document search = new Document("project", project)
                    .append("year", year)
                    .append("crop", crop)
                    .append("technology", technology);
            Document indicatorBson = new Document(indicator, value);
            Document newRecord = new Document(search).append("indicators", indicatorBson);
            
            try {
               collection.insertOne(newRecord);
               System.out.println("Insert OK");
            }  catch (MongoWriteException e) {
                if(e.getMessage().contains("duplicate key")) {
                    ArrayList<Bson> sets = new ArrayList();
                    for (String key : indicatorBson.keySet()) {
                        sets.add(set("indicators." + key, indicatorBson.getString(key)));
                    }
                    
                    Document ret = collection.findOneAndUpdate(search, combine(sets));
                    return ret != null;
                } else {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } catch (MongoWriteException ex) {
            LOG.warn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    @GET
    @Path("project/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String listProjectByGet() {
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            ArrayList<String> ret = new ArrayList();
            collection.distinct("project", String.class).forEach(new Consumer<String>() {
                    @Override
                    public void accept(String t) {
                        ret.add(t);
                    }
                });
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(ret);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    @GET
    @Path("crop/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String listCropByGet() {
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            ArrayList<String> ret = new ArrayList();
            collection.distinct("crop", String.class).forEach(new Consumer<String>() {
                    @Override
                    public void accept(String t) {
                        ret.add(t);
                    }
                });
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(ret);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    @GET
    @Path("technology/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String listTechnologyByGet() {
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            ArrayList<String> ret = new ArrayList();
            collection.distinct("technology", String.class).forEach(new Consumer<String>() {
                    @Override
                    public void accept(String t) {
                        ret.add(t);
                    }
                });
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(ret);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    @POST
    @Path("activity/check")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean checkActivityByPost(
            @FormDataParam("project") String project,
            @FormDataParam("crop") String crop,
            @FormDataParam("technology") String technology) {
        return checkActivity(project, crop, technology);
    }
    
    @GET
    @Path("activity/check")
    public boolean checkActivityByGet(
            @QueryParam("project") String project,
            @QueryParam("crop") String crop,
            @QueryParam("technology") String technology) {
        return checkActivity(project, crop, technology);
    }
    
    public boolean checkActivity(String project, String crop, String technology) {
        
        if (project == null) {
            return false;
        }
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
         
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");

            Document search = new Document("project", project);
            if (crop != null) {
                search = search.append("crop", crop);
            }
            if (technology != null) {
                search = search.append("technology", technology);
            }
            Document ret = collection.find(search).first();
            return ret != null;
        }
    }
    
    @POST
    @Path("activity/find")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String findActivityByPost(
            @FormDataParam("project") String project,
            @FormDataParam("crop") String crop,
            @FormDataParam("technology") String technology) {
        return findActivity(project, crop, technology);
    }
    
    @GET
    @Path("activity/find")
    @Produces(MediaType.APPLICATION_JSON)
    public String findActivityByGet(
            @QueryParam("project") String project,
            @QueryParam("crop") String crop,
            @QueryParam("technology") String technology) {
        return findActivity(project, crop, technology);
    }
    
    public String findActivity(String project, String crop, String technology) {
        
        if (project == null) {
            return "";
        }
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
         
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            HashSet<String> ret = new HashSet();

            Document search = new Document("project", project);
            
            if (crop != null) {
                search = search.append("crop", crop);
            }
            if (technology != null) {
                search = search.append("technology", technology);
            }
            collection.find(search)
                    .projection(fields(include("project", "crop", "technology"), excludeId()))
                    .forEach(new Consumer<Document> () {

                @Override
                public void accept(Document t) {
                    String key =
                    t.get("project", String.class) + "_" +
                    t.get("crop", String.class) + "_" +
                    t.get("technology", String.class);
                    ret.add(key);
                }
            });
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(ret);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    @GET
    @Path("project/find")
    @Produces(MediaType.APPLICATION_JSON)
    public String findProjectByGet(
            @QueryParam("project_name") String projectName) {
        
        JsonFactory jf = new JsonFactory();
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JsonGenerator generator = jf.createGenerator(baos);) {
     
            
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("medata");
            generator.writeStartArray();
            collection.find(new Document("project", projectName))
//                    .projection(fields(include("project"), excludeId()))
                    .forEach(new Consumer<Document>() {
                @Override
                public void accept(Document record) {
                    try {
                        generator.writeRawValue(record.toJson());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            generator.writeEndArray();
            generator.flush();
            return baos.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
