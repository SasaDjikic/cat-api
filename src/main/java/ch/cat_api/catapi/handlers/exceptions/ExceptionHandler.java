package ch.cat_api.catapi.handlers.exceptions;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BodyProcessorException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class ExceptionHandler implements Handler<RoutingContext>
{
  private static final Logger logger = Logger.getLogger(ExceptionHandler.class);

  public void handle(final RoutingContext routingContext)
  {
    final Throwable failure = routingContext.failure();

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

    if (failure instanceof BadRequestException || failure instanceof BodyProcessorException) {
      routingContext
        .response()
        .setStatusCode(BAD_REQUEST.code())
        .setStatusMessage(BAD_REQUEST.reasonPhrase())
        .end(failure.getMessage());
      return;
    }

    logger.log(Priority.ERROR, failure.getCause(), failure);

    routingContext
      .response()
      .setStatusCode(INTERNAL_SERVER_ERROR.code())
      .setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase())
      .end(failure.getMessage());
  }
}
