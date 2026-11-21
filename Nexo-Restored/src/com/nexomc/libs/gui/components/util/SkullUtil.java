package com.nexomc.libs.gui.components.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class SkullUtil {
   private static final Material SKULL = getSkullMaterial();
   private static final Gson GSON = new Gson();

   private static Material getSkullMaterial() {
      return VersionHelper.IS_ITEM_LEGACY ? Material.valueOf("SKULL_ITEM") : Material.PLAYER_HEAD;
   }

   public static ItemStack skull() {
      return VersionHelper.IS_ITEM_LEGACY ? new ItemStack(SKULL, 1, (short)3) : new ItemStack(SKULL);
   }

   public static boolean isPlayerSkull(@NotNull ItemStack item) {
      if (!VersionHelper.IS_ITEM_LEGACY) {
         return item.getType() == SKULL;
      } else {
         return item.getType() == SKULL && item.getDurability() == 3;
      }
   }

   public static String getSkinUrl(String base64Texture) {
      String decoded = new String(Base64.getDecoder().decode(base64Texture));
      JsonObject object = (JsonObject)GSON.fromJson(decoded, JsonObject.class);
      JsonElement textures = object.get("textures");
      if (textures == null) {
         return null;
      } else {
         JsonElement skin = textures.getAsJsonObject().get("SKIN");
         if (skin == null) {
            return null;
         } else {
            JsonElement url = skin.getAsJsonObject().get("url");
            return url == null ? null : url.getAsString();
         }
      }
   }
}
