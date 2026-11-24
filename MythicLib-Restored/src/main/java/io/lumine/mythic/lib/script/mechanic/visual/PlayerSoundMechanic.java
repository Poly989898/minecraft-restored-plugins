package io.lumine.mythic.lib.script.mechanic.visual;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import org.apache.commons.lang.Validate;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MechanicMetadata
public class PlayerSoundMechanic extends TargetMechanic {
   private Sound sound;
   private final String soundString;
   private final DoubleFormula vol;
   private final DoubleFormula pitch;

   public PlayerSoundMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("sound");
      this.soundString = config.getString("sound");
      this.sound = io.lumine.mythic.lib.version.Sounds.find(this.soundString);

      this.vol = config.getDoubleFormula("volume", DoubleFormula.constant(1.0));
      this.pitch = config.getDoubleFormula("pitch", DoubleFormula.constant(1.0));
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      Validate.isTrue(target instanceof Player, "Target is not player");
      Player player = (Player)target;
      float vol = (float)this.vol.evaluate(meta);
      float pitch = (float)this.pitch.evaluate(meta);
      if (this.sound == null) {
         player.playSound(target.getLocation(), this.soundString, vol, pitch);
      } else {
         player.playSound(target.getLocation(), this.sound, vol, pitch);
      }
   }
}
