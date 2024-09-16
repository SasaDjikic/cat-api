package ch.cat_api.catapi;

import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.verticles.CatVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MainVerticle extends AbstractVerticle
{

  public static void main(String[] args)
  {
    Launcher.executeCommand("run", MainVerticle.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise)
  {

    JsonObject mongoConfig = new JsonObject()
      .put("host", "localhost")
      .put("port", 27017)
      .put("db_name", "cat_api");

    MongoClient mongoClient = MongoClient.createShared(vertx, mongoConfig);
    CatRepository catRepository = new CatRepository(mongoClient);

    vertx.deployVerticle(new CatVerticle(catRepository), res2 -> {
      if (res2.succeeded()) {
        System.out.println("CatVerticle deployed successfully.");
        startPromise.complete();
      }
      else {
        System.out.println("Failed to deploy CatVerticle: " + res2.cause().getMessage());
        startPromise.fail(res2.cause());
      }
    });
  }
}
