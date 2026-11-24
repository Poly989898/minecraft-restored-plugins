package io.lumine.mythic.lib.script.mechanic.type;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.CasterLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.DefaultDirectionTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.EntityLocationType;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class DirectionMechanic extends Mechanic {
   private final LocationTargeter sourceLocation;
   private final LocationTargeter targetLocation;

   public DirectionMechanic(ConfigObject config) {
      this.sourceLocation = config.contains("source") ? config.getLocationTargeter("source") : new CasterLocationTargeter(EntityLocationType.EYES);
      this.targetLocation = config.contains("target") ? config.getLocationTargeter("target") : new DefaultDirectionTargeter();
   }

   public LocationTargeter getSource() {
      return this.sourceLocation;
   }

   public LocationTargeter getTarget() {
      return this.targetLocation;
   }

   @Override
   public void cast(SkillMetadata meta) {
      Location source = this.sourceLocation.findTargets(meta).get(0);
      Vector sourceVector = source.toVector();

      for (Location loc : this.targetLocation.findTargets(meta)) {
         Vector dir = loc.toVector().subtract(sourceVector);
         Validate.isTrue(dir.lengthSquared() > 0.0, "Direction cannot be zero");
         this.cast(meta, source, dir.normalize());
      }
   }

   public abstract void cast(SkillMetadata var1, Location var2, Vector var3);
}
