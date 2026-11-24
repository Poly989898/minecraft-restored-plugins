package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class Burn extends SkillHandler<TargetSkillResult> {
   public Burn() {
      this.registerModifiers("duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      (new BukkitRunnable() {
         final Location loc = target.getLocation();
         double y = 0.0;

         public void run() {
            for (int j1 = 0; j1 < 3; j1++) {
               this.y += 0.04;

               for (int j = 0; j < 2; j++) {
                  double xz = this.y * Math.PI * 1.3 + j * Math.PI;
                  Location loc1 = this.loc.clone().add(Math.cos(xz), this.y, Math.sin(xz));
                  this.loc.getWorld().spawnParticle(Particle.FLAME, loc1, 0);
               }
            }

            if (this.y >= 1.7) {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0F, 2.0F);
      target.setFireTicks((int)(Math.max(0, target.getFireTicks()) + skillMeta.getParameter("duration") * 20.0));
   }
}
