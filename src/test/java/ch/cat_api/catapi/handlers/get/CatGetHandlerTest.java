package ch.cat_api.catapi.handlers.get;

import static org.junit.jupiter.api.Assertions.*;

import ch.cat_api.catapi.repositories.CatRepository;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatGetHandlerTest
{

  @InjectMocks
  private CatGetHandler mockCatGetHandler;

  @Mock
  private CatRepository mockCatRepository;

  @Test
  public void test(final VertxTestContext testContext)
  {

  }

}
