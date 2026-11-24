package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VPotionEffectType;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class Stun extends SkillHandler<TargetSkillResult> {
   public Stun() {
      this.registerModifiers("duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = result.getTarget();
      target.getWorld().playSound(target.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 2.0F);
      target.getWorld().playEffect(target.getLocation(), Effect.STEP_SOUND, 42);
      target.getWorld().playEffect(target.getLocation().add(0.0, 1.0, 0.0), Effect.STEP_SOUND, 42);
      UtilityMethods.forcePotionEffect(target, VPotionEffectType.SLOWNESS.get(), skillMeta.getParameter("duration"), 254);
   }
}
