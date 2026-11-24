package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class ProjectileMechanic extends DirectionMechanic {
   private final DoubleFormula speed;
   private final DoubleFormula size;
   private final DoubleFormula lifeSpan;
   private final DoubleFormula step;
   private final Script onHitBlock;
   private final Script onHitEntity;
   private final Script onTick;
   private final boolean ignorePassable;
   private final boolean stopOnBlock;
   private final boolean offense;
   private final int hitLimit;
   private static final double DEFAULT_LIFE_SPAN = 60.0;
   private static final double DEFAULT_SIZE = 0.2;
   private static final double DEFAULT_STEP = 0.2;

   public ProjectileMechanic(ConfigObject config) {
      super(config);
      this.onTick = config.contains("tick") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("tick")) : null;
      this.onHitEntity = config.contains("hit_entity") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("hit_entity")) : null;
      this.onHitBlock = config.contains("hit_block") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("hit_block")) : null;
      this.ignorePassable = config.getBoolean("ignore_passable", false);
      this.offense = config.getBoolean("offense", true);
      this.hitLimit = config.getInt("hits", 1);
      this.stopOnBlock = config.getBoolean("stop_on_block", true);
      this.speed = config.contains("speed") ? new DoubleFormula(config.getString("speed")) : new DoubleFormula(10.0);
      this.size = config.contains("size") ? new DoubleFormula(config.getString("size")) : new DoubleFormula(0.2);
      this.step = config.contains("step") ? new DoubleFormula(config.getString("step")) : new DoubleFormula(0.2);
      this.lifeSpan = config.contains("life_span") ? new DoubleFormula(config.getString("life_span")) : new DoubleFormula(60.0);
   }

   @Override
   public void cast(final SkillMetadata meta, final Location source, final Vector dir) {
      final double speed = this.speed.evaluate(meta);
      Validate.isTrue(speed > 0.0, "Speed must be strictly positive");
      final double projectileSize = this.size.evaluate(meta);
      Validate.isTrue(projectileSize >= 0.0, "Size must be positive or null");
      final double projLifeSpan = this.lifeSpan.evaluate(meta);
      Validate.isTrue(projLifeSpan > 0.0, "Life span must be strictly positive (don't make it too low)");
      (new BukkitRunnable() {
            final List<Integer> hitEntities = ProjectileMechanic.this.hitLimit > 1 ? new ArrayList<>() : null;
            final Vector dr = dir.clone().multiply(0.05 * speed);
            final double dl = this.dr.length();
            final double smallest_d = ProjectileMechanic.this.step.evaluate(meta);
            Location current = source.clone();
            int counter = 0;

            public void run() {
               if (this.counter++ >= projLifeSpan) {
                  this.cancel();
               } else {
                  this.current.add(this.dr);
                  Predicate<Entity> filter = entity -> MythicLib.plugin
                     .getEntities()
                     .canInteract(
                        meta.getCaster().getPlayer(), entity, ProjectileMechanic.this.offense ? InteractionType.OFFENSE_SKILL : InteractionType.SUPPORT_SKILL
                     );
                  RayTraceResult result = ProjectileMechanic.this.onHitBlock != null
                     ? this.current
                        .getWorld()
                        .rayTrace(this.current, dir, this.dl, FluidCollisionMode.NEVER, ProjectileMechanic.this.ignorePassable, projectileSize, filter)
                     : this.current.getWorld().rayTraceEntities(this.current, dir, this.dl, projectileSize, filter);
                  if (ProjectileMechanic.this.onTick != null) {
                     for (double j = 0.0; j < this.dl; j += this.smallest_d) {
                        Location intermediate = this.current.clone().add(dir.clone().multiply(j));
                        ProjectileMechanic.this.onTick.cast(meta.clone(source, intermediate, null, null));
                     }
                  }

                  if (result != null) {
                     if (ProjectileMechanic.this.onHitBlock != null && result.getHitBlock() != null) {
                        ProjectileMechanic.this.onHitBlock.cast(meta.clone(source, result.getHitPosition().toLocation(this.current.getWorld()), null, null));
                        if (ProjectileMechanic.this.stopOnBlock) {
                           this.cancel();
                        }
                     }

                     if (ProjectileMechanic.this.onHitEntity != null && result.getHitEntity() != null) {
                        if (this.hitEntities != null && this.hitEntities.size() + 1 < ProjectileMechanic.this.hitLimit) {
                           this.hitEntities.add(result.getHitEntity().getEntityId());
                        } else {
                           this.cancel();
                        }

                        ProjectileMechanic.this.onHitEntity
                           .cast(meta.clone(source, result.getHitPosition().toLocation(this.current.getWorld()), result.getHitEntity(), null));
                     }
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
