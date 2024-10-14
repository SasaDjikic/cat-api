package ch.cat_api.catapi.handlers.get;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CatGetHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  @Inject
  public CatGetHandler(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  public void handle(final RoutingContext routingContext)
  {
    catRepository.load()
      .onSuccess(routingContext::json)
      .onFailure(routingContext::fail);
  }
}
