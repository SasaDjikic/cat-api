package ch.cat_api.catapi.factories;

import io.micronaut.context.annotation.Factory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Factory
public class MongoClientFactory
{
  @Singleton
  public MongoClient createClient(@Named("mongoConfig") final JsonObject mongoConfig)
  {
    try {
      return MongoClient.createShared(Vertx.vertx(), mongoConfig);
    }
    catch (Exception e) {
      throw new InternalError(e);
    }
  }
}
