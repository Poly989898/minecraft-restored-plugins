package io.lumine.mythic.lib.script.mechanic.visual;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.LocationMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.Sound;

@MechanicMetadata
public class SoundMechanic extends LocationMechanic {
   private Sound sound;
   private final String soundString;
   private final DoubleFormula vol;
   private final DoubleFormula pitch;

   public SoundMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("sound");
      this.soundString = config.getString("sound");
      this.sound = io.lumine.mythic.lib.version.Sounds.find(this.soundString);

      this.vol = config.getDoubleFormula("volume", DoubleFormula.constant(1.0));
      this.pitch = config.getDoubleFormula("pitch", DoubleFormula.constant(1.0));
   }

   @Override
   public void cast(SkillMetadata meta, Location loc) {
      float vol = (float)this.vol.evaluate(meta);
      float pitch = (float)this.pitch.evaluate(meta);
      if (this.sound == null) {
         loc.getWorld().playSound(loc, this.soundString, vol, pitch);
      } else {
         loc.getWorld().playSound(loc, this.sound, vol, pitch);
      }
   }
}
