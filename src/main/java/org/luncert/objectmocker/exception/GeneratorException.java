package org.luncert.objectmocker.exception;

public class GeneratorException extends RuntimeException {

  private static final long serialVersionUID = 2598986311953699268L;

  public GeneratorException() {
  }

  public GeneratorException(String message) {
    super(message);
  }

  public GeneratorException(Throwable cause) {
    super(cause);
  }

  public GeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

}
