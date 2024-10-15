package ch.cat_api.catapi.karate;

import ch.cat_api.catapi.verticles.CatVerticle;
import com.intuit.karate.junit5.Karate;
import io.micronaut.context.BeanContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.vertx.core.Vertx;
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
  @SuppressWarnings("resource")
  protected static final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE)
    .asCompatibleSubstituteFor("mongo"))
    .withExposedPorts(27017)
    .withStartupTimeout(STARTUP_TIMEOUT);

  static MongoClient mongoClient;

  @BeforeAll
  static void setup(final Vertx vertx, final VertxTestContext vertxTestContext)
  {

    mongoContainer.start();
    BeanContext beanContextContext = BeanContext.run();

    final Integer mappedPort = mongoContainer.getMappedPort(27017);

    beanContextContext.registerSingleton(JsonObject.class, new JsonObject()
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
      .put("socketTimeoutMS", 1000), Qualifiers.byName("mongoConfig"));

    final CatVerticle bean = beanContextContext.getBean(CatVerticle.class);

    vertx.deployVerticle(bean)
      .onSuccess(res -> logger.log(Level.INFO, "MainVerticle successfully deployed"))
      .onFailure(err -> logger.log(Level.ERROR, err.getMessage(), err))
      .onComplete(vertxTestContext.succeedingThenComplete());

  }

  // TODO: single tests instead of testAll
  @Karate.Test
  Karate testAll()
  {
    return Karate.run("cats").relativeTo(getClass());
  }

  @AfterAll
  static void tearDown(final Vertx vertx)
  {
    mongoContainer.stop();
    if (mongoClient != null) {
      mongoClient.close();
    }
    vertx.close();
  }
}
