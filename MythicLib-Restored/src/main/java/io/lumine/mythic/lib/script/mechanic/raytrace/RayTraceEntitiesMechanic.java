package io.lumine.mythic.lib.script.mechanic.raytrace;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.SkillOrientation;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.function.Predicate;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Deprecated
@MechanicMetadata
public class RayTraceEntitiesMechanic extends DirectionMechanic {
   private final DoubleFormula range;
   private final DoubleFormula size;
   private final DoubleFormula step;
   private final Script onHit;
   private final Script onTick;

   public RayTraceEntitiesMechanic(ConfigObject config) {
      super(config);
      this.onTick = config.contains("tick") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("tick")) : null;
      this.onHit = config.contains("hit_entity") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("hit_entity")) : null;
      this.range = config.getDoubleFormula("range", DoubleFormula.constant(50.0));
      this.size = config.getDoubleFormula("size", DoubleFormula.constant(0.2));
      this.step = config.getDoubleFormula("step", DoubleFormula.constant(0.4));
   }

   @Override
   public void cast(SkillMetadata meta, Location source, Vector dir) {
      double range = this.range.evaluate(meta);
      double size = this.size.evaluate(meta);
      double step = this.step.evaluate(meta);
      Validate.isTrue(range > 0.0, "Range must be strictly positive");
      Validate.isTrue(size >= 0.0, "Size must be positive or null");
      Validate.isTrue(step > 0.0, "Step must be strictly positive (don't make it too low)");
      Predicate<Entity> filter = entity -> entity instanceof LivingEntity && !entity.equals(meta.getCaster().getPlayer());
      RayTraceResult result = source.getWorld().rayTraceEntities(source, dir, range, size, filter);
      double length = result == null ? range : result.getHitPosition().distance(source.toVector());
      if (this.onTick != null) {
         for (double j = 0.0; j < length; j += step) {
            Location intermediate = source.clone().add(dir.clone().multiply(j));
            this.onTick.cast(meta.clone(source, intermediate, null, new SkillOrientation(intermediate, dir)));
         }
      }

      if (result != null && this.onHit != null && result.getHitEntity() != null) {
         Location hitPosition = result.getHitPosition().toLocation(source.getWorld());
         SkillOrientation orientation = new SkillOrientation(hitPosition, dir);
         this.onHit.cast(meta.clone(source, hitPosition, result.getHitEntity(), orientation));
      }
   }
}
