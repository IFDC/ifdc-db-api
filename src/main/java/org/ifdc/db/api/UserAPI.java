package org.ifdc.db.api;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.ifdc.db.util.DBUtil;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Meng Zhang
 */
@Path("user")
public class UserAPI {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String help(@DefaultValue("unknow user") @QueryParam("user") String user) {
        return "user/<user_name>/ : searching user ";
    }
    
    @GET
    @Path("{userName: [a-zA-Z][a-zA-Z_0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findByUserName(@PathParam("userName") String userName) {
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {

            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("users");
            return collection.find(new Document("userName", userName)).first().toJson();
        }
//        HashMap<String, String> ret = new HashMap<>();
//        ret.put("userName", userName);
//        ret.put("salt", "$2a$10$GjrkM8BOszMYrczF0ylIou");
//        ret.put("hashedPassword", "$2a$10$GjrkM8BOszMYrczF0ylIou4yN8Y/Bh3MUN985..n9BykvhiK7kPDe");
//        try {
//            return mapper.writeValueAsString(ret);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return "{}";
//        }
    }
    
    @POST
    @Path("register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public boolean registerUserByPost(
            @FormDataParam("userName") @QueryParam("userName") String userName,
            @FormDataParam("password") @QueryParam("password") String password,
            @FormDataParam("userRank") @QueryParam("userRank") @DefaultValue("regular") String userRank) {
        return registerUser(userName, password, userRank);
    }
    
    @GET
    @Path("register")
    public boolean registerUserByGet(
            @QueryParam("userName") String userName,
            @QueryParam("password") String password,
            @QueryParam("userRank") @DefaultValue("regular") String userRank) {
        return registerUser(userName, password, userRank);
    }
    
    private boolean registerUser(String userName, String password, String userRank) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("users");
//            if (collection.find(new Document("userName", userName)).first() == null) {
//                return false;
//            }
            collection.insertOne(
                    new Document("userName", userName)
                    .append("salt", salt)
                    .append("hashedPassword", hashedPassword)
                    .append("userRank", userRank));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
