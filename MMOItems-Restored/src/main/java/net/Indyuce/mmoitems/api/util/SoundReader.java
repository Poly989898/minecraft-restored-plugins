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

   public SoundReader(@NotNull String var1, @NotNull Sound var2) {
      if (var1.isEmpty()) {
         this.sound = var2;
         this.soundKey = "";
      } else {
         Sound var3;
         String var4;
         try {
            var3 = Sound.valueOf(var1);
            var4 = null;
         } catch (Exception var6) {
            var3 = null;
            var4 = var1.toLowerCase();
         }

         this.sound = var3;
         this.soundKey = var4;
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

   public void play(@NotNull Player var1) {
      this.play(var1, 1.0F, 1.0F);
   }

   public void play(@NotNull Player var1, float var2, float var3) {
      if (this.sound != null) {
         var1.playSound(var1.getLocation(), this.sound, var2, var3);
      } else {
         var1.playSound(var1.getLocation(), this.soundKey, var2, var3);
      }
   }

   public void play(@NotNull Location var1) {
      this.play(var1, 1.0F, 1.0F);
   }

   public void play(@NotNull Location var1, float var2, float var3) {
      if (this.sound != null) {
         var1.getWorld().playSound(var1, this.sound, var2, var3);
      } else {
         var1.getWorld().playSound(var1, this.soundKey, var2, var3);
      }
   }
}
