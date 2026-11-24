package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;

public class TargetLocationTargeter extends LocationTargeter {
   public TargetLocationTargeter() {
      super(false);
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      return Arrays.asList(meta.getTargetLocation());
   }
}
