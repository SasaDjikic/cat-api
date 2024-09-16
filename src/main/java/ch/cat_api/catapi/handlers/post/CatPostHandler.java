package ch.cat_api.catapi.handlers.post;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class CatPostHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  public CatPostHandler(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  @Override
  public void handle(final RoutingContext routingContext)
  {
    JsonObject cat = routingContext.body().asJsonObject();

    catRepository.save(cat)
      .onSuccess((voidResult) -> {
        routingContext.json(cat);
        routingContext.response().end();
      })
      .onFailure(throwable -> {
        //TODO deal with the exception
        // Loggging ??
        routingContext.response().setStatusCode(500).end("Failed to load cats");
      });
  }
}
