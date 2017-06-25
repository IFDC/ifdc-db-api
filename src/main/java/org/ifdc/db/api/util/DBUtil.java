package org.ifdc.db.api.util;

import com.mongodb.MongoClientURI;

/**
 *
 * @author Meng Zhang
 */
public class DBUtil {
    
    public static MongoClientURI getDBURI() {
        String dbPath = ""; // Give your DB path here
        MongoClientURI uri = new MongoClientURI(dbPath);
        return uri;
    }
    
    public static void InitializeUserCollection() {
        // TODO managing index definitions
    }
}
