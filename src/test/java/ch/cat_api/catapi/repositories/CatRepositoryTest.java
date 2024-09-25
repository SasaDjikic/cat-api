package ch.cat_api.catapi.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatRepositoryTest
{
  private MongoClient mockMongoClient;
  private CatMapper mockCatMapper;
  private CatRepository catRepository;

  @BeforeEach
  void setup()
  {
    mockMongoClient = mock(MongoClient.class);
    mockCatMapper = mock(CatMapper.class);
    catRepository = new CatRepository(mockMongoClient, mockCatMapper);
  }

  @Test
  public void testLoadReturnsResultOfCatRepository()
  {
    final String collection = "cats";

    when(mockMongoClient.find(collection, new JsonObject()))
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    final List<JsonObject> cats = catRepository.load().result();

    assertEquals(1, cats.size());
    assertNotNull(cats.get(0));
    verify(mockMongoClient, times(1)).find(collection, new JsonObject());
  }

  @Test
  public void testLoadAllByBuyerReturnsResultOfCatRepository()
  {
    final String buyer = "Jeff";
    final String collection = "cats";
    final JsonObject query = new JsonObject().put("buyer", buyer);

    when(mockMongoClient.find(collection, query))
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    final List<JsonObject> cats = catRepository.loadAllByBuyer(buyer).result();

    assertEquals(1, cats.size());
    assertNotNull(cats.get(0));
    verify(mockMongoClient, times(1)).find(collection, query);
  }

  @Test
  public void testLoadByIdReturnsResultOfCatRepository()
  {
    final String id = "66f12b0a3655d07a490908a6";
    final String collection = "cats";
    final JsonObject query = new JsonObject().put("_id", id);

    when(mockMongoClient.findOne(collection, query, null))
      .thenReturn(Future.succeededFuture(new JsonObject("{}")));

    final JsonObject cat = catRepository.loadById(id).result();

    assertNotNull(cat);
    verify(mockMongoClient, times(1)).findOne(collection, query, null);
  }

  @Test
  public void testSaveReturnsResultOfCatRepository() throws BadRequestException
  {
    final String collection = "cats";
    final CatRequest cat = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapRequestToJsonObject(cat))
      .thenReturn(new JsonObject("{}"));
    when(mockMongoClient.save(collection, new JsonObject("{}")))
      .thenReturn(Future.succeededFuture("66f12b0a3655d07a490908a6"));

    final String catId = catRepository.save(cat).result();

    assertNotNull(catId);
    verify(mockCatMapper, times(1)).mapRequestToJsonObject(cat);
    verify(mockMongoClient, times(1)).save(collection, new JsonObject("{}"));
  }

  @Test
  public void testUpdateReturnsResultOfCatRepository() throws BadRequestException
  {
    final String collection = "cats";
    final CatRequest cat = new CatRequest("Jeff", 2, "Peter");
    final String id = "66f12b0a3655d07a490908a6";
    final JsonObject query = new JsonObject().put("_id", id);
    final JsonObject update = new JsonObject().put("$set", cat);

    when(mockCatMapper.mapRequestToJsonObject(cat))
      .thenReturn(new JsonObject("{}"));
    when(mockMongoClient.updateCollection(collection, query, update))
      .thenReturn(Future.succeededFuture());

    catRepository.update(id, cat).result();

    verify(mockMongoClient, times(1)).updateCollection(collection, query, update);
    verify(mockCatMapper, times(1)).mapRequestToJsonObject(cat);
  }

  @Test
  public void testDeleteReturnsResultOfCatRepository()
  {
    final String collection = "cats";
    final String id = "66f12b0a3655d07a490908a6";
    final JsonObject query = new JsonObject().put("_id", id);

    when(mockMongoClient.removeDocument(collection, query)).thenReturn(Future.succeededFuture());

    catRepository.delete(id).result();

    verify(mockMongoClient, times(1)).removeDocument(collection, query);
  }
}
