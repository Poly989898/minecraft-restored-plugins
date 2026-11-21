package litebans;

/**
 * Restored Edition helper: the original code is Kotlin, which has no checked exceptions, so exceptions such as SQLException propagate
 * out of lambdas/overrides that Java would not allow to declare them. This rethrows them unchanged without a `throws` clause.
 */
public final class SneakyThrow {
   private SneakyThrow() {
   }

   @SuppressWarnings("unchecked")
   public static <T extends Throwable> RuntimeException rethrow(Throwable t) throws T {
      throw (T) t;
   }
}
