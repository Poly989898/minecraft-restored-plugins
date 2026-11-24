package io.lumine.mythic.lib.script.mechanic.type;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.DefaultLocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;

public abstract class LocationMechanic extends Mechanic {
   private final LocationTargeter targeter;

   public LocationMechanic(ConfigObject config) {
      this.targeter = config.contains("target") ? config.getLocationTargeter("target") : new DefaultLocationTargeter();
   }

   public LocationTargeter getTargeter() {
      return this.targeter;
   }

   @Override
   public void cast(SkillMetadata meta) {
      for (Location loc : this.targeter.findTargets(meta)) {
         this.cast(meta, loc);
      }
   }

   public abstract void cast(SkillMetadata var1, Location var2);
}
