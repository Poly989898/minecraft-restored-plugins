package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.util.CustomProjectileHandler;
import io.lumine.mythic.lib.util.RayTrace;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Fireball extends SkillHandler<SimpleSkillResult> {
   public Fireball() {
      this.registerModifiers("damage", "ignite", "ratio");
   }

   @NotNull
   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 1.0F);
      (new BukkitRunnable() {
         int j = 0;
         final Vector vec = caster.getPlayer().getEyeLocation().getDirection();
         final Location loc = caster.getPlayer().getLocation().add(0.0, 1.3, 0.0);
         final CustomProjectileHandler proj = new CustomProjectileHandler(skillMeta.getCaster(), InteractionType.OFFENSE_SKILL);

         public void run() {
            if (this.j++ > 40) {
               this.cancel();
            } else {
               this.loc.add(this.vec);
               if (this.j % 3 == 0) {
                  this.loc.getWorld().playSound(this.loc, Sound.BLOCK_FIRE_AMBIENT, 2.0F, 1.0F);
               }

               this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 4, 0.02, 0.02, 0.02, 0.0);
               this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 0);
               LivingEntity target = this.proj.findTarget(this.loc);
               if (target != null) {
                  this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 8);
                  this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 32, 0.0, 0.0, 0.0, 0.1);
                  this.loc.getWorld().playSound(this.loc, Sound.ENTITY_BLAZE_HURT, 2.0F, 1.0F);
                  target.setFireTicks((int)(target.getFireTicks() + skillMeta.getParameter("ignite") * 20.0));
                  final double damage = skillMeta.getParameter("damage");
                  skillMeta.getCaster().attack(target, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                  (new BukkitRunnable() {
                     int i = 0;

                     public void run() {
                        if (this.i++ > 2) {
                           this.cancel();
                        } else {
                           double range = 2.5 * (1.0 + Fireball.RANDOM.nextDouble());
                           Vector dir = Fireball.this.randomDirection();
                           loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_HURT, 2.0F, 1.5F);
                           RayTrace result = new RayTrace(loc, dir, range, entity -> UtilityMethods.canTarget(caster, entity));
                           if (result.hasHit()) {
                              skillMeta.getCaster().attack(result.getHit(), damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                           }

                           result.draw(0.13, tick -> tick.getWorld().spawnParticle(Particle.FLAME, tick, 0));
                        }
                     }
                  }).runTaskTimer(MythicLib.plugin, 3L, 3L);
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   private Vector randomDirection() {
      double x = RANDOM.nextDouble() - 0.5;
      double y = (RANDOM.nextDouble() - 0.2) / 2.0;
      double z = RANDOM.nextDouble() - 0.5;
      Vector dir = new Vector(x, y, z);
      return dir.lengthSquared() == 0.0 ? new Vector(1, 0, 0) : dir.normalize();
   }
}
