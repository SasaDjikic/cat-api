package ch.cat_api.catapi.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class ObjectMapperFactory
{

  @Singleton
  ObjectMapper objectMapper()
  {
    return new ObjectMapper();
  }
}
