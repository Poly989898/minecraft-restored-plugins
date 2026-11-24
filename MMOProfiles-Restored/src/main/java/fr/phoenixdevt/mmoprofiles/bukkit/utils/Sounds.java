package fr.phoenixdevt.mmoprofiles.bukkit.utils;

import java.util.Locale;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

public final class Sounds {
   private Sounds() {
   }

   /** Finds a sound by names like ENTITY_PLAYER_LEVELUP (key entity.player.levelup). */
   @Nullable
   public static Sound fromName(String... names) {
      for (String name : names) {
         String wanted = name.toUpperCase(Locale.ROOT).replace('.', '_');
         for (Sound sound : Registry.SOUNDS) {
            if (Registry.SOUNDS.getKey(sound).getKey().toUpperCase(Locale.ROOT).replace('.', '_').equals(wanted)) {
               return sound;
            }
         }
      }
      return null;
   }
}
