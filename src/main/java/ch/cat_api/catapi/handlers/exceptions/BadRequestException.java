package ch.cat_api.catapi.handlers.exceptions;

public class BadRequestException extends Exception
{
  public BadRequestException()
  {
    super("Bad request");
  }

  public BadRequestException(Exception exception)
  {
    super("Bad request: " + exception.getMessage());
  }

  public BadRequestException(String id)
  {
    super("Bad request with invalid object id: " + id);
  }
}
