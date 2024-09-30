package ch.cat_api.catapi.handlers.exceptions;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;

import com.mongodb.MongoTimeoutException;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BodyProcessorException;
import jakarta.inject.Singleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@Singleton
public class ExceptionHandler implements Handler<RoutingContext>
{
  private static final Logger logger = Logger.getLogger(ExceptionHandler.class);

  public void handle(final RoutingContext routingContext)
  {
    final Throwable failure = routingContext.failure();
    routingContext.json(failure.getMessage());

    if (!routingContext.failed()) {
      return;
    }

    if (failure instanceof NotFoundException) {
      routingContext
        .response()
        .setStatusCode(NOT_FOUND.code())
        .setStatusMessage(NOT_FOUND.reasonPhrase())
        .end(failure.getMessage());
      return;
    }

    if (failure instanceof MongoTimeoutException) {
      routingContext
        .response()
        .setStatusCode(SERVICE_UNAVAILABLE.code())
        .setStatusMessage(SERVICE_UNAVAILABLE.reasonPhrase())
        .end(failure.getMessage());
      return;
    }

    if (failure instanceof BadRequestException || failure instanceof BodyProcessorException) {
      routingContext
        .response()
        .setStatusCode(BAD_REQUEST.code())
        .setStatusMessage(BAD_REQUEST.reasonPhrase())
        .end(failure.getMessage());
      return;
    }

    logger.log(Level.ERROR, failure.getCause(), failure);

    routingContext
      .response()
      .setStatusCode(INTERNAL_SERVER_ERROR.code())
      .setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase())
      .end(failure.getMessage());
  }
}
