package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Death_Mark extends SkillHandler<TargetSkillResult> {
   public Death_Mark() {
      this.registerModifiers("damage", "duration", "amplifier");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      final double duration = skillMeta.getParameter("duration") * 20.0;
      final double dps = skillMeta.getParameter("damage") / duration * 20.0;
      (new BukkitRunnable() {
         double ti = 0.0;

         public void run() {
            this.ti++;
            if (!(this.ti > duration) && !target.isDead()) {
               target.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), target.getLocation(), 4, 0.2, 0.0, 0.2, 0.0);
               if (this.ti % 20.0 == 0.0) {
                  skillMeta.getCaster().attack(target, dps, false, DamageType.SKILL, DamageType.MAGIC);
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0F, 2.0F);
      target.removePotionEffect(VPotionEffectType.SLOWNESS.get());
      target.addPotionEffect(new PotionEffect(VPotionEffectType.SLOWNESS.get(), (int)duration, (int)skillMeta.getParameter("amplifier")));
   }
}
