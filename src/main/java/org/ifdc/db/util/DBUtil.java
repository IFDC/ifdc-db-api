package org.ifdc.db.util;

import com.mongodb.MongoClientURI;

/**
 *
 * @author Meng Zhang
 */
public class DBUtil {
    
    public static MongoClientURI getDBURI() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb://mikecomic:Mike0105@cluster0-shard-00-00-upixo.mongodb.net:27017,cluster0-shard-00-01-upixo.mongodb.net:27017,cluster0-shard-00-02-upixo.mongodb.net:27017/mydb?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
        return uri;
    }
    
    public static void InitializeUserCollection() {
        // TODO managing index definitions
    }
}
