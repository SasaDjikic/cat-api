package ch.cat_api.catapi.handlers.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.handlers.VertxTestBase;
import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatGetHandlerIntegrationTest extends VertxTestBase
{
  private CatGetHandler catGetHandler;
  private CatRepository mockCatRepository;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    catGetHandler = new CatGetHandler(mockCatRepository);
  }

  @Test
  void testHandlerReturnsListOfCats(final VertxTestContext testContext)
  {
    when(mockCatRepository.load())
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    createRouter(HttpMethod.GET, "/cats")
      .handler(catGetHandler);

    webClient()
      .get("/cats")
      .send()
      .onComplete(testContext.succeeding(response -> testContext.verify(() -> {
        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertEquals(1, response.bodyAsJsonArray().size());
        assertNotNull(response.bodyAsJsonArray().getList().get(0));

        testContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueToNoCatCouldBeFound(final VertxTestContext vertxTestContext)
  {
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    when(mockCatRepository.load())
      .thenReturn(Future.failedFuture(new NotFoundException()));

    createRouter(HttpMethod.GET, "/cats")
      .handler(catGetHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(NotFoundException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(404).end();
      });

    webClient()
      .get("/cats")
      .send()
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(404, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }
}
