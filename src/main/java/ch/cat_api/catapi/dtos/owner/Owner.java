package ch.cat_api.catapi.dtos.owner;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.Getter;

@Getter
@Singleton
public class Owner
{
  @Inject
  private final OwnerService ownerService;

  public Owner(final OwnerService ownerService)
  {
    this.ownerService = ownerService;
  }

  public Future<List<JsonObject>> getAllCats(String buyer) {
    return ownerService.getAllCats(buyer);
  }
}
