package ch.cat_api.catapi.handlers.exceptions;

public class NotFoundException extends Exception
{
  public NotFoundException(String id)
  {
    super("Entity with id: " + id + " not found");
  }

  public NotFoundException()
  {
    super("Entity not found");
  }
}
