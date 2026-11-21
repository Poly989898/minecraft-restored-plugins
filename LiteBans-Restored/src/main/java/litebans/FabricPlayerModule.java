package litebans;

import java.util.List;

public class FabricPlayerModule extends PlayerModule {
   public FabricPlayerModule(Platform var1) {
      super(var1);
   }

   @Override
   public LiteSender[] a_5() {
      List var2 = this.a_8().g_3().c_30();
      return var2 == null ? LiteSender.a_f : var2.stream().map(this.a_f::AbstractPlatform).toArray(var0 -> (LiteSender[])AbstractPlatform(var0));
   }

   @Override
   public int b_5() {
      List var2 = this.a_8().g_3().c_30();
      return var2 == null ? 0 : var2.size();
   }

   private final FabricPlugin a_8() {
      return (FabricPlugin)this.a_f;
   }

   private static final Object AbstractPlatform(int var0) {
      return new LiteSender[var0];
   }
}
