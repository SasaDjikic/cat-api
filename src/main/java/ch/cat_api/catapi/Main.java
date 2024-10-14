package ch.cat_api.catapi;

import ch.cat_api.catapi.verticles.CatVerticle;
import io.micronaut.context.BeanContext;
import io.vertx.core.Vertx;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main
{
  private static final Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args)
  {
    final CatVerticle bean;
    try (BeanContext beanContextContext = BeanContext.run()) {
      bean = beanContextContext.getBean(CatVerticle.class);
    }
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(bean)
      .onSuccess(res -> logger.log(Level.INFO, "CatVerticle successfully deployed"))
      .onFailure(err -> logger.log(Level.ERROR, err.getMessage(), err));
  }
}
