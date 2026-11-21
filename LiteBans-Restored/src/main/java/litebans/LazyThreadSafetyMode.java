package litebans;

public enum LazyThreadSafetyMode {
   SYNCHRONIZED,
   PUBLICATION,
   NONE;

   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   public static LazyThreadSafetyMode computeLazyThreadSafetyMode(String text) {
      return Enum.valueOf(LazyThreadSafetyMode.class, text);
   }

   private static final LazyThreadSafetyMode[] obtainLazyThreadSafetyModeArray() {
      return new LazyThreadSafetyMode[]{SYNCHRONIZED, PUBLICATION, NONE};
   }
}
