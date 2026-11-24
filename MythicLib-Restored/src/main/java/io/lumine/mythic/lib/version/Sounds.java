package io.lumine.mythic.lib.version;

import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

/** Sound constants whose name changed over the Minecraft versions, resolved through the sound registry. */
public class Sounds {
   public static final Sound BLOCK_ANVIL_LAND = lookup("BLOCK_ANVIL_LAND");
   public static final Sound BLOCK_BREWING_STAND_BREW = lookup("BLOCK_BREWING_STAND_BREW");
   public static final Sound BLOCK_END_PORTAL_FRAME_FILL = lookup("BLOCK_END_PORTAL_FRAME_FILL");
   public static final Sound BLOCK_FIRE_AMBIENT = lookup("BLOCK_FIRE_AMBIENT");
   public static final Sound BLOCK_FIRE_EXTINGUISH = lookup("BLOCK_FIRE_EXTINGUISH");
   public static final Sound BLOCK_GLASS_BREAK = lookup("BLOCK_GLASS_BREAK");
   public static final Sound BLOCK_GRAVEL_BREAK = lookup("BLOCK_GRAVEL_BREAK");
   public static final Sound BLOCK_IRON_DOOR_OPEN = lookup("BLOCK_IRON_DOOR_OPEN");
   public static final Sound BLOCK_NOTE_BLOCK_BELL = lookup("BLOCK_NOTE_BLOCK_BELL", "BLOCK_NOTE_BELL");
   public static final Sound BLOCK_NOTE_BLOCK_HAT = lookup("BLOCK_NOTE_BLOCK_HAT", "BLOCK_NOTE_HAT");
   public static final Sound BLOCK_NOTE_BLOCK_PLING = lookup("BLOCK_NOTE_BLOCK_PLING", "BLOCK_NOTE_PLING");
   public static final Sound BLOCK_SNOW_BREAK = lookup("BLOCK_SNOW_BREAK");
   public static final Sound ENTITY_BLAZE_AMBIENT = lookup("ENTITY_BLAZE_AMBIENT");
   public static final Sound ENTITY_BLAZE_HURT = lookup("ENTITY_BLAZE_HURT");
   public static final Sound ENTITY_CHICKEN_EGG = lookup("ENTITY_CHICKEN_EGG");
   public static final Sound ENTITY_CHICKEN_HURT = lookup("ENTITY_CHICKEN_HURT");
   public static final Sound ENTITY_COW_HURT = lookup("ENTITY_COW_HURT");
   public static final Sound ENTITY_ENDER_DRAGON_FLAP = lookup("ENTITY_ENDER_DRAGON_FLAP", "ENTITY_ENDERDRAGON_FLAP");
   public static final Sound ENTITY_ENDER_DRAGON_GROWL = lookup("ENTITY_ENDER_DRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL");
   public static final Sound ENTITY_ENDERMAN_DEATH = lookup("ENTITY_ENDERMAN_DEATH", "ENTITY_ENDERMEN_DEATH");
   public static final Sound ENTITY_ENDERMAN_HURT = lookup("ENTITY_ENDERMAN_HURT", "ENTITY_ENDERMEN_HURT");
   public static final Sound ENTITY_ENDERMAN_TELEPORT = lookup("ENTITY_ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT");
   public static final Sound ENTITY_EXPERIENCE_ORB_PICKUP = lookup("ENTITY_EXPERIENCE_ORB_PICKUP");
   public static final Sound ENTITY_FIREWORK_ROCKET_BLAST = lookup("ENTITY_FIREWORK_ROCKET_BLAST", "ENTITY_FIREWORK_BLAST");
   public static final Sound ENTITY_FIREWORK_ROCKET_LARGE_BLAST = lookup("ENTITY_FIREWORK_ROCKET_LARGE_BLAST", "ENTITY_FIREWORK_LARGE_BLAST");
   public static final Sound ENTITY_FIREWORK_ROCKET_TWINKLE = lookup("ENTITY_FIREWORK_ROCKET_TWINKLE", "ENTITY_FIREWORK_TWINKLE");
   public static final Sound ENTITY_GENERIC_EAT = lookup("ENTITY_GENERIC_EAT", "ENTITY_PLAYER_BURP");
   public static final Sound ENTITY_GENERIC_EXPLODE = lookup("ENTITY_GENERIC_EXPLODE", "ENTITY_DRAGON_FIREBALL_EXPLODE");
   public static final Sound ENTITY_ITEM_BREAK = lookup("ENTITY_ITEM_BREAK");
   public static final Sound ENTITY_LLAMA_ANGRY = lookup("ENTITY_LLAMA_ANGRY");
   public static final Sound ENTITY_PLAYER_ATTACK_CRIT = lookup("ENTITY_PLAYER_ATTACK_CRIT");
   public static final Sound ENTITY_PLAYER_ATTACK_KNOCKBACK = lookup("ENTITY_PLAYER_ATTACK_KNOCKBACK");
   public static final Sound ENTITY_PLAYER_ATTACK_SWEEP = lookup("ENTITY_PLAYER_ATTACK_SWEEP");
   public static final Sound ENTITY_PLAYER_LEVELUP = lookup("ENTITY_PLAYER_LEVELUP");
   public static final Sound ENTITY_SHEEP_DEATH = lookup("ENTITY_SHEEP_DEATH");
   public static final Sound ENTITY_SNOWBALL_THROW = lookup("ENTITY_SNOWBALL_THROW");
   public static final Sound ENTITY_VILLAGER_NO = lookup("ENTITY_VILLAGER_NO");
   public static final Sound ENTITY_WITCH_DRINK = lookup("ENTITY_WITCH_DRINK");
   public static final Sound ENTITY_WITHER_SHOOT = lookup("ENTITY_WITHER_SHOOT");
   public static final Sound ENTITY_ZOMBIE_ATTACK_IRON_DOOR = lookup("ENTITY_ZOMBIE_ATTACK_IRON_DOOR");
   public static final Sound ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR = lookup("ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR", "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD");
   public static final Sound ENTITY_ZOMBIE_HURT = lookup("ENTITY_ZOMBIE_HURT");
   public static final Sound ENTITY_ZOMBIE_PIGMAN_ANGRY = lookup("ENTITY_ZOMBIE_PIGMAN_ANGRY", "ENTITY_ZOMBIFIED_PIGLIN_ANGRY");
   public static final Sound UI_BUTTON_CLICK = lookup("UI_BUTTON_CLICK");

   /** The first candidate (Bukkit enum style name, e.g. ENTITY_VILLAGER_NO) that is a registered sound. */
   @NotNull
   public static Sound fromName(@NotNull String... candidates) {
      Sound sound = lookup(candidates);
      if (sound == null) {
         throw new IllegalArgumentException("Could not find sound with names " + String.join(", ", candidates));
      }
      return sound;
   }

   /** Like {@link #fromName(String...)} but returns null when no candidate exists on this server version. */
   private static Sound lookup(String... candidates) {
      for (String candidate : candidates) {
         try {
            Object field = Sound.class.getField(candidate.toUpperCase(Locale.ROOT)).get(null);
            if (field instanceof Sound) {
               return (Sound)field;
            }
         } catch (ReflectiveOperationException | RuntimeException ignored) {
            // not a constant of this version, try the registry key below
         }
         NamespacedKey namespaced = NamespacedKey.fromString(candidate.toLowerCase(Locale.ROOT).replace('_', '.'));
         Sound sound = namespaced == null ? null : Registry.SOUNDS.get(namespaced);
         if (sound != null) {
            return sound;
         }
      }
      return null;
   }
}
