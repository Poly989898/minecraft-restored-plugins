package fr.phoenixdevt.mmoprofiles.bukkit.utils.message;

import fr.phoenixdevt.mmoprofiles.bukkit.utils.Sounds;
import org.apache.commons.lang3.Validate;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundReader {
   private final Sound sound;
   private final float vol;
   private final float pitch;

   public SoundReader(Sound sound, float vol, float pitch) {
      this.sound = sound;
      this.vol = vol;
      this.pitch = pitch;
   }

   public SoundReader(ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      String format = config.getString("name");
      Validate.notNull(format, "Could not read sound name");
      this.sound = Sounds.fromName(new String[]{format.toUpperCase().replace(" ", "_").replace("-", "_")});
      this.vol = (float)config.getDouble("vol");
      this.pitch = (float)config.getDouble("pitch");
   }

   public Sound getSound() {
      return this.sound;
   }

   public float getVolume() {
      return this.vol;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void play(Player player) {
      player.playSound(player.getLocation(), this.sound, this.vol, this.pitch);
   }
}
