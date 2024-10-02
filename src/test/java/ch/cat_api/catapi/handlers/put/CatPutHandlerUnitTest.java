package ch.cat_api.catapi.handlers.put;

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
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatPutHandlerUnitTest
{

  private CatPutHandler catPutHandler;
  private CatRepository mockCatRepository;
  private CatMapper mockCatMapper;
  private RoutingContext mockRoutingContext;
  private HttpServerResponse mockHttpServerResponse;
  private HttpServerRequest mockHttpServerRequest;
  private JsonObject mockJsonObject;
  private CatRequest mockCatRequest;
  private RequestBody mockRequestBody;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockCatMapper = mock(CatMapper.class);
    mockRoutingContext = mock(RoutingContext.class);
    mockJsonObject = mock(JsonObject.class);
    mockCatRequest = mock(CatRequest.class);
    mockHttpServerResponse = mock(HttpServerResponse.class);
    mockHttpServerRequest = mock(HttpServerRequest.class);
    mockRequestBody = mock(RequestBody.class);

    when(mockRoutingContext.request()).thenReturn(mockHttpServerRequest);
    when(mockRoutingContext.body()).thenReturn(mockRequestBody);
    when(mockRoutingContext.response()).thenReturn(mockHttpServerResponse);

    catPutHandler = new CatPutHandler(mockCatRepository, mockCatMapper);
  }

  @Test
  void testPutOfCatPutHandlerSetsResultInRoutingContext() throws BadRequestException
  {
    final String id = "66f12b0a3655d07a490908a6";
    mockCatRequest = new CatRequest("Jeff", 2, "Peter");
    mockJsonObject = new JsonObject()
      .put("_id", id).put("name", "Jeff").put("age", 22);

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(mockJsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(mockCatRequest);
    when(mockCatRepository.update(id, mockCatRequest))
      .thenReturn(Future.succeededFuture());

    catPutHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockCatRepository, times(1)).update(id, mockCatRequest);
    verify(mockRoutingContext.response(), times(1)).end();
    verify(mockHttpServerResponse, times(1)).end();
  }

  @Test
  void testPutOfCatPutHandlerHasInvalidObjectId()
  {
    final String id = "invalid-id";

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);

    catPutHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(1)).fail(any(BadRequestException.class));
  }

  @Test
  void testPutOfCatPutHandlerThrowsNullPointerException() throws BadRequestException
  {
    final String id = "66f12b0a3655d07a490908a6";
    final CatRequest catRequest = new CatRequest("Jeff", 2, "Peter");
    final JsonObject jsonObject = new JsonObject()
      .put("_id", id).put("name", "Jeff").put("age", 22);

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(jsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(catRequest);
    when(mockCatRepository.update(id, catRequest))
      .thenThrow(new NullPointerException());

    catPutHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockCatRepository, times(1)).update(id, catRequest);
    verify(mockRoutingContext, times(1)).fail(any(NullPointerException.class));
  }

  @Test
  void testPutOfCatPutHandlerThrowsBadRequestException() throws BadRequestException
  {
    final String id = "66f12b0a3655d07a490908a6";
    final JsonObject jsonObject = new JsonObject()
      .put("_id", id).put("name", "Jeff").put("age", 22);

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(jsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenThrow(new BadRequestException());

    catPutHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockRoutingContext, times(1)).fail(any(BadRequestException.class));
  }

  @Test
  void testUpdateOfCatRepositoryFails() throws BadRequestException
  {
    final String id = "66f12b0a3655d07a490908a6";
    final CatRequest catRequest = new CatRequest("Jeff", 2, "Peter");
    final JsonObject jsonObject = new JsonObject()
      .put("_id", id).put("name", "Jeff").put("age", 22);

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockRoutingContext.body().asJsonObject())
      .thenReturn(jsonObject);
    when(mockCatMapper.mapJsonObjectToRequest(any(JsonObject.class)))
      .thenReturn(catRequest);
    when(mockCatRepository.update(id, catRequest))
      .thenReturn(Future.failedFuture(new BadRequestException()));

    catPutHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(2)).body();
    verify(mockCatMapper, times(1)).mapJsonObjectToRequest(any(JsonObject.class));
    verify(mockCatRepository, times(1)).update(id, catRequest);
    verify(mockRoutingContext, times(1)).fail(any(BadRequestException.class));
  }
}
