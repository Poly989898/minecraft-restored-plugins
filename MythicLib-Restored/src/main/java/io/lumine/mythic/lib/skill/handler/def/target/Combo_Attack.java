package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class Combo_Attack extends SkillHandler<TargetSkillResult> {
   private static final long ATTACK_PERIOD = 5L;

   public Combo_Attack() {
      this.registerModifiers("damage", "count");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta, 10.0, InteractionType.OFFENSE_SKILL);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final int count = (int)Math.max(1.0, skillMeta.getParameter("count"));
      final double damage = skillMeta.getParameter("damage") / count;
      final LivingEntity target = result.getTarget();
      this.playEffect(target);
      skillMeta.getCaster().attack(target, damage, DamageType.SKILL, DamageType.PHYSICAL);
      (new BukkitRunnable() {
            int counter = 1;

            public void run() {
               if (this.counter++ < count && !UtilityMethods.isInvalidated(skillMeta.getCaster()) && !target.isDead()) {
                  Combo_Attack.this.playEffect(target);
                  MythicLib.plugin
                     .getDamage()
                     .registerAttack(
                        new AttackMetadata(new DamageMetadata(damage, DamageType.SKILL, DamageType.PHYSICAL), target, skillMeta.getCaster()), true, true
                     );
               } else {
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 5L, 5L);
   }

   private void playEffect(Entity target) {
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0F, 2.0F);
      target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0), 24, 0.0, 0.0, 0.0, 0.7);
   }
}
