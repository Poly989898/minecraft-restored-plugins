package it.ytnoos.lpx.utils;

import com.github.retrooper.packetevents.wrapper.e1;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Base of the recipe checks (RECIPE_A / RECIPE_B): the recipe id sent by the client must be below the number of recipe displays known to the server.
 * The display list is read once by reflection from the NMS RecipeManager. String constants were decrypted from the original class
 * (tools/DecryptW7.java); the unused decoys BS(String)/tq() of the original are not restored.
 */
public abstract class ItemUtils_w7<T> extends PlayerCheck<T> {
   private static List<?> displays;

   public ItemUtils_w7(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public CheckResult check(ProtocolPlayer protocolPlayer, T value) {
      if (displays == null) {
         return CheckResult.ALLOW;
      }

      int value2 = this.getRecipeId(value);
      int value3 = displays.size();
      if (value2 >= 0 && value2 < value3) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(value, new FlagInfo[]{FlagInfo.of("recipeId", value2), FlagInfo.of("displaysSize", value3)});
      return CheckResult.DENY;
   }

   public abstract int getRecipeId(T value);

   static {
      try {
         Class<?> var11 = Class.forName("net.minecraft.server.MinecraftServer");
         Object var12 = var11.getMethod("getServer").invoke(null);
         Class<?> var20 = var12.getClass();
         Class<?>[] var30 = new Class[0];
         Object var13 = var20.getMethod(e1.b("getRecipeManager", var20, var30), var30).invoke(var12);
         Class<?> var21 = var13.getClass();
         Field var14 = var21.getDeclaredField(e1.c(var21, "allDisplays"));
         var14.setAccessible(true);
         displays = (List<?>)var14.get(var13);
      } catch (Exception var15) {
         var15.printStackTrace();
      }
   }
}
