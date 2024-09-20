package ch.cat_api.catapi;

import ch.cat_api.catapi.verticles.CatVerticle;
import io.micronaut.context.BeanContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle
{
  public static void main(String[] args)
  {
    final BeanContext beanContextContext = BeanContext.run();
    final CatVerticle bean = beanContextContext.getBean(CatVerticle.class);
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(bean).onSuccess(res -> System.out.println("Verticle successfully deployed"));
  }
}
