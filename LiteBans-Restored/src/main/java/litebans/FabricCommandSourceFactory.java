package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricCommandSourceFactory {
   public static FabricCommandSource PunishmentTypeSwitchMap2(Platform var0, String var1) {
      FabricCommandSourceV26 var2 = a_1(var0, var1);
      if (var2 != null) {
         return var2;
      }

      String var3 = "method_64396";

      try {
         Class.forName("net.minecraft.class_3222").getMethod(var3);
         return new FabricCommandSourceV121(var0);
      } catch (NoSuchMethodException var6) {
         return UInt(var0, var3);
      } catch (Exception | LinkageError var7) {
         throw new RuntimeException("Compatibility check failed (1.21) for " + var1, var7);
      }
   }

   private static final @Nullable FabricCommandSourceV26 a_1(Platform var0, String var1) {
      try {
         Class.forName("net.minecraft.WorldVersion");
         return new FabricCommandSourceV26(var0);
      } catch (ClassNotFoundException var4) {
         return null;
      } catch (Exception | LinkageError var5) {
         throw new RuntimeException("Compatibility check failed (26) for " + var1, var5);
      }
   }

   protected static @NotNull FabricCommandSource UInt(Platform var0, String var1) {
      try {
         Class.forName("net.minecraft.class_8710");
         return new FabricCommandSourceV120(var0);
      } catch (ClassNotFoundException var3) {
         return new FabricCommandSourceLegacy(var0);
      }
   }
}
