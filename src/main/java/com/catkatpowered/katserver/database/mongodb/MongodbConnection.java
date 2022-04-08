package com.catkatpowered.katserver.database.mongodb;

import com.catkatpowered.katserver.database.interfaces.DatabaseConnection;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MongodbConnection(Gson gson, MongoClient client, String database) implements DatabaseConnection {

    @Override
    public void create(String collection, Object data) {
        client.getDatabase(database).getCollection(collection).insertOne(Document.parse(gson.toJson(data)));
    }

    @Override
    public void create(String collection, List<Object> data) {
        data.forEach(d -> client.getDatabase(database)
                .getCollection(collection)
                .insertOne(Document.parse(gson.toJson(d))));
    }

    @Override
    public void update(String collection, Map<String, Object> index, Object data) {
        client.getDatabase(database).getCollection(collection).updateMany(
                Document.parse(gson.toJsonTree(index, Map.class).toString()),
                Document.parse(gson.toJson(data))
        );
    }

    @Override
    public void delete(String collection, Map<String, Object> index) {
        client.getDatabase(database).getCollection(collection)
                .deleteMany(Document.parse(gson.toJsonTree(index, Map.class).toString()));
    }

    @Override
    public <T> List<T> read(String collection, Map<String, Object> index, Class<T> type) {
        List<T> list = new ArrayList<>();
        // 获取数据库连接
        client.getDatabase(database).getCollection(collection)
                .find(Document.parse(gson.toJsonTree(index, Map.class).toString()))
                .iterator()
                .forEachRemaining(document -> {
                    // 将 document 转换回对象
                    list.add(gson.fromJson(document.toJson(), type));
                });
        return list;
    }

    @Override
    public <T> List<T> search(String collection, String data, String top, String bottom, int limit, Class<T> type) {
        List<T> list = new ArrayList<>();
        // 获取数据库连接
        client.getDatabase(database).getCollection(collection)
                .find(new BasicDBObject() {{
                    put(data, new BasicDBObject() {{
                        put("$gte", top);
                        put("$lte", bottom);
                    }});
                }})
                .skip(0)
                .limit(limit)
                .iterator()
                .forEachRemaining(document -> {
                    // 将 document 转换回对象
                    list.add(gson.fromJson(document.toJson(), type));
                });
        return list;
    }
}
