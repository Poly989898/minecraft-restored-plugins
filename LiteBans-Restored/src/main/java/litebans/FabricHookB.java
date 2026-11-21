package litebans;

import com.google.gson.JsonParser;
import net.minecraft.class_2561;
import net.minecraft.class_2561.class_2562;

class FabricHookB implements PlatformHook {
   final FabricCommandSourceLegacy a_f;

   FabricHookB(FabricCommandSourceLegacy var1) {
      this.a_f = var1;
   }

   public class_2561 b_6(CharSequence var1) {
      throw new UnsupportedOperationException();
   }

   public class_2561 a_8(CharSequence var1) {
      return class_2562.method_10872(JsonParser.parseString(var1.toString()));
   }

   @Override
   public Object PunishmentTypeSwitchMap2(CharSequence var1) {
      return this.a_8(var1);
   }

   @Override
   public Object AbstractPlatform(CharSequence var1) {
      return this.b_6(var1);
   }
}
