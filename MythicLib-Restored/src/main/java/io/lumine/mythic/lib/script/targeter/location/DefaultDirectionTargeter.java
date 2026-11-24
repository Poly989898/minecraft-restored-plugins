package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;

public class DefaultDirectionTargeter extends LocationTargeter {
   public DefaultDirectionTargeter() {
      super(false);
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      Location loc = meta.getCaster().getPlayer().getEyeLocation();
      return Arrays.asList(loc.add(loc.getDirection()));
   }
}
