package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.EntityLocationType;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;

public class TargetEntityLocationTargeter extends LocationTargeter {
   private final EntityLocationType entityLocationType;

   public TargetEntityLocationTargeter(ConfigObject config) {
      super(false);
      this.entityLocationType = config.contains("position") ? EntityLocationType.valueOf(config.getString("position").toUpperCase()) : EntityLocationType.BODY;
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      return Arrays.asList(this.entityLocationType.getLocation(meta.getTargetEntity()));
   }
}
