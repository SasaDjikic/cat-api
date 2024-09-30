package ch.cat_api.catapi.handlers.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatGetByIdHandlerTest
{

  private CatGetByIdHandler catGetByIdHandler;
  private CatRepository mockCatRepository;
  private HttpServerResponse mockHttpServerResponse;
  private HttpServerRequest mockHttpServerRequest;
  private RequestBody mockRequestBody;
  private RoutingContext mockRoutingContext;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockRoutingContext = mock(RoutingContext.class);
    mockHttpServerResponse = mock(HttpServerResponse.class);
    mockHttpServerRequest = mock(HttpServerRequest.class);
    mockRequestBody = mock(RequestBody.class);
    catGetByIdHandler = new CatGetByIdHandler(mockCatRepository);

    when(mockRoutingContext.request()).thenReturn(mockHttpServerRequest);
    when(mockRoutingContext.body()).thenReturn(mockRequestBody);
    when(mockRoutingContext.response()).thenReturn(mockHttpServerResponse);
  }

  @Test
  void testGetByIdReturnsResulOfCatGetByIdHandler()
  {
    final String id = "66f12b0a3655d07a490908a6";
    final JsonObject response = new JsonObject("{}");

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockCatRepository.loadById(id))
      .thenReturn(Future.succeededFuture(response));
    when(mockRoutingContext.response())
      .thenReturn(mockHttpServerResponse);

    catGetByIdHandler.handle(mockRoutingContext);

    verify(mockCatRepository, times(1)).loadById(id);
    verify(mockRoutingContext, times(1)).json(response);
    verify(mockRoutingContext.response(), times(1)).end();
    verify(mockHttpServerResponse, times(1)).end();
  }

  @Test
  void testGetByIdOfCatGetHandlerHasInvalidObjectId()
  {
    final String id = "invalid-id";

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);

    catGetByIdHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(1)).fail(any(BadRequestException.class));
  }
}
