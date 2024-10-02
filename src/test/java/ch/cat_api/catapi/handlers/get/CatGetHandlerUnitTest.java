package ch.cat_api.catapi.handlers.get;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatGetHandlerUnitTest
{

  private CatGetHandler catGetHandler;
  private CatRepository mockCatRepository;
  private RoutingContext mockRoutingContext;
  private HttpServerResponse mockHttpServerResponse;

  @BeforeEach
  void setup()
  {
    mockCatRepository = mock(CatRepository.class);
    mockRoutingContext = mock(RoutingContext.class);
    mockHttpServerResponse = mock(HttpServerResponse.class);
    catGetHandler = new CatGetHandler(mockCatRepository);
  }

  @Test
  void testLoadReturnsResultOfCatGetHandler()
  {
    List<JsonObject> response = List.of(new JsonObject("{}"));

    when(mockCatRepository.load())
      .thenReturn(Future.succeededFuture(response));
    when(mockRoutingContext.response())
      .thenReturn(mockHttpServerResponse);

    catGetHandler.handle(mockRoutingContext);

    verify(mockCatRepository, times(1)).load();
    verify(mockRoutingContext, times(1)).json(response);
    verify(mockRoutingContext.response(), times(1)).end();
    verify(mockHttpServerResponse, times(1)).end();
  }

  @Test
  void testLoadOfCatGetHandlerFails()
  {
    final NotFoundException notFoundException = new NotFoundException();

    when(mockCatRepository.load())
      .thenReturn(Future.failedFuture(notFoundException));
    when(mockRoutingContext.response())
      .thenReturn(mockHttpServerResponse);

    catGetHandler.handle(mockRoutingContext);

    verify(mockCatRepository, times(1)).load();
    verify(mockRoutingContext, times(1)).fail(notFoundException);
  }

}
