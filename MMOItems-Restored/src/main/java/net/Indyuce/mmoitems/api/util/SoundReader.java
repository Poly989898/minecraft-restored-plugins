package net.Indyuce.mmoitems.api.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundReader {
   @Nullable
   private final Sound sound;
   @Nullable
   private final String soundKey;

   public SoundReader(@NotNull String text, @NotNull Sound sound) {
      if (text.isEmpty()) {
         this.sound = sound;
         this.soundKey = "";
      } else {
         Sound sound2;
         String text2;
         try {
            sound2 = Sound.valueOf(text);
            text2 = null;
         } catch (Exception exception) {
            sound2 = null;
            text2 = text.toLowerCase();
         }

         this.sound = sound2;
         this.soundKey = text2;
      }
   }

   @Nullable
   public Sound getSound() {
      return this.sound;
   }

   @NotNull
   public String getSoundKey() {
      return this.soundKey;
   }

   public void play(@NotNull Player player) {
      this.play(player, 1.0F, 1.0F);
   }

   public void play(@NotNull Player player, float value, float value2) {
      if (this.sound != null) {
         player.playSound(player.getLocation(), this.sound, value, value2);
      } else {
         player.playSound(player.getLocation(), this.soundKey, value, value2);
      }
   }

   public void play(@NotNull Location location) {
      this.play(location, 1.0F, 1.0F);
   }

   public void play(@NotNull Location location, float value, float value2) {
      if (this.sound != null) {
         location.getWorld().playSound(location, this.sound, value, value2);
      } else {
         location.getWorld().playSound(location, this.soundKey, value, value2);
      }
   }
}
