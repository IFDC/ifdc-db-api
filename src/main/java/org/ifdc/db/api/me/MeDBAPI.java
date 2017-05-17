package org.ifdc.db.api.me;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.ifdc.db.api.util.DBUtil;

/**
 *
 * @author Meng Zhang
 */
public abstract class MeDBAPI {
    
    private static MongoClient mongoClient;
    private static final String DB_NAME = "ifdc_me";
    protected final static String DEF_SKIP = "0";
    protected final static String DEF_LIMIT = Integer.MAX_VALUE + "";
    
    protected MongoCollection<Document> getConnection() {
        if (mongoClient == null) {
            mongoClient = new MongoClient(DBUtil.getDBURI());
        }
        return mongoClient.getDatabase(DB_NAME).getCollection(getCollectionName());
    }
    
    protected static MongoCollection<Document> getConnection(String collectionName) {
        if (mongoClient == null) {
            mongoClient = new MongoClient(DBUtil.getDBURI());
        }
        return mongoClient.getDatabase(DB_NAME).getCollection(collectionName);
    }
    
    protected abstract String getCollectionName();
}
