package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Confuse extends SkillHandler<TargetSkillResult> {
   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_SHEEP_DEATH, 1.0F, 2.0F);
      (new BukkitRunnable() {
         final Location loc = target.getLocation();
         final double rads = Math.toRadians(caster.getEyeLocation().getYaw() - 90.0F);
         double ti = this.rads;

         public void run() {
            for (int j1 = 0; j1 < 3; j1++) {
               this.ti += Math.PI / 15;
               Location loc1 = this.loc.clone().add(Math.cos(this.ti), 1.0, Math.sin(this.ti));
               this.loc.getWorld().spawnParticle(VParticle.WITCH.get(), loc1, 0);
            }

            if (this.ti >= (Math.PI * 2) + this.rads) {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      Location loc = target.getLocation().clone();
      loc.setYaw(target.getLocation().getYaw() - 180.0F);
      target.teleport(loc);
   }
}
