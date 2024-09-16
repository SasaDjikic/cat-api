package ch.cat_api.catapi.handlers.delete;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CatDeleteHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  public CatDeleteHandler(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  @Override
  public void handle(final RoutingContext routingContext)
  {
    String id = routingContext.request().getParam("_id");

    catRepository.delete(id)
      .onSuccess((res) -> routingContext.response().setStatusCode(204).end())
      .onFailure(throwable -> {
        //TODO deal with the exception
        // Loggging ??
        routingContext.response().setStatusCode(500).end("Failed to delete cat: " + throwable.getMessage());
      });
  }
}
