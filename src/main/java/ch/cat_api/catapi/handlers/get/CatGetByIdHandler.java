package ch.cat_api.catapi.handlers.get;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CatGetByIdHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  public CatGetByIdHandler(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  @Override
  public void handle(final RoutingContext routingContext)
  {
    String id = routingContext.request().getParam("_id");

    catRepository.loadById(id)
      .onSuccess(catResponse -> {
        routingContext.json(catResponse);
        routingContext.response().end();
      })
      .onFailure(throwable -> {
        //TODO deal with the exception
        // Loggging ??
        routingContext.response().setStatusCode(500).end("Failed to load cats");
        System.out.println("failure " + throwable.getMessage());
      });
  }
}
