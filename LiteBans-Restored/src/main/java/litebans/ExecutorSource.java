package litebans;

public enum ExecutorSource {
   UUID,
   EXECUTOR_UUID,
   ID,
   CUSTOM;

   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   public static ExecutorSource computeExecutorSource(String text) {
      return Enum.valueOf(ExecutorSource.class, text);
   }

   private static final ExecutorSource[] obtainExecutorSourceArray() {
      return new ExecutorSource[]{UUID, EXECUTOR_UUID, ID, CUSTOM};
   }
}
