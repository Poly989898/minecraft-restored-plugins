package io.lumine.mythic.lib.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

/**
 * Version independent access to the vanilla attributes. Attribute names changed between
 * Minecraft versions (GENERIC_MAX_HEALTH became MAX_HEALTH), every lookup therefore accepts
 * several candidate names and resolves them against the attribute registry.
 */
public class Attributes {
   public static final Attribute ARMOR = fromName("ARMOR", "GENERIC_ARMOR");
   public static final Attribute ARMOR_TOUGHNESS = fromName("ARMOR_TOUGHNESS", "GENERIC_ARMOR_TOUGHNESS");
   public static final Attribute ATTACK_DAMAGE = fromName("ATTACK_DAMAGE", "GENERIC_ATTACK_DAMAGE");
   public static final Attribute ATTACK_SPEED = fromName("ATTACK_SPEED", "GENERIC_ATTACK_SPEED");
   public static final Attribute KNOCKBACK_RESISTANCE = fromName("KNOCKBACK_RESISTANCE", "GENERIC_KNOCKBACK_RESISTANCE");
   public static final Attribute LUCK = fromName("LUCK", "GENERIC_LUCK");
   public static final Attribute MAX_HEALTH = fromName("MAX_HEALTH", "GENERIC_MAX_HEALTH");
   public static final Attribute MOVEMENT_SPEED = fromName("MOVEMENT_SPEED", "GENERIC_MOVEMENT_SPEED");
   public static final Attribute FOLLOW_RANGE = fromName("FOLLOW_RANGE", "GENERIC_FOLLOW_RANGE");
   public static final Attribute ENTITY_INTERACTION_RANGE = fromName("ENTITY_INTERACTION_RANGE", "PLAYER_ENTITY_INTERACTION_RANGE");
   public static final Attribute BLOCK_INTERACTION_RANGE = fromName("BLOCK_INTERACTION_RANGE", "PLAYER_BLOCK_INTERACTION_RANGE");
   public static final Attribute ATTACK_KNOCKBACK = fromName("ATTACK_KNOCKBACK", "GENERIC_ATTACK_KNOCKBACK");

   /** Upper case name of an attribute, without namespace and with '_' as separator. */
   @NotNull
   public static String name(@NotNull Attribute attribute) {
      return attribute.getKey().getKey().toUpperCase(Locale.ROOT).replace('.', '_');
   }

   @NotNull
   public static Collection<Attribute> getAll() {
      Collection<Attribute> all = new ArrayList<>();
      for (Attribute attribute : Registry.ATTRIBUTE) {
         all.add(attribute);
      }
      return all;
   }

   /** Same as {@link #fromName(String...)}, but returns null when nothing matches. */
   public static Attribute adapt(@NotNull String name) {
      return find(name);
   }

   /** The first candidate name that matches an attribute. */
   public static Attribute fromName(@NotNull String... candidates) {
      for (String candidate : candidates) {
         Attribute found = find(candidate);
         if (found != null) {
            return found;
         }
      }
      throw new IllegalArgumentException("Could not find attribute with names " + String.join(", ", candidates));
   }

   private static Attribute find(String name) {
      String key = name.toLowerCase(Locale.ROOT).replace('-', '_');
      String[] variants = {key, strip(key), key.replace("generic_", "generic.").replace("player_", "player."), key.replace("zombie_", "zombie.")};
      for (String variant : variants) {
         NamespacedKey namespaced = NamespacedKey.fromString(variant);
         if (namespaced != null) {
            Attribute attribute = Registry.ATTRIBUTE.get(namespaced);
            if (attribute != null) {
               return attribute;
            }
         }
      }
      return null;
   }

   private static String strip(String key) {
      for (String prefix : new String[] {"generic_", "player_"}) {
         if (key.startsWith(prefix)) {
            return key.substring(prefix.length());
         }
      }
      return key;
   }
}
