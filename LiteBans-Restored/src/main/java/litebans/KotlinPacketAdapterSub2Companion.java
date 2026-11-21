package litebans;

public final class KotlinPacketAdapterSub2Companion {
   private KotlinPacketAdapterSub2Companion() {
   }

   private final Object obtainObj() {
      Lazy lazy = KotlinPacketAdapterSub2.obtainLazy();
      return lazy.getValue();
   }

   private final Object obtainObj2() {
      Lazy lazy = KotlinPacketAdapterSub2.obtainLazy2();
      return lazy.getValue();
   }

   public static final Object computeObj(KotlinPacketAdapterSub2Companion value) {
      return value.obtainObj();
   }

   public static final Object computeObj2(KotlinPacketAdapterSub2Companion value) {
      return value.obtainObj2();
   }

   public KotlinPacketAdapterSub2Companion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
