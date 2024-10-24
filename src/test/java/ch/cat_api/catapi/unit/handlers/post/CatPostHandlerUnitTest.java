package ch.cat_api.catapi.unit.handlers.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.post.CatPostHandler;
import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatPostHandlerUnitTest
{

  private CatPostHandler catPostHandler;
  private CatRepository mockCatRepository;
  private CatMapper mockCatMapper;
  private RoutingContext mockRoutingContext;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockCatMapper = mock(CatMapper.class);
    mockRoutingContext = mock(RoutingContext.class);

    HttpServerResponse mockHttpServerResponse = mock(HttpServerResponse.class);
    RequestBody mockRequestBody = mock(RequestBody.class);
    catPostHandler = new CatPostHandler(mockCatRepository, mockCatMapper);

    when(mockRoutingContext.body()).thenReturn(mockRequestBody);
    when(mockRoutingContext.response()).thenReturn(mockHttpServerResponse);
  }

  @Test
  void testPostReturnsResultOfCatPostHandler() throws BadRequestException
  {
    final CatRequest catRequest = new CatRequest("Jeff", 2, "Peter");
    final JsonObject jsonObject = new JsonObject()
      .put("name", "Jeff").put("age", 22);

    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(jsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(catRequest);
    when(mockCatRepository.save(catRequest))
      .thenReturn(Future.succeededFuture());

    catPostHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockCatRepository, times(1)).save(catRequest);
  }

  @Test
  void testPostOfCatPostHandlerThrowsNullPointerException() throws BadRequestException
  {
    final CatRequest catRequest = new CatRequest("Jeff", 2, "Peter");
    final JsonObject jsonObject = new JsonObject().put("name", "Jeff")
      .put("age", 22).put("buyer", "Peter");

    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(jsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(catRequest);
    when(mockCatRepository.save(catRequest))
      .thenThrow(new NullPointerException());

    catPostHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockRoutingContext, times(1)).fail(any(NullPointerException.class));
  }

  @Test
  void testPostOfCatPostHandlerThrowsBadRequestException() throws BadRequestException
  {
    final JsonObject jsonObject = new JsonObject().put("name", "Jeff")
      .put("age", 22).put("buyer", "Peter");

    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(jsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenThrow(new BadRequestException());

    catPostHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockRoutingContext, times(1)).fail(any(BadRequestException.class));
  }
}
