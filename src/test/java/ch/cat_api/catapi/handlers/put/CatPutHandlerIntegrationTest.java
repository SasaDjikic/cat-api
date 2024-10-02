package ch.cat_api.catapi.handlers.put;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.VertxTestBase;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatPutHandlerIntegrationTest extends VertxTestBase
{
  private CatPutHandler catPutHandler;
  private CatMapper mockCatMapper;
  private CatRequest mockCatRequest;
  private CatRepository mockCatRepository;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockCatMapper = mock(CatMapper.class);
    mockCatRequest = mock(CatRequest.class);
    catPutHandler = new CatPutHandler(mockCatRepository, mockCatMapper);
  }

  @Test
  void testHandlerUpdatesCat(final VertxTestContext vertxTestContext) throws BadRequestException
  {
    final String id = "66fb9023b7fdac4f3d7416a3";
    mockCatRequest = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(mockCatRequest);
    when(mockCatRepository.update(id, mockCatRequest))
      .thenReturn(Future.succeededFuture());

    createRouter(HttpMethod.PUT, "/cats/:_id")
      .handler(catPutHandler);

    webClient()
      .put("/cats/" + id)
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
    final String id = "66fb9023b7fdac4f3d7416a3";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenThrow(new BadRequestException());

    createRouter(HttpMethod.PUT, "/cats/:_id")
      .handler(catPutHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(BadRequestException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .put("/cats/" + id)
      .sendJsonObject(new JsonObject("{}"))
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(400, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }

  @Test
  void testHandlerFailsDueToBadObjectId(final VertxTestContext vertxTestContext)
  {
    final String id = "invalid-id";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);

    createRouter(HttpMethod.PUT, "/cats/:_id")
      .handler(catPutHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(BadRequestException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .put("/cats/" + id)
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
    final String id = "66fb9023b7fdac4f3d7416a3";
    final AtomicBoolean failureHandlerCalled = new AtomicBoolean(false);
    mockCatRequest = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(mockCatRequest);
    when(mockCatRepository.update(id, mockCatRequest))
      .thenReturn(Future.failedFuture(new NullPointerException()));

    createRouter(HttpMethod.PUT, "/cats/:_id")
      .handler(catPutHandler)
      .failureHandler(routingContext -> {
        vertxTestContext.verify(() -> {
          assertTrue(routingContext.failed());
          assertInstanceOf(NullPointerException.class, routingContext.failure());
        });

        failureHandlerCalled.set(true);
        routingContext.response().setStatusCode(400).end();
      });

    webClient()
      .put("/cats/" + id)
      .sendJsonObject(new JsonObject("{}"))
      .onComplete(vertxTestContext.succeeding(response -> vertxTestContext.verify(() -> {
        assertEquals(400, response.statusCode());
        assertNull(response.body());
        assertTrue(failureHandlerCalled.get());

        vertxTestContext.completeNow();
      })));
  }
}
