package io.lumine.mythic.lib.util.gson;

import io.lumine.mythic.lib.MythicLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffect;

public class MythicLibGson {
   @Nullable
   private static GsonBuilder builder = new GsonBuilder();

   public static GsonBuilder getBuilder() {
      return Objects.requireNonNull(builder, "MythicLib GSON already built");
   }

   public static Gson build() {
      Validate.notNull(builder, "MythicLib GSON already built");
      builder.registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter());
      builder.registerTypeAdapter(Location.class, new LocationAdapter());
      builder.registerTypeAdapter(
         AttributeModifier.class, MythicLib.plugin.getVersion().isAbove(1, 21) ? new AttributeModifierAdapter() : new LegacyAttributeModifierAdapter()
      );
      Gson gson = builder.create();
      builder = null;
      return gson;
   }
}
