package ch.cat_api.catapi.karate;

import ch.cat_api.catapi.MainVerticle;
import com.intuit.karate.junit5.Karate;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(VertxExtension.class)
class KarateTests {

  private static final String IMAGE = "dockerregistry.shop.corp.competec.ch/tools/mongo-connection-mongodb:stable";
  private static final LogMessageWaitStrategy WAIT_STRATEGY = Wait.forLogMessage(".*waiting for connections on port 27017.*", 2);
  private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(15);

  @SuppressWarnings("resource")
  protected static final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE)
    .asCompatibleSubstituteFor("mongo"))
    .withExposedPorts(27017)
    .waitingFor(WAIT_STRATEGY)
    .withStartupTimeout(STARTUP_TIMEOUT);

  static MongoClient mongoClient;

  @BeforeAll
  static void setup(final Vertx vertx) throws Exception
  {

    mongoContainer.start();

    final MainVerticle mainVerticle = new MainVerticle();
    mainVerticle.start();

  }

  // TODO: single tests instead of testAll
  @Karate.Test
  Karate testAll() {
    return Karate.run("cats").relativeTo(getClass());
  }

  @AfterAll
  static void tearDown(final Vertx vertx) {
    mongoContainer.stop();
    if (mongoClient != null) {
      mongoClient.close();
    }
    vertx.close();
  }
}
