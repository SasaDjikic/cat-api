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
  private CatRequest mockCatRequest;
  private JsonObject mockJsonObject;

  @BeforeEach
  void setup()
  {
    mockCatRequest = mock(CatRequest.class);
    mockJsonObject = mock(JsonObject.class);
    catMapper = new CatMapper();
  }

  @Test
  public void testMapJsonObjectToRequestReturnsResultOfCatMapper() throws BadRequestException
  {
    final Class<CatRequest> catRequestClass = CatRequest.class;

    when(mockJsonObject.mapTo(catRequestClass))
      .thenReturn(new CatRequest("Jeff", 2, "Peter"));

    mockCatRequest = catMapper.mapJsonObjectToRequest(mockJsonObject);

    assertNotNull(mockCatRequest);
    verify(mockJsonObject, times(1)).mapTo(catRequestClass);
  }

  @Test
  public void testValidCatRequestReturnsPopulatedJsonObject() throws BadRequestException
  {
    when(mockCatRequest.getName()).thenReturn("Alfred");
    when(mockCatRequest.getAge()).thenReturn(2);
    when(mockCatRequest.getBuyer()).thenReturn("Gustav");

    mockJsonObject = catMapper.mapRequestToJsonObject(mockCatRequest);

    assertEquals(mockJsonObject.getString("name"), mockCatRequest.getName());
    assertEquals(mockJsonObject.getInteger("age"), mockCatRequest.getAge());
    assertEquals(mockJsonObject.getString("buyer"), mockCatRequest.getBuyer());
  }

  @Test
  public void testNullCatRequest()
  {
    assertThrows(BadRequestException.class, () -> catMapper.mapRequestToJsonObject(null));
  }

  @Test
  public void testValidJsonObjectReturnsPopulatedCatRequest() throws BadRequestException
  {
    mockJsonObject = mock(JsonObject.class);

    when(mockJsonObject.getString("name")).thenReturn("Alfred");
    when(mockJsonObject.getInteger("age")).thenReturn(2);
    when(mockJsonObject.getString("buyer")).thenReturn("Gustav");

    mockCatRequest = catMapper.mapJsonObjectToRequest(mockJsonObject);

    assertEquals(mockCatRequest.getName(), mockJsonObject.getString("name"));
    assertEquals(mockJsonObject.getInteger("age"), mockCatRequest.getAge());
    assertEquals(mockJsonObject.getString("buyer"), mockCatRequest.getBuyer());
  }

  @Test
  public void testNullJsonObject()
  {
    assertThrows(BadRequestException.class, () -> catMapper.mapJsonObjectToRequest(null));
  }
}
