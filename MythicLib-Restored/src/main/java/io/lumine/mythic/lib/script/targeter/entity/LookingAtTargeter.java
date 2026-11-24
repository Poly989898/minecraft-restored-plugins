package io.lumine.mythic.lib.script.targeter.entity;

import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;

public class LookingAtTargeter implements EntityTargeter {
   private final DoubleFormula range;
   private final DoubleFormula size;
   private final boolean ignorePassable;

   public LookingAtTargeter(ConfigObject config) {
      this.size = config.getDoubleFormula("size", DoubleFormula.constant(0.2F));
      this.range = config.getDoubleFormula("length", DoubleFormula.constant(50.0));
      this.ignorePassable = config.getBoolean("ignore_passable", true);
   }

   @Override
   public List<Entity> findTargets(SkillMetadata meta) {
      double size = this.size.evaluate(meta);
      double range = this.range.evaluate(meta);
      Validate.isTrue(size >= 0.0, "Size must be positive");
      Validate.isTrue(range > 0.0, "Length must be strictly positive");
      Location source = meta.getCaster().getPlayer().getEyeLocation();
      RayTraceResult result = meta.getSourceLocation()
         .getWorld()
         .rayTrace(source, source.getDirection(), range, FluidCollisionMode.NEVER, this.ignorePassable, size, entity -> true);
      return result != null && result.getHitEntity() != null ? Arrays.asList(result.getHitEntity()) : new ArrayList<>();
   }
}
