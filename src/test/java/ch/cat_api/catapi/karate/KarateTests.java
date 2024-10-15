package ch.cat_api.catapi.karate;

import ch.cat_api.catapi.verticles.MainVerticle;
import com.intuit.karate.junit5.Karate;
import io.micronaut.context.BeanContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Duration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(VertxExtension.class)
class KarateTests
{
  private static final Logger logger = Logger.getLogger(KarateTests.class);

  private static final String IMAGE = "mongo:latest";
  private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(15);

  private static MongoClient mongoClient;
  @SuppressWarnings("resource")
  protected static final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE)
    .asCompatibleSubstituteFor("mongo"))
    .withExposedPorts(27017)
    .withStartupTimeout(STARTUP_TIMEOUT);

  @BeforeAll
  static void setup(final Vertx vertx, final VertxTestContext vertxTestContext)
  {
    mongoContainer.start();
    final MainVerticle bean;
    try (BeanContext beanContextContext = BeanContext.run()) {
      final Integer mappedPort = mongoContainer.getMappedPort(27017);

      final JsonObject mongoConfig = new JsonObject()
        .put("host", "localhost")
        .put("port", mappedPort)
        .put("db_name", "cat_api")
        .put("connectTimeoutMS", 1000)
        .put("serverSelectionTimeoutMS", 1000)
        .put("maxIdleTimeMS", 1000)
        .put("maxLifeTimeMS", 1000)
        .put("waitQueueTimeoutMS", 1000)
        .put("maintenanceFrequencyMS", 1000)
        .put("maintenanceInitialDelayMS", 1000)
        .put("socketTimeoutMS", 1000);

      beanContextContext.registerSingleton(JsonObject.class, mongoConfig, Qualifiers.byName("mongoConfig"));

      mongoClient = MongoClient.createShared(vertx, mongoConfig);

      final JsonArray cats = new JsonArray("""
        [
         {
          "_id": "670e4a2e8213ed68adccd199",
          "name": "Fluffy",
          "age": 1,
          "buyer": "Jeff"
         },
         {
          "_id": "670e4a5b0937f8de1a9d605e",
          "name": "Whiskers",
          "age": 3,
          "buyer": null
         },
         {
          "_id": "670e4a5f7899964d2f7f3d4c",
          "name": "Cookie",
          "age": 12,
          "buyer": null
         }
        ]
        """);

      for (int i = 0; i < cats.size(); i++) {
        final JsonObject cat = cats.getJsonObject(i);

        mongoClient.insert("cats", cat, res -> {
          if (res.succeeded()) {
            System.out.println("Successfully inserted cat " + cat.getString("name"));
          }
          else {
            res.cause().printStackTrace();
          }
        });
      }

      bean = beanContextContext.getBean(MainVerticle.class);
    }

    vertx.deployVerticle(bean)
      .onSuccess(res -> logger.log(Level.INFO, "MainVerticle successfully deployed"))
      .onFailure(err -> logger.log(Level.ERROR, err.getMessage(), err))
      .onComplete(vertxTestContext.succeedingThenComplete());

  }

  @Karate.Test
  Karate testCatGetEndpoints()
  {
    return Karate.run("cats-get").relativeTo(getClass());
  }

  @Karate.Test
  Karate testCatPostEndpoints()
  {
    return Karate.run("cats-post").relativeTo(getClass());
  }

  @Karate.Test
  Karate testCatPutEndpoints()
  {
    return Karate.run("cats-put").relativeTo(getClass());
  }

  @Karate.Test
  Karate testCatDeleteEndpoints()
  {
    return Karate.run("cats-delete").relativeTo(getClass());
  }

  @AfterAll
  static void tearDown(final Vertx vertx)
  {
    mongoContainer.stop();
    mongoClient.close();
    vertx.close();
  }
}
