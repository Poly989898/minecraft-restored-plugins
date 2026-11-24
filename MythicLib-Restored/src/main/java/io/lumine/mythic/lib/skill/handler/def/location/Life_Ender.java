package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Life_Ender extends SkillHandler<LocationSkillResult> {
   public Life_Ender() {
      this.registerModifiers("damage", "knockback", "radius");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, final SkillMetadata skillMeta) {
      final Location loc = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      final double damage = skillMeta.getParameter("damage");
      final double knockback = skillMeta.getParameter("knockback");
      final double radius = skillMeta.getParameter("radius");
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 2.0F, 1.0F);
      (new BukkitRunnable() {
            final Location source = loc.clone()
               .add(5.0 * Math.cos(Life_Ender.RANDOM.nextDouble() * 2.0 * Math.PI), 20.0, 5.0 * Math.sin(Life_Ender.RANDOM.nextDouble() * 2.0 * Math.PI));
            final Vector vec = loc.subtract(this.source).toVector().multiply(0.03333333333333333);
            int ti = 0;

            public void run() {
               if (this.ti == 0) {
                  loc.setDirection(this.vec);
               }

               for (int k = 0; k < 2; k++) {
                  this.ti++;
                  this.source.add(this.vec);

                  for (double i = 0.0; i < Math.PI * 2; i += Math.PI / 6) {
                     Vector vec = UtilityMethods.rotate(new Vector(Math.cos(i), Math.sin(i), 0.0), loc.getDirection());
                     this.source.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), this.source, 0, vec.getX(), vec.getY(), vec.getZ(), 0.1);
                  }
               }

               if (this.ti >= 30) {
                  this.source.getWorld().playSound(this.source, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 1.0F);
                  this.source.getWorld().spawnParticle(Particle.FLAME, this.source, 64, 0.0, 0.0, 0.0, 0.25);
                  this.source.getWorld().spawnParticle(Particle.LAVA, this.source, 32);

                  for (double j = 0.0; j < Math.PI * 2; j += 0.1308996938995747) {
                     this.source.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), this.source, 0, Math.cos(j), 0.0, Math.sin(j), 0.5);
                  }

                  for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.source)) {
                     if (entity.getLocation().distanceSquared(this.source) < radius * radius && UtilityMethods.canTarget(caster, entity)) {
                        skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC);
                        entity.setVelocity(entity.getLocation().subtract(this.source).toVector().setY(0.75).normalize().multiply(knockback));
                     }
                  }

                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
