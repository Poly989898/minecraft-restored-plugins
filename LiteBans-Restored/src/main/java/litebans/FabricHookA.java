package litebans;

import com.mojang.serialization.JsonOps;
import net.minecraft.class_11341;
import net.minecraft.class_2561;
import net.minecraft.class_5455;
import net.minecraft.class_8824;

public class FabricHookA implements PlatformHook {
   private static final boolean b_f;
   private FabricHookD c_f;

   public FabricHookA() {
      if (b_f) {
         this.c_f = new FabricHookD();
      }
   }

   @Override
   public Object AbstractPlatform(CharSequence var1) {
      return class_2561.method_43470(var1.toString());
   }

   @Override
   public Object PunishmentTypeSwitchMap2(CharSequence var1) {
      return b_f ? this.c_f.PunishmentTypeSwitchMap2(var1) : this.a_1(var1);
   }

   private final class_2561 a_1(CharSequence var1) {
      return (class_2561)class_8824.field_46597
         .parse(class_5455.field_40585.method_57093(JsonOps.INSTANCE), class_11341.method_71360(var1.toString()))
         .getOrThrow(var0 -> new IllegalArgumentException(var0));
   }

   static {
      boolean var0 = false;

      try {
         Class var1 = Class.forName("net.minecraft.class_11341");
         var1.getMethod("method_71360", String.class);
      } catch (ReflectiveOperationException var3) {
         var0 = true;
      }

      b_f = var0;
   }
}
