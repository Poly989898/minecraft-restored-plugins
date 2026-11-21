package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.core.skills.variables.VariableRegistry;
import java.util.Locale;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

final class EffectMetadataSupport {
   private EffectMetadataSupport() {
   }

   static int resolveLevel(PotionEffect effect) {
      return effect == null ? 0 : effect.getAmplifier() + 1;
   }

   static int resolveDuration(PotionEffect effect) {
      return effect == null ? 0 : effect.getDuration();
   }

   static String resolveEffectId(PotionEffectType type) {
      if (type == null) {
         return "";
      } else {
         String legacy = type.getName();
         return legacy != null && !legacy.isBlank() ? legacy.toUpperCase(Locale.ROOT) : type.getKey().getKey().toUpperCase(Locale.ROOT);
      }
   }

   static void putString(VariableRegistry vars, String key, String value) {
      vars.putString(key, value);
   }

   static void putInt(VariableRegistry vars, String key, int value) {
      vars.putInt(key, value);
   }
}
