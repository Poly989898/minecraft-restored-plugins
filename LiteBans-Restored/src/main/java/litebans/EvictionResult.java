package litebans;

public enum EvictionResult {
   CONTINUE_EVICT,
   DO_NOT_EVICT;

   public static EvictionResult computeEvictionResult(String text) {
      return Enum.valueOf(EvictionResult.class, text);
   }
}
