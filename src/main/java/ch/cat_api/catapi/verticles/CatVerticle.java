package ch.cat_api.catapi.verticles;

import ch.cat_api.catapi.handlers.delete.CatDeleteHandler;
import ch.cat_api.catapi.handlers.exceptions.ExceptionHandler;
import ch.cat_api.catapi.handlers.get.CatGetByIdHandler;
import ch.cat_api.catapi.handlers.get.CatGetHandler;
import ch.cat_api.catapi.handlers.post.CatPostHandler;
import ch.cat_api.catapi.handlers.put.CatPutHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.openapi.RouterBuilder;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CatVerticle extends AbstractVerticle
{


  public final ExceptionHandler exceptionHandler;
  public final CatGetByIdHandler catGetByIdHandler;
  public final CatGetHandler catGetHandler;
  public final CatPostHandler catPostHandler;
  public final CatPutHandler catPutHandler;
  public final CatDeleteHandler catDeleteHandler;

  @Inject
  public CatVerticle(
    final ExceptionHandler exceptionHandler,
    final CatGetByIdHandler catGetByIdHandler,
    final CatGetHandler catGetHandler,
    final CatPostHandler catPostHandler,
    final CatPutHandler catPutHandler,
    final CatDeleteHandler catDeleteHandler
  )
  {
    this.exceptionHandler = exceptionHandler;
    this.catGetByIdHandler = catGetByIdHandler;
    this.catGetHandler = catGetHandler;
    this.catPostHandler = catPostHandler;
    this.catPutHandler = catPutHandler;
    this.catDeleteHandler = catDeleteHandler;
  }

  public void start()
  {
    RouterBuilder.create(vertx, "src/main/resources/openapi3.yaml")
      .onSuccess(routerBuilder -> {
        System.out.println("Successfully created router builder with yaml");
        routerBuilder
          .operation("get-cats")
          .handler(catGetHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("post-cats")
          .handler(catPostHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("get-cats-id")
          .handler(catGetByIdHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("put-cats-id")
          .handler(catPutHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation("delete-cats-id")
          .handler(catDeleteHandler)
          .failureHandler(exceptionHandler);

        HttpServer server = vertx.createHttpServer().requestHandler(routerBuilder.createRouter());

        server.listen(8400);
      })
      .onFailure(err -> {
        throw new RuntimeException("Could not create router builder with yaml");
      });
  }
}
