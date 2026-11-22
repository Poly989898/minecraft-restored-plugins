package net.Zrips.CMILib.Equations;

public class ParseError extends RuntimeException {
  private static final long serialVersionUID = -3688639666380211029L;
  private int position;

  public ParseError(String str, int position) {
    super(str);
    this.position = position;
  }

  public int getPosition() {
    return this.position;
  }

  public ParseError(String message) {
    super(message);
  }

  public ParseError(Throwable cause) {
    super(cause);
  }

  public ParseError(String message, Throwable cause) {
    super(message, cause);
  }
}
