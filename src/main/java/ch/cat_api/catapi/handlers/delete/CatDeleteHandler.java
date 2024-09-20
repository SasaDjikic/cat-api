package ch.cat_api.catapi.handlers.delete;

import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

public class CatDeleteHandler implements Handler<RoutingContext>
{
  private final CatRepository catRepository;

  public CatDeleteHandler(final CatRepository catRepository)
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

    catRepository.delete(id)
      .onSuccess((res) -> routingContext.response().setStatusCode(204).end())
      .onFailure(routingContext::fail);
  }
}
