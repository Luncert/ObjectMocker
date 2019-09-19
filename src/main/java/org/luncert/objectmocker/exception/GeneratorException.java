package org.luncert.objectmocker.exception;

public class GeneratorException extends RuntimeException {

  private static final long serialVersionUID = 2598986311953699268L;

  public GeneratorException() {
  }

  public GeneratorException(Throwable cause) {
    super(cause);
  }

  public GeneratorException(String format, Object ...args) {
    this(null, format, args);
  }

  public GeneratorException(Throwable cause, String format, Object ...args) {
    super(String.format(format, args), cause);
  }

}
