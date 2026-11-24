package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poison extends SkillHandler<TargetSkillResult> {
   public Poison() {
      this.registerModifiers("duration", "amplifier");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = result.getTarget();
      target.getWorld().spawnParticle(VParticle.ITEM_SLIME.get(), target.getLocation().add(0.0, 1.0, 0.0), 32, 1.0, 1.0, 1.0, 0.0);
      target.getWorld().spawnParticle(VParticle.HAPPY_VILLAGER.get(), target.getLocation().add(0.0, 1.0, 0.0), 24, 1.0, 1.0, 1.0, 0.0);
      target.getWorld().playSound(target.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.5F, 2.0F);
      target.addPotionEffect(
         new PotionEffect(PotionEffectType.POISON, (int)(skillMeta.getParameter("duration") * 20.0), (int)skillMeta.getParameter("amplifier"))
      );
   }
}
