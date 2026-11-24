package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Frozen_Aura extends SkillHandler<SimpleSkillResult> {
   public Frozen_Aura() {
      this.registerModifiers("duration", "amplifier", "radius");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      final double duration = skillMeta.getParameter("duration") * 20.0;
      final double radiusSquared = Math.pow(skillMeta.getParameter("radius"), 2.0);
      final double amplifier = skillMeta.getParameter("amplifier") - 1.0;
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
            double j = 0.0;
            int ti = 0;

            public void run() {
               if (this.ti++ > duration) {
                  this.cancel();
               }

               this.j += 0.05235987755982988;

               for (double k = 0.0; k < Math.PI * 2; k += Math.PI / 2) {
                  caster.getWorld()
                     .spawnParticle(
                        VParticle.INSTANT_EFFECT.get(),
                        caster.getLocation().add(Math.cos(k + this.j) * 2.0, 1.0 + Math.sin(k + this.j * 7.0) / 3.0, Math.sin(k + this.j) * 2.0),
                        0
                     );
               }

               if (this.ti % 2 == 0) {
                  caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_SNOW_BREAK, 1.0F, 1.0F);
               }

               if (this.ti % 7 == 0) {
                  for (Entity entity : UtilityMethods.getNearbyChunkEntities(caster.getLocation())) {
                     if (entity.getLocation().distanceSquared(caster.getLocation()) < radiusSquared && UtilityMethods.canTarget(caster, entity)) {
                        UtilityMethods.forcePotionEffect((LivingEntity)entity, VPotionEffectType.SLOWNESS.get(), 2.0, (int)amplifier);
                     }
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
