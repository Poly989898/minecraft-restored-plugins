package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;

public class ConstantLocationTargeter extends LocationTargeter {
   private final double x;
   private final double y;
   private final double z;

   public ConstantLocationTargeter(double x, double y, double z) {
      super(false);
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      return Arrays.asList(new Location(meta.getSourceLocation().getWorld(), this.x, this.y, this.z));
   }
}
