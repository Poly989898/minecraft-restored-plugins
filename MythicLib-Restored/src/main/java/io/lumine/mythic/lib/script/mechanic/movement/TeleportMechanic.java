package io.lumine.mythic.lib.script.mechanic.movement;

import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.DefaultLocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class TeleportMechanic extends TargetMechanic {
   private final double yOffset;
   private final LocationTargeter targetLocation;

   public TeleportMechanic(ConfigObject config) {
      super(config);
      this.yOffset = config.getDouble("y_offset", 0.0);
      this.targetLocation = config.contains("target_location") ? config.getLocationTargeter("target_location") : new DefaultLocationTargeter();
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      Location targetLocation = this.targetLocation.findTargets(meta).get(0);
      targetLocation.add(0.0, this.yOffset, 0.0);
      target.teleport(targetLocation);
   }
}
