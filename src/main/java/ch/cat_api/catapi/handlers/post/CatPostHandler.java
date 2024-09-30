package ch.cat_api.catapi.handlers.post;

import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CatPostHandler implements Handler<RoutingContext>
{

  private final CatRepository catRepository;
  private final CatMapper catMapper;

  @Inject
  public CatPostHandler(final CatRepository catRepository, final CatMapper catMapper)
  {
    this.catRepository = catRepository;
    this.catMapper = catMapper;
  }

  public void handle(final RoutingContext routingContext)
  {
    final JsonObject cat = routingContext.body().asJsonObject();

    try {
      catRepository.save(catMapper.mapJsonObjectToRequest(cat))
        .onSuccess(res -> {
          cat.put("_id", res);
          routingContext.json(cat);
          routingContext.response().end();
        })
        .onFailure(routingContext::fail);
    }
    catch (Exception e) {
      routingContext.fail(e);
    }
  }
}
