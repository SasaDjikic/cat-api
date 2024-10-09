package ch.cat_api.catapi.unit.util;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.util.CatMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatMapperUnitTest
{
  private CatMapper catMapper;
  private CatRequest mockCatRequest;
  private JsonObject mockJsonObject;

  @BeforeEach
  void setup()
  {
    mockCatRequest = mock(CatRequest.class);
    mockJsonObject = mock(JsonObject.class);

    ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
    catMapper = new CatMapper(mockObjectMapper);
  }

  @Test
  void testMapJsonObjectToRequestReturnsResultOfCatMapper() throws BadRequestException
  {
    final Class<CatRequest> catRequestClass = CatRequest.class;

    when(mockJsonObject.mapTo(catRequestClass))
      .thenReturn(new CatRequest("Jeff", 2, "Peter"));

    mockCatRequest = catMapper.mapJsonObjectToRequest(mockJsonObject);

    assertNotNull(mockCatRequest);
    verify(mockJsonObject, times(1)).mapTo(catRequestClass);
  }

  @Test
  void testNullCatRequest()
  {
    assertThrows(BadRequestException.class, () -> catMapper.mapRequestToJsonObject(null));
  }

  @Test
  void testNullJsonObject()
  {
    assertThrows(BadRequestException.class, () -> catMapper.mapJsonObjectToRequest(null));
  }
}
