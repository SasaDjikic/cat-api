package ch.cat_api.catapi.inegration;

import io.micronaut.core.io.socket.SocketUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class VertxTestBase
{
  protected static final int DEFAULT_HTTP_PORT = SocketUtils.findAvailableTcpPort();
  protected static final String DEFAULT_HTTP_HOST = "localhost";

  private static WebClient webclient;

  private static HttpServer server;
  private static Router router;

  @BeforeEach
  void startServer(Vertx vertx, VertxTestContext vertxTestContext)
  {
    HttpClient client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(DEFAULT_HTTP_PORT).setDefaultHost(DEFAULT_HTTP_HOST));
    webclient = WebClient.wrap(client);
    router = Router.router(vertx);

    if (server != null) {
      server.close();
    }

    server = vertx.createHttpServer(new HttpServerOptions().setPort(DEFAULT_HTTP_PORT).setHost(DEFAULT_HTTP_HOST))
      .requestHandler(router)
      .listen(DEFAULT_HTTP_PORT, vertxTestContext.succeedingThenComplete());
  }

  @AfterAll
  static void cleanUp(VertxTestContext vertxTestContext)
  {
    if (!vertxTestContext.completed()) {
      vertxTestContext.completeNow();
    }
  }

  @AfterEach
  void serverCleanup(VertxTestContext vertxTestContext)
  {
    server.close().onComplete(h -> vertxTestContext.completeNow());
  }

  protected WebClient webClient()
  {
    return webclient;
  }

  protected Route createRouter(final HttpMethod httpMethod, final String endpointUrl)
  {
    return router
      .route(httpMethod, endpointUrl)
      .handler(BodyHandler.create());
  }
}
