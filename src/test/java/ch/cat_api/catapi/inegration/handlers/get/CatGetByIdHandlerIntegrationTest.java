package ch.cat_api.catapi.inegration.handlers.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.handlers.get.CatGetByIdHandler;
import ch.cat_api.catapi.inegration.VertxTestBase;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatGetByIdHandlerIntegrationTest extends VertxTestBase
{
  private CatRepository mockCatRepository;
  private CatGetByIdHandler catGetByIdHandler;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    catGetByIdHandler = new CatGetByIdHandler(mockCatRepository);
  }

  @Test
  void testHandlerReturnsCat(final VertxTestContext vertxTestContext)
  {
    final String id = "66fb9023b7fdac4f3d7416a3";

    when(mockCatRepository.loadById(id))
      .thenReturn(Future.succeededFuture(new JsonObject("{}")));

    createRouter(HttpMethod.GET, "/cats/:_id")
      .handler(catGetByIdHandler);

    webClient()
      .get("/cats/" + id)
      .send()
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(200, response.statusCode());
        assertNotNull(response.body());

        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueToBadObjectId(final VertxTestContext vertxTestContext)
  {
    final String id = "invalid-id";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    createRouter(HttpMethod.GET, "/cats/:_id")
      .handler(catGetByIdHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(BadRequestException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .get("/cats/" + id)
      .send()
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(400, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueToNoCatCouldBeFound(final VertxTestContext vertxTestContext)
  {
    final String id = "66fb9023b7fdac4f3d7416a1";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    when(mockCatRepository.loadById(id))
      .thenReturn(Future.failedFuture(new NotFoundException()));

    createRouter(HttpMethod.GET, "/cats/:_id")
      .handler(catGetByIdHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(NotFoundException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(404).end();
      });

    webClient()
      .get("/cats/" + id)
      .send()
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(404, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }
}
