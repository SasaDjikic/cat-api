package ch.cat_api.catapi.dtos.cat.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CatRequest
{
  private String name;
  private int age;
  private String buyer;
}
