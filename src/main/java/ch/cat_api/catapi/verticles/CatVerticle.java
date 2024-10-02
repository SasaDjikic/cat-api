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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@Singleton
public class CatVerticle extends AbstractVerticle
{
  private final ExceptionHandler exceptionHandler;
  private final CatGetByIdHandler catGetByIdHandler;
  private final CatGetHandler catGetHandler;
  private final CatPostHandler catPostHandler;
  private final CatPutHandler catPutHandler;
  private final CatDeleteHandler catDeleteHandler;
  private static final Logger logger = Logger.getLogger(CatVerticle.class);

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

  @Override
  public void start()
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

        final HttpServer server = vertx.createHttpServer().requestHandler(routerBuilder.createRouter());
        server.listen(8400);
      })
      .onFailure(err -> {
        logger.log(Level.ERROR, err.getMessage(), err);
        throw new InternalError("Router builder failed to created");
      });
  }
}
