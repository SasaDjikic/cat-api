package ch.cat_api.catapi.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import java.util.List;

public class CatRepository
{
  private final MongoClient mongoClient;

  public CatRepository(MongoClient mongoClient)
  {
    this.mongoClient = mongoClient;
  }

  public Future<List<JsonObject>> load()
  {
    return mongoClient.find("cats", new JsonObject());
  }

  public Future<JsonObject> loadById(String id)
  {
    JsonObject query = new JsonObject().put("_id", id);
    return mongoClient.findOne("cats", query, null);
  }

  public Future<String> save(JsonObject cat)
  {
    return mongoClient.save("cats", cat);
  }

  public Future<Void> update(String id, JsonObject cat)
  {
    JsonObject query = new JsonObject().put("_id", id);
    JsonObject update = new JsonObject().put("$set", cat);

    return mongoClient.updateCollection("cats", query, update).compose(res -> {
      if (res.getDocModified() == 1) {
        return Future.succeededFuture();
      }
      return Future.failedFuture("Update failed");
    });
  }

  public Future<Void> delete(String id)
  {
    JsonObject query = new JsonObject().put("_id", id);

    return mongoClient.removeDocument("cats", query).compose(res -> {
      if (res.getRemovedCount() == 1) {
        return Future.succeededFuture();
      }
      return Future.failedFuture("Delete failed");
    });
  }
}
