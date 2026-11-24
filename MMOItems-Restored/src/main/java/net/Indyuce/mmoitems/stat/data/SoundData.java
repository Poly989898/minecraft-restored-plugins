package net.Indyuce.mmoitems.stat.data;

import org.bukkit.configuration.ConfigurationSection;

public class SoundData {
   private final String sound;
   private final double volume;
   private final double pitch;

   public SoundData(String var1, double var2, double var4) {
      this.sound = var1;
      this.volume = var2;
      this.pitch = var4;
   }

   public SoundData(Object var1) {
      if (var1 instanceof String) {
         this.sound = var1.toString();
         this.volume = 1.0;
         this.pitch = 1.0;
      } else {
         if (!(var1 instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("You must provide a string or config section");
         }

         ConfigurationSection var2 = (ConfigurationSection)var1;
         this.sound = var2.getString("sound");
         this.volume = var2.getDouble("volume");
         this.pitch = var2.getDouble("pitch");
      }
   }

   public String getSound() {
      return this.sound;
   }

   public double getVolume() {
      return this.volume;
   }

   public double getPitch() {
      return this.pitch;
   }
}
