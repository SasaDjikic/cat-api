package ch.cat_api.catapi.verticles;

import ch.cat_api.catapi.handlers.delete.CatDeleteHandler;
import ch.cat_api.catapi.handlers.exceptions.ExceptionHandler;
import ch.cat_api.catapi.handlers.get.CatGetByIdHandler;
import ch.cat_api.catapi.handlers.get.CatGetHandler;
import ch.cat_api.catapi.handlers.post.CatPostHandler;
import ch.cat_api.catapi.handlers.put.CatPutHandler;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.openapi.RouterBuilder;

public class CatVerticle extends AbstractVerticle
{
  private final ExceptionHandler exceptionHandler;
  private final CatRepository catRepository;

  public CatVerticle(final ExceptionHandler exceptionHandler, final CatRepository catRepository) {
    this.exceptionHandler = exceptionHandler;
    this.catRepository = catRepository;
  }

  @Override
  public void start(Promise<Void> startPromise)
  {
    RouterBuilder.create(vertx, "src/main/resources/openapi3.yaml")
      .onSuccess(routerBuilder -> {
        System.out.println("Successfully created router builder with yaml");
        routerBuilder
          .operation("get-cats")
          .handler(new CatGetHandler(catRepository))
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("post-cats")
          .handler(new CatPostHandler(catRepository))
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("get-cats-id")
          .handler(new CatGetByIdHandler(catRepository))
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("put-cats-id")
          .handler(new CatPutHandler(catRepository))
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("delete-cats-id")
          .handler(new CatDeleteHandler(catRepository))
          .failureHandler(exceptionHandler);

        HttpServer server = vertx.createHttpServer().requestHandler(routerBuilder.createRouter());

        server.listen(8400, res -> {
          if (res.succeeded()) {
            startPromise.complete();
          }
          else {
            startPromise.fail(res.cause());
          }
        });
      })
      .onFailure(err -> {
        throw new RuntimeException("Could not create router builder with yaml");
      });
  }
}
