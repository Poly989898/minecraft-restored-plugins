package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Wither extends SkillHandler<TargetSkillResult> {
   public Wither() {
      this.registerModifiers("duration", "amplifier");
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
            if (this.y > 3.0) {
               this.cancel();
            }

            for (int j1 = 0; j1 < 3; j1++) {
               this.y += 0.07;

               for (int j = 0; j < 3; j++) {
                  double a = this.y * Math.PI + j * Math.PI * 2.0 / 3.0;
                  double x = Math.cos(a) * (3.0 - this.y) / 2.5;
                  double z = Math.sin(a) * (3.0 - this.y) / 2.5;
                  this.loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), this.loc.clone().add(x, this.y, z), 1, new DustOptions(Color.BLACK, 1.0F));
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_SHOOT, 2.0F, 2.0F);
      target.addPotionEffect(
         new PotionEffect(PotionEffectType.WITHER, (int)(skillMeta.getParameter("duration") * 20.0), (int)skillMeta.getParameter("amplifier"))
      );
   }
}
