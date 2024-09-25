package ch.cat_api.catapi.util;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Singleton;

@Singleton
public class CatMapper
{
  public CatRequest mapJsonObjectToRequest(JsonObject cat) throws BadRequestException
  {
    try {
      return cat.mapTo(CatRequest.class);
    }
    catch (Exception e) {
      throw new BadRequestException(e);
    }
  }

  public JsonObject mapRequestToJsonObject(CatRequest cat) throws BadRequestException
  {
    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      return new JsonObject(objectMapper.writeValueAsString(cat));
    }
    catch (Exception e) {
      throw new BadRequestException(e);
    }
  }
}
