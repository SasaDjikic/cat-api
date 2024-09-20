package ch.cat_api.catapi;

import ch.cat_api.catapi.factories.MongoDbFactory;
import ch.cat_api.catapi.handlers.exceptions.ExceptionHandler;
import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.verticles.CatVerticle;
import io.micronaut.runtime.Micronaut;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle
{

  public static void main(String[] args)
  {
    Micronaut.run(MainVerticle.class);
    Launcher.executeCommand("run", MainVerticle.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise)
  {
    ExceptionHandler exceptionHandler = new ExceptionHandler();
    MongoDbFactory mongoDbFactory = new MongoDbFactory(Vertx.vertx());
    CatRepository catRepository = new CatRepository(mongoDbFactory);

    vertx.deployVerticle(new CatVerticle(exceptionHandler, catRepository), res -> {
      if (res.succeeded()) {
        System.out.println("CatVerticle deployed successfully.");
        startPromise.complete();
      }
      else {
        System.out.println("Failed to deploy CatVerticle: " + res.cause().getMessage());
        startPromise.fail(res.cause());
      }
    });
  }
}
