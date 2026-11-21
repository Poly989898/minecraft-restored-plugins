package litebans;

import java.util.function.Supplier;

public final class FabricSupplierA implements Supplier {
   final FabricLoginListener a_f;
   final FabricCommandSource b_f;

   public FabricSupplierA(FabricLoginListener var1, FabricCommandSource var2) {
      this.a_f = var1;
      this.b_f = var2;
   }

   public final PlatformListener[] a_51() {
      FabricListenerB[] var3 = new FabricListenerB[1];
      Platform var10004 = this.a_f.a_f;
      FabricCommandSource var2 = this.b_f;
      Intrinsics.a_15(var2);
      var3[0] = new FabricListenerB(var10004, var2);
      return var3;
   }

   @Override
   public Object get() {
      return this.a_51();
   }
}
