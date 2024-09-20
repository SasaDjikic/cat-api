package ch.cat_api.catapi.repositories;

import ch.cat_api.catapi.dtos.cat.requests.CatRequest;
import ch.cat_api.catapi.factories.MongoDbFactory;
import ch.cat_api.catapi.handlers.exceptions.BadRequestException;
import ch.cat_api.catapi.handlers.exceptions.NotFoundException;
import ch.cat_api.catapi.util.CatMapper;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.List;

public class CatRepository
{
  private final MongoDbFactory mongoDbFactory;

  public CatRepository(final MongoDbFactory mongoDbFactory)
  {
    this.mongoDbFactory = mongoDbFactory;
  }

  public Future<List<JsonObject>> load()
  {
    return mongoDbFactory.createClient()
      .compose(mongoClient -> mongoClient.find("cats", new JsonObject())
        .compose(res -> {
          if (!res.isEmpty()) {
            return Future.succeededFuture(res);
          }
          return Future.failedFuture(new NotFoundException());
        }));
  }

  public Future<JsonObject> loadById(String id)
  {
    JsonObject query = new JsonObject().put("_id", id);

    return mongoDbFactory.createClient()
      .compose(mongoClient -> mongoClient.findOne("cats", query, null)
        .compose(res -> {
          mongoClient.close();
          if (res != null) {
            return Future.succeededFuture(res);
          }
          return Future.failedFuture(new NotFoundException(id));
        }));
  }

  public Future<String> save(CatRequest cat)
  {
    return mongoDbFactory.createClient()
      .compose(mongoClient -> {
        try {
          return mongoClient.save("cats", CatMapper.mapCatToJsonObject(cat))
            .compose(res -> {
              mongoClient.close();
              if (res != null) {
                return Future.succeededFuture(res);
              }
              return Future.failedFuture(new NotFoundException());
            });
        }
        catch (BadRequestException e) {
          return Future.failedFuture(e);
        }
      });
  }

  public Future<Void> update(String id, CatRequest cat)
  {
    JsonObject query = new JsonObject().put("_id", id);

    return mongoDbFactory.createClient().compose(mongoClient -> {
      try {
        JsonObject update = new JsonObject().put("$set", CatMapper.mapCatToJsonObject(cat));

        return mongoClient.updateCollection("cats", query, update)
          .compose(res -> {
            mongoClient.close();
            if (res.getDocModified() == 1) {
              return Future.succeededFuture();
            }
            return Future.failedFuture(new NotFoundException(id));
          });
      }
      catch (BadRequestException e) {
        return Future.failedFuture(e);
      }
    });
  }

  public Future<Void> delete(String id)
  {
    JsonObject query = new JsonObject().put("_id", id);

    return mongoDbFactory.createClient().compose(mongoClient -> mongoClient.removeDocument("cats", query)
      .compose(res -> {
        mongoClient.close();
        if (res.getRemovedCount() == 1) {
          return Future.succeededFuture();
        }
        return Future.failedFuture(new NotFoundException(id));
      }));
  }
}
