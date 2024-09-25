package ch.cat_api.catapi.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatRepositoryTest
{
  private MongoClient mockMongoClient;
  private CatMapper mockCatMapper;
  private CatRepository catRepository;
  private MongoClientDeleteResult mockMongoClientDeleteResult;
  private MongoClientUpdateResult mockMongoClientUpdateResult;

  @BeforeEach
  void setup()
  {
    mockMongoClient = mock(MongoClient.class);
    mockCatMapper = mock(CatMapper.class);
    mockMongoClientDeleteResult = mock(MongoClientDeleteResult.class);
    mockMongoClientUpdateResult = mock(MongoClientUpdateResult.class);
    catRepository = new CatRepository(mockMongoClient, mockCatMapper);
  }

  @Test
  public void testLoadReturnsResultOfCatRepository()
  {
    final String collection = "cats";

    when(mockMongoClient.find(collection, new JsonObject()))
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    final List<JsonObject> cats = catRepository
      .load()
      .result();

    assertEquals(1, cats.size());
    assertNotNull(cats.get(0));
    verify(mockMongoClient, times(1))
      .find(collection, new JsonObject());
  }

  @Test
  public void testLoadAllByBuyerReturnsResultOfCatRepository()
  {
    final String buyer = "Jeff";
    final String collection = "cats";
    final JsonObject query = new JsonObject()
      .put("buyer", buyer);

    when(mockMongoClient.find(collection, query))
      .thenReturn(Future.succeededFuture(List.of(new JsonObject("{}"))));

    final List<JsonObject> cats = catRepository
      .loadAllByBuyer(buyer)
      .result();

    assertEquals(1, cats.size());
    assertNotNull(cats.get(0));
    verify(mockMongoClient, times(1))
      .find(collection, query);
  }

  @Test
  public void testLoadByIdReturnsResultOfCatRepository()
  {
    final String id = "66f12b0a3655d07a490908a6";
    final String collection = "cats";
    final JsonObject query = new JsonObject()
      .put("_id", id);

    when(mockMongoClient.findOne(collection, query, null))
      .thenReturn(Future.succeededFuture(new JsonObject("{}")));

    final JsonObject cat = catRepository
      .loadById(id)
      .result();

    assertNotNull(cat);
    verify(mockMongoClient, times(1)).findOne(collection, query, null);
  }

  @Test
  public void testSaveReturnsResultOfCatRepository() throws BadRequestException
  {
    final String collection = "cats";
    final CatRequest cat = new CatRequest("Jeff", 2, "Peter");

    when(mockCatMapper.mapRequestToJsonObject(any(CatRequest.class)))
      .thenReturn(new JsonObject("{}"));

    when(mockMongoClient.save(eq(collection), any(JsonObject.class)))
      .thenReturn(Future.succeededFuture("66f12b0a3655d07a490908a6"));

    final String catId = catRepository
      .save(cat)
      .result();

    assertNotNull(catId);
    verify(mockCatMapper, times(1))
      .mapRequestToJsonObject(any(CatRequest.class));
    verify(mockMongoClient, times(1))
      .save(eq(collection), any(JsonObject.class));
  }

  @Test
  public void testUpdateReturnsResultOfCatRepository() throws BadRequestException
  {
    final CatRequest catRequest = new CatRequest("Jeff", 2, "Peter");
    final String id = "66f12b0a3655d07a490908a6";
    final String collection = "cats";
    final JsonObject update = new JsonObject().put("$set", new JsonObject());
    final JsonObject query = new JsonObject().put("_id", id);

    when(mockCatMapper.mapRequestToJsonObject(any(CatRequest.class)))
      .thenReturn(new JsonObject("{}"));

    when(mockMongoClientUpdateResult.getDocModified())
      .thenReturn(1L);

    when(mockMongoClient.updateCollection(collection, query, update))
      .thenReturn(Future.succeededFuture(mockMongoClientUpdateResult));

    final boolean succeeded = catRepository
      .update(id, catRequest)
      .succeeded();

    assertTrue(succeeded);
    verify(mockMongoClient, times(1))
      .updateCollection(collection, query, update);
    verify(mockCatMapper, times(1))
      .mapRequestToJsonObject(catRequest);
    verify(mockMongoClientUpdateResult, times(1))
      .getDocModified();
  }

  @Test
  public void testDeleteReturnsResultOfCatRepository()
  {
    final String id = "66f12b0a3655d07a490908a6";
    final String collection = "cats";
    final JsonObject query = new JsonObject().put("_id", id);

    when(mockMongoClient.removeDocument(collection, query))
      .thenReturn(Future.succeededFuture(mockMongoClientDeleteResult));
    when(mockMongoClientDeleteResult.getRemovedCount())
      .thenReturn(1L);

    final boolean succeeded = catRepository
      .delete(id)
      .succeeded();

    assertTrue(succeeded);
    verify(mockMongoClient, times(1))
      .removeDocument(collection, query);
    verify(mockMongoClientDeleteResult, times(1))
      .getRemovedCount();
  }
}
