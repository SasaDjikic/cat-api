package ch.cat_api.catapi.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.owner.Owner;
import ch.cat_api.catapi.dtos.owner.OwnerService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OwnerUnitTest
{
  private Owner owner;
  private OwnerService mockOwnerService;

  @BeforeEach
  void setup()
  {
    mockOwnerService = Mockito.mock(OwnerService.class);
    owner = new Owner(mockOwnerService);
  }

  @Test
  void testGetAllCatsReturnsResultOfOwnerService()
  {
    when(mockOwnerService.getAllCats(anyString()))
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    final List<JsonObject> cats = owner.getAllCats(anyString()).result();

    assertEquals(1, cats.size());
    assertNotNull(cats);
    assertNotNull(cats.get(0));
    verify(mockOwnerService, times(1)).getAllCats(anyString());
  }
}
