package io.lumine.mythic.lib.script.mechanic.raytrace;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Deprecated
@MechanicMetadata
public class RayTraceBlocksMechanic extends DirectionMechanic {
   private final DoubleFormula range;
   private final DoubleFormula step;
   private final Script onHit;
   private final Script onTick;
   private final boolean ignorePassable;

   public RayTraceBlocksMechanic(ConfigObject config) {
      super(config);
      this.onTick = config.contains("tick") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("tick")) : null;
      this.onHit = config.contains("hit_block") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("hit_block")) : null;
      this.ignorePassable = config.getBoolean("ignore_passable", false);
      this.range = config.getDoubleFormula("range", DoubleFormula.constant(50.0));
      this.step = config.getDoubleFormula("step", DoubleFormula.constant(0.4));
   }

   @Override
   public void cast(SkillMetadata meta, Location source, Vector dir) {
      double range = this.range.evaluate(meta);
      double step = this.step.evaluate(meta);
      Validate.isTrue(range > 0.0, "Range must be strictly positive");
      Validate.isTrue(step > 0.0, "Step must be strictly positive (don't make it too low)");
      RayTraceResult result = source.getWorld().rayTraceBlocks(source, dir, range, FluidCollisionMode.NEVER, this.ignorePassable);
      double length = result == null ? range : result.getHitPosition().distance(source.toVector());
      if (this.onTick != null) {
         for (double j = 0.0; j < length; j += step) {
            Location intermediate = source.clone().add(dir.clone().multiply(j));
            this.onTick.cast(meta.clone(source, intermediate, null, null));
         }
      }

      if (result != null && this.onHit != null && result.getHitBlock() != null) {
         Location hitPosition = result.getHitPosition().toLocation(source.getWorld());
         this.onHit.cast(meta.clone(source, hitPosition, null, null));
      }
   }
}
