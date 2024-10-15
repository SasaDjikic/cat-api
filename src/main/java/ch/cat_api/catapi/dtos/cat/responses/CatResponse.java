package ch.cat_api.catapi.dtos.cat.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatResponse
{
  private String _id;
  private String name;
  private int age;
  private String buyer;
}
