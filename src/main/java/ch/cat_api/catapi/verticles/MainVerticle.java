package ch.cat_api.catapi.verticles;

import ch.cat_api.catapi.handlers.delete.CatDeleteHandler;
import ch.cat_api.catapi.handlers.exceptions.ExceptionHandler;
import ch.cat_api.catapi.handlers.get.CatGetByIdHandler;
import ch.cat_api.catapi.handlers.get.CatGetHandler;
import ch.cat_api.catapi.handlers.post.CatPostHandler;
import ch.cat_api.catapi.handlers.put.CatPutHandler;
import io.micronaut.context.annotation.Prototype;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.openapi.RouterBuilder;
import jakarta.inject.Inject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@Prototype
public class MainVerticle extends AbstractVerticle
{
  private final ExceptionHandler exceptionHandler;
  private final CatGetByIdHandler catGetByIdHandler;
  private final CatGetHandler catGetHandler;
  private final CatPostHandler catPostHandler;
  private final CatPutHandler catPutHandler;
  private final CatDeleteHandler catDeleteHandler;
  private static final Logger logger = Logger.getLogger(MainVerticle.class);

  @Inject
  public MainVerticle(
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

  @Override
  public void start(final Promise<Void> startPromise)
  {
    RouterBuilder.create(vertx, "src/main/resources/openapi3.yaml")
      .onSuccess(routerBuilder -> {
        logger.log(Level.INFO, "Router builder successfully created");
        routerBuilder
          .operation(OperationIds.GET_CATS)
          .handler(catGetHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation(OperationIds.POST_CATS)
          .handler(catPostHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation(OperationIds.GET_CATS_ID)
          .handler(catGetByIdHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation(OperationIds.PUT_CATS_ID)
          .handler(catPutHandler)
          .failureHandler(exceptionHandler);
        routerBuilder
          .operation(OperationIds.DELETE_CATS_ID)
          .handler(catDeleteHandler)
          .failureHandler(exceptionHandler);

        vertx.createHttpServer(new HttpServerOptions().setHost("localhost").setPort(8400))
          .requestHandler(routerBuilder.createRouter())
          .listen()
          .onComplete(res -> {
            if (res.succeeded()) {
              logger.log(Level.INFO, "HTTP server started on port 8400");
              startPromise.complete();
            }
            else {
              logger.log(Level.ERROR, res.cause().getMessage(), res.cause());
              startPromise.fail(res.cause());
            }
          });
      })
      .onFailure(err -> {
        logger.log(Level.ERROR, err.getMessage(), err);
        throw new InternalError("Router builder failed to created");
      });
  }
}
