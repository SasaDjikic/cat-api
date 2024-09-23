package ch.cat_api.catapi.repositories;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class CatRepository
{
  private final MongoClient mongoClient;
  private final CatMapper catMapper;

  @Inject
  public CatRepository(final MongoClient mongoClient, final CatMapper catMapper)
  {
    this.mongoClient = mongoClient;
    this.catMapper = catMapper;
  }

  public Future<List<JsonObject>> load()
  {
    return mongoClient.find("cats", new JsonObject())
      .compose(res -> {
        if (!res.isEmpty()) {
          return Future.succeededFuture(res);
        }
        return Future.failedFuture(new NotFoundException());
      });
  }

  public Future<JsonObject> loadById(String id)
  {
    JsonObject query = new JsonObject().put("_id", id);

    return mongoClient.findOne("cats", query, null)
      .compose(res -> {
        if (res != null) {
          return Future.succeededFuture(res);
        }
        return Future.failedFuture(new NotFoundException(id));
      });
  }

  public Future<String> save(CatRequest cat)
  {
    try {
      return mongoClient.save("cats", catMapper.mapCatToJsonObject(cat))
        .compose(res -> {
          if (res != null) {
            return Future.succeededFuture(res);
          }
          return Future.failedFuture(new NotFoundException());
        });
    }
    catch (BadRequestException e) {
      return Future.failedFuture(e);
    }
  }

  public Future<Void> update(String id, CatRequest cat)
  {
    JsonObject query = new JsonObject().put("_id", id);

    try {
      JsonObject update = new JsonObject().put("$set", catMapper.mapCatToJsonObject(cat));

      return mongoClient.updateCollection("cats", query, update)
        .compose(res -> {
          if (res.getDocModified() == 1) {
            return Future.succeededFuture();
          }
          return Future.failedFuture(new NotFoundException(id));
        });
    }
    catch (BadRequestException e) {
      return Future.failedFuture(e);
    }
  }

  public Future<Void> delete(String id)
  {
    JsonObject query = new JsonObject().put("_id", id);

    return mongoClient.removeDocument("cats", query)
      .compose(res -> {
        if (res.getRemovedCount() == 1) {
          return Future.succeededFuture();
        }
        return Future.failedFuture(new NotFoundException(id));
      });
  }
}
