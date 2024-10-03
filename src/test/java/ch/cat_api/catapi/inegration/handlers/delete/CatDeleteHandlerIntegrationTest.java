package ch.cat_api.catapi.inegration.handlers.delete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.handlers.delete.CatDeleteHandler;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.inegration.VertxTestBase;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatDeleteHandlerIntegrationTest extends VertxTestBase
{
  private CatDeleteHandler catDeleteHandler;
  private CatRepository mockCatRepository;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    catDeleteHandler = new CatDeleteHandler(mockCatRepository);
  }

  @Test
  void testHandlerDeletesCat(final VertxTestContext vertxTestContext)
  {
    final String id = "66f12b0a3655d07a490908a6";

    when(mockCatRepository.delete(id))
      .thenReturn(Future.succeededFuture());

    createRouter(HttpMethod.DELETE, "/cats/:_id")
      .handler(catDeleteHandler);

    webClient()
      .delete("/cats/" + id)
      .send()
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(204, response.statusCode());

        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueBadObjectId(final VertxTestContext vertxTestContext)
  {
    final String id = "invalid-id";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    createRouter(HttpMethod.DELETE, "/cats/:_id")
      .handler(catDeleteHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(BadRequestException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .delete("/cats/" + id)
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
    final String id = "66f12b0a3655d07a490908a6";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    when(mockCatRepository.delete(id))
      .thenReturn(Future.failedFuture(new NotFoundException()));

    createRouter(HttpMethod.DELETE, "/cats/:_id")
      .handler(catDeleteHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(NotFoundException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(404).end();
      });

    webClient()
      .delete("/cats/" + id)
      .send()
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(404, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }
}
