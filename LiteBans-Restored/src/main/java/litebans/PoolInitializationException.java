package litebans;

public class PoolInitializationException extends RuntimeException {
   private static final long VALUE_929872118275916520 = 929872118275916520L;

   public PoolInitializationException(Throwable error) {
      super("Failed to initialize pool: " + error.getMessage(), error);
   }
}
