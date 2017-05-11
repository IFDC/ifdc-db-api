package org.ifdc.db.api;

import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
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
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Path("user")
public class UserAPI {
    
    private final static Logger LOG = (Logger) LoggerFactory.getLogger(UserAPI.class);
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String help(@DefaultValue("unknow user") @QueryParam("user") String user) {
        return "user/find/<user_name>/ : searching user /r/n" +
               "user/register?userName&password&rank/ : register user /r/n";
    }
    
    @GET
    @Path("find/{userName: [a-zA-Z][a-zA-Z_0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findByUserName(@PathParam("userName") String userName) {
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {

            MongoDatabase database = mongoClient.getDatabase("ifdc_db");
            MongoCollection<Document> collection = database.getCollection("users");
            Document ret = collection.find(new Document("userName", userName)).first();
            if (ret == null) {
                return "";
            }
            return ret.toJson();
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
            @FormDataParam("userName") String userName,
            @FormDataParam("password") String password,
            @FormDataParam("userRank") @DefaultValue("regular") String userRank) {
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
        System.out.println(userName);
        System.out.println(salt);
        System.out.println(hashedPassword);
        System.out.println(userRank);
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
        } catch (MongoWriteException ex) {
            LOG.warn(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
