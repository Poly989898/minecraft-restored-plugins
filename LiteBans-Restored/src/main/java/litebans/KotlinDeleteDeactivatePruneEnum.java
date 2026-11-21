package litebans;

public enum KotlinDeleteDeactivatePruneEnum {
   DELETE,
   DEACTIVATE,
   PRUNE;

   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   public static KotlinDeleteDeactivatePruneEnum computeKotlinDeleteDeactivatePruneEnum(String text) {
      return Enum.valueOf(KotlinDeleteDeactivatePruneEnum.class, text);
   }

   private static final KotlinDeleteDeactivatePruneEnum[] obtainKotlinDeleteDeactivatePruneEnumArray() {
      return new KotlinDeleteDeactivatePruneEnum[]{DELETE, DEACTIVATE, PRUNE};
   }
}
