package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Heavy_Charge extends SkillHandler<VectorSkillResult> {
   public Heavy_Charge() {
      this.registerModifiers("damage", "knockback");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final double knockback = skillMeta.getParameter("knockback");
      (new BukkitRunnable() {
         final Vector vec = result.getTarget().setY(-1);
         double ti = 0.0;

         public void run() {
            if (this.ti++ > 20.0) {
               this.cancel();
            }

            if (this.ti < 9.0) {
               caster.setVelocity(this.vec);
               caster.getWorld().spawnParticle(VParticle.EXPLOSION.get(), caster.getLocation().add(0.0, 1.0, 0.0), 3, 0.13, 0.13, 0.13, 0.0);
            }

            for (Entity target : caster.getNearbyEntities(1.0, 1.0, 1.0)) {
               if (UtilityMethods.canTarget(caster, target)) {
                  caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0F, 1.0F);
                  caster.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), target.getLocation().add(0.0, 1.0, 0.0), 0);
                  target.setVelocity(caster.getVelocity().setY(0.3).multiply(1.7 * knockback));
                  caster.setVelocity(caster.getVelocity().setX(0).setY(0).setZ(0));
                  skillMeta.getCaster().attack((LivingEntity)target, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.PHYSICAL);
                  this.cancel();
                  break;
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
