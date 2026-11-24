package io.lumine.mythic.lib.script.mechanic.misc;

import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.LocationMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;

@MechanicMetadata
public class LightningStrikeMechanic extends LocationMechanic {
   private final boolean effect;

   public LightningStrikeMechanic(ConfigObject config) {
      super(config);
      this.effect = config.getBoolean("effect", false);
   }

   @Override
   public void cast(SkillMetadata meta, Location loc) {
      if (this.effect) {
         loc.getWorld().strikeLightningEffect(loc);
      } else {
         loc.getWorld().strikeLightning(loc);
      }
   }
}
