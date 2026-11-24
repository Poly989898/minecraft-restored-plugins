package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Shock extends SkillHandler<TargetSkillResult> {
   public Shock() {
      this.registerModifiers("duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = skillMeta.getParameter("duration");
      target.getWorld().playSound(target.getLocation(), VSound.ENTITY_ZOMBIE_PIGMAN_ANGRY.get(), 1.0F, 2.0F);
      (new BukkitRunnable() {
         final Location loc = target.getLocation();
         final double rads = Math.toRadians(caster.getEyeLocation().getYaw() - 90.0F);
         double ti = this.rads;

         public void run() {
            for (int j = 0; j < 3; j++) {
               this.ti += Math.PI / 15;
               target.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), this.loc.clone().add(Math.cos(this.ti), 1.0, Math.sin(this.ti)), 0);
            }

            if (this.ti >= (Math.PI * 2) + this.rads) {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      (new BukkitRunnable() {
         int ti;

         public void run() {
            if (!(this.ti++ > (duration > 300.0 ? 300.0 : duration * 10.0)) && !target.isDead()) {
               target.playEffect(EntityEffect.HURT);
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 2L);
   }
}
