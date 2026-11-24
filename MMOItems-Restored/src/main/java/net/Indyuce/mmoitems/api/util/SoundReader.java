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
         Sound resolvedSound;
         String resolvedName;
         try {
            resolvedSound = Sound.valueOf(text);
            resolvedName = null;
         } catch (Exception exception) {
            resolvedSound = null;
            resolvedName = text.toLowerCase();
         }

         this.sound = resolvedSound;
         this.soundKey = resolvedName;
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

   public void play(@NotNull Player player, float value, float pitch) {
      if (this.sound != null) {
         player.playSound(player.getLocation(), this.sound, value, pitch);
      } else {
         player.playSound(player.getLocation(), this.soundKey, value, pitch);
      }
   }

   public void play(@NotNull Location location) {
      this.play(location, 1.0F, 1.0F);
   }

   public void play(@NotNull Location location, float value, float pitch) {
      if (this.sound != null) {
         location.getWorld().playSound(location, this.sound, value, pitch);
      } else {
         location.getWorld().playSound(location, this.soundKey, value, pitch);
      }
   }
}
