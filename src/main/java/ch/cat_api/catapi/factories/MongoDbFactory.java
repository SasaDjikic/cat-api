package ch.cat_api.catapi.factories;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import jakarta.inject.Singleton;

@Singleton
public class MongoDbFactory
{

  public Future<MongoClient> createClient()
  {
    try {
      JsonObject mongoConfig = new JsonObject()
        .put("host", "localhost")
        .put("port", 27017)
        .put("db_name", "cat_api");

      MongoClient mongoClient = MongoClient.createShared(Vertx.vertx(), mongoConfig);

      return Future.succeededFuture(mongoClient);
    }
    catch (Exception e) {
      return Future.failedFuture("failed to create mongo client");
    }
  }
}
