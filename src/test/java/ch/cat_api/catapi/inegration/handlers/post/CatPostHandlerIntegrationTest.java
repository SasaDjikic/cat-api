package ch.cat_api.catapi.inegration.handlers.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.post.CatPostHandler;
import ch.cat_api.catapi.inegration.VertxTestBase;
import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatPostHandlerIntegrationTest extends VertxTestBase
{
  private CatPostHandler catPostHandler;
  private CatRepository mockCatRepository;
  private CatRequest mockCatRequest;
  private CatMapper mockCatMapper;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockCatMapper = mock(CatMapper.class);
    mockCatRequest = mock(CatRequest.class);
    catPostHandler = new CatPostHandler(mockCatRepository, mockCatMapper);
  }

  @Test
  void testHandleCreatesNewCat(final VertxTestContext vertxTestContext) throws BadRequestException
  {
    mockCatRequest = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(mockCatRequest);
    when(mockCatRepository.save(any(CatRequest.class)))
      .thenReturn(Future.succeededFuture("object-id"));

    createRouter(HttpMethod.POST, "/cats")
      .handler(catPostHandler);

    webClient()
      .post("/cats")
      .sendJsonObject(new JsonObject("{}"))
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(200, response.statusCode());
        assertNotNull(response.body());

        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueToBadRequest(final VertxTestContext vertxTestContext) throws BadRequestException
  {
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);
    mockCatRequest = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenThrow(new BadRequestException());

    createRouter(HttpMethod.POST, "/cats")
      .handler(catPostHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(BadRequestException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .post("/cats")
      .sendJsonObject(new JsonObject("{}"))
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(400, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueToNullPointer(final VertxTestContext vertxTestContext) throws BadRequestException
  {
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);
    mockCatRequest = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(mockCatRequest);
    when(mockCatRepository.save(any(CatRequest.class)))
      .thenReturn(Future.failedFuture(new BadRequestException()));

    createRouter(HttpMethod.POST, "/cats")
      .handler(catPostHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(BadRequestException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .post("/cats")
      .sendJsonObject(new JsonObject("{}"))
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(400, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }
}
