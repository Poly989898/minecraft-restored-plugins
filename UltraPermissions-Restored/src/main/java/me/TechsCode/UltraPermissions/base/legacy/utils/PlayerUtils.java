package me.TechsCode.UltraPermissions.base.legacy.utils;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.InvocationTargetException;
import me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar.Reflection;
import org.bukkit.entity.Player;

public class PlayerUtils {
   private static final Class<?> CRAFT_PLAYER = Reflection.getCraftClass("entity.CraftPlayer");

   public PlayerUtils() {
   }

   private static Object getCraftPlayer(Player player) {
      return CRAFT_PLAYER.cast(player);
   }

   public static GameProfile getGameProfile(Player player) {
      Object craftPlayer = getCraftPlayer(player);

      try {
         return (GameProfile)CRAFT_PLAYER.getMethod("getProfile").invoke(craftPlayer);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
         exception.printStackTrace();
         return null;
      }
   }
}
