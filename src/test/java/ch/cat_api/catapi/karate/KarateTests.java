package ch.cat_api.catapi.karate;

import ch.cat_api.catapi.verticles.CatVerticle;
import com.intuit.karate.junit5.Karate;
import io.micronaut.context.BeanContext;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(VertxExtension.class)
class KarateTests
{
  private static final String IMAGE = "mongo:latest";

  @SuppressWarnings("resource")
  protected static final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE)
    .asCompatibleSubstituteFor("mongo"))
    .withExposedPorts(27017);

  static MongoClient mongoClient;

  @BeforeAll
  static void setup()
  {
    System.out.println("test1");
    mongoContainer.start();
    System.out.println(mongoContainer.getMappedPort(27017));

    final CatVerticle catVerticleBean;
    try (BeanContext beanContextContext = BeanContext.run()) {
      System.out.println("test3");
      catVerticleBean = beanContextContext.getBean(CatVerticle.class);
    }

    Vertx vertx = Vertx.vertx();

    System.out.println(vertx.deployVerticle(catVerticleBean).result());
    System.out.println("test4");
//      .onSuccess(res -> System.out.println("Deployed verticle: " + res))
//      .onFailure(err -> System.out.println("Failed to deploy verticle: " + err));

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
