package ch.cat_api.catapi.dtos.owner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwnerTest
{
  private Owner owner;
  private OwnerService mockOwnerService;

  @BeforeEach
  void setup() {
    mockOwnerService = mock(OwnerService.class);
    owner = new Owner(mockOwnerService);
  }

  @Test
  public void testGetAllCatsReturnsResultOfOwnerService()
  {
    final String buyer = "Jeff";
    when(mockOwnerService.getAllCats(buyer))
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    final List<JsonObject> cats = owner.getAllCats(buyer).result();

    assertEquals(1, cats.size());
    assertNotNull(cats);
    assertNotNull(cats.get(0));
    verify(mockOwnerService, times(1)).getAllCats(buyer);
  }
}
