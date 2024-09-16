package ch.cat_api.catapi.verticles;

import ch.cat_api.catapi.handlers.delete.CatDeleteHandler;
import ch.cat_api.catapi.handlers.get.CatGetByIdHandler;
import ch.cat_api.catapi.handlers.get.CatGetHandler;
import ch.cat_api.catapi.handlers.post.CatPostHandler;
import ch.cat_api.catapi.handlers.put.CatPutHandler;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.openapi.RouterBuilder;

public class CatVerticle extends AbstractVerticle
{
  private final CatRepository catRepository;

  public CatVerticle(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  @Override
  public void start(Promise<Void> startPromise)
  {
    RouterBuilder.create(vertx, "src/main/resources/openapi3.yaml")
      .onSuccess(routerBuilder -> {
        System.out.println("successfully loaded yaml");
        routerBuilder
          .operation("get-cats")
          .handler(new CatGetHandler(catRepository))
          .failureHandler(routingContext -> {
            // Handle failure

            routingContext.response().setStatusCode(500).end("Internal Server error: " + routingContext.failure().getMessage());
          });
        routerBuilder
          .operation("post-cats")
          .handler(new CatPostHandler(catRepository))
          .failureHandler(routingContext -> {
            // Handle failure

            routingContext.response().setStatusCode(500).end("Internal Server error: " + routingContext.failure().getMessage());
          });
        routerBuilder
          .operation("get-cats-id")
          .handler(new CatGetByIdHandler(catRepository))
          .failureHandler(routingContext -> {
            // Handle failure

            routingContext.response().setStatusCode(500).end("Internal Server error: " + routingContext.failure().getMessage());
          });
        routerBuilder
          .operation("put-cats-id")
          .handler(new CatPutHandler(catRepository))
          .failureHandler(routingContext -> {
            // Handle failure

            routingContext.response().setStatusCode(500).end("Internal Server error: " + routingContext.failure().getMessage());
          });
        routerBuilder
          .operation("delete-cats-id")
          .handler(new CatDeleteHandler(catRepository))
          .failureHandler(routingContext -> {
            // Handle failure

            routingContext.response().setStatusCode(500).end("Internal Server error: " + routingContext.failure().getMessage());
          });

        vertx.createHttpServer().requestHandler(routerBuilder.createRouter()).listen(8400, result -> {
          if (result.succeeded()) {
            startPromise.complete();
          }
          else {
            startPromise.fail(result.cause());
          }
        });

      })
      .onFailure(err -> {
        System.out.println("failure");
        // Something went wrong during router builder initialization
      });
  }
}
