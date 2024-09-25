package ch.cat_api.catapi.util;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CatMapperTest
{
  private CatMapper catMapper;

  @BeforeEach
  void setup() {
    catMapper = new CatMapper();
  }

  // TODO needs some refactoring
  @Test
  public void testMapJsonObjectToRequestReturnsResultOfCatMapper() throws BadRequestException
  {
    JsonObject cat = new JsonObject();
    cat.put("name", "Peter");
    cat.put("age", 12);
    cat.put("buyer", "Jeff");
    when(catMapper.mapJsonObjectToRequest(cat))
      .thenReturn(new CatRequest());

    final CatRequest request = catMapper.mapJsonObjectToRequest(new JsonObject());

    assertNotNull(request);
    verify(catMapper, times(1)).mapJsonObjectToRequest(new JsonObject());
  }

  @Test
  public void testValidCatRequestReturnsPopulatedJsonObject() throws BadRequestException
  {
    final CatRequest mockCatRequest = mock(CatRequest.class);

    when(mockCatRequest.getName()).thenReturn("Alfred");
    when(mockCatRequest.getAge()).thenReturn(2);
    when(mockCatRequest.getBuyer()).thenReturn("Gustav");

    JsonObject result = catMapper.mapRequestToJsonObject(mockCatRequest);

    assertEquals(result.getString("name"), mockCatRequest.getName());
    assertEquals(result.getInteger("age"), mockCatRequest.getAge());
    assertEquals(result.getString("buyer"), mockCatRequest.getBuyer());
  }

  @Test
  public void testNullCatRequest()
  {
    assertThrows(BadRequestException.class, () -> catMapper.mapRequestToJsonObject(null));
  }

  @Test
  public void testValidJsonObjectReturnsPopulatedCatRequest() throws BadRequestException
  {
    final JsonObject mockJsonObject = mock(JsonObject.class);

    when(mockJsonObject.getString("name")).thenReturn("Alfred");
    when(mockJsonObject.getInteger("age")).thenReturn(2);
    when(mockJsonObject.getString("buyer")).thenReturn("Gustav");

    CatRequest result = catMapper.mapJsonObjectToRequest(mockJsonObject);


    assertEquals(result.getName(), mockJsonObject.getString("name"));
    assertEquals(mockJsonObject.getInteger("age"), result.getAge()) ;
    assertEquals(mockJsonObject.getString("buyer"), result.getBuyer());
  }

  @Test
  public void testNullJsonObject()
  {
    assertThrows(BadRequestException.class, () -> catMapper.mapJsonObjectToRequest(null));
  }
}
