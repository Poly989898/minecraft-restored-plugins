package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Explosive_Turkey extends SkillHandler<VectorSkillResult> {
   public Explosive_Turkey() {
      this.registerModifiers("damage", "radius", "duration", "knockback");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = skillMeta.getParameter("duration") * 10.0;
      final double damage = skillMeta.getParameter("damage");
      final double radiusSquared = Math.pow(skillMeta.getParameter("radius"), 2.0);
      final double knockback = skillMeta.getParameter("knockback");
      final Vector vec = result.getTarget().normalize().multiply(0.6);
      final Chicken chicken = (Chicken)caster.getWorld().spawnEntity(caster.getLocation().add(0.0, 1.3, 0.0).add(vec), EntityType.CHICKEN);
      final Explosive_Turkey.ChickenHandler chickenHandler = new Explosive_Turkey.ChickenHandler(chicken);
      chicken.setInvulnerable(true);
      chicken.setSilent(true);
      chicken.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2048.0);
      chicken.setHealth(2048.0);
      chicken.setVelocity(vec);
      final double trajRatio = chicken.getVelocity().getX() / chicken.getVelocity().getZ();
      (new BukkitRunnable() {
            int ti = 0;

            public void run() {
               if (!(this.ti++ > duration) && !chicken.isDead()) {
                  chicken.setVelocity(vec);
                  if (this.ti % 4 == 0) {
                     chicken.getWorld().playSound(chicken.getLocation(), Sound.ENTITY_CHICKEN_HURT, 2.0F, 1.0F);
                  }

                  chicken.getWorld().spawnParticle(VParticle.EXPLOSION.get(), chicken.getLocation().add(0.0, 0.3, 0.0), 0);
                  chicken.getWorld().spawnParticle(VParticle.FIREWORK.get(), chicken.getLocation().add(0.0, 0.3, 0.0), 1, 0.0, 0.0, 0.0, 0.05);
                  double currentTrajRatio = chicken.getVelocity().getX() / chicken.getVelocity().getZ();
                  if (chicken.isOnGround() || Math.abs(trajRatio - currentTrajRatio) > 0.1) {
                     chickenHandler.close();
                     this.cancel();
                     chicken.getWorld().spawnParticle(VParticle.FIREWORK.get(), chicken.getLocation().add(0.0, 0.3, 0.0), 128, 0.0, 0.0, 0.0, 0.25);
                     chicken.getWorld().spawnParticle(VParticle.EXPLOSION.get(), chicken.getLocation().add(0.0, 0.3, 0.0), 24, 0.0, 0.0, 0.0, 0.25);
                     chicken.getWorld().playSound(chicken.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.5F);

                     for (Entity entity : UtilityMethods.getNearbyChunkEntities(chicken.getLocation())) {
                        if (!entity.isDead()
                           && entity.getLocation().distanceSquared(chicken.getLocation()) < radiusSquared
                           && UtilityMethods.canTarget(caster, entity)) {
                           skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                           entity.setVelocity(
                              entity.getLocation().toVector().subtract(chicken.getLocation().toVector()).multiply(0.1 * knockback).setY(0.4 * knockback)
                           );
                        }
                     }
                  }
               } else {
                  chickenHandler.close();
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   public static class ChickenHandler extends TemporaryListener {
      private final Chicken chicken;

      public ChickenHandler(Chicken chicken) {
         super();
         this.chicken = chicken;
      }

      @Override
      public void whenClosed() {
         this.chicken.remove();
      }

      @EventHandler
      public void a(EntityDeathEvent event) {
         if (event.getEntity().equals(this.chicken)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
         }
      }
   }
}
