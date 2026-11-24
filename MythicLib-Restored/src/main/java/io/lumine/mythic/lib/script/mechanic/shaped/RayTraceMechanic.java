package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.function.Predicate;
import org.apache.commons.lang.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@MechanicMetadata
public class RayTraceMechanic extends DirectionMechanic {
   private final DoubleFormula range;
   private final DoubleFormula size;
   private final DoubleFormula step;
   private final Script onHitBlock;
   private final Script onHitEntity;
   private final Script onTick;
   private final boolean ignorePassable;
   private final boolean offense;
   private final boolean neutral;
   private final RayTraceMechanic.RayTraceType rayTraceType;
   public static final double DEFAULT_RANGE = 50.0;
   public static final double DEFAULT_SIZE = 0.2;
   public static final double DEFAULT_STEP = 0.4;

   public RayTraceMechanic(ConfigObject config) {
      super(config);
      this.onTick = config.getScriptOrNull("tick");
      this.onHitEntity = config.getScriptOrNull("hit_entity");
      this.onHitBlock = config.getScriptOrNull("hit_block");
      this.ignorePassable = config.getBoolean("ignore_passable", false);
      this.neutral = config.getBoolean("neutral", true);
      this.offense = config.getBoolean("offense", true);
      this.rayTraceType = config.contains("mode")
         ? RayTraceMechanic.RayTraceType.valueOf(UtilityMethods.enumName(config.getString("mode")))
         : RayTraceMechanic.RayTraceType.DEFAULT;
      this.range = config.getDoubleFormula("range", DoubleFormula.constant(50.0));
      this.size = config.getDoubleFormula("size", DoubleFormula.constant(0.2));
      this.step = config.getDoubleFormula("step", DoubleFormula.constant(0.4));
   }

   @Override
   public void cast(SkillMetadata meta, Location source, Vector direction) {
      double step = this.step.evaluate(meta);
      double range = this.range.evaluate(meta);
      Validate.isTrue(range > 0.0, "Range must be strictly positive");
      Validate.isTrue(step > 0.0, "Step must be strictly positive (don't make it too low)");
      RayTraceResult result = this.getResult(meta, source, direction, range);
      double length = result == null ? range : result.getHitPosition().distance(source.toVector());
      if (this.onTick != null) {
         for (double j = 0.0; j < length; j += step) {
            Location intermediate = source.clone().add(direction.clone().multiply(j));
            this.onTick.cast(meta.clone(source, intermediate, null, null));
         }
      }

      if (result != null) {
         Location hitPosition = result.getHitPosition().toLocation(source.getWorld());
         if (this.onHitBlock != null && result.getHitBlock() != null) {
            this.onHitBlock.cast(meta.clone(source, hitPosition, null, null));
         }

         if (this.onHitEntity != null && result.getHitEntity() != null) {
            this.onHitEntity.cast(meta.clone(source, hitPosition, result.getHitEntity(), null));
         }
      }
   }

   private RayTraceResult getResult(SkillMetadata meta, Location source, Vector direction, double range) {
      if (this.rayTraceType == RayTraceMechanic.RayTraceType.BLOCKS) {
         return source.getWorld().rayTraceBlocks(source, direction, range, FluidCollisionMode.NEVER, this.ignorePassable);
      } else {
         Predicate<Entity> filter = this.neutral
            ? entity -> entity instanceof LivingEntity && !entity.equals(meta.getCaster().getPlayer())
            : entity -> MythicLib.plugin
               .getEntities()
               .canInteract(meta.getCaster().getPlayer(), entity, this.offense ? InteractionType.OFFENSE_SKILL : InteractionType.SUPPORT_SKILL);
         double size = this.size.evaluate(meta);
         Validate.isTrue(size >= 0.0, "Size must be positive or null");
         if (this.rayTraceType == RayTraceMechanic.RayTraceType.ENTITIES) {
            return source.getWorld().rayTraceEntities(source, direction, range, size, filter);
         } else if (this.rayTraceType == RayTraceMechanic.RayTraceType.DEFAULT) {
            return source.getWorld().rayTrace(source, direction, range, FluidCollisionMode.NEVER, this.ignorePassable, size, filter);
         } else {
            throw new RuntimeException("Not implemented");
         }
      }
   }

   public enum RayTraceType {
      DEFAULT,
      BLOCKS,
      ENTITIES;
   }
}
