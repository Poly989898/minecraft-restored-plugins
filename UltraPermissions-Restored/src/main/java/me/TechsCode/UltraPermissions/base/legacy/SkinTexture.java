package me.TechsCode.UltraPermissions.base.legacy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.UUID;
import me.TechsCode.UltraPermissions.base.debug.Debugger;
import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;
import me.TechsCode.UltraPermissions.base.legacy.utils.PlayerUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;

public class SkinTexture {
   private final String url;
   private final long createdAt;

   public static SkinTexture fromPlayer(Player player) {
      GameProfile gameProfile = PlayerUtils.getGameProfile(player);

      try {
         if (gameProfile == null) {
            return null;
         }

         if (MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_20_R2)) {
            Debugger.debug("SkinTexture", "Using 1.20.2+ PlayerProfile API");

            try {
               URL skin = player.getPlayerProfile().getTextures().getSkin();
               if (skin == null) {
                  return null;
               }

               return new SkinTexture(skin.toString(), System.currentTimeMillis());
            } catch (Throwable exception) {
               Debugger.debug("SkinTexture", "Fallback to reflection (authlib): " + exception.getMessage());
            }
         }

         Property property = null;

         try {
            Method method = gameProfile.getClass().getMethod("getProperties");
            if (method.invoke(gameProfile) instanceof PropertyMap propertyMap && propertyMap.containsKey("textures")) {
               property = (Property)propertyMap.get("textures").iterator().next();
            }
         } catch (Throwable exception) {
            Debugger.debug("SkinTexture", "Reflection failed: " + exception.getMessage());
         }

         if (property == null) {
            Debugger.debug("SkinTexture", "No texture property found");
            return null;
         } else {
            String text = property.getValue();
            String textureUrl = base64ToUrl(text);
            return textureUrl != null && !textureUrl.equals("null") ? new SkinTexture(textureUrl, System.currentTimeMillis()) : null;
         }
      } catch (NoSuchElementException exception) {
         return null;
      }
   }

   public static SkinTexture fromMojangAPI(String text) {
      String requestUrl = "https://api.mojang.com/users/profiles/minecraft/" + text;
      String response = getResponse(requestUrl);

      try {
         String asString = new JsonParser().parse(response).getAsJsonObject().get("id").getAsString();
         String uuidString = new StringBuffer(asString).insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-").toString();
         return fromMojangAPI(UUID.fromString(uuidString));
      } catch (Exception exception) {
         return null;
      }
   }

   public static SkinTexture fromMojangAPI(UUID uuid) {
      String text = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false";
      String response = getResponse(text);

      try {
         JsonObject asJsonObject = new JsonParser().parse(response).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
         String asString = asJsonObject.get("value").getAsString();
         return new SkinTexture(base64ToUrl(asString), System.currentTimeMillis());
      } catch (Exception exception) {
         return null;
      }
   }

   public static SkinTexture fromBase64(String text) {
      return new SkinTexture(base64ToUrl(text), System.currentTimeMillis());
   }

   private static String getResponse(String text) {
      try {
         URL url = new URL(text);
         return IOUtils.toString(url.openStream(), StandardCharsets.UTF_8);
      } catch (IOException exception) {
         return null;
      }
   }

   private static String base64ToUrl(String text) {
      try {
         if (!text.contains("{")) {
            text = new String(Base64.decodeBase64(text.getBytes()));
         }

         JsonObject jsonObject = (JsonObject)new JsonParser().parse(text.trim());
         JsonObject asJsonObject = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN");
         return asJsonObject == null ? null : asJsonObject.get("url").getAsString();
      } catch (Exception exception) {
         System.out.println("Could not retrieve Url from Base64");
         System.out.println(text);
         System.out.println("Trace:");
         exception.printStackTrace();
         return null;
      }
   }

   public SkinTexture(String text, long value) {
      this.url = text;
      this.createdAt = value;
   }

   public SkinTexture(String text) {
      this.url = text.split(":split:")[0];
      this.createdAt = Long.parseLong(text.split(":split:")[1]);
   }

   public String getUrl() {
      return this.url;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   @Override
   public String toString() {
      return this.url + ":split:" + this.createdAt;
   }
}
