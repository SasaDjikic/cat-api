package ch.cat_api.catapi.handlers.get;

import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

public class CatGetByIdHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  public CatGetByIdHandler(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }
  public void handle(final RoutingContext routingContext)
  {
    String id = routingContext.request().getParam("_id");

    if (!ObjectId.isValid(id)) {
      routingContext.fail(new BadRequestException(id));
      return;
    }

    catRepository.loadById(id)
      .onSuccess(catResponse -> {
        routingContext.json(catResponse);
        routingContext.response().end();
      })
      .onFailure(routingContext::fail);
  }
}
