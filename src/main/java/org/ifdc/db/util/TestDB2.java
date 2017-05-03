/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ifdc.db.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import org.bson.Document;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mike
 */
public class TestDB2 {
    
    public void test() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.WARN);
        MongoClientURI uri = new MongoClientURI(
                "mongodb://mikecomic:Mike0105@cluster0-shard-00-00-upixo.mongodb.net:27017,cluster0-shard-00-01-upixo.mongodb.net:27017,cluster0-shard-00-02-upixo.mongodb.net:27017/mydb?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
        try (MongoClient mongoClient = new MongoClient(uri);) {

//        try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoCollection<Document> collection = database.getCollection("test");
            
            // Prepare Data
//            Document doc1 = new Document("name", "MongoDB")
//                    .append("type", "database")
//                    .append("count", 1)
//                    .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
//                    .append("info", new Document("x", 203).append("y", 102));
//            List tags = Arrays.asList(new String[]{"abc", "kkk"});
//            doc1.put("tags", tags);
//            Document doc2 = Document.parse("{"
//                    + "'name':'Riak' "
//                    + "'type':'database' "
//                    + "'versions':['v1.0','v1.1'] "
//                    + "}");
            
            // Insert Data
//            collection.drop();
//            collection.insertOne(doc1);
//            collection.insertOne(doc2);
//            ArrayList<Document> documents = new ArrayList<Document>();
//            for (int i = 0; i < 100; i++) {
//                documents.add(new Document("i", i));
//            }
//            collection.insertMany(documents);
//            System.out.println(tags.getClass().toString());
//            System.out.println(tags instanceof ArrayList);
//            System.out.println(collection.count());
            
            // Search Data
            // method 1
            System.out.println(collection.find(Document.parse("{'name':'MongoDB'}")).projection(fields(include("name", "versions"), excludeId())).first());
            // method 2
//            System.out.println(collection.find(new Document("name", "Riak")).first());
            // method 3
            MongoCursor<Document> cursor = collection.find().filter(new Document("type", "database")).iterator();
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }

            //        MongoCursor<Document> cursor2 = collection.find().iterator();
//        try {
//            int count = 0;
//            while (cursor2.hasNext()) {
//                if (count > 100) {
//                    collection.deleteOne(cursor2.next());
//                }
//                count++;
//            }
//        } finally {
//            cursor2.close();
//            System.out.println();
//        }
//        System.out.println(collection.count());
//            Document myDoc = collection.find().first();
//            System.out.println(myDoc.toJson());
//            try (MongoCursor<Document> cursor = collection.find().iterator()) {
//                while (cursor.hasNext()) {
//                    System.out.println(cursor.next().toJson());
//                }
//            } finally {
//                System.out.println();
//            }
//            Block<Document> printBlock = new Block<Document>() {
//                @Override
//                public void apply(final Document document) {
//                    System.out.println(document.toJson());
//                }
//            };
//            collection.find(gt("i", 50)).forEach(printBlock);
//            System.out.println(gt("i", 50).toString());
        }
    }
}
