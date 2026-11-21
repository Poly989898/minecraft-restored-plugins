package litebans;

public final class KotlinPacketAdapterSubCompanion {
   private KotlinPacketAdapterSubCompanion() {
   }

   private final Object obtainObj() {
      Lazy lazy = KotlinPacketAdapterSub.obtainLazy();
      return lazy.getValue();
   }

   public static final Object computeObj(KotlinPacketAdapterSubCompanion value) {
      return value.obtainObj();
   }

   public KotlinPacketAdapterSubCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
