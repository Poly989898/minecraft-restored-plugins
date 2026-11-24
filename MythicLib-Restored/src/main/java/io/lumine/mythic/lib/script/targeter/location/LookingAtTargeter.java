package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;

public class LookingAtTargeter extends LocationTargeter {
   private final double rayCastLength;
   private final boolean ignorePassable;

   public LookingAtTargeter(ConfigObject config) {
      super(false);
      this.rayCastLength = config.getDouble("length", 50.0);
      this.ignorePassable = config.getBoolean("ignore_passable", true);
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      Location source = meta.getCaster().getPlayer().getEyeLocation();
      RayTraceResult result = meta.getSourceLocation()
         .getWorld()
         .rayTraceBlocks(source, source.getDirection(), this.rayCastLength, FluidCollisionMode.NEVER, this.ignorePassable);
      return result == null ? new ArrayList<>() : Arrays.asList(result.getHitPosition().toLocation(source.getWorld()));
   }
}
