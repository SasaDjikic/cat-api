package ch.cat_api.catapi.factories;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoDbFactory
{
  private final Vertx vertx;

  public MongoDbFactory(final Vertx vertx)
  {
    this.vertx = vertx;
  }

  public Future<MongoClient> createClient()
  {
    try {
      JsonObject mongoConfig = new JsonObject()
        .put("host", "localhost")
        .put("port", 27017)
        .put("db_name", "cat_api");

      MongoClient mongoClient = MongoClient.createShared(vertx, mongoConfig);

      return Future.succeededFuture(mongoClient);
    }
    catch (Exception e) {
      return Future.failedFuture("failed to create mongo client");
    }
  }
}
