package ch.cat_api.catapi;

import ch.cat_api.catapi.verticles.CatVerticle;
import io.micronaut.context.BeanContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle
{
  public static void main(String[] args)
  {
    final CatVerticle bean;
    try (BeanContext beanContextContext = BeanContext.run()) {
      bean = beanContextContext.getBean(CatVerticle.class);
    }
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(bean)
      .onSuccess(res -> System.out.println("MainVerticle successfully deployed"))
      .onFailure(err -> System.out.println("MainVerticle failed to deploy"));
  }
}
