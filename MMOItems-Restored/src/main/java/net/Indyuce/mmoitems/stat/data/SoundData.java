package net.Indyuce.mmoitems.stat.data;

import org.bukkit.configuration.ConfigurationSection;

public class SoundData {
   private final String sound;
   private final double volume;
   private final double pitch;

   public SoundData(String text, double value, double value2) {
      this.sound = text;
      this.volume = value;
      this.pitch = value2;
   }

   public SoundData(Object obj) {
      if (obj instanceof String) {
         this.sound = obj.toString();
         this.volume = 1.0;
         this.pitch = 1.0;
      } else {
         if (!(obj instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("You must provide a string or config section");
         }

         ConfigurationSection section = (ConfigurationSection)obj;
         this.sound = section.getString("sound");
         this.volume = section.getDouble("volume");
         this.pitch = section.getDouble("pitch");
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
