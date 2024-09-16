package ch.cat_api.catapi.handlers.put;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class CatPutHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  public CatPutHandler(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  @Override
  public void handle(final RoutingContext routingContext)
  {
    JsonObject cat = routingContext.body().asJsonObject();
    String id = routingContext.request().getParam("_id");

    catRepository.update(id, cat)
      .onSuccess((mongo) -> {
        cat.put("_id", id);
        routingContext.json(cat);
        routingContext.response().end();
      })
      .onFailure(throwable -> {
        //TODO deal with the exception
        // Loggging ??
        routingContext.response().setStatusCode(500).end("Failed to update cat");
      });
  }
}
