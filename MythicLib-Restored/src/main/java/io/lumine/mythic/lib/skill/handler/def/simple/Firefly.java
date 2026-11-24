package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Firefly extends SkillHandler<SimpleSkillResult> {
   public Firefly() {
      this.registerModifiers("damage", "duration", "knockback");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, final SkillMetadata skillMeta) {
      final double duration = skillMeta.getParameter("duration") * 20.0;
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         int j = 0;

         public void run() {
            if (this.j++ > duration) {
               this.cancel();
            }

            if (caster.getLocation().getBlock().getType() == Material.WATER) {
               caster.setVelocity(caster.getVelocity().multiply(3).setY(1.8));
               caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.5F);
               caster.getWorld().spawnParticle(VParticle.EXPLOSION.get(), caster.getLocation().add(0.0, 1.0, 0.0), 32, 0.0, 0.0, 0.0, 0.2);
               caster.getWorld().spawnParticle(Particle.CLOUD, caster.getLocation().add(0.0, 1.0, 0.0), 32, 0.0, 0.0, 0.0, 0.2);
               this.cancel();
            } else {
               for (Entity entity : caster.getNearbyEntities(1.0, 1.0, 1.0)) {
                  if (UtilityMethods.canTarget(caster, entity)) {
                     double damage = skillMeta.getParameter("damage");
                     double knockback = skillMeta.getParameter("knockback");
                     caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0F, 0.5F);
                     caster.getWorld().spawnParticle(Particle.LAVA, caster.getLocation().add(0.0, 1.0, 0.0), 32);
                     caster.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), caster.getLocation().add(0.0, 1.0, 0.0), 24, 0.0, 0.0, 0.0, 0.3);
                     caster.getWorld().spawnParticle(Particle.FLAME, caster.getLocation().add(0.0, 1.0, 0.0), 24, 0.0, 0.0, 0.0, 0.3);
                     entity.setVelocity(caster.getVelocity().setY(0.3).multiply(1.7 * knockback));
                     caster.setVelocity(caster.getEyeLocation().getDirection().multiply(-3).setY(0.5));
                     skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC);
                     this.cancel();
                     return;
                  }
               }

               Location loc = caster.getLocation().add(0.0, 1.0, 0.0);

               for (double a = 0.0; a < Math.PI * 2; a += Math.PI / 9) {
                  Vector vec = new Vector(0.6 * Math.cos(a), 0.6 * Math.sin(a), 0.0);
                  vec = UtilityMethods.rotate(vec, loc.getDirection());
                  loc.add(vec);
                  caster.getWorld().spawnParticle(VParticle.SMOKE.get(), loc, 0);
                  if (Firefly.RANDOM.nextDouble() < 0.3) {
                     caster.getWorld().spawnParticle(Particle.FLAME, loc, 0);
                  }

                  loc.add(vec.multiply(-1));
               }

               caster.setVelocity(caster.getEyeLocation().getDirection());
               caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 1.0F);
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
