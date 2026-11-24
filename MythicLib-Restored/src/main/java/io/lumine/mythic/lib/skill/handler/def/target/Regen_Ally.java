package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ally extends SkillHandler<TargetSkillResult> {
   public Regen_Ally() {
      this.registerModifiers("heal", "duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta, InteractionType.SUPPORT_SKILL);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      (new BukkitRunnable() {
         final double duration = Math.min(skillMeta.getParameter("duration"), 60.0) * 20.0;
         final double hps = skillMeta.getParameter("heal") / this.duration * 4.0;
         double ti = 0.0;
         double a = 0.0;

         public void run() {
            if (!(this.ti++ > this.duration) && !target.isDead()) {
               this.a += Math.PI / 16;
               target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(1.3 * Math.cos(this.a), 0.3, 1.3 * Math.sin(this.a)), 0);
               if (this.ti % 4.0 == 0.0) {
                  UtilityMethods.heal(target, this.hps);
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
