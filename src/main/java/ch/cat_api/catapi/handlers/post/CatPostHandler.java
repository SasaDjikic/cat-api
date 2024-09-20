package ch.cat_api.catapi.handlers.post;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
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

  @Inject
  public CatRepository catRepository;

  public void handle(final RoutingContext routingContext)
  {
    JsonObject cat = routingContext.body().asJsonObject();

    try {
      cat.mapTo(CatRequest.class);
    }
    catch (Exception e) {
      routingContext.fail(new BadRequestException());
      return;
    }

    try {
      catRepository.save(CatMapper.mapCatToRequest(cat))
        .onSuccess((res) -> {
          routingContext.json(cat);
          routingContext.response().end();
        })
        .onFailure(routingContext::fail);
    }
    catch (BadRequestException e) {
      routingContext.fail(e);
    }
  }
}
