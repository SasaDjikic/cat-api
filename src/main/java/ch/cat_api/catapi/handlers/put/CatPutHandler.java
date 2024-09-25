package ch.cat_api.catapi.handlers.put;

import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class CatPutHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;
  private final CatMapper catMapper;

  @Inject
  public CatPutHandler(final CatRepository catRepository, final CatMapper catMapper)
  {
    this.catRepository = catRepository;
    this.catMapper = catMapper;
  }

  public void handle(final RoutingContext routingContext)
  {
    String id = routingContext.request().getParam("_id");

    if (!ObjectId.isValid(id)) {
      routingContext.fail(new BadRequestException(id));
      return;
    }
    JsonObject cat = routingContext.body().asJsonObject();

    try {
      catRepository.update(id, catMapper.mapJsonObjectToRequest(cat))
        .onSuccess((mongo) -> {
          cat.put("_id", id);
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
