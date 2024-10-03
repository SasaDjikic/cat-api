package ch.cat_api.catapi.unit.handlers.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.handlers.delete.CatDeleteHandler;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatDeleteHandlerUnitTest
{

  private CatDeleteHandler catDeleteHandler;
  private CatRepository mockCatRepository;
  private RoutingContext mockRoutingContext;
  private HttpServerResponse mockHttpServerResponse;
  private HttpServerRequest mockHttpServerRequest;
  private RequestBody mockRequestBody;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockRoutingContext = mock(RoutingContext.class);
    mockHttpServerResponse = mock(HttpServerResponse.class);
    mockHttpServerRequest = mock(HttpServerRequest.class);
    mockRequestBody = mock(RequestBody.class);

    when(mockRoutingContext.request()).thenReturn(mockHttpServerRequest);
    when(mockRoutingContext.body()).thenReturn(mockRequestBody);
    when(mockRoutingContext.response()).thenReturn(mockHttpServerResponse);

    catDeleteHandler = new CatDeleteHandler(mockCatRepository);
  }

  @Test
  void testDeleteReturnsResultOfCatDeleteHandler()
  {
    final String id = "66f12b0a3655d07a490908a6";

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockCatRepository.delete(id))
      .thenReturn(Future.succeededFuture());
    when(mockRoutingContext.response().setStatusCode(204))
      .thenReturn(mockHttpServerResponse);
    when(mockHttpServerResponse.end())
      .thenReturn(Future.succeededFuture());

    catDeleteHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockCatRepository, times(1)).delete(id);
    verify(mockRoutingContext.response(), times(1)).end();
    verify(mockHttpServerResponse, times(1)).end();
  }

  @Test
  void testDeleteOfCatDeleteHandlerHasInvalidObjectId()
  {
    final String id = "invalid-id";

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);

    catDeleteHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockRoutingContext, times(1)).fail(any(BadRequestException.class));
  }

  @Test
  void testDeleteOfCatRepositoryFails()
  {
    final String id = "66f12b0a3655d07a490908a3";

    when(mockRoutingContext.request().getParam("_id"))
      .thenReturn(id);
    when(mockCatRepository.delete(id))
      .thenReturn(Future.failedFuture(new NotFoundException()));

    catDeleteHandler.handle(mockRoutingContext);

    verify(mockRoutingContext, times(2)).request();
    verify(mockCatRepository, times(1)).delete(id);
    verify(mockRoutingContext, times(1)).fail(any(NotFoundException.class));
  }
}
