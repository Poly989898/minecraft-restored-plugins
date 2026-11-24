package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Slow extends SkillHandler<TargetSkillResult> {
   public Slow() {
      this.registerModifiers("duration", "amplifier");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      (new BukkitRunnable() {
            final Location loc = target.getLocation();
            double ti = 0.0;

            public void run() {
               this.ti += Math.PI / 10;
               if (this.ti >= Math.PI * 2) {
                  this.cancel();
               }

               for (double j = 0.0; j < Math.PI * 2; j += Math.PI) {
                  for (double r = 0.0; r < 0.7; r += 0.1) {
                     this.loc
                        .getWorld()
                        .spawnParticle(
                           VParticle.REDSTONE.get(),
                           this.loc.clone().add(Math.cos(this.ti / 2.0 + j + Math.PI * r) * r * 2.0, 0.1, Math.sin(this.ti / 2.0 + j + Math.PI * r) * r * 2.0),
                           1,
                           new DustOptions(Color.WHITE, 1.0F)
                        );
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LLAMA_ANGRY, 1.0F, 2.0F);
      target.addPotionEffect(
         new PotionEffect(VPotionEffectType.SLOWNESS.get(), (int)(skillMeta.getParameter("duration") * 20.0), (int)skillMeta.getParameter("amplifier"))
      );
   }
}
