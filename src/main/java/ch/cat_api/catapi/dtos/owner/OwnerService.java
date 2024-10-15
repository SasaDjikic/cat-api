package ch.cat_api.catapi.dtos.owner;

import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class OwnerService
{
  private final CatRepository catRepository;

  @Inject
  public OwnerService(final CatRepository catRepository)
  {
    this.catRepository = catRepository;
  }

  public Future<List<JsonObject>> getAllCats(String buyer)
  {
    return catRepository.loadAllByBuyer(buyer)
      .compose(cats -> {
          if (cats != null) {
            return Future.succeededFuture(cats);
          }
          return Future.failedFuture(new NotFoundException());
        }
      );
  }
}
