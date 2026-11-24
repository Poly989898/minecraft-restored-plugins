package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Firebolt extends SkillHandler<VectorSkillResult> {
   public Firebolt() {
      this.registerModifiers("damage", "ignite");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 1.0F);
      (new BukkitRunnable() {
            final Vector vec = result.getTarget().multiply(0.8);
            final Location loc = caster.getEyeLocation();
            int ti = 0;

            public void run() {
               this.ti++;
               if (this.ti > 20) {
                  this.cancel();
               }

               List<Entity> entities = UtilityMethods.getNearbyChunkEntities(this.loc);
               this.loc.getWorld().playSound(this.loc, Sound.BLOCK_FIRE_AMBIENT, 2.0F, 1.0F);

               for (int j = 0; j < 2; j++) {
                  this.loc.add(this.vec);
                  if (this.loc.getBlock().getType().isSolid()) {
                     this.cancel();
                  }

                  this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 5, 0.12, 0.12, 0.12, 0.0);
                  if (Firebolt.RANDOM.nextDouble() < 0.3) {
                     this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 0);
                  }

                  for (Entity target : entities) {
                     if (UtilityMethods.canTarget(caster, this.loc, target)) {
                        this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 32, 0.0, 0.0, 0.0, 0.1);
                        this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 8, 0.0, 0.0, 0.0, 0.0);
                        this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc, 0);
                        this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 1.0F);
                        skillMeta.getCaster()
                           .attack((LivingEntity)target, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                        target.setFireTicks((int)skillMeta.getParameter("ignite") * 20);
                        this.cancel();
                        return;
                     }
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
