package ch.cat_api.catapi.handlers.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.repositories.CatRepository;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatPostHandlerTest
{

  private CatPostHandler catPostHandler;
  private CatRepository mockCatRepository;
  private CatMapper mockCatMapper;
  private RoutingContext mockRoutingContext;
  private HttpServerResponse mockHttpServerResponse;
  private RequestBody mockRequestBody;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockCatMapper = mock(CatMapper.class);
    mockRoutingContext = mock(RoutingContext.class);
    mockHttpServerResponse = mock(HttpServerResponse.class);
    mockRequestBody = mock(RequestBody.class);

    when(mockRoutingContext.body()).thenReturn(mockRequestBody);
    when(mockRoutingContext.response()).thenReturn(mockHttpServerResponse);

    catPostHandler = new CatPostHandler(mockCatRepository, mockCatMapper);
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
    verify(mockRoutingContext.response(), times(1)).end();
    verify(mockHttpServerResponse, times(1)).end();
  }

  @Test
  void testPostOfCatPostHandlerThrowsException() throws BadRequestException
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
}
