package io.lumine.mythic.api.volatilecode.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Banner;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Cat.Type;
import org.bukkit.entity.Wolf.Variant;
import org.bukkit.inventory.meta.BannerMeta;

public interface VolatileCompatibilityHandler {
   default void setLegacyBannerPattern(Banner bannerMeta, DyeColor color, String pattern) {
   }

   default void setLegacyBannerPattern(BannerMeta bannerMeta, DyeColor color, String pattern) {
   }

   default void setCatVariant(Cat cat, String key) {
      key = normalizeRegistryKey(key);
      Type type = Registry.CAT_VARIANT.getOrThrow(NamespacedKey.fromString(key));
      cat.setCatType(type);
   }

   default void setWolfVariant(Wolf wolf, String key) {
      key = normalizeRegistryKey(key);
      Variant type = Registry.WOLF_VARIANT.getOrThrow(NamespacedKey.fromString(key));
      wolf.setVariant(type);
   }

   default void setCowVariant(Cow cow, String key) {
   }

   default void setSulfurCubeArchetype(Entity sulfurCube, String key) {
   }

   default void setCubeMobSize(Entity cubeMob, int size) {
   }

   static String normalizeRegistryKey(String key) {
      key = key.toLowerCase(Locale.ROOT);
      if (!key.contains(":")) {
         key = "minecraft:" + key;
      }

      return key;
   }

   static Set<String> variantValues(String value, NamespacedKey key) {
      Set<String> values = new HashSet<>();
      values.add(value);
      values.add(key.toString());
      values.add(key.getKey());
      return values;
   }

   default Set<String> getWolfVariantValues(Wolf wolf) {
      return Collections.emptySet();
   }

   default Set<String> getCowVariantValues(Cow cow) {
      return Collections.emptySet();
   }

   default Set<String> getChickenVariantValues(Chicken chicken) {
      return Collections.emptySet();
   }

   default Set<String> getPigVariantValues(Pig pig) {
      return Collections.emptySet();
   }
}
