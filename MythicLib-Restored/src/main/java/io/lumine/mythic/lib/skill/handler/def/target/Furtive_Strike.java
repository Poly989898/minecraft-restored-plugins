package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.util.SmallParticleEffect;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class Furtive_Strike extends SkillHandler<TargetSkillResult> {
   public Furtive_Strike() {
      this.registerModifiers("damage", "extra", "radius");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = result.getTarget();
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2.0F, 1.5F);
      target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0), 32, 0.0, 0.0, 0.0, 0.5);
      target.getWorld().spawnParticle(VParticle.SMOKE.get(), target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0), 64, 0.0, 0.0, 0.0, 0.08);
      double damage = skillMeta.getParameter("damage");
      double radius = skillMeta.getParameter("radius");
      if (target.getNearbyEntities(radius, radius, radius).stream().allMatch(entity -> entity.equals(skillMeta.getCaster().getPlayer()))) {
         new SmallParticleEffect(target, VParticle.WITCH.get());
         damage *= 1.0 + skillMeta.getParameter("extra") / 100.0;
      }

      skillMeta.getCaster().attack(target, damage, DamageType.SKILL, DamageType.PHYSICAL);
   }
}
