package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Bouncy_Fireball extends SkillHandler<VectorSkillResult> {
   public Bouncy_Fireball() {
      this.registerModifiers("damage", "ignite", "speed", "radius");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 2.0F, 0.0F);
      (new BukkitRunnable() {
         final Vector vec = result.getTarget().setY(0).normalize().multiply(0.5 * skillMeta.getParameter("speed"));
         final Location loc = caster.getLocation().clone().add(0.0, 1.2, 0.0);
         int j = 0;
         int bounces = 0;
         double y = 0.3;

         public void run() {
            if (this.j++ > 100) {
               this.loc.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), this.loc, 32, 0.0, 0.0, 0.0, 0.05);
               this.loc.getWorld().playSound(this.loc, Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
               this.cancel();
            } else {
               this.loc.add(this.vec);
               this.loc.add(0.0, this.y, 0.0);
               if (this.y > -0.6) {
                  this.y -= 0.05;
               }

               this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 0);
               this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 4, 0.0, 0.0, 0.0, 0.03);
               this.loc.getWorld().spawnParticle(VParticle.SMOKE.get(), this.loc, 1, 0.0, 0.0, 0.0, 0.03);
               if (this.loc.getBlock().getType().isSolid()) {
                  this.loc.add(0.0, -this.y, 0.0);
                  this.loc.add(this.vec.clone().multiply(-1));
                  this.y = 0.4;
                  this.bounces++;
                  this.loc.getWorld().playSound(this.loc, Sound.ENTITY_BLAZE_HURT, 3.0F, 2.0F);
               }

               if (this.bounces > 2) {
                  double radius = skillMeta.getParameter("radius");
                  double damage = skillMeta.getParameter("damage");
                  double ignite = skillMeta.getParameter("ignite");

                  for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                     if (entity.getLocation().distanceSquared(this.loc) < radius * radius && UtilityMethods.canTarget(caster, entity)) {
                        skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                        entity.setFireTicks((int)(ignite * 20.0));
                     }
                  }

                  this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc, 12, 2.0, 2.0, 2.0, 0.0);
                  this.loc.getWorld().spawnParticle(VParticle.EXPLOSION.get(), this.loc, 48, 0.0, 0.0, 0.0, 0.2);
                  this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 0.0F);
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
