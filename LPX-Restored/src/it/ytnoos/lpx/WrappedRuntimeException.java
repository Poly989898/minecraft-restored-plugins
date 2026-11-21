package it.ytnoos.lpx;

public final class WrappedRuntimeException extends RuntimeException {
   public WrappedRuntimeException(RuntimeException runtimeException) {
      super(runtimeException.getMessage(), runtimeException, false, false);
   }

   public synchronized RuntimeException unwrap() {
      return (RuntimeException)super.getCause();
   }
}
