package fr.phoenixdevt.mmoprofiles.bukkit.utils;

import java.util.Collection;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.Nullable;

/** Attribute lookup that accepts both the old (GENERIC_MAX_HEALTH) and the current (MAX_HEALTH) names. */
public final class Attributes {
   public static final Attribute MAX_HEALTH = Attribute.MAX_HEALTH;

   private Attributes() {
   }

   public static Collection<Attribute> getAll() {
      return Registry.ATTRIBUTE.stream().toList();
   }

   @Nullable
   public static Attribute fromName(String... names) {
      for (String name : names) {
         Attribute found = adapt(name);
         if (found != null) {
            return found;
         }
      }
      return null;
   }

   @Nullable
   public static Attribute adapt(String name) {
      String key = name.toLowerCase(Locale.ROOT).replace('.', '_');
      for (String prefix : new String[]{"generic_", "player_"}) {
         if (key.startsWith(prefix)) {
            key = key.substring(prefix.length());
            break;
         }
      }
      return Registry.ATTRIBUTE.get(NamespacedKey.minecraft(key));
   }

   /** Upper-case name without namespace or old prefix, e.g. MAX_HEALTH. */
   public static String name(Attribute attribute) {
      return attribute.getKey().getKey().toUpperCase(Locale.ROOT).replace('.', '_');
   }
}
