package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Deep_Wound extends SkillHandler<TargetSkillResult> {
   public Deep_Wound() {
      this.registerModifiers("damage", "extra");
   }

   @NotNull
   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = result.getTarget();
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2.0F, 2.0F);
      target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0), 32, 0.0, 0.0, 0.0, 0.7);
      target.getWorld()
         .spawnParticle(
            VParticle.BLOCK.get(),
            target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0),
            32,
            0.0,
            0.0,
            0.0,
            2.0,
            Material.REDSTONE_BLOCK.createBlockData()
         );
      double max = target.getAttribute(Attribute.MAX_HEALTH).getValue();
      double ratio = (max - target.getHealth()) / max;
      double damage = skillMeta.getParameter("damage") * (1.0 + skillMeta.getParameter("extra") * ratio / 100.0);
      skillMeta.getCaster().attack(target, damage, DamageType.SKILL, DamageType.PHYSICAL);
   }
}
