package ch.cat_api.catapi;

import ch.cat_api.catapi.verticles.CatVerticle;
import io.micronaut.context.BeanContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main
{
  private static final Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args)
  {
    final CatVerticle bean;
    BeanContext beanContextContext = BeanContext.run();

    beanContextContext.registerSingleton(JsonObject.class, new JsonObject()
      .put("host", "localhost")
      .put("port", 27017)
      .put("db_name", "cat_api")
      .put("connectTimeoutMS", 1000)
      .put("serverSelectionTimeoutMS", 1000)
      .put("maxIdleTimeMS", 1000)
      .put("maxLifeTimeMS", 1000)
      .put("waitQueueTimeoutMS", 1000)
      .put("maintenanceFrequencyMS", 1000)
      .put("maintenanceInitialDelayMS", 1000)
      .put("socketTimeoutMS", 1000), Qualifiers.byName("mongoConfig"));

    bean = beanContextContext.getBean(CatVerticle.class);
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(bean)
      .onSuccess(res -> logger.log(Level.INFO, "MainVerticle successfully deployed"))
      .onFailure(err -> logger.log(Level.ERROR, err.getMessage(), err));
  }
}
