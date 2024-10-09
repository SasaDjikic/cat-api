package ch.cat_api.catapi.factories;

import io.micronaut.context.annotation.Factory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import jakarta.inject.Singleton;

@Factory
public class MongoClientFactory
{

  @Singleton
  public MongoClient createClient()
  {
    try {
      final JsonObject mongoConfig = new JsonObject()
        .put("host", "localhost")
        .put("port", 27017)
        .put("db_name", "cat_api");

      return MongoClient.createShared(Vertx.vertx(), mongoConfig);
    }
    catch (Exception e) {
      throw new InternalError(e);
    }
  }
}
