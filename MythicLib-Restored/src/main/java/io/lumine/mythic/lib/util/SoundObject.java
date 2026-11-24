package io.lumine.mythic.lib.util;

import io.lumine.mythic.lib.UtilityMethods;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundObject {
   @Nullable
   private final Sound sound;
   @Nullable
   private final String key;
   private final float volume;
   private final float pitch;

   public SoundObject(String input) {
      String[] split = input.split(",");
      Sound sound = null;
      String key = null;

      sound = io.lumine.mythic.lib.version.Sounds.find(split[0]);
      if (sound == null) {
         key = split[0];
      }

      this.sound = sound;
      this.key = key;
      this.volume = split.length > 1 ? Float.parseFloat(split[1]) : 1.0F;
      this.pitch = split.length > 2 ? Float.parseFloat(split[2]) : 1.0F;
   }

   public SoundObject(ConfigurationSection config) {
      String input = config.getString("sound");
      Sound sound = null;
      String key = null;

      sound = io.lumine.mythic.lib.version.Sounds.find(input);
      if (sound == null) {
         key = input;
      }

      this.sound = sound;
      this.key = key;
      this.volume = (float)config.getDouble("volume", 1.0);
      this.pitch = (float)config.getDouble("pitch", 1.0);
   }

   public boolean isCustom() {
      return this.sound == null;
   }

   @Nullable
   public Sound getSound() {
      return this.sound;
   }

   @Nullable
   public String getKey() {
      return this.key;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void playTo(Player player) {
      this.playTo(player, this.volume, this.pitch);
   }

   public void playTo(Player player, float volume, float pitch) {
      if (this.isCustom()) {
         player.playSound(player.getLocation(), this.key, volume, pitch);
      } else {
         player.playSound(player.getLocation(), this.sound, volume, pitch);
      }
   }

   public void playAt(Location loc) {
      this.playAt(loc, this.volume, this.pitch);
   }

   public void playAt(Location loc, float volume, float pitch) {
      if (this.isCustom()) {
         loc.getWorld().playSound(loc, this.key, volume, pitch);
      } else {
         loc.getWorld().playSound(loc, this.sound, volume, pitch);
      }
   }
}
