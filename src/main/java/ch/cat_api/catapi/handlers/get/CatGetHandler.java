package ch.cat_api.catapi.handlers.get;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CatGetHandler implements Handler<RoutingContext>
{

  @Inject
  public CatRepository catRepository;

  public void handle(final RoutingContext routingContext)
  {
    catRepository.load()
      .onSuccess(catResponses -> {
        routingContext.json(catResponses);
        routingContext.response().end();
      })
      .onFailure(routingContext::fail);
  }
}
