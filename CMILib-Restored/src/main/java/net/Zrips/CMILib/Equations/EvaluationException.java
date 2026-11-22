package net.Zrips.CMILib.Equations;

public class EvaluationException extends RuntimeException {
  private static final long serialVersionUID = -1297776708634902362L;

  public EvaluationException() {}

  public EvaluationException(String message) {
    super(message);
  }

  public EvaluationException(Throwable cause) {
    super(cause);
  }

  public EvaluationException(String message, Throwable cause) {
    super(message, cause);
  }
}
